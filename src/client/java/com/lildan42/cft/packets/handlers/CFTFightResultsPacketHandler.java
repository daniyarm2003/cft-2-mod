package com.lildan42.cft.packets.handlers;

import com.lildan42.cft.fights.CFTFightResultsEntry;
import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.packets.ClientBoundCFTFightResultsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class CFTFightResultsPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<@org.jetbrains.annotations.NotNull ClientBoundCFTFightResultsPacket> {
    private final ClientCFTFightManager fightManager;

    public CFTFightResultsPacketHandler(ClientCFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    @Override
    public void receive(ClientBoundCFTFightResultsPacket payload, ClientPlayNetworking.@NotNull Context context) {
        UUID fightId = UUID.fromString(payload.fightId());

        if(!this.fightManager.isPreviousForegroundFight(fightId)) {
            return;
        }

        List<CFTFightResultsEntry> fightResults = payload.fightResults();
        context.client().execute(() -> this.fightManager.handleFightResults(fightResults, context.client()));
    }
}
