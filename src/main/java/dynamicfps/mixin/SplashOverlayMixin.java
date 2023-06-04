package dynamicfps.mixin;

import dynamicfps.DynamicFPSMod.SplashOverlayAccessor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoadingOverlay.class)
public class SplashOverlayMixin
		implements SplashOverlayAccessor {
	@Shadow
	@Final
	private ReloadInstance reload;
	
	@Override
	public boolean isReloadComplete() {
		return this.reload.isDone();
	}
}
