package com.lildan42.cft.utils;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class CodecUtils {
    public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> enumClass) {
        return Codec.stringResolver(T::toString, str -> Enum.valueOf(enumClass, str));
    }

    public static <T extends Enum<T>> PacketCodec<ByteBuf, T> enumPacketCodec(T[] values) {
        return PacketCodecs.indexed(index -> values[index], T::ordinal);
    }
}
