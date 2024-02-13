package dynamicfps.util;

import dynamicfps.DynamicFPSMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public final class Localization {
	/** e.g. keyString("title", "config") -> "title.dynamicfps.config") */
	public static String translationKey(String domain, String path) {
		return domain + "." + DynamicFPSMod.MOD_ID + "." + path;
	}
	
	public static Component localized(String domain, String path, Object... args) {
		return new TranslatableComponent(translationKey(domain, path), args);
	}

	public static String config(String name) {
		return translationKey("config",name);
	}

	public static String keyBinding(String name) {
		return translationKey("key",name);
	}
}
