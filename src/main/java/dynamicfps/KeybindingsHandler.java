package dynamicfps;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dynamicfps.util.Localization.keyBinding;

@Mod.EventBusSubscriber
public class KeybindingsHandler {
    private static final KeyMapping toggleForcedKeybinding = new KeyMapping(
            keyBinding( "toggle_forced"),
            KeyConflictContext.UNIVERSAL,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KeyMapping.CATEGORY_MISC
    );

    public static final KeyMapping toggleDisabledKeybinding = new KeyMapping(
            keyBinding( "toggle_disabled"),
            KeyConflictContext.UNIVERSAL,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KeyMapping.CATEGORY_MISC
    );

    @SubscribeEvent
    public static void interaction(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.getKeyMapping().same(toggleDisabledKeybinding)) {
            DynamicFPSMod.isDisabled = !DynamicFPSMod.isDisabled;
        }
        else if (event.getKeyMapping().same(toggleForcedKeybinding)) {
            DynamicFPSMod.isForcingLowFPS = !DynamicFPSMod.isForcingLowFPS;
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void keybindingRegistration(RegisterKeyMappingsEvent event) {
            event.register(toggleForcedKeybinding);
            event.register(toggleDisabledKeybinding);
        }
    }
}
