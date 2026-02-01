package com.lildan42.cft.packets.handlers;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.fights.CFTFight;
import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.packets.ClientBoundCFTFightStartPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CFTFightStartPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<@NotNull ClientBoundCFTFightStartPacket> {

    private final ClientCFTFightManager fightManager;

    public CFTFightStartPacketHandler(ClientCFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    @Override
    public void receive(ClientBoundCFTFightStartPacket clientBoundCFTFightStartPacket, ClientPlayNetworking.@NotNull Context context) {
        context.client().execute(() -> {
            UUID fightId = UUID.fromString(clientBoundCFTFightStartPacket.fightId());
            List<CFTFighterEntity> fighters;

            ClientWorld world = context.client().world;

            if (world == null) {
                CFT2Mod.LOGGER.error("CFT fight start packet was ignored due to missing world data");
                return;
            }

            fighters = clientBoundCFTFightStartPacket.fighterEntityIds()
                    .stream().map(world::getEntityById)
                    .filter(e -> e instanceof CFTFighterEntity)
                    .map(e -> (CFTFighterEntity) e)
                    .toList();

            if (fighters.size() != clientBoundCFTFightStartPacket.fighterEntityIds().size()) {
                CFT2Mod.LOGGER.error("CFT fight start packet was ignored due to invalid fighter IDs");
                return;
            }

            Instant fightStartTime = Instant.ofEpochMilli(clientBoundCFTFightStartPacket.fightStartEpochMillis());
            boolean foregroundFight = clientBoundCFTFightStartPacket.foregroundFight();

            CFTFight fight = new CFTFight(fightId, fightStartTime, fighters, this.fightManager);

            this.fightManager.handleFightStart(fight, foregroundFight);
        });
    }
}
