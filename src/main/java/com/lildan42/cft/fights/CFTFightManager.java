package com.lildan42.cft.fights;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.packets.ClientBoundCFTFightEndPacket;
import com.lildan42.cft.packets.ClientBoundCFTFightStartPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CFTFightManager {
    private static final File DEFAULT_EXPORT_FILE = new File("fights.csv");

    protected final List<CFTFight> fights = new ArrayList<>();

    private final CFTFightStatsManager statsManager = new CFTFightStatsManager();

    public void startFight(List<CFTFighterEntity> fighters, boolean foregroundFight) {
        if(fighters.size() < 2) {
            throw new IllegalArgumentException("A fight must have at least 2 fighters");
        }

        CFTFight fight = new CFTFight(fighters, this);
        fighters.forEach(fighter -> fighter.setFight(fight));

        this.fights.add(fight);

        ClientBoundCFTFightStartPacket packet = fight.createStartPacket(foregroundFight);

        for(ServerPlayerEntity player : PlayerLookup.tracking(fighters.getFirst())) {
            ServerPlayNetworking.send(player, packet);
        }
    }

    public void finishFight(CFTFight fight, @Nullable CFTFighterEntity winner) {
        this.fights.remove(fight);

        ClientBoundCFTFightEndPacket packet = fight.createEndPacket(winner);

        for(ServerPlayerEntity player : PlayerLookup.tracking(fight.getFighters().getFirst())) {
            ServerPlayNetworking.send(player, packet);
        }
    }

    public CFTFightStatsRecorder getStatsRecorder() {
        return this.statsManager;
    }

    public void recordStats() {
        this.statsManager.exportStats(CFTFightStatsManager.StatsEntryExporter.csv(DEFAULT_EXPORT_FILE));
    }
}
