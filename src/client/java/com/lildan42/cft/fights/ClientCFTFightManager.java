package com.lildan42.cft.fights;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.utils.StringFormatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ClientCFTFightManager extends CFTFightManager {
    public static final String FOREGROUND_FIGHT_ENDED_TRANSLATABLE_KEY = CFT2Mod.getTranslatableKey("messages", "foregroundFightEnded");
    public static final String FOREGROUND_FIGHT_ENDED_HP_TRANSLATABLE_KEY = CFT2Mod.getTranslatableKey("messages", "foregroundFightEndedHp");

    public static final String BACKGROUND_FIGHT_ENDED_TRANSLATABLE_KEY = CFT2Mod.getTranslatableKey("messages", "backgroundFightEnded");

    private static final int HP_MESSAGE_DELAY_MILLIS = 2000;

    private CFTFight foregroundFight;
    private int finishedBackgroundFights = 0;

    public void handleFightStart(CFTFight fight, boolean foreground) {
        this.fights.add(fight);

        if(foreground) {
            this.foregroundFight = fight;
        }
    }

    public void handleFightEnd(UUID fightId, @Nullable Integer winnerId, ClientPlayerEntity player, MinecraftClient client) {
        Optional<CFTFight> endedFightOpt = this.fights.stream().filter(fight -> fight.getId().equals(fightId)).findFirst();

        if(endedFightOpt.isEmpty()) {
            CFT2Mod.LOGGER.error("CFT fight could not be ended due to invalid fight ID");
            return;
        }

        CFTFight endedFight = endedFightOpt.get();
        this.fights.remove(endedFight);

        Optional<CFTFighterEntity> winner;

        if(winnerId != null) {
            winner = endedFight.getFighterById(winnerId);
        }
        else {
            winner = Optional.empty();
        }

        if(this.foregroundFight != null && this.foregroundFight.equals(endedFight)) {
            player.sendMessage(Text.translatable(FOREGROUND_FIGHT_ENDED_TRANSLATABLE_KEY, StringFormatUtils.formatDuration(this.foregroundFight.getElapsedTime())), false);

            if(winner.isEmpty()) {
                return;
            }

            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(HP_MESSAGE_DELAY_MILLIS);
                }
                catch (InterruptedException e) {
                    CFT2Mod.LOGGER.warn("Virtual thread sleep on fight end was interrupted");
                }

                int hp = MathHelper.ceil(winner.get().getHealth());
                int maxHp = MathHelper.ceil(winner.get().getMaxHealth());

                client.execute(() -> player.sendMessage(Text.translatable(FOREGROUND_FIGHT_ENDED_HP_TRANSLATABLE_KEY, hp, maxHp), false));
            });

            this.foregroundFight = null;
        }
        else {
            winner.ifPresent(entity -> player.sendMessage(Text.translatable(BACKGROUND_FIGHT_ENDED_TRANSLATABLE_KEY,
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
