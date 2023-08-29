package dev.ov4client.addon.modules.combat;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.enums.RotationType;
import dev.ov4client.addon.enums.SwingHand;
import dev.ov4client.addon.hud.ToastNotifications;
import dev.ov4client.addon.managers.Managers;
import dev.ov4client.addon.modules.info.Notifications;
import dev.ov4client.addon.utils.SettingUtils;
import dev.ov4client.addon.utils.entity.EntityInfo;
import dev.ov4client.addon.utils.world.BlockInfo;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.DamageUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CevBreaker extends ov4Module {
    public CevBreaker() {
        super(ov4client.Combat, "Cev Breaker", "Break crystals over a ppl's head to deal massive damage!");
    }

    private final SettingGroup sgGeneral = settings.createGroup("General");
    private final SettingGroup sgBreaking = settings.createGroup("Breaking");
    private final SettingGroup sgPause = settings.createGroup("Pause");
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgNone = settings.createGroup("");

    // General
    private final Setting<Boolean> toggleModules = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle-modules")
        .description("Turn off other modules when Cev Breaker is activated.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> toggleBack = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle-back-on")
        .description("Turn the modules back on when Cev Breaker is deactivated.")
        .defaultValue(false)
        .visible(toggleModules::get)
        .build()
    );

    private final Setting<List<Module>> modules = sgGeneral.add(new ModuleListSetting.Builder()
        .name("modules")
        .description("Which modules to toggle.")
        .visible(toggleModules::get)
        .build()
    );

    // Breaking
    private final Setting<Mode> mode = sgBreaking.add(new EnumSetting.Builder<Mode>()
        .name("Mode")
        .description("Which mode to use for breaking the obsidian.")
        .defaultValue(Mode.Instant)
        .build()
    );

    private final Setting<Boolean> smartDelay = sgBreaking.add(new BoolSetting.Builder()
        .name("Smart Delay")
        .description("Waits until the target can get damaged again with breaking the block.")
        .defaultValue(true)
        .visible(() -> mode.get() == Mode.Instant)
        .build()
    );

    private final Setting<Integer> switchDelay = sgBreaking.add(new IntSetting.Builder()
        .name("switch-delay")
        .description("How many ticks to wait before hitting an entity after switching hotbar slots.")
        .defaultValue(1)
        .range(0,20)
        .sliderRange(0,20)
        .visible(() -> mode.get() == Mode.Packet)
        .build()
    );

    // Pause
    private final Setting<Double> pauseAtHealth = sgPause.add(new DoubleSetting.Builder()
        .name("pause-health")
        .description("Pauses when you go below a certain health.")
        .defaultValue(5)
        .min(0)
        .build()
    );

    private final Setting<Boolean> eatPause = sgPause.add(new BoolSetting.Builder()
        .name("pause-on-eat")
        .description("Pauses Crystal Aura when eating.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> drinkPause = sgPause.add(new BoolSetting.Builder()
        .name("pause-on-drink")
        .description("Pauses Crystal Aura when drinking.")
        .defaultValue(true)
        .build()
    );

    // Render
    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder()
        .name("Swing")
        .description("Renders your swing client-side.")
        .defaultValue(true)
        .build()
    );
    private final Setting<SwingHand> placeHand = sgRender.add(new EnumSetting.Builder<SwingHand>()
        .name("Swing Hand")
        .description("Which hand should be swung.")
        .defaultValue(SwingHand.RealHand)
        .visible(swing::get)
        .build()
    );
    private final Setting<Notifications.Mode> notifications = sgNone.add(new EnumSetting.Builder<Notifications.Mode>()
        .name("Notifications")
        .defaultValue(Notifications.Mode.Chat)
        .build()
    );

    private PlayerEntity target;
    private boolean startedYet;
    private int switchDelayLeft, timer, breakDelayLeft;
    private final List<PlayerEntity> blacklisted = new ArrayList<>();
    private final List<EndCrystalEntity> crystals = new ArrayList<>();
    public ArrayList<Module> toActivate;

    boolean pause = false;

    public enum Mode {
        Normal,
        Packet,
        Instant
    }

    @EventHandler
    public void onActivate() {
        target = null;
        startedYet = false;
        switchDelayLeft = 0;
        timer = 0;
        blacklisted.clear();

        toActivate = new ArrayList<>();

        if (toggleModules.get() && !modules.get().isEmpty() && mc.world != null && mc.player != null) {
            for (Module module : modules.get()) {
                if (module.isActive()) {
                    module.toggle();
                    toActivate.add(module);
                }
            }
        }
    }

    @Override
    public void onDeactivate() {
        if (toggleBack.get() && !toActivate.isEmpty() && mc.world != null && mc.player != null) {
            for (Module module : toActivate) {
                if (!module.isActive()) {
                    module.toggle();
                }
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        switchDelayLeft--;
        breakDelayLeft--;
        timer--;
        int crystalSlot = InvUtils.findInHotbar(Items.END_CRYSTAL).slot();
        int obsidianSlot = InvUtils.findInHotbar(Items.OBSIDIAN).slot();
        int pickSlot = InvUtils.findInHotbar(Items.NETHERITE_PICKAXE).slot();
        pickSlot = pickSlot == -1 ? InvUtils.findInHotbar(Items.DIAMOND_PICKAXE).slot() : pickSlot;

        if((crystalSlot == -1 && !(mc.player.getOffHandStack().getItem() instanceof EndCrystalItem)) || obsidianSlot == -1 || pickSlot == -1) {
            switch (notifications.get()) {
                case Toast -> ToastNotifications.addToast("No " + (crystalSlot == -1 && !(mc.player.getOffHandStack().getItem() instanceof EndCrystalItem) ? "crystals" : (obsidianSlot == -1 ? "obsidian" :  "pickaxe")) + " found, disabling...");
                case Notification -> Managers.NOTIFICATION.warn(title, "No " + (crystalSlot == -1 && !(mc.player.getOffHandStack().getItem() instanceof EndCrystalItem) ? "crystals" : (obsidianSlot == -1 ? "obsidian" :  "pickaxe")) + " found, disabling...");
                case Chat -> warning("No " + (crystalSlot == -1 && !(mc.player.getOffHandStack().getItem() instanceof EndCrystalItem) ? "crystals" : (obsidianSlot == -1 ? "obsidian" :  "pickaxe")) + " found, disabling...");
            }
            toggle();
            return;
        }
        getEntities();
        if(target == null) {
            switch (notifications.get()) {
                case Toast -> ToastNotifications.addToast("No target found, disabling...");
                case Notification -> Managers.NOTIFICATION.error(title, "No target found, disabling...");
                case Chat -> error("No target found, disabling...");
            }
            toggle();
            return;
        }

        // Check pause settings
        if (PlayerUtils.shouldPause(false, eatPause.get(), drinkPause.get()) || PlayerUtils.getTotalHealth() <= pauseAtHealth.get()) {
            switch (notifications.get()) {
                case Toast -> ToastNotifications.addToast("Pausing");
                case Notification -> Managers.NOTIFICATION.warn(title, "Pausing");
                case Chat -> warning("Pausing");
            }
            pause = true;
            return;
        } else {
            pause = false;
        }

        BlockPos blockPos = target.getBlockPos().add(0, 2, 0);
        BlockState blockState = mc.world.getBlockState(blockPos);
        boolean crystalThere = false;
        for(EndCrystalEntity crystal : crystals) {
            if(crystal.getBlockPos().add(0, -1, 0).equals(blockPos)) {
                crystalThere = true;
                break;
            }
        }

        //Placing obby
        if(!blockState.isOf(Blocks.OBSIDIAN) && !crystalThere && (mc.player.getMainHandStack().getItem().equals(Items.OBSIDIAN) || switchDelayLeft <= 0)) {
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) Managers.ROTATION.start(blockPos, 50, RotationType.BlockPlace, Objects.hash(name + "placing"));
            if (!BlockUtils.place(blockPos, InvUtils.findInHotbar(Items.OBSIDIAN), false, 50, swing.get(), true, true)) {
                blacklisted.add(target);
                getEntities();
                if(target == null) {
                    switch (notifications.get()) {
                        case Toast -> ToastNotifications.addToast("Can't place obsidian above the target! Disabling...");
                        case Notification -> Managers.NOTIFICATION.warn(title, "Can't place obsidian above the target! Disabling...");
                        case Chat -> warning("Can't place obsidian above the target! Disabling...");
                    }
                    toggle();
                }
                return;
            }
        }

        //Placing crystal
        boolean offhand = mc.player.getOffHandStack().getItem() instanceof EndCrystalItem;
        boolean mainhand = mc.player.getMainHandStack().getItem() instanceof EndCrystalItem;
        if(!crystalThere && blockState.isOf(Blocks.OBSIDIAN)) {
            if(!(offhand || mainhand || switchDelayLeft <= 0)) return;
            double x = blockPos.up().getX();
            double y = blockPos.up().getY();
            double z = blockPos.up().getZ();

            if(!mc.world.getOtherEntities(null, new Box(x, y, z, x + 1D, y + 2D, z + 1D)).isEmpty()
                || !mc.world.getBlockState(blockPos.up()).isAir()) {
                blacklisted.add(target);
                getEntities();
                if(target == null) {
                    switch (notifications.get()) {
                        case Toast -> ToastNotifications.addToast("Can't place the crystal! Disabling...");
                        case Notification -> Managers.NOTIFICATION.warn(title, "Can't place the crystal! Disabling...");
                        case Chat -> warning("Can't place the crystal! Disabling...");
                    }
                    toggle();
                }
                return;
            }
            else {
                if(!offhand && !mainhand) mc.player.getInventory().selectedSlot = crystalSlot;
                Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
                BlockHitResult result = new BlockHitResult(mc.player.getPos(), blockPos.getY() < mc.player.getY() ? Direction.UP : Direction.DOWN, blockPos, false);
                if (swing.get()) clientSwing(placeHand.get(), Hand.MAIN_HAND);
                else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                    Managers.ROTATION.start(blockPos, 25, RotationType.BlockPlace, Objects.hash(name + "placing"));
                    mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, result, 0));
                } else mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, result, 0));
            }
        }

        //Breaking obby
        if(blockState.isAir() && mode.get() == Mode.Packet) startedYet = false;
        if((mc.player.getInventory().selectedSlot == pickSlot || switchDelayLeft <= 0) && crystalThere && blockState.isOf(Blocks.OBSIDIAN)) {
            Direction direction = EntityInfo.rayTraceCheck(blockPos, true);
            if(mode.get() == Mode.Instant) {
                if(!startedYet) {
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
                    startedYet = true;
                } else {
                    if(smartDelay.get() && target.hurtTime > 0) return;
                    mc.player.getInventory().selectedSlot = pickSlot;
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
                }
            }
            else if(mode.get() == Mode.Normal) {
                mc.player.getInventory().selectedSlot = pickSlot;
                mc.interactionManager.updateBlockBreakingProgress(blockPos, direction);
            }
            else if(mode.get() == Mode.Packet) {
                timer = startedYet ? timer : BlockInfo.getBlockBreakingSpeed(blockState, blockPos, pickSlot);
                if(!startedYet) {
                    packetMine(blockPos, swing.get());
                    startedYet = true;
                }
                else if(timer <= 0){
                    mc.player.getInventory().selectedSlot = pickSlot;
                }
            }
        }

        // Breaking the crystal
        AutoCrystal ACrystal = Modules.get().get(AutoCrystal.class);
        if(ACrystal.bestTarget == null || ACrystal.bestTarget != target || ACrystal.minDmg.get() >= 6) {
            if (mode.get() == Mode.Packet && breakDelayLeft >= 0) return;
            for(EndCrystalEntity crystal : crystals) {
                if(DamageUtils.crystalDamage(target, crystal.getPos()) >= 6) {
                    if (swing.get()) clientSwing(placeHand.get(), Hand.MAIN_HAND);
                    else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    if (SettingUtils.shouldRotate(RotationType.Mining)) {
                        Managers.ROTATION.start(crystal.getBoundingBox(), 30, RotationType.Mining, Objects.hash(name + "mining"));
                        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, false));
                    } else mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, false));
                    break;
                }
            }
        }
    }

    private void getEntities() {
        target = null;
        crystals.clear();

        for (Entity entity : mc.world.getEntities()) {
            if (entity.isInRange(mc.player, 6) && entity.isAlive()) {
                if (entity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity) entity;
                    if (playerEntity != mc.player && Friends.get().shouldAttack(playerEntity)) {
                        if (target == null || mc.player.distanceTo(playerEntity) < mc.player.distanceTo(target)) {
                            if (!blacklisted.contains(playerEntity)) {
                                target = playerEntity;
                            }
                        }
                    }
                } else if (entity instanceof EndCrystalEntity) {
                    crystals.add((EndCrystalEntity) entity);
                }
            }
        }
    }

    public void packetMine(BlockPos blockPos, boolean swing) {
        if (SettingUtils.shouldRotate(RotationType.Mining)) {
            Managers.ROTATION.start(blockPos, priority, RotationType.Mining, Objects.hash(name + "mining"));
            packetMine(blockPos, swing);
        } else {
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
            if (swing) clientSwing(placeHand.get(), Hand.MAIN_HAND);
            else mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
        }
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof UpdateSelectedSlotC2SPacket) {
            switchDelayLeft = 1;
            breakDelayLeft = switchDelay.get();
        }
    }

    @Override
    public String getInfoString() {
        if (target != null) return target.getEntityName();
        return null;
    }
}
