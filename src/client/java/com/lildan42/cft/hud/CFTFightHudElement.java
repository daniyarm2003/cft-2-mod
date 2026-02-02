package com.lildan42.cft.hud;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.fights.CFTFight;
import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.utils.StringFormatUtils;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CFTFightHudElement implements HudElement {
    public static final Identifier HUD_ELEMENT_IDENTIFIER = CFT2Mod.createModIdentifier("fight");

    public static final String FIGHTERS_TEXT_TRANSLATABLE_KEY = CFT2Mod.getTranslatableKey("hud", "fight.fighters");
    public static final String TIMER_TEXT_TRANSLATABLE_KEY = CFT2Mod.getTranslatableKey("hud", "fight.timer");

    private static final int TOP_MARGIN = 10;
    private static final int SIDE_MARGIN = 10;

    private static final int VERTICAL_TEXT_SPACING = 5;

    private final ClientCFTFightManager fightManager;
    private final MinecraftClient client;

    public CFTFightHudElement(ClientCFTFightManager fightManager, MinecraftClient client) {
        this.fightManager = fightManager;
        this.client = client;
    }

    @Override
    public void render(@NotNull DrawContext context, @NotNull RenderTickCounter tickCounter) {
        CFTFight fight = this.fightManager.getForegroundFight();

        if(fight == null || fight.getFighters().size() < 2) {
            return;
        }

        CFTFighterEntity fighter1 = fight.getFighters().getFirst();
        CFTFighterEntity fighter2 = fight.getFighters().get(1);

        context.drawText(this.client.textRenderer, Text.translatable(FIGHTERS_TEXT_TRANSLATABLE_KEY, fighter1.getDisplayName(), fighter2.getDisplayName()),
                SIDE_MARGIN, TOP_MARGIN, 0xFFFFFFFF, true);

        context.drawText(this.client.textRenderer, Text.translatable(TIMER_TEXT_TRANSLATABLE_KEY, StringFormatUtils.formatDuration(fight.getElapsedTime())),
                SIDE_MARGIN, TOP_MARGIN + this.client.textRenderer.fontHeight + VERTICAL_TEXT_SPACING, 0xFFFFFFFF, true);
    }
}
