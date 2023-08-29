package dev.ov4client.addon.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Mesh;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(Mesh.class)
public class MixinMesh {
    @Shadow
    public boolean depthTest;

    @Shadow
    private boolean rendering3D;

    @Shadow
    private boolean beganRendering;

    /**
     * @author Fin_ov4Kee
     * @reason ov4client Screen Break, so i use this mixin to fix 2d pos translate.
     */
    @Overwrite
    public void beginRender(MatrixStack matrices) {
        GL.saveState();

        if (depthTest) GL.enableDepth();
        else GL.disableDepth();
        GL.enableBlend();
        GL.disableCull();
        GL.enableLineSmooth();

        if (rendering3D) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();

            if (matrices != null) matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());

            // Fix 2d pos translate
            if (mc.world != null) {
                Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
                matrixStack.translate(0, -cameraPos.y, 0);
            }
        }

        beganRendering = true;
    }
}
