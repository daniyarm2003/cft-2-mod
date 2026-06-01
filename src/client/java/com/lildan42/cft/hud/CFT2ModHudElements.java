package com.lildan42.cft.hud;

import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.initialization.CFT2Initializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;

@SuppressWarnings("deprecation")
public class CFT2ModHudElements implements CFT2Initializer {

    private final HudRenderCallback[] hideHudIgnoringHudElements;

    public CFT2ModHudElements(ClientCFTFightManager fightManager, MinecraftClient client) {

        this.hideHudIgnoringHudElements = new HudRenderCallback[] {
                new CFTFightHudElement(fightManager, client)
        };
    }

    @Override
    public String getInitializationStageName() {
        return "HUD element registry";
    }

    @Override
    public void initialize(Logger logger) {
        for (HudRenderCallback hudElement : this.hideHudIgnoringHudElements) {
            HudRenderCallback.EVENT.register(hudElement);
        }
    }
}
