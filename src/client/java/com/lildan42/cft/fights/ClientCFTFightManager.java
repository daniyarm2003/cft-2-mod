package com.lildan42.cft.fights;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.hud.CFTFightResultsScreen;
import com.lildan42.cft.utils.StringFormatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientCFTFightManager extends CFTFightManager {

    public static final String BACKGROUND_FIGHT_ENDED_TRANSLATABLE_KEY = CFT2Mod.getTranslatableKey("messages", "backgroundFightEnded");

    private CFTFight foregroundFight;

    private UUID prevForegroundFight = null;
    private Duration prevForegroundFightElapsedTime = null;

    private int finishedBackgroundFights = 0;

    public void handleFightStart(CFTFight fight, boolean foreground) {
        this.fights.add(fight);

        if(foreground) {
            this.foregroundFight = fight;
        }
    }

    public void handleFightEnd(UUID fightId, @Nullable Integer winnerId, ClientPlayerEntity player) {
        Optional<CFTFight> endedFightOpt = this.fights.stream().filter(fight -> fight.getId().equals(fightId)).findFirst();

        if(endedFightOpt.isEmpty()) {
            CFT2Mod.LOGGER.error("CFT fight could not be ended due to invalid fight ID");
            return;
        }

        CFTFight endedFight = endedFightOpt.get();
        this.fights.remove(endedFight);

        Optional<CFTFighterEntity> winner = winnerId != null ? endedFight.getFighterById(winnerId) : Optional.empty();

        if(this.foregroundFight != null && this.foregroundFight.equals(endedFight)) {
            this.prevForegroundFight = this.foregroundFight.getId();
            this.prevForegroundFightElapsedTime = this.foregroundFight.getElapsedTime();

            this.foregroundFight = null;
        }
        else {
            winner.ifPresent(entity -> player.sendMessage(Text.translatable(BACKGROUND_FIGHT_ENDED_TRANSLATABLE_KEY,
                    ++this.finishedBackgroundFights, entity.getDisplayName()), false));
        }
    }

    public void handleFightResults(List<CFTFightResultsEntry> results, MinecraftClient client) {
        client.setScreen(new CFTFightResultsScreen(this.prevForegroundFightElapsedTime, results));
    }

    public CFTFight getForegroundFight() {
        return this.foregroundFight;
    }

    public boolean isPreviousForegroundFight(UUID fightId) {
        return fightId != null && fightId.equals(this.prevForegroundFight);
    }

    @Override
    public CFTFightStatsRecorder getStatsRecorder() {
        return null;
    }
}
