package dev.ov4client.addon.modules.combat;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.enums.RotationType;
import dev.ov4client.addon.enums.SwingHand;
import dev.ov4client.addon.managers.Managers;
import dev.ov4client.addon.utils.SettingUtils;
import dev.ov4client.addon.utils.entity.EntityInfo;
import dev.ov4client.addon.utils.timers.HarvestTimerUtils;
import dev.ov4client.addon.utils.world.BlockInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class SpeedMine extends ov4Module {
    public SpeedMine() {
        super(ov4client.Combat, "Speed Mine+", "Sends packets to mine blocks without the mining animation.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgFastBreak = settings.createGroup("Fast Break");
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> offset = sgGeneral.add(new IntSetting.Builder()
        .name("Offset")
        .defaultValue(0)
        .sliderRange(0, 200)
        .build()
    );
    private final Setting<SwitchMode> switchMode = sgGeneral.add(new EnumSetting.Builder<SwitchMode>()
        .name("Switch Mode")
        .defaultValue(SwitchMode.Fastest)
        .build()
    );
    private final Setting<Integer> actionDelay = sgGeneral.add(new IntSetting.Builder()
        .name("Action Delay")
        .defaultValue(0)
        .sliderRange(0, 5)
        .build()
    );
    private final Setting<BypassMode> bypass = sgGeneral.add(new EnumSetting.Builder<BypassMode>()
        .name("Bypass")
        .defaultValue(BypassMode.Auto)
        .build()
    );
    private final Setting<Boolean> instant = sgGeneral.add(new BoolSetting.Builder()
        .name("Instant")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> crystal = sgGeneral.add(new BoolSetting.Builder()
        .name("Crystal")
        .description("Place crystal on target block.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> age = sgGeneral.add(new IntSetting.Builder()
        .name("Age")
        .defaultValue(1)
        .sliderRange(0, 3)
        .build()
    );
    private final Setting<Boolean> surroundOnly = sgGeneral.add(new BoolSetting.Builder()
        .name("Only Surround")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> amplifier = sgGeneral.add(new IntSetting.Builder()
        .name("Amplifiter")
        .defaultValue(2)
        .sliderRange(1, 2)
        .build()
    );
    private final Setting<Boolean> ignoreAir = sgGeneral.add(new BoolSetting.Builder()
        .name("Ignore Air")
        .defaultValue(true)
        .build()
    );

    //--------------------Fast Break--------------------//
    private final Setting<Boolean> fastBreak = sgFastBreak.add(new BoolSetting.Builder()
        .name("Fast Break")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> haste = sgFastBreak.add(new BoolSetting.Builder()
        .name("Haste")
        .defaultValue(false)
        .build()
    );

    //--------------------Render--------------------//
    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
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
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("Shape Mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("Side Color")
        .description(ov4client.COLOR)
        .defaultValue(new SettingColor(255, 255, 255, 100))
        .build()
    );
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("Line Color")
        .description(ov4client.COLOR)
        .defaultValue(new SettingColor(255, 255, 255,100))
        .build()
    );

    public enum BypassMode {
        Auto,
        Manual,
        OFF
    }

    public enum SwitchMode {
        NoDrop,
        Fastest
    }

    private BlockPos blockPos = null;
    private Direction direction = null;

    private FindItemResult crystalItem;
    private int pickSlot;

    private int breakTimes;

    private long start, total;
    private final HarvestTimerUtils timer = new HarvestTimerUtils();
    private final HarvestTimerUtils mineTimer = new HarvestTimerUtils();

    @Override
    public void onActivate() {
        breakTimes = 0;
    }

    @Override
    public void onDeactivate() {
        blockPos = null;
        direction = null;
        if (mc.player.hasStatusEffect(StatusEffects.HASTE)) mc.player.removeStatusEffect(StatusEffects.HASTE);
    }

    @EventHandler
    public void onStartBreaking(StartBreakingBlockEvent event) {
        BlockPos blockPos = new BlockPos(event.blockPos);

        if (!BlockInfo.isBreakable(blockPos)) return;
        if (surroundOnly.get() && !EntityInfo.isPlayerNear(blockPos)) return;

        this.blockPos = blockPos;
        this.direction = event.direction;

        breakTimes = 0;
        BlockInfo.progress = 0;

        start = System.currentTimeMillis();
        total = -1;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (blockPos == null) return;
        pickSlot = pickSlot();

        if (pickSlot == 420) return;

        if (!SettingUtils.inMineRange(blockPos)) {
            return;
        }

        addhaste(haste.get());

        crystalItem = InvUtils.find(Items.END_CRYSTAL);
        if (!BlockInfo.canBreak(pickSlot, blockPos)) {
            timer.reset();

            total = (System.currentTimeMillis() - start) + offset.get();
            mineTimer.reset();
        } else {
            if (this.crystal.get() && crystalItem.found()) swap(crystalItem.slot(), blockPos, true, true);
            swap(pickSlot, blockPos, false, true);
        }

        if (bypass.get().equals(BypassMode.Auto) && !BlockInfo.isAir(blockPos) && BlockInfo.distanceTo(blockPos) < 5) {
            long total = this.total;

            long age = total - (this.age.get() * 20);
            if (mineTimer.passedMillis(age)) {
                if (this.crystal.get() && crystalItem.found()) swap(crystalItem.slot(), blockPos, true, false);
            }

            if (SettingUtils.shouldRotate(RotationType.Mining)) Managers.ROTATION.start(blockPos, priority, RotationType.Mining, Objects.hash(name + "mining"));

            if (mineTimer.passedMillis(total)) {
                swap(pickSlot, blockPos, false, false);
                mineTimer.reset();
            }
        }
    }

    @EventHandler
    public void onBreak(BreakBlockEvent event) {
        if (pickSlot == 420) return;
        if (!bypass.get().equals(BypassMode.Manual)) return;

        if (this.crystal.get() && crystalItem.found()) swap(crystalItem.slot(), blockPos, true, false);
        swap(pickSlot, blockPos, false, false);
    }

    private void swap(int slot, BlockPos pos, boolean crystal, boolean check) {
        if (check && (slot == 420 || BlockInfo.progress < 1 || (ignoreAir.get() && BlockInfo.isAir(blockPos)) || (!instant.get() && breakTimes >= 1) || !timer.passedTicks(actionDelay.get())))
            return;

        move(mc.player.getInventory().selectedSlot, slot);
        if (crystal) {
            BlockHitResult hitResult = new BlockHitResult(BlockInfo.closestVec3d2(blockPos), Direction.UP, new BlockPos(blockPos), false);
            if (BlockInfo.of(Blocks.OBSIDIAN, blockPos) && EntityInfo.isPlayerNear(blockPos)) {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
            }
        } else {
            mine(pos);
        }
        move(mc.player.getInventory().selectedSlot, slot);
        timer.reset();
    }

    private int pickSlot() {
        FindItemResult pick = switchMode.get().equals(SwitchMode.Fastest) ? InvUtils.findFastestTool(mc.world.getBlockState(blockPos)) : InvUtils.find(Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE);
        return pick.found() ? pick.slot() : 420;
    }

    private void move(int from, int to) {
        ScreenHandler handler = mc.player.currentScreenHandler;

        Int2ObjectArrayMap<ItemStack> stack = new Int2ObjectArrayMap<>();
        stack.put(to, handler.getSlot(to).getStack());

        sendPacket(new ClickSlotC2SPacket(handler.syncId, handler.getRevision(), PlayerInventory.MAIN_SIZE + from, to, SlotActionType.SWAP, handler.getCursorStack().copy(), stack));
    }

    private void mine(BlockPos blockPos) {
        sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
        if (swing.get()) clientSwing(placeHand.get(), Hand.MAIN_HAND);

        if (fastBreak.get()) BlockInfo.state(Blocks.AIR, blockPos);
        breakTimes++;
    }

    private void addhaste(boolean haste) {
        if (mc.player.hasStatusEffect(StatusEffects.HASTE) || !haste) return;

        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 255, amplifier.get() - 1, false, false, true));
    }


    @EventHandler
    public void onRender(Render3DEvent event) {
        if (blockPos == null) return;

        int slot = pickSlot();
        if (slot == 420) return;

        double min = BlockInfo.progress / 2;
        Vec3d vec3d = blockPos.toCenterPos();
        Box box = new Box(vec3d.x - min, vec3d.y - min, vec3d.z - min, vec3d.x + min, vec3d.y + min, vec3d.z + min);

        event.renderer.box(box, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
