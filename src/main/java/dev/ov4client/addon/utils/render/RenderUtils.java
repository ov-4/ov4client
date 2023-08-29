package dev.ov4client.addon.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static dev.ov4client.addon.utils.world.BlockInfo.*;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RenderUtils {
    public static boolean visibleHeight(RenderMode renderMode) {
        return renderMode == RenderMode.UpperSide || renderMode == RenderMode.LowerSide;
    }

    public static boolean visibleSide(ShapeMode shapeMode) {
        return shapeMode == ShapeMode.Both || shapeMode == ShapeMode.Sides;
    }

    public static boolean visibleLine(ShapeMode shapeMode) {
        return shapeMode == ShapeMode.Both || shapeMode == ShapeMode.Lines;
    }

    public static void render(RenderInfo ri, BlockPos blockPos, Color sideColor, Color lineColor, double height) {
        if (isNull(blockPos)) return;

        switch (ri.renderMode) {
            case Box -> box(ri, blockPos, sideColor, lineColor);
            case UpperSide -> side(ri, blockPos, sideColor, lineColor, Side.Upper, height);
            case LowerSide -> side(ri, blockPos, sideColor, lineColor, Side.Lower, height);
            case Shape -> shape(ri, blockPos, sideColor, lineColor);
            case Romb -> romb(ri, blockPos, sideColor, lineColor, Side.Default, height);
            case UpperRomb -> romb(ri, blockPos, sideColor, lineColor, Side.Upper, height);
        }
    }

    private static void romb(RenderInfo ri, BlockPos blockPos, Color sideColor, Color lineColor, Side side, double height) {
        switch (side) {
            case Default -> {
                // North
                render(ri, blockPos, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.5, 0, 0, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.5, 0, 0, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0, 0.5, 0, 0, 0.5, 0, 0, sideColor, lineColor, ri.shapeMode);

                // South
                render(ri, blockPos, 0.0, 0.0, 1.0, 0.0, 0.5, 0.0, 0.5, 0, 0, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 0.0, 1.0, 0.5, 0.5, 0.0, 0.5, 0, 0, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, 0.5, 1.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 0.5, 1.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0, 0.5, 0, 0, 0.5, 0, 0, sideColor, lineColor, ri.shapeMode);

                // East
                render(ri, blockPos, 1.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 1.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 1.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0, 0, 0.5, 0, 0.5, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 1.0, -0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 1, 0.5, 0, 0.5, 0.5, 0, 0.5, 0, sideColor, lineColor, ri.shapeMode);

                // West
                render(ri, blockPos, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0, 0, 0.5, 0, 0.5, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, -0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 1, 0.5, 0, 0.5, 0.5, 0, 0.5, 0, sideColor, lineColor, ri.shapeMode);

                // Up
                render(ri, blockPos, 0.0, 1, 0.0, 0.5, 0.0, 0.0, 0.0, 0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 1, 0.0, 0.5, 0.0, 0.0, 0.5, 0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 1, 0.5, 0.5, 0.0, 0.0, 0.5, 0, 0.5, 0, 0, 0.5, 0.5, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, 1, 0.5, 0.5, 0.0, 0.5, 0.0, 0, 0.5, 0, 0, 0.5, 0.0, 0, 0, sideColor, lineColor, ri.shapeMode);

                // Down
                render(ri, blockPos, 0.0, 0, 0.0, 0.5, 0.0, 0.0, 0.0, 0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 0, 0.0, 0.5, 0.0, 0.0, 0.5, 0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 0, 0.5, 0.5, 0.0, 0.0, 0.5, 0, 0.5, 0, 0, 0.5, 0.5, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, 0, 0.5, 0.5, 0.0, 0.5, 0.0, 0, 0.5, 0, 0, 0.5, 0.0, 0, 0, sideColor, lineColor, ri.shapeMode);
            }
            case Upper -> {
                // Up
                render(ri, blockPos, 0.0, 1, 0.0, 0.5, 0.0, 0.0, 0.0, 0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 1, 0.0, 0.5, 0.0, 0.0, 0.5, 0, 0.5, 0, 0, 0, 0, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.5, 1, 0.5, 0.5, 0.0, 0.0, 0.5, 0, 0.5, 0, 0, 0.5, 0.5, 0, 0, sideColor, lineColor, ri.shapeMode);
                render(ri, blockPos, 0.0, 1, 0.5, 0.5, 0.0, 0.5, 0.0, 0, 0.5, 0, 0, 0.5, 0.0, 0, 0, sideColor, lineColor, ri.shapeMode);
            }
        }
    }

    private static void render(RenderInfo ri, BlockPos blockPos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color sideColor, Color lineColor, ShapeMode shapeMode) {
        Vec3d vec3d = new Vec3d(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);

        ri.event.renderer.side(vec3d.x + x1, vec3d.y + y1, vec3d.z + z1, vec3d.x + x2, vec3d.y + y2, vec3d.z + z2, vec3d.x + x3, vec3d.y + y3, vec3d.z + z3, vec3d.x + x4, vec3d.y + y4, vec3d.z + z4, sideColor, lineColor, shapeMode);
    }

    private static void line(RenderInfo ri, BlockPos blockPos, double x, double y, double z, double x1, double y1, double z1, Color lineColor) {
        Vec3d vec3d = new Vec3d(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);

        ri.event.renderer.line(vec3d.x + x, vec3d.y + y, vec3d.z + z,x1,y1,z1, lineColor);
    }

    private static void shape(RenderInfo ri, BlockPos blockPos, Color sideColor, Color lineColor) {
        if (getShape(blockPos).isEmpty()) return;

        render(ri, blockPos, getBox(blockPos), sideColor, lineColor);
    }

    private static void box(RenderInfo ri, BlockPos blockPos, Color sideColor, Color lineColor) {
        ri.event.renderer.box(blockPos, sideColor, lineColor, ri.shapeMode, 0);
    }

    private static void side(RenderInfo ri, BlockPos blockPos, Color sideColor, Color lineColor, Side side, double height) {
        double y = side == Side.Upper ? blockPos.getY() + 1 : blockPos.getY();
        ri.event.renderer.box(
                blockPos.getX(), blockPos.getY() + height, blockPos.getZ(),
                blockPos.getX() + 1, y, blockPos.getZ() + 1,
                sideColor, lineColor, ri.shapeMode, 0);
    }

    private static void render(RenderInfo ri, BlockPos blockPos, Box box, Color sideColor, Color lineColor) {
        ri.event.renderer.box(blockPos.getX() + box.minX, blockPos.getY() + box.minY, blockPos.getZ() + box.minZ, blockPos.getX() + box.maxX, blockPos.getY() + box.maxY, blockPos.getZ() + box.maxZ, sideColor, lineColor, ri.shapeMode, 0);
    }

    public static void thickRender(Render3DEvent event, BlockPos pos, ShapeMode mode, Color sideColor, Color sideColor2, Color lineColor, Color lineColor2, double lineSize) {
        double low = lineSize;
        double high = 1 - low;

        if (mode == ShapeMode.Lines || mode == ShapeMode.Both) {
            // Sides
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ() + low, lineColor, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + low, pos.getY() + 1, pos.getZ(), lineColor, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + low, lineColor, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + high, pos.getY() + 1, pos.getZ(), lineColor, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX(), pos.getY() + 1, pos.getZ() + high, lineColor, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX() + low, pos.getY() + 1, pos.getZ() + 1, lineColor, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + high, lineColor, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY(), pos.getZ() + 1, pos.getX() + high, pos.getY() + 1, pos.getZ() + 1, lineColor, lineColor2);

            // Up
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + high, pos.getZ(), lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getZ() + low, lineColor);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX(), pos.getY() + high, pos.getZ() + 1, lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + low, pos.getZ() + 1, lineColor);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY() + 1, pos.getZ() + 1, pos.getX() + 1, pos.getY() + high, pos.getZ() + 1, lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ() + 1, pos.getX() + 1, pos.getZ() + high, lineColor);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + high, pos.getZ() + 1, lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX() + 1, pos.getY() + 1, pos.getZ(), pos.getX() + high, pos.getZ() + 1, lineColor);

            // Down
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + low, pos.getZ(), lineColor2, lineColor2);
            event.renderer.quadHorizontal(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getZ() + low, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + low, pos.getZ() + 1, lineColor2, lineColor2);
            event.renderer.quadHorizontal(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + low, pos.getZ() + 1, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getY() + low, pos.getZ() + 1, lineColor2, lineColor2);
            event.renderer.quadHorizontal(pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getZ() + high, lineColor2);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + low, pos.getZ() + 1, lineColor2, lineColor2);
            event.renderer.quadHorizontal(pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + high, pos.getZ() + 1, lineColor2);
        }

        if (mode == ShapeMode.Sides || mode == ShapeMode.Both) {
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ(), sideColor, sideColor2);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ() + 1, sideColor, sideColor2);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getY() + 1, pos.getZ(), sideColor, sideColor2);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY(), pos.getZ() + 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1, sideColor, sideColor2);
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getZ() + 1, sideColor);
            event.renderer.quadHorizontal(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getZ() + 1, sideColor2);
        }
    }

    public static void thickUpperSide(Render3DEvent event, BlockPos pos, ShapeMode mode, Color sideColor, Color lineColor, double lineSize) {
        double low = lineSize;
        double high = 1 - low;

        if (mode == ShapeMode.Lines || mode == ShapeMode.Both) {
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + high, pos.getZ(), lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getZ() + low, lineColor);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX(), pos.getY() + high, pos.getZ() + 1, lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + low, pos.getZ() + 1, lineColor);
            event.renderer.gradientQuadVertical(pos.getX(), pos.getY() + 1, pos.getZ() + 1, pos.getX() + 1, pos.getY() + high, pos.getZ() + 1, lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ() + 1, pos.getX() + 1, pos.getZ() + high, lineColor);
            event.renderer.gradientQuadVertical(pos.getX() + 1, pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + high, pos.getZ() + 1, lineColor, lineColor);
            event.renderer.quadHorizontal(pos.getX() + 1, pos.getY() + 1, pos.getZ(), pos.getX() + high, pos.getZ() + 1, lineColor);
        }

        if (mode == ShapeMode.Sides || mode == ShapeMode.Both) {
            event.renderer.quadHorizontal(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getZ() + 1, sideColor);
        }
    }

    public enum Render {
        Meteor, ov4client, None
    }

    public enum RenderMode {
        Box, Smooth, UpperSide, LowerSide, Shape, Romb, UpperRomb, None
    }

    public enum Side {
        Default, Upper, Lower
    }

    private static final VertexConsumerProvider.Immediate vertex = VertexConsumerProvider.immediate(new BufferBuilder(2048));

    public static void rounded(MatrixStack stack, float x, float y, float w, float h, float radius, int p, int color) {

        Matrix4f matrix4f = stack.peek().getPositionMatrix();

        float a = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        float r = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        float g = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        float b = (float) ColorHelper.Argb.getBlue(color) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        corner(x + w, y, radius, 360, p, r, g, b, a, bufferBuilder, matrix4f);
        corner(x, y, radius, 270, p, r, g, b, a, bufferBuilder, matrix4f);
        corner(x, y + h, radius, 180, p, r, g, b, a, bufferBuilder, matrix4f);
        corner(x + w, y + h, radius, 90, p, r, g, b, a, bufferBuilder, matrix4f);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void corner(float x, float y, float radius, int angle, float p, float r, float g, float b, float a, BufferBuilder bufferBuilder, Matrix4f matrix4f) {
        for (float i = angle; i > angle - 90; i -= 90 / p) {
            bufferBuilder.vertex(matrix4f, (float) (x + Math.cos(Math.toRadians(i)) * radius), (float) (y + Math.sin(Math.toRadians(i)) * radius), 0).color(r, g, b, a).next();
        }
    }

    public static void text(String text, MatrixStack stack, float x, float y, int color) {
        mc.textRenderer.draw(text, x, y, color, false, stack.peek().getPositionMatrix(), vertex, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        vertex.draw();
    }

    public static void quad(MatrixStack stack, float x, float y, float w, float h, int color) {
        Matrix4f matrix4f = stack.peek().getPositionMatrix();

        float a = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        float r = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        float g = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        float b = (float) ColorHelper.Argb.getBlue(color) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix4f, x + w, y, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix4f, x, y, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix4f, x, y + h, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix4f, x + w, y + h, 0).color(r, g, b, a).next();


        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static double easeInOutQuad(double x) {
        double percent;
        if (x < 0.5D) {
            percent = (double)2 * x * x;
        } else {
            percent = 1;
            double ii = (double)-2 * x + (double)2;
            byte i = 2;
            percent -= Math.pow(ii, i) / (double)2;
        }

        return percent;
    }

    public static void drawSigma(Renderer3D renderer, MatrixStack matrices, Entity entity, Color lineColor) {
        int everyTime = 3000;

        int drawTime = (int) (System.currentTimeMillis() % everyTime);
        boolean drawMode = drawTime > (everyTime/2);
        double drawPercent = drawTime / (everyTime/2.0);
        if (drawMode) {
            drawPercent -= 1;
        } else {
            drawPercent = (double)1 - drawPercent;
        }
        drawPercent = easeInOutQuad(drawPercent);
        matrices.push();
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

        Box entitybb = entity.getBoundingBox();
        double radius = ((entitybb.maxX - entitybb.minX) + (entitybb.maxZ - entitybb.minZ)) * 0.5f;
        double height = entitybb.maxY - entitybb.minY;

        double x = entity.lastRenderX + (entity.getX() - entity.lastRenderX);
        double z = entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ);
        double y = entity.lastRenderY + (entity.getY() - entity.lastRenderY) + height * drawPercent;
        drawCircle(renderer, matrices, x, z, x, z, y, radius, lineColor);

        RenderSystem.disableBlend();
        matrices.pop();
    }

    public static void drawCircle(Renderer3D renderer, MatrixStack matrices, double x1, double z1, double x2, double z2, double y, double radius, Color color) {
        matrices.push();
        for (int i = 5; i <= 360; i++) {
            double MPI = Math.PI;
            double x = x1 - Math.sin((double) i * MPI / (double) 180.0F) * radius;
            double z = z1 + Math.cos((double) i * MPI / (double) 180.0F) * radius;
            double xx = x2 - Math.sin((double) (i - 5) * MPI / (double) 180.0F) * radius;
            double zz = z2 + Math.cos((double) (i - 5) * MPI / (double) 180.0F) * radius;

            renderer.line(x, y,  z, xx, y, zz, color);
        }
        matrices.pop();
    }
}
