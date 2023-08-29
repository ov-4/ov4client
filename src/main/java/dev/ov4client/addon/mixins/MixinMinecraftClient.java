package dev.ov4client.addon.mixins;

import dev.ov4client.addon.events.InteractEvent;
import dev.ov4client.addon.modules.misc.MultiTask;
import dev.ov4client.addon.hwid.Hwid;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IMinecraftClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {MinecraftClient.class}, priority = 1001)
public abstract class MixinMinecraftClient implements IMinecraftClient {
    @Redirect(method = {"handleBlockBreaking"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean breakBlockCheck(ClientPlayerEntity clientPlayerEntity) {
        return !Modules.get().isActive(MultiTask.class) && ((InteractEvent) MeteorClient.EVENT_BUS.post((Object) InteractEvent.get(clientPlayerEntity.isUsingItem()))).usingItem;
    }

    @Redirect(method = {"doItemUse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
    public boolean useItemBreakCheck(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        return !Modules.get().isActive(MultiTask.class) && ((InteractEvent) MeteorClient.EVENT_BUS.post((Object) InteractEvent.get(clientPlayerInteractionManager.isBreakingBlock()))).usingItem;
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V", shift = At.Shift.BEFORE))
    private void init(RunArgs args, CallbackInfo ci) {
        Hwid.get();
    }
}
