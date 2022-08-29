package de.ambertation.wunderreich.mixin.client.overlay;

import de.ambertation.wunderreich.config.Configs;
import de.ambertation.wunderreich.gui.overlay.InputManager;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    public void wunderreich_keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (Configs.MAIN.allowConstructionTools.get()) {
            if (l == Minecraft.getInstance().getWindow().getWindow()) {
                InputConstants.Key key = InputConstants.getKey(i, j);
                if (InputManager.INSTANCE.handleKey(key, k)) {
                    ci.cancel();
                }
            }
        }
    }
}
