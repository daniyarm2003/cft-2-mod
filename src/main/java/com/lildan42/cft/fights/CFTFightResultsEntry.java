package com.lildan42.cft.fights;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record CFTFightResultsEntry(String name, float remainingHp, float maxHp, int landedAttacks, int totalAttacks) {
    public static final PacketCodec<ByteBuf, CFTFightResultsEntry> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, CFTFightResultsEntry::name,
            PacketCodecs.FLOAT, CFTFightResultsEntry::remainingHp,
            PacketCodecs.FLOAT, CFTFightResultsEntry::maxHp,
            PacketCodecs.INTEGER, CFTFightResultsEntry::landedAttacks,
            PacketCodecs.INTEGER, CFTFightResultsEntry::totalAttacks,
            CFTFightResultsEntry::new
    );

    public float getRemainingHpRatio() {
        return this.remainingHp / this.maxHp;
    }

    public float getStrikingAccuracy() {
        return (float)this.landedAttacks / (float)this.totalAttacks;
    }
}
