package com.lildan42.cft.hud;

import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.initialization.CFT2Initializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;

public class CFT2ModHudElements implements CFT2Initializer {
    private final ClientCFTFightManager fightManager;

    public CFT2ModHudElements(ClientCFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    @Override
    public String getInitializationStageName() {
        return "HUD element registry";
    }

    @Override
    public void initialize(Logger logger) {
        ClientLifecycleEvents.CLIENT_STARTED.register(this::registerHudElements);
    }

    private void registerHudElements(MinecraftClient client) {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, CFTFightHudElement.HUD_ELEMENT_IDENTIFIER, new CFTFightHudElement(this.fightManager, client));
    }
}
