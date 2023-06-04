package dynamicfps.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dynamicfps.DynamicFPSMod;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SystemToast.class)
public class ToastManagerMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onDraw(PoseStack p_94844_, ToastComponent p_94845_, long p_94846_, CallbackInfoReturnable<Toast.Visibility> cir) {
        if (!DynamicFPSMod.shouldShowToasts()) {
			cir.cancel();
		}
    }
}
