package dev.ov4client.addon.mixins;

import dev.ov4client.addon.gui.themes.rounded.ov4clientGuiTheme;
import dev.ov4client.addon.utils.render.MSAAFramebuffer;
import dev.ov4client.addon.utils.render.MeteorSystem;
import dev.ov4client.addon.utils.timers.MSTimer;
import meteordevelopment.meteorclient.gui.GuiKeyEvents;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiDebugRenderer;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = WidgetScreen.class)
public abstract class MixinWidgetScreen {
    @Shadow
    @Final
    private static GuiRenderer RENDERER;

    @Shadow
    public double animProgress;

    @Shadow
    @Final
    private WContainer root;

    @Shadow
    private boolean debug;

    @Shadow
    @Final
    private static GuiDebugRenderer DEBUG_RENDERER;

    @Shadow
    protected abstract void runAfterRenderTasks();

    @Shadow
    protected abstract void onRenderBefore(DrawContext drawContext, float delta);

    @Shadow
    @Final
    protected GuiTheme theme;
    private final MeteorSystem meteorSystem = new MeteorSystem(30);
    private final MSTimer timer = new MSTimer();

    public MixinWidgetScreen() {
        timer.reset();
    }

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        WidgetScreen screen = (WidgetScreen) (Object) this;

        if (!Utils.canUpdate()) screen.renderBackground(context);

        double s = mc.getWindow().getScaleFactor();
        mouseX *= s;
        mouseY *= s;

        animProgress += delta / 20 * 14;
        animProgress = MathHelper.clamp(animProgress, 0, 1);

        GuiKeyEvents.canUseKeys = true;

        // Apply projection without scaling
        Utils.unscaledProjection();

        onRenderBefore(context, delta);

        RENDERER.theme = theme;
        theme.beforeRender();

        int finalMouseX = mouseX;
        int finalMouseY = mouseY;
        Runnable task = () -> {
            boolean a = false;
            if (theme instanceof ov4clientGuiTheme t) {
                if (t.meteorRainbow.get()) {
                    a = true;
                }
            }
            this.meteorSystem.setRainbow(a);
            this.meteorSystem.tick();
            this.meteorSystem.render(context);

            RENDERER.begin(context);
            RENDERER.setAlpha(animProgress);
            root.render(RENDERER, finalMouseX, finalMouseY, delta / 20);
            RENDERER.setAlpha(1);
            RENDERER.end();

            boolean tooltip = RENDERER.renderTooltip(context, finalMouseX, finalMouseY, delta / 20);
            if (debug) {
                MatrixStack matrices = context.getMatrices();

                DEBUG_RENDERER.render(root, matrices);
                if (tooltip) DEBUG_RENDERER.render(RENDERER.tooltipWidget, matrices);
            }
        };

        if (GuiThemes.get() instanceof ov4clientGuiTheme) {
            MSAAFramebuffer.use(task);
        } else task.run();
        Utils.scaledProjection();
        runAfterRenderTasks();
        ci.cancel();
    }
}
