package dynamicfps;

import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import static dynamicfps.util.Localization.config;

public final class DynamicFPSConfig {
	static final ForgeConfigSpec SPECS;
	static final DynamicFPSConfig CLIENT;


	static {
		Pair<DynamicFPSConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(DynamicFPSConfig::new);
		SPECS = clientPair.getRight();
		CLIENT = clientPair.getLeft();
	}

	/// Whether to disable or enable the frame rate drop when unfocused.
	ForgeConfigSpec.BooleanValue reduceFPSWhenUnfocused;
	/// The frame rate to target when unfocused (only applies if `enableUnfocusedFPS` is true).
	ForgeConfigSpec.IntValue unfocusedFPS;
	/// Whether to uncap FPS when hovered, even if it would otherwise be reduced.
	ForgeConfigSpec.BooleanValue restoreFPSWhenHovered;
	/// Volume multiplier when not focused.
	ForgeConfigSpec.ConfigValue<Float> unfocusedVolumeMultiplier;
	/// Volume multiplier when not visible.
	ForgeConfigSpec.ConfigValue<Float> hiddenVolumeMultiplier;
	ForgeConfigSpec.IntValue volumeTransitionSpeed;
	/// Whether to
	ForgeConfigSpec.BooleanValue runGCOnUnfocus;

	DynamicFPSConfig(ForgeConfigSpec.Builder builder) {
		this.reduceFPSWhenUnfocused = builder.comment("disable or enable the frame rate drop when unfocused")
				.translation(config("reduce_when_unfocused"))
				.define("reduceFPSWhenUnfocused",true);
		this.unfocusedFPS = builder.comment("The frame rate to target when unfocused (only applies if `enableUnfocusedFPS` is true).")
				.translation(config("unfocused_fps"))
				.defineInRange("unfocusedFPS",1,0,60);
		this.restoreFPSWhenHovered = builder.comment("uncap FPS when hovered, even if it would otherwise be reduced")
				.translation(config("restore_when_hovered"))
				.define("restoreFPSWhenHovered", true);
		this.unfocusedVolumeMultiplier = builder.comment("Volume multiplier when not focused.")
				.translation(config("unfocused_volume"))
				.define("unfocusedVolumeMultiplier",0.25f);
		this.hiddenVolumeMultiplier = builder.comment("Volume multiplier when not visible.")
				.translation(config("hidden_volume"))
				.define("hiddenVolumeMultiplier", 0f);
		this.volumeTransitionSpeed = builder.comment("The percentage of the volume will be decrease in 1 second")
				.translation(config("volume_transition"))
				.defineInRange("volumeTransitionSpeed",35,1,100);
		this.runGCOnUnfocus = builder.comment("trigger a garbage collector run whenever the game is unfocused.")
				.translation(config("run_gc_on_unfocus"))
				.define("runGCOnUnfocus",false);
	}

	public boolean reduceFPSWhenUnfocused() {
		return this.reduceFPSWhenUnfocused.get();
	}

	public DynamicFPSConfig reduceFPSWhenUnfocused(boolean var) {
		this.reduceFPSWhenUnfocused.set(var);
		return this;
	}

	public int unfocusedFPS() {
		return this.unfocusedFPS.get();
	}
	public DynamicFPSConfig unfocusedFPS(int var) {
		this.unfocusedFPS.set(var);
		return this;
	}

	public boolean restoreFPSWhenHovered() {
		return this.restoreFPSWhenHovered.get();
	}
	public DynamicFPSConfig restoreFPSWhenHovered(boolean var) {
		this.restoreFPSWhenHovered.set(var);
		return this;
	}

	public float unfocusedVolumeMultiplier() {
		return this.unfocusedVolumeMultiplier.get();
	}
	public DynamicFPSConfig unfocusedVolumeMultiplier(float var) {
		this.unfocusedVolumeMultiplier.set(var);
		return this;
	}

	public float hiddenVolumeMultiplier() {
		return this.hiddenVolumeMultiplier.get();
	}
	public DynamicFPSConfig hiddenVolumeMultiplier(float var) {
		this.hiddenVolumeMultiplier.set(var);
		return this;
	}

	public float volumeTransitionSpeed() {
		return (float) this.volumeTransitionSpeed.get() / 100f;
	}

	public DynamicFPSConfig volumeTransitionSpeed(float speed) {
		this.volumeTransitionSpeed.set(Math.round(Mth.clamp(speed,0f,1f) * 100f));
		return this;
	}

	public DynamicFPSConfig volumeTransitionSpeed(int speed) {
		this.volumeTransitionSpeed.set(Mth.clamp(speed,1,100));
		return this;
	}

	public boolean runGCOnUnfocus() {
		return this.runGCOnUnfocus.get();
	}
	public DynamicFPSConfig runGCOnUnfocus(boolean var) {
		this.runGCOnUnfocus.set(var);
		return this;
	}
}
