package com.lildan42.cft.fights;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.packets.ClientBoundCFTFightEndPacket;
import com.lildan42.cft.packets.ClientBoundCFTFightResultsPacket;
import com.lildan42.cft.packets.ClientBoundCFTFightStartPacket;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CFTFight {
    private final UUID id;
    private final Instant startTime;

    private final List<CFTFighterEntity> fighters;
    private final Map<Integer, Integer> fighterIdToIndexMap;
    private final List<CFTFightResultsEntry> fighterStats;

    private final CFTFightManager fightManager;

    public CFTFight(List<CFTFighterEntity> fighters, CFTFightManager fightManager) {
        this(Instant.now(), fighters, fightManager);
    }

    public CFTFight(Instant startTime, List<CFTFighterEntity> fighters, CFTFightManager fightManager) {
        this(UUID.randomUUID(), startTime, fighters, fightManager);
    }

    public CFTFight(UUID id, Instant startTime, List<CFTFighterEntity> fighters, CFTFightManager fightManager) {
        this.id = id;
        this.startTime = startTime;

        this.fighters = new ArrayList<>(fighters);
        this.fighterIdToIndexMap = IntStream.range(0, this.fighters.size()).boxed()
                .collect(Collectors.toMap(i -> this.fighters.get(i).getId(), Function.identity()));

        this.fighterStats = new ArrayList<>(Collections.nCopies(this.fighters.size(), null));

        this.fightManager = fightManager;
    }

    public UUID getId() {
        return this.id;
    }

    public Duration getElapsedTime() {
        Instant now = Instant.now();
        return Duration.between(this.startTime, now);
    }

    private void addFightResults(CFTFighterEntity fighter) {
        Integer fighterStatsIndex = this.fighterIdToIndexMap.get(fighter.getId());

        if(fighterStatsIndex == null) {
            throw new IllegalArgumentException("Invalid state: attempted to update fight results for an unregistered fighter");
        }

        this.fighterStats.set(fighterStatsIndex, fighter.getFightResults());
    }

    private List<CFTFightResultsEntry> getFightResults() {
        return List.copyOf(this.fighterStats);
    }

    public void onFighterDefeat(CFTFighterEntity entity) {
        this.fighters.removeIf(other -> other.getId() == entity.getId());
        this.fightManager.getStatsRecorder().reportLoss(entity.getFighterData());

        this.addFightResults(entity);

        if(this.fighters.size() == 1) {
            this.onFightEnd(this.fighters.getFirst());
        }
    }

    public void onFightEnd(@Nullable CFTFighterEntity winner) {
        this.fighters.forEach(fighter -> fighter.setFight(null));

        if(winner != null) {
            this.fightManager.getStatsRecorder().reportWin(winner.getFighterData());
        }

        this.fightManager.finishFight(this, winner);
    }

    public Optional<CFTFighterEntity> getFighterById(int id) {
        return this.fighters.stream().filter(fighter -> fighter.getId() == id).findFirst();
    }

    public List<CFTFighterEntity> getFighters() {
        return List.copyOf(this.fighters);
    }

    public ClientBoundCFTFightStartPacket createStartPacket(boolean foregroundFight) {
        return new ClientBoundCFTFightStartPacket(this.id.toString(), this.fighters.stream().map(CFTFighterEntity::getId).toList(), this.startTime.toEpochMilli(), foregroundFight);
    }

    public ClientBoundCFTFightEndPacket createEndPacket(@Nullable CFTFighterEntity winner) {
        return new ClientBoundCFTFightEndPacket(this.id.toString(), winner != null ? OptionalInt.of(winner.getId()) : OptionalInt.empty());
    }

    public ClientBoundCFTFightResultsPacket createResultsPacket(CFTFighterEntity winner) {
        this.addFightResults(winner);
        return new ClientBoundCFTFightResultsPacket(this.id.toString(), this.getFightResults());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CFTFight cftFight = (CFTFight) o;
        return Objects.equals(getId(), cftFight.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
