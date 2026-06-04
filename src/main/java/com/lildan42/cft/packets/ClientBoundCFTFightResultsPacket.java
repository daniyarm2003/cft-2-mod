package com.lildan42.cft.packets;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fights.CFTFightResultsEntry;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record ClientBoundCFTFightResultsPacket(String fightId, List<CFTFightResultsEntry> fightResults) implements CustomPayload {
    private static final String PAYLOAD_TYPE_NAME = "client_cft_fight_results";
    public static final CustomPayload.Id<ClientBoundCFTFightResultsPacket> PACKET_ID = new CustomPayload.Id<>(CFT2Mod.createModIdentifier(PAYLOAD_TYPE_NAME));

    public static final PacketCodec<ByteBuf, ClientBoundCFTFightResultsPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ClientBoundCFTFightResultsPacket::fightId,
            CFTFightResultsEntry.PACKET_CODEC.collect(PacketCodecs.toList()), ClientBoundCFTFightResultsPacket::fightResults,
            ClientBoundCFTFightResultsPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static void registerServerToClient() {
        try {
            PayloadTypeRegistry.playS2C().register(PACKET_ID, PACKET_CODEC);
        }
        catch(Exception ignored) {

        }
    }
}
