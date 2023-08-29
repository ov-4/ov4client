package dev.ov4client.addon.hud;

import dev.ov4client.addon.ov4client;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.RainbowColor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.Identifier;

public class Logo extends HudElement {
    public static final HudElementInfo<Logo> INFO = new HudElementInfo<>(ov4client.HUD_GROUP, "Logo", "You should use fabric api to see it!", Logo::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<LogoEnum> logo = sgGeneral.add(new EnumSetting.Builder<LogoEnum>().name("Logo").defaultValue(LogoEnum.Text).build());
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("Scale").description("The scale of the logo.").defaultValue(3.5).min(0.1).sliderRange(0.1, 5).build());
    public final Setting<Boolean> chroma = sgGeneral.add(new BoolSetting.Builder().name("Chroma").description("Chroma logo animation.").defaultValue(false).build());
    private final Setting<Double> chromaSpeed = sgGeneral.add(new DoubleSetting.Builder().name("Factor").defaultValue(0.10).min(0.01).sliderMax(5).decimalPlaces(4).visible(chroma::get).build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder().name("Color").defaultValue(new SettingColor(255, 255, 255)).visible(() -> !chroma.get()).build());

    private Identifier image = new Identifier("ov4-client", "text.png");

    private static final RainbowColor RAINBOW = new RainbowColor();

    public Logo() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        box.setSize(72 * scale.get(), 15 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        switch (logo.get()) {
            case Text -> image = new Identifier("ov4-client", "text.png");
            case NewText -> image = new Identifier("ov4-client", "newtext.png");
            case Jello -> image = new Identifier("ov4-client", "jellotext.png");
            case Logo -> image = new Identifier("ov4-client", "icons/icon.png");
        }

        GL.bindTexture(image);
        Renderer2D.TEXTURE.begin();
        if (chroma.get()) {
            RAINBOW.setSpeed(chromaSpeed.get() / 100);
            Renderer2D.TEXTURE.texQuad(this.x, this.y - 29 * scale.get(), 70 * scale.get(), 70 * scale.get(), RAINBOW.getNext(renderer.delta));
        } else {
            Renderer2D.TEXTURE.texQuad(this.x, this.y - 29 * scale.get(), 70 * scale.get(), 70 * scale.get(), color.get());
        }
        Renderer2D.TEXTURE.render(null);
    }

    public enum LogoEnum {
        Text,
        NewText,
        Jello,
        Logo
    }
}
