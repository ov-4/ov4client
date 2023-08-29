package dev.ov4client.addon.hud;

import dev.ov4client.addon.ov4client;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.Identifier;

public class CatGirl extends HudElement {
    public static final HudElementInfo<CatGirl> INFO = new HudElementInfo<>(ov4client.HUD_GROUP, "Cat Girl", "You should use fabric api to see it!", CatGirl::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<LogoEnum> logo = sgGeneral.add(new EnumSetting.Builder<LogoEnum>().name("Logo").defaultValue(LogoEnum.CatGirl).build());
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("Scale").description("The scale of the logo.").defaultValue(3.5).min(0.1).sliderRange(0.1, 5).build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder().name("Color").defaultValue(new SettingColor(255, 255, 255)).build());

    public CatGirl() {
        super(INFO);
    }

    private Identifier image = new Identifier("ov4-client", "catgirl.png");

    @Override
    public void tick(HudRenderer renderer) {
        box.setSize(72 * scale.get(), 15 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        switch (logo.get()) {
            case CatGirl -> image = new Identifier("ov4-client", "catgirl.png");
            case LiLiBai -> image = new Identifier("ov4-client", "lilibai.png");
            case CNMB -> image = new Identifier("ov4-client", "cnmb.png");
            case SBGun -> image = new Identifier("ov4-client", "sbgun.png");
            case FUFU -> image = new Identifier("ov4-client", "fufu.png");
        }

        GL.bindTexture(image);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texQuad(this.x, this.y - 29 * scale.get(), 70 * scale.get(), 70 * scale.get(), color.get());
        Renderer2D.TEXTURE.render(null);
    }

    public enum LogoEnum {
        CatGirl,
        LiLiBai,
        CNMB,
        SBGun,
        FUFU
    }
}
