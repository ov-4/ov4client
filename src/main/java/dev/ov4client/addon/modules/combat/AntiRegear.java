package dev.ov4client.addon.modules.combat;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.enums.RotationType;
import dev.ov4client.addon.managers.Managers;
import dev.ov4client.addon.utils.SettingUtils;
import dev.ov4client.addon.utils.network.PacketUtils;
import dev.ov4client.addon.utils.others.Task;
import dev.ov4client.addon.utils.player.Interaction;
import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import static dev.ov4client.addon.utils.world.BlockInfo.*;

public class AntiRegear extends ov4Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    /*----General----*/
    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder().name("radius").description("The radius of the sphere around you.").defaultValue(5).sliderRange(1, 10).build());
    private final Setting<Boolean> own = sgGeneral.add(new BoolSetting.Builder().name("own").description("Whether or not to break your own blocks.").defaultValue(false).build());

    /*----Render----*/
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("Render")
        .description(ov4client.COLOR)
        .defaultValue(true)
        .build()
    );
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("Shape Mode")
        .description("Which parts should be renderer.")
        .defaultValue(ShapeMode.Both)
        .build()
    );
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("Side Color")
        .description(ov4client.COLOR)
        .defaultValue(new SettingColor(123, 123, 123, 160))
        .build()
    );
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("Line Color")
        .description(ov4client.COLOR)
        .defaultValue(new SettingColor(123, 123, 123, 160))
        .build()
    );

    public AntiRegear() {
        super(ov4client.Combat, "Anti Regear", "Automatically breaks shulkers and EChests which was placed by enemy.");
    }

    private final ArrayList<BlockPos> ownBlocks = new ArrayList<>();
    private FindItemResult tool;
    private BlockPos currentPos;
    private BlockState currentState;
    private int timer;

    private final Task mine = new Task();
    private final PacketUtils packetMine = new PacketUtils();

    @Override
    public void onActivate() {
        timer = 0;
        currentPos = null;
        currentState = null;
        ownBlocks.clear();

        mine.reset();
    }

    @EventHandler
    public void onPlace(PlaceBlockEvent event) {
        if (event.block instanceof ShulkerBoxBlock || event.block instanceof EnderChestBlock) {
            ownBlocks.add(event.blockPos);
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (getBlocks(radius.get()).isEmpty()) return;

        if (currentPos != null) {
            tool = InvUtils.findFastestTool(currentState);
            if (!tool.found()) return;

            if (SettingUtils.shouldRotate(RotationType.Mining)) Managers.ROTATION.start(currentPos, priority, RotationType.Mining, Objects.hash(name + "mining"));
            packetMine.mine(currentPos, mine);
            mc.world.setBlockBreakingInfo(mc.player.getId(), currentPos, (int) (packetMine.getProgress() * 10.0F) - 1);

            if (packetMine.isReadyOn(0.95)) Interaction.updateSlot(tool.slot(), false);

            boolean shouldStop = PlayerUtils.distanceTo(currentPos) > 5 || isBugged();
            if (isAir(currentPos) || shouldStop) {
                if (shouldStop) packetMine.abortMining(currentPos);
                currentPos = null;
                currentState = null;
                packetMine.reset();
                mine.reset();
            }
        } else {
            getBlocks(radius.get()).forEach(blockPos -> {
                currentPos = blockPos;
                currentState = mc.world.getBlockState(currentPos);
            });
        }
    }

    private ArrayList<BlockPos> getBlocks(int radius) {
        ArrayList<BlockPos> sphere = new ArrayList<>(getSphere(mc.player.getBlockPos(), radius, radius));
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for (BlockPos blockPos : sphere) {
            if (isAir(blockPos)) continue;
            if (!own.get() && ownBlocks.contains(blockPos)) continue;

            if (!blocks.contains(blockPos) && mc.world.getBlockState(blockPos).getBlock() instanceof ShulkerBoxBlock || mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST) {
                blocks.add(blockPos);
            }
        }

        blocks.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        return blocks;
    }

    private boolean isBugged() {
        if (!packetMine.isReady()) return false;
        timer++;

        if (timer >= 10) {
            timer = 0;
            return true;
        }

        return false;
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (render.get()) {
            if (getBlocks(5).isEmpty()) return;

            getBlocks(5).forEach(blockPos -> event.renderer.box(blockPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0));
        }
    }
}
