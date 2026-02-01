package com.lildan42.cft.packets;

import com.lildan42.cft.CFT2Mod;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.List;

public record ClientBoundCFTFightStartPacket(String fightId, List<Integer> fighterEntityIds, long fightStartEpochMillis, boolean foregroundFight) implements CustomPayload {
    private static final String PAYLOAD_TYPE_NAME = "client_cft_fight_start";
    public static final CustomPayload.Id<ClientBoundCFTFightStartPacket> PACKET_ID = new CustomPayload.Id<>(CFT2Mod.createModIdentifier(PAYLOAD_TYPE_NAME));

    public static final PacketCodec<ByteBuf, ClientBoundCFTFightStartPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ClientBoundCFTFightStartPacket::fightId,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), ClientBoundCFTFightStartPacket::fighterEntityIds,
            PacketCodecs.LONG, ClientBoundCFTFightStartPacket::fightStartEpochMillis,
            PacketCodecs.BOOLEAN, ClientBoundCFTFightStartPacket::foregroundFight,
            ClientBoundCFTFightStartPacket::new
    );

    @Override
    public Id<ClientBoundCFTFightStartPacket> getId() {
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
