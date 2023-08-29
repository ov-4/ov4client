package dev.ov4client.addon.mixins;

import dev.ov4client.addon.hud.ToastNotifications;
import dev.ov4client.addon.managers.Managers;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Modules.class)
public class MixinToggle {

    @Inject(method = "addActive", at = @At(value = "INVOKE", target = "Lmeteordevelopment/orbit/IEventBus;post(Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private void addActive(Module module, CallbackInfo ci) {
        if (ToastNotifications.getInstance() != null && ToastNotifications.getInstance().toggleMessage.get() && ToastNotifications.getInstance().toggleList.get().contains(module)) {
            ToastNotifications.addToggled(module, " ON!");
        }

        Managers.NOTIFICATION.info(module.title,module.title+" ON!");
    }

    @Inject(method = "removeActive", at = @At(value = "INVOKE", target = "Lmeteordevelopment/orbit/IEventBus;post(Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private void removeActive(Module module, CallbackInfo ci) {
        if (ToastNotifications.getInstance() != null && ToastNotifications.getInstance().toggleMessage.get() && ToastNotifications.getInstance().toggleList.get().contains(module)) {
            ToastNotifications.addToggled(module, " OFF!");
        }

        Managers.NOTIFICATION.info(module.title,module.title+" OFF!");
    }
}
