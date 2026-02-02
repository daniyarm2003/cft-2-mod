package com.lildan42.cft.packets.handlers;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.packets.ClientBoundCFTFightEndPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CFTFightEndPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<@org.jetbrains.annotations.NotNull ClientBoundCFTFightEndPacket> {

    private final ClientCFTFightManager fightManager;

    public CFTFightEndPacketHandler(ClientCFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    @Override
    public void receive(ClientBoundCFTFightEndPacket payload, ClientPlayNetworking.@NotNull Context context) {
        context.client().execute(() -> {
            UUID fightId = UUID.fromString(payload.fightId());
            ClientWorld world = context.client().world;

            if(world == null) {
                CFT2Mod.LOGGER.error("CFT fight end packet was ignored due to missing world data");
                return;
            }

            Integer winnerId = payload.winnerId().isPresent() ? payload.winnerId().getAsInt() : null;
            this.fightManager.handleFightEnd(fightId, winnerId, context.player(), context.client());
        });
    }
}
