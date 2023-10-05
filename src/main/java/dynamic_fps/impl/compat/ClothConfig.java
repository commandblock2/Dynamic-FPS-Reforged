package dynamic_fps.impl.compat;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static dynamic_fps.impl.util.Localization.localized;

import java.util.Optional;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.config.DynamicFPSConfig;

public final class ClothConfig {
	public static Screen genConfigScreen(Screen parent) {
		var builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(localized("config", "title"))
			.setSavingRunnable(DynamicFPSMod.modConfig::save);

		var entryBuilder = builder.entryBuilder();

		for (var state : PowerState.values()) {
			if (!state.configurable) {
				continue;
			}

			var config = DynamicFPSMod.modConfig.get(state);
			var standard = DynamicFPSConfig.getDefaultConfig(state);

			var category = builder.getOrCreateCategory(
				localized("config", "category." + state.toString().toLowerCase())
			);

			category.addEntry(
				entryBuilder.startIntSlider(
					localized("config", "frame_rate_target"),
					fromConfigFpsTarget(config.frameRateTarget()),
					0, 61
				)
				.setDefaultValue(fromConfigFpsTarget(standard.frameRateTarget()))
				.setSaveConsumer(value -> config.setFrameRateTarget(toConfigFpsTarget(value)))
				.setTextGetter(ClothConfig::fpsTargetMessage)
				.build()
			);

			category.addEntry(
				entryBuilder.startIntSlider(
					localized("config", "volume_multiplier"),
					(int) (config.volumeMultiplier() * 100),
					0, 100
				)
				.setDefaultValue((int) (standard.volumeMultiplier() * 100))
				.setSaveConsumer(value -> config.setVolumeMultiplier(value / 100f))
				.setTextGetter(ClothConfig::volumeMultiplierMessage)
				.build()
			);

			category.addEntry(
				entryBuilder.startEnumSelector(
					localized("config", "graphics_state"),
					GraphicsState.class,
					config.graphicsState()
				)
				.setDefaultValue(standard.graphicsState())
				.setSaveConsumer(config::setGraphicsState)
				.setEnumNameProvider(ClothConfig::graphicsStateMessage)
				.setTooltipSupplier(ClothConfig::graphicsStateTooltip)
				.build()
			);

			category.addEntry(
				entryBuilder.startBooleanToggle(
					localized("config", "show_toasts"),
					config.showToasts()
				)
				.setDefaultValue(standard.showToasts())
				.setSaveConsumer(config::setShowToasts)
				.setTooltip(localized("config", "show_toasts_tooltip"))
				.build()
			);

			category.addEntry(
				entryBuilder.startBooleanToggle(
					localized("config", "run_garbage_collector"),
					config.runGarbageCollector()
				)
				.setDefaultValue(standard.runGarbageCollector())
				.setSaveConsumer(config::setRunGarbageCollector)
				.setTooltip(localized("config", "run_garbage_collector_tooltip"))
				.build()
			);
		}

		return builder.build();
	}

	// Convert magic -1 number to 61 (and reverse)
	// So the "unlocked" FPS value is on the right
	private static int toConfigFpsTarget(int value) {
		return value == 61 ? -1 : value;
	}

	private static int fromConfigFpsTarget(int value) {
		return value == -1 ? 61 : value;
	}

	private static Component fpsTargetMessage(int value) {
		if (toConfigFpsTarget(value) != -1) {
			return Component.translatable("options.framerate", value);
		} else {
			return Component.translatable("options.framerateLimit.max");
		}
	}

	private static Component volumeMultiplierMessage(int value) {
		return Component.literal(Integer.toString(value) + "%");
	}

	private static Component graphicsStateMessage(Enum<GraphicsState> graphicsState) {
		return localized("config", "graphics_state_" + graphicsState.toString());
	}

	private static Optional<Component[]> graphicsStateTooltip(GraphicsState graphicsState) {
		if (!graphicsState.equals(GraphicsState.MINIMAL)) {
			return Optional.empty();
		}

		return Optional.of(new Component[]{ localized("config", "graphics_state_minimal_tooltip").withStyle(ChatFormatting.RED) });
	}
}
