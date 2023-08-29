package dev.ov4client.addon.gui.screen;

import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GuiRender extends Screen {
    public HudRenderer renderer = HudRenderer.INSTANCE;
    //public MinecraftClient mc = MinecraftClient.getInstance();
    //public MinecraftClient minecraft = MinecraftClient.getInstance();

    public GuiRender(Text text) {
        super(text);
    }

    public GuiRender() {
        super(Text.of("ov4client"));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        //Utils.unscaledProjection();
        draw(drawContext, mouseX, mouseY, tickDelta);
        //Utils.scaledProjection();
    }

    public void draw(DrawContext matrices, int mouseX, int mouseY, float tickDelta) {
    }

    public boolean isMouseHoveringRect(double x, double y, double w, double h, double mouseX, double mouseY){
        return mouseX >= x && mouseY >= y && mouseX <= x+w && mouseY <= y+h;
    }
}
