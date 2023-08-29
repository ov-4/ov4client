package dev.ov4client.addon.mixins;

/*import dev.ov4client.addon.modules.info.ChatConfig;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatUtils.class)
public class ChatUtilsMixin {
    @Inject(method = "getPrefix", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getPrefix(CallbackInfoReturnable<MutableText> cir) {
        if (!Modules.get().isActive(ChatConfig.class)) return;
        if (Modules.get().get(ChatConfig.class).mode.get() != ChatConfig.Mode.ov4) {
            MutableText PREFIX = Text.literal(Modules.get().get(ChatConfig.class).mode.get() == ChatConfig.Mode.None ? "" : Modules.get().get(ChatConfig.class).text.get());
            MutableText prefix = Text.literal("");
            PREFIX.setStyle(PREFIX.getStyle().withFormatting(Formatting.BLUE));
            prefix.setStyle(prefix.getStyle().withFormatting(Formatting.BLUE));
            prefix.append(Modules.get().get(ChatConfig.class).mode.get() == ChatConfig.Mode.None ? "" : "[");
            prefix.append(PREFIX);
            prefix.append(Modules.get().get(ChatConfig.class).mode.get() == ChatConfig.Mode.None ? "" : "] ");
            cir.setReturnValue(prefix);
        }
    }
}
*/
