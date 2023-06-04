package dynamicfps.mixin;

import dynamicfps.DynamicFPSMod;
import dynamicfps.DynamicFPSMod.SplashOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	
	/**
	 Implements the mod's big feature.
	 */
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void onRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.checkForRender()) {
			callbackInfo.cancel();
		}
	}
	
	/**
	 cancels world rendering under certain conditions
	 */
	@Inject(at = @At("HEAD"), method = "renderLevel", cancellable = true)
	private void onRenderWorld(CallbackInfo callbackInfo) {
		Overlay overlay = minecraft.getOverlay();
		if (overlay instanceof LoadingOverlay) {
			SplashOverlayAccessor splashScreen = (SplashOverlayAccessor) overlay;
			if (!splashScreen.isReloadComplete()) {
				callbackInfo.cancel();
			}
		}
	}
}
