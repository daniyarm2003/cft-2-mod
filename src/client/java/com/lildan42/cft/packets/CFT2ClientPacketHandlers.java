package com.lildan42.cft.packets;

import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.initialization.CFT2Initializer;
import com.lildan42.cft.packets.handlers.CFTFightEndPacketHandler;
import com.lildan42.cft.packets.handlers.CFTFightStartPacketHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class CFT2ClientPacketHandlers implements CFT2Initializer, ClientPlayConnectionEvents.Init {
    private final ClientCFTFightManager fightManager;

    public CFT2ClientPacketHandlers(ClientCFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    private void registerPayloads() {
        ClientBoundCFTFightStartPacket.registerServerToClient();
        ClientBoundCFTFightEndPacket.registerServerToClient();
    }

    private void registerPayloadHandlers() {
        ClientPlayNetworking.registerReceiver(ClientBoundCFTFightStartPacket.PACKET_ID,
                new CFTFightStartPacketHandler(this.fightManager));

        ClientPlayNetworking.registerReceiver(ClientBoundCFTFightEndPacket.PACKET_ID,
                new CFTFightEndPacketHandler(this.fightManager));
    }

    @Override
    public String getInitializationStageName() {
        return "Client packet handler registration";
    }

    @Override
    public void initialize(Logger logger) {
        this.registerPayloads();
        ClientPlayConnectionEvents.INIT.register(this);
    }

    @Override
    public void onPlayInit(@NotNull ClientPlayNetworkHandler handler, @NotNull MinecraftClient client) {
        this.registerPayloadHandlers();
    }
}
