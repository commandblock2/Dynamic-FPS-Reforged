package dynamicfps;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.concurrent.locks.LockSupport;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(DynamicFPSMod.MOD_ID)
public class DynamicFPSMod {
	public static final String MOD_ID = "dynamicfps";
	
	static boolean isDisabled = false;
	public static boolean isDisabled() { return isDisabled; }
	
	static boolean isForcingLowFPS = false;
	public static boolean isForcingLowFPS() { return isForcingLowFPS; }

	public DynamicFPSMod() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,DynamicFPSConfig.SPECS);
	}
	
	private static Minecraft client;
	private static Window window;
	private static boolean isFocused, isVisible, isHovered;
	private static long lastRender;
	/**
	 * Should replace endpoints in a future update
	 */
	private static boolean preventSkip = false;
	private static float volumeStep;
	private static float volume;
	/**
	 Determines whether the game should render anything at this time. If not, blocks for a short time.
	 
	 @return whether the game should be rendered after this.
	 */
	public static boolean checkForRender() {
		if (!DynamicFPSConfig.SPECS.isLoaded()) return true;
		if (isDisabled || preventSkip) return true;
		if (client == null) {
			client = Minecraft.getInstance();
			window = client.getWindow();
		}
		updateSound();
		isFocused = client.isWindowActive();
		isVisible = GLFW.glfwGetWindowAttrib(window.getWindow(), GLFW.GLFW_VISIBLE) != 0;
		isHovered = GLFW.glfwGetWindowAttrib(window.getWindow(), GLFW.GLFW_HOVERED) != 0;
		
		checkForStateChanges();
		
		long currentTime = Util.getMillis();
		long timeSinceLastRender = currentTime - lastRender;
		
		if (!checkForRender(timeSinceLastRender)) return false;
		
		lastRender = currentTime;
		return true;
	}

	private static void updateSound() {
		float master = client.getSoundManager().soundEngine.listener.getGain();
		if (volume != master) {
			float newVol;
			if (volume > master) newVol = Math.min(volume, master + volumeStep);
			else newVol = Math.max(volume, master - volumeStep);

			client.getSoundManager().updateSourceVolume(SoundSource.MASTER,newVol);
		}
	}

	public static boolean shouldShowToasts() {
		return isDisabled || preventSkip || fpsOverride() == null;
	}

	private static boolean wasFocused = true;
	private static boolean wasVisible = true;
	private static void checkForStateChanges() {
		if (isFocused != wasFocused) {
			wasFocused = isFocused;
			if (isFocused) onFocus();
			else onUnfocus();
		}
		
		if (isVisible != wasVisible) {
			wasVisible = isVisible;
			if (isVisible) onAppear();
			else onDisappear();
		}
	}
	
	private static void onFocus() {
		setVolumeMultiplier(1);
	}
	
	private static void onUnfocus() {
		if (isVisible)
			setVolumeMultiplier(config().unfocusedVolumeMultiplier());
		
		if (config().runGCOnUnfocus()) System.gc();
	}
	
	private static void onAppear() {
		if (!isFocused) setVolumeMultiplier(config().unfocusedVolumeMultiplier());
	}
	
	private static void onDisappear() {
		setVolumeMultiplier(config().hiddenVolumeMultiplier());
	}
	
	private static void setVolumeMultiplier(float multiplier) {
		// setting the volume to 0 stops all sounds (including music), which we want to avoid if possible.
		var clientWillPause = !isFocused && client.options.pauseOnLostFocus && client.screen == null;
		// if the client pauses anyway, we don't need to do anything because that will already pause all sounds.
		if (multiplier == 0 && clientWillPause) return;
		
		var baseVolume = client.options.getSoundSourceVolume(SoundSource.MASTER);
		volume = baseVolume * multiplier;
		volumeStep = config().volumeTransitionSpeed() / 20;
	}
	
	// we always render one last frame before actually reducing FPS, so the hud text shows up instantly when forcing low fps.
	// additionally, this would enable mods which render differently while mc is inactive.
	private static boolean hasRenderedLastFrame = false;
	private static boolean checkForRender(long timeSinceLastRender) {
		Integer fpsOverride = fpsOverride();
		if (fpsOverride == null) {
			hasRenderedLastFrame = false;
			return true;
		}
		
		if (!hasRenderedLastFrame) {
			// render one last frame before reducing, to make sure differences in this state show up instantly.
			hasRenderedLastFrame = true;
			return true;
		}
		
		if (fpsOverride == 0) {
			idle(1000);
			return false;
		}
		
		long frameTime = 1000 / fpsOverride;
		boolean shouldSkipRender = timeSinceLastRender < frameTime;
		if (!shouldSkipRender) return true;
		
		idle(frameTime);
		return false;
	}
	
	/**
	 force minecraft to idle because otherwise we'll be busy checking for render again and again
	 */
	private static void idle(long waitMillis) {
		// cap at 30 ms before we check again so user doesn't have to wait long after tabbing back in
		waitMillis = Math.min(waitMillis, 30);
		LockSupport.parkNanos("waiting to render", waitMillis * 1_000_000);
	}
	
	@Nullable
	private static Integer fpsOverride() {
		if (!isVisible) return 0;
		if (isForcingLowFPS) return config().unfocusedFPS();
		if (config().restoreFPSWhenHovered() && isHovered) return null;
		if (config().reduceFPSWhenUnfocused() && !client.isWindowActive()) return config().unfocusedFPS();
		return null;
	}

	public static DynamicFPSConfig config() {
		return DynamicFPSConfig.CLIENT;
	}


	public interface SplashOverlayAccessor {
		boolean isReloadComplete();
	}
}
