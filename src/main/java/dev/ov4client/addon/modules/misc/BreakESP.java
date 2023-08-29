package dev.ov4client.addon.modules.misc;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.modules.combat.AutoCrystal;
import dev.ov4client.addon.modules.combat.AutoCrystalPlus;
import dev.ov4client.addon.utils.ov4Utils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BreakESP extends ov4Module {
    public BreakESP() {
        super(ov4client.Misc, "Break ESP", "Show the destruction progress of the box.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    /*----General----*/
    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("Mode")
        .description("Render mode.")
        .defaultValue(Mode.Both)
        .build()
    );
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("Scale")
        .defaultValue(1)
        .sliderRange(0.1, 2)
        .build()
    );
    private final Setting<Double> maxTime = sgGeneral.add(new DoubleSetting.Builder()
        .name("Max Time")
        .description("Removes rendered box after this time.")
        .defaultValue(10)
        .min(0)
        .sliderRange(0, 50)
        .visible(() -> mode.get() == Mode.Box)
        .build()
    );
    /*----Render----*/
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
    private final Setting<Boolean> renderProgess = sgRender.add(new BoolSetting.Builder()
        .name("Render Progess")
        .defaultValue(true)
        .build()
    );
    private final Setting<SettingColor> progressColor = sgRender.add(new ColorSetting.Builder()
        .name("Progress Text Color")
        .description(ov4client.COLOR)
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(renderProgess::get)
        .build()
    );
    private final Setting<Boolean> renderName = sgRender.add(new BoolSetting.Builder()
        .name("Render Name")
        .defaultValue(true)
        .build()
    );
    private final Setting<SettingColor> nameColor = sgRender.add(new ColorSetting.Builder()
        .name("Name Text Color")
        .description(ov4client.COLOR)
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(renderName::get)
        .build()
    );

    Map<Integer, BlockBreakingInfo> blocks = ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos();

    private final List<BreakESP.Render> renders = new ArrayList<>();
    BreakESP.Render render = null;

    @EventHandler
    private void onRender(Render3DEvent event) {
        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
//            int stage = info.getStage();

            BlockState state = mc.world.getBlockState(pos);
            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape.isEmpty()) return;
//            double shrinkFactor = (9 - (stage + 1)) / 9d;
//            double progress = 1d - shrinkFactor;

            if (!mode.get().equals(Mode.Text)) {
/*                double max = ((double) Math.round(progress * 100) / 100);
                double min = 1 - max;

                Vec3d vec3d = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                Box box = new Box(vec3d.x + min, vec3d.y + min, vec3d.z + min, vec3d.x + max, vec3d.y + max, vec3d.z + max);

                event.renderer.box(box,sideColor.get(),lineColor.get(),shapeMode.get(),0);*/
                if (mc.player == null || mc.world == null) return;

                if (render != null && contains()) render = null;

                renders.removeIf(r -> System.currentTimeMillis() > r.time + Math.round(maxTime.get() * 1000) || (render != null && r.id == render.id) || !ov4Utils.solid2(r.pos));

                if (render != null) {
                    renders.add(render);
                    render = null;
                }

                renders.forEach(r -> {
                    double delta = Math.min((System.currentTimeMillis() - r.time) / (maxTime.get() * 1000d), 1);
                    event.renderer.box(getBox(r.pos, getProgress(Math.min(delta * 4, 1))), getColor(sideColor.get(), 1 - delta), getColor(lineColor.get(), 1 - delta), shapeMode.get(), 0);
                });
            }
        });
    }

    @EventHandler
    private void onReceive(PacketEvent.Receive event) {
        if (event.packet instanceof BlockBreakingProgressS2CPacket packet) {
            render = new BreakESP.Render(packet.getPos(), packet.getEntityId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
            int stage = info.getStage();

            boolean compatibility = hasRenderPos(pos);
            Entity entity = mc.world.getEntityById(info.getActorId());
            PlayerEntity player = entity == null ? null : (PlayerEntity) entity;

            double shrinkFactor = (9 - (stage + 1)) / 9d;
            double progress = 1d - shrinkFactor;

            if (!mode.get().equals(Mode.Box) && player != null) {
                Vec3d rPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5);
                Vector3d p1 = new Vector3d(rPos.x, compatibility ? rPos.y - 0.30 : ((rPos.y - (scale.get() / 9.9)) - scale.get() / 3.333), rPos.z);
                if (!NametagUtils.to2D(p1, scale.get(), true)) {
                    return;
                }

                NametagUtils.begin(p1);
                TextRenderer font = TextRenderer.get();
                font.begin(scale.get());
                String text = ((double) Math.round(progress * 100) / 100) + "%";
                String name = player.getGameProfile().getName();
                if (renderProgess.get()) font.render(text, -(font.getWidth(text) / 2), -(font.getHeight()), progressColor.get(), false);
                if (renderName.get()) font.render(name, -(font.getWidth(name) / 2), -(font.getHeight() / 2) + 2 + font.getHeight(), nameColor.get(), false);
                font.end();
                NametagUtils.end();
            }
        });
    }

    private boolean isAutoCrystalEnable() {
        boolean clientCombat = isEnable(AutoCrystal.class) || isEnable(AutoCrystalPlus.class);
        boolean meteorCombat = isEnable(CrystalAura.class);
        return clientCombat || meteorCombat;
    }

    private boolean hasRenderPos(BlockPos pos) {
        if (isAutoCrystalEnable() && getEnable() != null) {
            Module module = getEnable();
            if (module instanceof AutoCrystal c) {
                return c.renderPos.equals(pos);
            }
        }

        return false;
    }

    private Module getEnable() {
        if (isEnable(AutoCrystal.class)) return Modules.get().get(AutoCrystal.class);
        if (isEnable(AutoCrystalPlus.class)) return Modules.get().get(AutoCrystalPlus.class);
        if (isEnable(CrystalAura.class)) return Modules.get().get(CrystalAura.class);

        return null;
    }

    private boolean isEnable(Class<? extends Module> moduleClass) {
        return Modules.get().isActive(moduleClass);
    }

    public enum Mode {
        Text,
        Box,
        Both
    }

    private boolean contains() {
        for (Render r : renders) {
            if (r.id == render.id && r.pos.equals(render.pos)) return true;
        }
        return false;
    }

    private Color getColor(Color color, double delta) {
        return new Color(color.r, color.g, color.b, (int) Math.floor(color.a * delta));
    }

    private double getProgress(double delta) {
        return 1 - Math.pow(1 - (delta), 5);
    }

    private Box getBox(BlockPos pos, double progress) {
        return new Box(pos.getX() + 0.5 - progress / 2, pos.getY() + 0.5 - progress / 2,pos.getZ() + 0.5 - progress / 2, pos.getX() + 0.5 + progress / 2, pos.getY() + 0.5 + progress / 2, pos.getZ() + 0.5 + progress / 2);
    }

    private record Render(BlockPos pos, int id, long time) {}
}
