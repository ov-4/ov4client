package dev.ov4client.addon.mixins;

import dev.ov4client.addon.gui.screen.ov4clientScreen;
import meteordevelopment.meteorclient.gui.GuiThemes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    public MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (GuiThemes.get() !=  null) {
            client.setScreen(new ov4clientScreen());
        }
    }

    @Inject(method = "init",at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        if (GuiThemes.get() !=  null) {
            client.setScreen(new ov4clientScreen());
        }
    }

/*    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I", ordinal = 0))
    private void onRenderIdkDude(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Utils.firstTimeTitleScreen) {
            Utils.firstTimeTitleScreen = false;

            if (!ov4client.VERSION.isZero()) {
                ov4client.log("Checking latest version of Meteor Client");

                MeteorExecutor.execute(() -> {
                    Version latest;

                    try {
                        latest = Version.getLatest();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if (latest.isHigherThan(ov4client.VERSION)) {
                        YesNoPrompt.create()
                            .title("New Update")
                            .message("A new version of ov4client has been released.")
                            .message("Your version: %s", ov4client.VERSION)
                            .message("Latest version: %s", latest)
                            .message("Do you want to update?")
                            .onYes(() -> Util.getOperatingSystem().open("https://ov4client.cn/"))
                            .onNo(() -> OkPrompt.create()
                                .title("Are you sure?")
                                .message("Using old versions of Meteor is not recommended")
                                .message("and could report in issues.")
                                .id("new-update-no")
                                .onOk(this::close)
                                .show())
                            .id("new-update")
                            .show();
                    }
                });
            }
        }
    }*/
}
