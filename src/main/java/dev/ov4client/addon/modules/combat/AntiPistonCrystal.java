package dev.ov4client.addon.modules.combat;
/*
import dev.ov4client.addon.Le;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.enums.RotationType;
import dev.ov4client.addon.enums.SwingHand;
import dev.ov4client.addon.managers.Managers;
import dev.ov4client.addon.utils.SettingUtils;
import dev.ov4client.addon.utils.world.BlockInfo;
import meteordevelopment.meteorclient.events.entity.player.BlockBreakingCooldownEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class AntiPistonCrystal extends ov4Module {
    public AntiPistonCrystal() {
        super(Le.Combat, "Anti Piston Crystal", "Automatically break piston block and place block on it.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgBreak = settings.createGroup("Break Crystal");
    private final SettingGroup sgRender = settings.createGroup("Render");

    //--------------------General--------------------//
    private final Setting<Boolean> pauseOnEat = sgGeneral.add(new BoolSetting.Builder()
        .name("Pause On Eat")
        .description("Pause while eating.")
        .defaultValue(true)
        .build()
    );
    private final Setting<List<Block>> pistons = sgGeneral.add(new BlockListSetting.Builder()
        .name("Pistons")
        .description("What piston to mine.")
        .defaultValue(Blocks.PISTON, Blocks.STICKY_PISTON)
        .filter(this::mineBlockList)
        .build()
    );
    private final Setting<List<Block>> fillBlock = sgGeneral.add(new BlockListSetting.Builder()
        .name("Fill Block")
        .description("What block to fill piston.")
        .defaultValue(Blocks.OBSIDIAN)
        .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay in ticks between breaking blocks.")
        .defaultValue(0)
        .build()
    );
    private final Setting<Integer> maxBlocksPerTick = sgGeneral.add(new IntSetting.Builder()
        .name("max-blocks-per-tick")
        .description("Maximum blocks to try to break per tick. Useful when insta mining.")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 6)
        .build()
    );
    private final Setting<SortMode> sortMode = sgGeneral.add(new EnumSetting.Builder<SortMode>()
        .name("sort-mode")
        .description("The blocks you want to mine first.")
        .defaultValue(SortMode.Closest)
        .build()
    );
    private final Setting<MineMode> mineMode = sgGeneral.add(new EnumSetting.Builder<MineMode>()
        .name("Mine Mode")
        .description("Which mine mod should be used.")
        .defaultValue(MineMode.Normal)
        .build()
    );

    //--------------------Break Crystal--------------------//
    private final Setting<Boolean> breakCrystal = sgBreak.add(new BoolSetting.Builder()
        .name("Break Crystal")
        .description("Automatically break crystals to help you place burrow block.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Double> breakDelay = sgBreak.add(new DoubleSetting.Builder()
        .name("Break Delay")
        .description("Break crystals delay.")
        .defaultValue(1)
        .sliderRange(0, 10)
        .visible(breakCrystal::get)
        .build()
    );
    public final Setting<Double> safeHealth = sgBreak.add(new DoubleSetting.Builder()
        .name("Safe Health")
        .defaultValue(16)
        .sliderRange(0, 36)
        .visible(breakCrystal::get)
        .build()
    );

    //--------------------Render--------------------//
    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder()
        .name("Swing")
        .description("Renders swing animation when placing a block.")
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
    private final Setting<Boolean> enableRenderBounding = sgRender.add(new BoolSetting.Builder()
        .name("bounding-box")
        .description("Enable rendering bounding box for Cube and Uniform Cube.")
        .defaultValue(true)
        .build()
    );

    private final Setting<meteordevelopment.meteorclient.renderer.ShapeMode> shapeModeBox = sgRender.add(new EnumSetting.Builder<meteordevelopment.meteorclient.renderer.ShapeMode>()
        .name("nuke-box-mode")
        .description("How the shape for the bounding box is rendered.")
        .defaultValue(meteordevelopment.meteorclient.renderer.ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColorBox = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the bounding box.")
        .defaultValue(new SettingColor(16,106,144, 100))
        .build()
    );

    private final Setting<SettingColor> lineColorBox = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the bounding box.")
        .defaultValue(new SettingColor(16,106,144, 255))
        .build()
    );

    private final Setting<Boolean> enableRenderBreaking = sgRender.add(new BoolSetting.Builder()
        .name("broken-blocks")
        .description("Enable rendering bounding box for Cube and Uniform Cube.")
        .defaultValue(false)
        .build()
    );

    private final Setting<meteordevelopment.meteorclient.renderer.ShapeMode> shapeModeBreak = sgRender.add(new EnumSetting.Builder<meteordevelopment.meteorclient.renderer.ShapeMode>()
        .name("Nuke Pistons Mode")
        .description("How the shapes for broken blocks are rendered.")
        .defaultValue(meteordevelopment.meteorclient.renderer.ShapeMode.Both)
        .visible(enableRenderBreaking::get)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 80))
        .visible(enableRenderBreaking::get)
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 255))
        .visible(enableRenderBreaking::get)
        .build()
    );
    private final Setting<ShapeMode> shape = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("Shape Mode")
        .description("The shape of nuking algorithm.")
        .defaultValue(ShapeMode.Sphere)
        .build()
    );
    private final Setting<Double> range = sgRender.add(new DoubleSetting.Builder()
        .name("Range")
        .description("The break range.")
        .defaultValue(4)
        .min(0)
        .visible(() -> shape.get() != ShapeMode.Cube)
        .build()
    );
    private final Setting<Integer> rangeUp = sgRender.add(new IntSetting.Builder()
        .name("Up")
        .description("The break range.")
        .defaultValue(1)
        .min(0)
        .visible(() -> shape.get() == ShapeMode.Cube)
        .build()
    );
    private final Setting<Integer> rangeDown = sgRender.add(new IntSetting.Builder()
        .name("Down")
        .description("The break range.")
        .defaultValue(1)
        .min(0)
        .visible(() -> shape.get() == ShapeMode.Cube)
        .build()
    );
    private final Setting<Integer> rangeLeft = sgRender.add(new IntSetting.Builder()
        .name("Left")
        .description("The break range.")
        .defaultValue(1)
        .min(0)
        .visible(() -> shape.get() == ShapeMode.Cube)
        .build()
    );
    private final Setting<Integer> rangeRight = sgRender.add(new IntSetting.Builder()
        .name("Right")
        .description("The break range.")
        .defaultValue(1)
        .min(0)
        .visible(() -> shape.get() == ShapeMode.Cube)
        .build()
    );
    private final Setting<Integer> range_forward = sgRender.add(new IntSetting.Builder()
        .name("forward")
        .description("The break range.")
        .defaultValue(1)
        .min(0)
        .visible(() -> shape.get() == ShapeMode.Cube)
        .build()
    );
    private final Setting<Integer> range_back = sgRender.add(new IntSetting.Builder()
        .name("back")
        .description("The break range.")
        .defaultValue(1)
        .min(0)
        .visible(() -> shape.get() == ShapeMode.Cube)
        .build()
    );

    private final Pool<BlockPos.Mutable> blockPosPool = new Pool<>(BlockPos.Mutable::new);
    private final List<BlockPos.Mutable> blocks = new ArrayList<>();

    private boolean firstBlock;
    private final BlockPos.Mutable lastBlockPos = new BlockPos.Mutable();

    private int timer;
    private int noBlockTimer;

    private BlockPos.Mutable pos1 = new BlockPos.Mutable();
    private BlockPos.Mutable pos2 = new BlockPos.Mutable();
    private Box box;
    int maxh = 0;
    int maxv = 0;

    public enum MineMode {
        Normal,
        AutoMine
    }

    public enum SortMode {
        None,
        Closest,
        Furthest,
        TopDown
    }

    public enum ShapeMode {
        Cube,
        UniformCube,
        Sphere
    }

    private boolean mineBlockList(Block block) {
        return block == Blocks.PISTON
            || block == Blocks.STICKY_PISTON
            || block == Blocks.PISTON_HEAD
            || block == Blocks.MOVING_PISTON
            || block == Blocks.REDSTONE_BLOCK
            || block == Blocks.REDSTONE_TORCH
            || block == Blocks.ACACIA_BUTTON
            || block == Blocks.BAMBOO_BUTTON
            || block == Blocks.BIRCH_BUTTON
            || block == Blocks.CHERRY_BUTTON
            || block == Blocks.CRIMSON_BUTTON
            || block == Blocks.DARK_OAK_BUTTON
            || block == Blocks.JUNGLE_BUTTON
            || block == Blocks.MANGROVE_BUTTON
            || block == Blocks.OAK_BUTTON
            || block == Blocks.STONE_BUTTON
            || block == Blocks.WARPED_BUTTON
            || block == Blocks.SPRUCE_BUTTON
            || block == Blocks.POLISHED_BLACKSTONE_BUTTON;
    }

    @Override
    public void onActivate() {
        firstBlock = true;
        timer = 0;
        noBlockTimer = 0;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (enableRenderBounding.get()){
            if (shape.get() != ShapeMode.Sphere) {
                box = new Box(pos1, pos2);
                event.renderer.box(box, sideColorBox.get(), lineColorBox.get(), shapeModeBox.get(), 0);
            }
        }
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        if (mc.player.isUsingItem() && pauseOnEat.get()) return;

        if (timer > 0) {
            timer--;
            return;
        }

        double pX = mc.player.getX();
        double pY = mc.player.getY();
        double pZ = mc.player.getZ();

        double rangeSq = Math.pow(range.get(), 2);

        if (shape.get() == ShapeMode.UniformCube) range.set((double) Math.round(range.get()));

        double pX_ = pX;
        double pZ_ = pZ;
        int r = (int) Math.round(range.get());

        if (shape.get() == ShapeMode.UniformCube) {
            pX_ += 1; // weired position stuff
            pos1.set(pX_ - r, pY - r + 1, pZ - r+1); // down
            pos2.set(pX_ + r-1, pY + r, pZ + r); // up
        } else {
            int direction = Math.round((mc.player.getRotationClient().y % 360) / 90);
            direction = Math.floorMod(direction, 4);

            pos1.set(pX_ - (range_forward.get()), Math.ceil(pY) - rangeDown.get(), pZ_ - rangeRight.get()); // down
            pos2.set(pX_ + range_back.get()+1, Math.ceil(pY + rangeUp.get() + 1), pZ_ + rangeLeft.get()+1); // up

            if (direction == 2) {
                pX_ += 1;
                pZ_ += 1;
                pos1.set(pX_ - (rangeLeft.get()+1), Math.ceil(pY) - rangeDown.get(), pZ_ - (range_forward.get()+1)); // down
                pos2.set(pX_ + rangeRight.get(), Math.ceil(pY + rangeUp.get() + 1), pZ_ + range_back.get()); // up
            } else if (direction == 3) {
                pX_ += 1;
                pos1.set(pX_ - (range_back.get()+1), Math.ceil(pY) - rangeDown.get(), pZ_ - rangeLeft.get()); // down
                pos2.set(pX_ + range_forward.get(), Math.ceil(pY + rangeUp.get() + 1), pZ_ + rangeRight.get()+1); // up
            } else if (direction == 0) {
                pZ_ += 1;
                pX_ += 1;
                pos1.set(pX_ - (rangeRight.get()+1), Math.ceil(pY) - rangeDown.get(), pZ_ - (range_back.get()+1)); // down
                pos2.set(pX_ + rangeLeft.get(), Math.ceil(pY + rangeUp.get() + 1), pZ_ + range_forward.get()); // up
            }

            maxh = 1 + Math.max(Math.max(Math.max(range_back.get(),rangeRight.get()),range_forward.get()), rangeLeft.get());
            maxv = 1 + Math.max(rangeUp.get(), rangeDown.get());
        }

        pos1.setY((int) Math.floor(pY));

        box = new Box(pos1, pos2);

        BlockIterator.register(Math.max((int) Math.ceil(range.get()+1), maxh), Math.max((int) Math.ceil(range.get()), maxv), (blockPos, blockState) -> {
            boolean toofarSphere = Utils.squaredDistance(pX, pY, pZ, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) > rangeSq;
            boolean toofarUniformCube = maxDist(Math.floor(pX), Math.floor(pY), Math.floor(pZ), blockPos.getX(), blockPos.getY(), blockPos.getZ()) >= range.get();
            boolean toofarCube = !box.contains(Vec3d.ofCenter(blockPos));

            if (!BlockUtils.canBreak(blockPos, blockState) || (toofarSphere && shape.get() == ShapeMode.Sphere) || (toofarUniformCube && shape.get() == ShapeMode.UniformCube) || (toofarCube && shape.get() == ShapeMode.Cube)) return;

            if (blockPos.getY() < Math.floor(mc.player.getY())) return;

            if (!pistons.get().contains(blockState.getBlock())) return;

            blocks.add(blockPosPool.get().set(blockPos));
        });

        BlockIterator.after(() -> {
            if (sortMode.get() == SortMode.TopDown)
                blocks.sort(Comparator.comparingDouble(value -> -1*value.getY()));
            else if (sortMode.get() != SortMode.None)
                blocks.sort(Comparator.comparingDouble(value -> Utils.squaredDistance(pX, pY, pZ, value.getX() + 0.5, value.getY() + 0.5, value.getZ() + 0.5) * (sortMode.get() == SortMode.Closest ? 1 : -1)));

            if (blocks.isEmpty()) {
                if (noBlockTimer++ >= delay.get()) firstBlock = true;
                return;
            }
            else {
                noBlockTimer = 0;
            }

            // Update timer
            if (!firstBlock && !lastBlockPos.equals(blocks.get(0))) {
                timer = delay.get();

                firstBlock = false;
                lastBlockPos.set(blocks.get(0));

                if (timer > 0) return;
            }

            // Break
            int count = 0;

            for (BlockPos block : blocks) {
                if (count >= maxBlocksPerTick.get()) break;

                boolean canInstaMine = BlockUtils.canInstaBreak(block);
                Direction mineDir = SettingUtils.getPlaceOnDirection(block);

                switch (mineMode.get()) {
                    case Normal -> {
                        if (mineDir != null) {
                            int oldSlot = mc.player.getInventory().selectedSlot;
                            int pickaxeSlot = InvUtils.findInHotbar(Items.NETHERITE_PICKAXE).slot();
                            InvUtils.swap(pickaxeSlot, false);

                            if (pickaxeSlot == -1) {
                                sendToggledMsg("No pickaxe found!");
                                toggle();
                                return;
                            }

                            sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, block, mineDir));
                            sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, block, mineDir));
                            mc.player.getInventory().selectedSlot = oldSlot;
                        }
                    }
                    case AutoMine -> {
                        AutoMine autoMine = Modules.get().get(AutoMine.class);
                        if (autoMine.isActive()) {
                            if (block.equals(autoMine.targetPos())) return;
                            autoMine.onStart(block);
                        } else {
                            info("please toggle 'AutoMine' ON!");
                        }
                    }
                }

                if (enableRenderBreaking.get()) RenderUtils.renderTickingBlock(block.toImmutable(), sideColor.get(), lineColor.get(), shapeModeBreak.get(), 0, 8, true, false);
                lastBlockPos.set(block);

                count++;
                if (!canInstaMine) break;
            }

            firstBlock = false;

            for (BlockPos.Mutable blockPos : blocks) blockPosPool.free(blockPos);
            blocks.clear();
        });

        if (BlockInfo.canPlace(lastBlockPos, breakCrystal.get(), safeHealth.get())) {
            Predicate<ItemStack> fillBlockPredicate = itemStack -> {
                if (!(itemStack.getItem() instanceof BlockItem block)) return false;
                return fillBlock.get().contains(block.getBlock());
            };

            Direction dir = SettingUtils.getPlaceOnDirection(lastBlockPos);
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) Managers.ROTATION.start(lastBlockPos, priority, RotationType.BlockPlace, Objects.hash(name + "placing"));

            InvUtils.swap(InvUtils.findInHotbar(fillBlockPredicate).slot(), true);
            placeBlockAndAttackCrystal(lastBlockPos, dir, pauseOnEat.get(), swing.get(), placeHand.get(), breakCrystal.get(), breakDelay.get().longValue(), safeHealth.get());
            InvUtils.swapBack();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreakingCooldown(BlockBreakingCooldownEvent event) {
        event.cooldown = 0;
    }

    public static double maxDist(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dX = Math.ceil(Math.abs(x2 - x1));
        double dY = Math.ceil(Math.abs(y2 - y1));
        double dZ = Math.ceil(Math.abs(z2 - z1));
        return Math.max(Math.max(dX, dY), dZ);
    }
}
*/
