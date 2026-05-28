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
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CFTFightHudElement implements HudElement {
    public static final Identifier HUD_ELEMENT_IDENTIFIER = CFT2Mod.createModIdentifier("fight");

    private final ClientCFTFightManager fightManager;
    private final MinecraftClient client;

    public CFTFightHudElement(ClientCFTFightManager fightManager, MinecraftClient client) {
        this.fightManager = fightManager;
        this.client = client;
    }

    private void drawCenteredScaledText(@NotNull DrawContext context, String text, float centerX, float centerY, float scaleX, float scaleY, int color) {
        float textWidth = this.client.textRenderer.getWidth(text);
        float textHeight = this.client.textRenderer.fontHeight;

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(centerX, centerY);

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scaleX, scaleY);

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(-textWidth / 2.0F, -textHeight / 2.0F);

        context.drawText(this.client.textRenderer, text, 0, 0, color, false);

        context.getMatrices().popMatrix();
        context.getMatrices().popMatrix();
        context.getMatrices().popMatrix();
    }

    @Override
    public void render(@NotNull DrawContext context, @NotNull RenderTickCounter tickCounter) {
        CFTFight fight = this.fightManager.getForegroundFight();

        if(fight == null || fight.getFighters().size() < 2) {
            return;
        }

        CFTFighterEntity fighter1 = fight.getFighters().getFirst();
        CFTFighterEntity fighter2 = fight.getFighters().get(1);

        String heartClassName = fighter1.getFighterData().getHeartClass().getHeartClassName();

        int textColor = Colors.WHITE;
        int primaryBgColor = Colors.BLACK;
        int secondaryBgColor = ColorHelper.getArgb(0x43, 0x43, 0x43);

        float windowWidth = context.getScaledWindowWidth();
        float windowCenterX = windowWidth / 2.0F;

        float windowHeight = context.getScaledWindowHeight();

        float bottomHudYSpacing = 0.02F * windowHeight;

        float heartClassBoxWidth = 0.3F * windowWidth;
        float heartClassBoxHeight = 3.0F * bottomHudYSpacing;
        float heartClassBoxX = windowCenterX - heartClassBoxWidth / 2.0F;
        float heartClassBoxY = windowHeight - bottomHudYSpacing - heartClassBoxHeight;

        context.fill((int)heartClassBoxX, (int)heartClassBoxY, (int)(heartClassBoxX + heartClassBoxWidth), (int)(heartClassBoxY + heartClassBoxHeight), primaryBgColor);

        String heartClassText = "%s BOUT".formatted(heartClassName.toUpperCase(Locale.ROOT));

        float heartClassBoxCenterX = heartClassBoxX + heartClassBoxWidth / 2.0F;
        float heartClassBoxCenterY = heartClassBoxY + heartClassBoxHeight / 2.0F;
        float heartClassTextScaleX = 1.0F;
        float heartClassTextScaleY = 0.8F;

        this.drawCenteredScaledText(context, heartClassText, heartClassBoxCenterX, heartClassBoxCenterY, heartClassTextScaleX, heartClassTextScaleY, textColor);

        float timerBoxWidth = 0.1F * windowWidth;
        float timerBoxHeight = 0.1F * windowHeight;
        float timerBoxX = windowCenterX - timerBoxWidth / 2.0F;
        float timerBoxY = heartClassBoxY - bottomHudYSpacing - timerBoxHeight;

        context.fill((int)timerBoxX, (int)timerBoxY, (int)(timerBoxX + timerBoxWidth), (int)(timerBoxY + timerBoxHeight), primaryBgColor);

        String timerText = StringFormatUtils.formatDuration(fight.getElapsedTime());

        float timerBoxCenterX = timerBoxX + timerBoxWidth / 2.0F;
        float timerBoxCenterY = timerBoxY + timerBoxHeight / 2.0F;
        float timerTextScale = 1.5F;

        this.drawCenteredScaledText(context, timerText, timerBoxCenterX, timerBoxCenterY, timerTextScale, timerTextScale, textColor);

        float fighterBoxWidth = 3.0F * timerBoxWidth;
        float fighter1BoxX = timerBoxX - fighterBoxWidth;
        float fighter2BoxX = timerBoxX + timerBoxWidth;

        context.fill((int)fighter1BoxX, (int)timerBoxY, (int)(fighter1BoxX + fighterBoxWidth), (int)(timerBoxY + timerBoxHeight), secondaryBgColor);
        context.fill((int)fighter2BoxX, (int)timerBoxY, (int)(fighter2BoxX + fighterBoxWidth), (int)(timerBoxY + timerBoxHeight), secondaryBgColor);

        float fighter1BoxCenterX = fighter1BoxX + fighterBoxWidth / 2.0F;
        float fighter2BoxCenterX = fighter2BoxX + fighterBoxWidth / 2.0F;

        this.drawCenteredScaledText(context, fighter1.getStringifiedName(), fighter1BoxCenterX, timerBoxCenterY, timerTextScale, timerTextScale, textColor);
        this.drawCenteredScaledText(context, fighter2.getStringifiedName(), fighter2BoxCenterX, timerBoxCenterY, timerTextScale, timerTextScale, textColor);
    }
}
