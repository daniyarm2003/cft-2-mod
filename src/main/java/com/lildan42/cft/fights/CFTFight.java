package com.lildan42.cft.fights;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.packets.ClientBoundCFTFightEndPacket;
import com.lildan42.cft.packets.ClientBoundCFTFightStartPacket;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CFTFight {
    private final UUID id;
    private final Instant startTime;

    private final List<CFTFighterEntity> fighters;
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
        this.fightManager = fightManager;
    }

    public UUID getId() {
        return this.id;
    }

    public Duration getElapsedTime() {
        Instant now = Instant.now();
        return Duration.between(this.startTime, now);
    }

    public void onFighterDefeat(CFTFighterEntity entity) {
        this.fighters.removeIf(other -> other.getId() == entity.getId());
        this.fightManager.getStatsRecorder().reportLoss(entity.getFighterData());

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
