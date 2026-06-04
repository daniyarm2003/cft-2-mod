package com.lildan42.cft.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class GuiUtils {
    public static void drawCenteredScaledText(@NotNull DrawContext context, @NotNull TextRenderer textRenderer, String text, float centerX, float centerY, float scaleX, float scaleY, int color) {
        float textWidth = textRenderer.getWidth(text);
        float textHeight = textRenderer.fontHeight;

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(centerX, centerY);

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scaleX, scaleY);

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(-textWidth / 2.0F, -textHeight / 2.0F);

        context.drawText(textRenderer, text, 0, 0, color, false);

        context.getMatrices().popMatrix();
        context.getMatrices().popMatrix();
        context.getMatrices().popMatrix();
    }

    public static float getAdjustedTextScale(@NotNull TextRenderer textRenderer, String text, float containerWidth, float targetScale, float minScale) {
        return MathHelper.clamp(containerWidth / (targetScale * textRenderer.getWidth(text)), minScale, targetScale);
    }
}
