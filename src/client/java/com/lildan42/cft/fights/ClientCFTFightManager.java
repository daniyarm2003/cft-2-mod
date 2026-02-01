package com.lildan42.cft.fights;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ClientCFTFightManager extends CFTFightManager {
    private CFTFight foregroundFight;
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

        Optional<CFTFighterEntity> winner = Optional.empty();

        if(winnerId != null) {
            winner = endedFight.getFighterById(winnerId);
        }

        if(this.foregroundFight != null && this.foregroundFight.equals(endedFight)) {
            this.foregroundFight = null;
        }
        else {
            winner.ifPresent(entity -> player.sendMessage(Text.translatable(CFT2Mod.getTranslatableKey("messages", "backgroundFightEnded"),
                    ++this.finishedBackgroundFights, entity.getDisplayName()), false));
        }
    }

    public CFTFight getForegroundFight() {
        return this.foregroundFight;
    }

    @Override
    public CFTFightStatsRecorder getStatsRecorder() {
        return null;
    }
}
