package com.lildan42.cft.packets;

import com.lildan42.cft.CFT2Mod;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.OptionalInt;

public record ClientBoundCFTFightEndPacket(String fightId, OptionalInt winnerId) implements CustomPayload {
    private static final String PAYLOAD_TYPE_NAME = "client_cft_fight_end";
    public static final CustomPayload.Id<ClientBoundCFTFightEndPacket> PACKET_ID = new CustomPayload.Id<>(CFT2Mod.createModIdentifier(PAYLOAD_TYPE_NAME));

    public static final PacketCodec<ByteBuf, ClientBoundCFTFightEndPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ClientBoundCFTFightEndPacket::fightId,
            PacketCodecs.OPTIONAL_INT, ClientBoundCFTFightEndPacket::winnerId,
            ClientBoundCFTFightEndPacket::new
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
