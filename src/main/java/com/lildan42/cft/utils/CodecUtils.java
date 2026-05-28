package com.lildan42.cft.utils;

import com.lildan42.cft.fighterdata.attacks.SmallProjectileAttack;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public class CodecUtils {
    public static final StringIdentifiable.EnumCodec<SmallProjectileAttack.Type> SMALL_PROJECTILE_TYPE_CODEC =
            StringIdentifiable.createCodec(SmallProjectileAttack.Type::values);

    public static final PacketCodec<ByteBuf, SmallProjectileAttack.Type> SMALL_PROJECTILE_TYPE_PACKET_CODEC =
            PacketCodecs.indexed(ValueLists.createIndexToValueFunction(SmallProjectileAttack.Type::ordinal, SmallProjectileAttack.Type.values(),
                    SmallProjectileAttack.Type.SHURIKEN), SmallProjectileAttack.Type::ordinal);
}
