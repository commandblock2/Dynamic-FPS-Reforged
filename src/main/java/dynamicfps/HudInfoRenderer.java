package dynamicfps;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dynamicfps.util.Localization.localized;

@Mod.EventBusSubscriber
public final class HudInfoRenderer {
	@SubscribeEvent
	public void render(RenderGuiEvent event) {
		if (DynamicFPSMod.isDisabled()) {
			drawCenteredText(event.getPoseStack(), localized("gui", "hud.disabled"), 32);
		}
		else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(event.getPoseStack(), localized("gui", "hud.reducing"), 32);
		}
	}
	
	private void drawCenteredText(PoseStack stack, Component text, float y) {
		Minecraft client = Minecraft.getInstance();
		Font font = client.gui.getFont();

		int windowWidth = client.getWindow().getWidth();
		int stringWidth = font.width(text);
		font.drawShadow(
			stack,
			text,
			(windowWidth - stringWidth) / 2f, y,
			0xffffff
		);
	}
}
