package com.lildan42.cft.hud;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fights.CFTFightResultsEntry;
import com.lildan42.cft.utils.StringFormatUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;

import java.time.Duration;
import java.util.List;

public class CFTFightResultsScreen extends Screen {
    public static final String SCREEN_TITLE_TRANSLATABLE_ID = CFT2Mod.getTranslatableKey("screen", "cft_fight_results.title");

    private final Duration fightElapsedTime;
    private final List<CFTFightResultsEntry> fightResults;

    public CFTFightResultsScreen(Duration fightElapsedTime, List<CFTFightResultsEntry> fightResults) {
        super(Text.translatable(SCREEN_TITLE_TRANSLATABLE_ID));

        this.fightElapsedTime = fightElapsedTime;
        this.fightResults = fightResults;
    }

    private void drawResultBox(DrawContext context, float centerX, float y, float width, float insideWidth, float itemHeight, List<FightResultBoxItem> items) {
        float x = centerX - width / 2.0F;
        float insideX = x + (width - insideWidth) / 2.0F;
        float totalHeight = itemHeight * items.size();
        float outsideSectionWidth = (width - insideWidth) / 2.0F;
        float outsideSection1CenterX = x + outsideSectionWidth / 2.0F;
        float outsideSection2CenterX = x + width - outsideSectionWidth / 2.0F;

        int outsideBoxColor = ColorHelper.getArgb(0xD9, 0xD9, 0xD9);
        int outsideBoxTextColor = Colors.BLACK;

        int insideBoxColor = ColorHelper.getArgb(0x66, 0x66, 0x66);
        int insideBoxTextColor = Colors.WHITE;

        context.fill((int) x, (int) y, (int) (x + width), (int) (y + totalHeight), outsideBoxColor);
        context.fill((int) insideX, (int) y, (int) (insideX + insideWidth), (int) (y + totalHeight), insideBoxColor);

        for(int i = 0; i < items.size(); i++) {
            FightResultBoxItem item = items.get(i);

            float itemCenterY = y + (0.5F + i) * itemHeight;
            float itemEndY = y + (1 + i) * itemHeight;
            float textScale = 1.0F;

            GuiUtils.drawCenteredScaledText(context, this.textRenderer, item.statName(), centerX, itemCenterY, textScale, textScale, insideBoxTextColor);
            GuiUtils.drawCenteredScaledText(context, this.textRenderer, item.fighter1Value(), outsideSection1CenterX, itemCenterY, textScale, textScale, outsideBoxTextColor);
            GuiUtils.drawCenteredScaledText(context, this.textRenderer, item.fighter2Value(), outsideSection2CenterX, itemCenterY, textScale, textScale, outsideBoxTextColor);

            if(i < items.size() - 1) {
                context.drawHorizontalLine((int) x, (int) (x + width) - 1, (int) itemEndY, Colors.WHITE);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        float windowWidth = context.getScaledWindowWidth();
        float windowHeight = context.getScaledWindowHeight();

        float centerX = windowWidth / 2.0F;
        float topMargin = 0.02F * windowHeight;

        int timerBoxColor = Colors.BLACK;
        int timerBoxTextColor = Colors.WHITE;

        float timerBoxWidth = 0.08F * windowWidth;
        float timerBoxHeight = 0.06F * windowHeight;
        float timerBoxX = centerX - timerBoxWidth / 2.0F;
        float timerBoxCenterY = topMargin + timerBoxHeight / 2.0F;

        float timerTextScale = 1.0F;
        String timerText = StringFormatUtils.formatDuration(this.fightElapsedTime);

        context.fill((int)timerBoxX, (int)topMargin, (int)(timerBoxX + timerBoxWidth), (int)(topMargin + timerBoxHeight), timerBoxColor);
        GuiUtils.drawCenteredScaledText(context, this.textRenderer, timerText, centerX, timerBoxCenterY, timerTextScale, timerTextScale, timerBoxTextColor);

        CFTFightResultsEntry fighter1Results = this.fightResults.get(0);
        CFTFightResultsEntry fighter2Results = this.fightResults.get(1);

        float timerBoxBottomMargin = 0.01F * windowHeight;
        float fighterNameBoxWidth = 0.28F * windowWidth;
        float fighterNameBoxMarginX = 0.015F * windowWidth;
        float fighterNameBoxHeight = timerBoxHeight + timerBoxBottomMargin;
        float fighterNameBoxCenterY = topMargin + fighterNameBoxHeight / 2.0F;

        float fighterNameBox1CenterX = fighterNameBoxMarginX + fighterNameBoxWidth / 2.0F;
        int fighterNameBox1Color = Colors.YELLOW;

        float fighterNameText1Scale = GuiUtils.getAdjustedTextScale(this.textRenderer, fighter1Results.name(), 0.9F * fighterNameBoxWidth, 1.0F, 0.25F);
        int fighterNameText1Color = Colors.BLACK;

        float fighterNameBox2X = windowWidth - fighterNameBoxMarginX - fighterNameBoxWidth;
        float fighterNameBox2CenterX = fighterNameBox2X + fighterNameBoxWidth / 2.0F;
        int fighterNameBox2Color = Colors.RED;

        float fighterNameText2Scale = GuiUtils.getAdjustedTextScale(this.textRenderer, fighter2Results.name(), 0.9F * fighterNameBoxWidth, 1.0F, 0.25F);
        int fighterNameText2Color = Colors.WHITE;

        context.fill((int)fighterNameBoxMarginX, (int)topMargin, (int)(fighterNameBoxMarginX + fighterNameBoxWidth), (int)(topMargin + fighterNameBoxHeight), fighterNameBox1Color);
        GuiUtils.drawCenteredScaledText(context, this.textRenderer, fighter1Results.name(), fighterNameBox1CenterX, fighterNameBoxCenterY, fighterNameText1Scale, fighterNameText1Scale, fighterNameText1Color);

        context.fill((int)fighterNameBox2X, (int)topMargin, (int)(fighterNameBox2X + fighterNameBoxWidth), (int)(topMargin + fighterNameBoxHeight), fighterNameBox2Color);
        GuiUtils.drawCenteredScaledText(context, this.textRenderer, fighter2Results.name(), fighterNameBox2CenterX, fighterNameBoxCenterY, fighterNameText2Scale, fighterNameText2Scale, fighterNameText2Color);

        float resultBoxWidth = 0.6F * windowWidth;
        float resultBoxInsideWidth = fighterNameBox2X - (fighterNameBoxMarginX + fighterNameBoxWidth);
        float itemHeight = 0.125F * windowHeight;

        List<FightResultBoxItem> hpResults = List.of(
                new FightResultBoxItem("Remaining Health (HP)", "%d".formatted((int)fighter1Results.remainingHp()), "%d".formatted((int)fighter2Results.remainingHp())),
                new FightResultBoxItem("Starting Health (HP)", "%d".formatted((int)fighter1Results.maxHp()), "%d".formatted((int)fighter2Results.maxHp())),
                new FightResultBoxItem("Remaining Health (%)", "%.2f".formatted(fighter1Results.getRemainingHpRatio() * 100.0F), "%.2f".formatted(fighter2Results.getRemainingHpRatio() * 100.0F))
        );

        List<FightResultBoxItem> attackResults = List.of(
                new FightResultBoxItem("Strikes Thrown", "%d".formatted(fighter1Results.totalAttacks()), "%d".formatted(fighter2Results.totalAttacks())),
                new FightResultBoxItem("Strikes Landed", "%d".formatted(fighter1Results.landedAttacks()), "%d".formatted(fighter2Results.landedAttacks())),
                new FightResultBoxItem("Striking Accuracy (%)", "%.2f".formatted(fighter1Results.getStrikingAccuracy() * 100.0F), "%.2f".formatted(fighter2Results.getStrikingAccuracy() * 100.0F))
        );

        this.drawResultBox(context, centerX, topMargin + fighterNameBoxHeight, resultBoxWidth, resultBoxInsideWidth, itemHeight, hpResults);
        this.drawResultBox(context, centerX, topMargin + fighterNameBoxHeight + hpResults.size() * itemHeight + 0.075F * windowHeight, resultBoxWidth, resultBoxInsideWidth, itemHeight, attackResults);
    }

    private record FightResultBoxItem(String statName, String fighter1Value, String fighter2Value) {}
}
