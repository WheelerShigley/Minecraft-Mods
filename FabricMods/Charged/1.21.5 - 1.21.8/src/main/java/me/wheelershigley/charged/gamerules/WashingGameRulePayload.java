package me.wheelershigley.charged.gamerules;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static me.wheelershigley.charged.Charged.MOD_ID;

public record WashingGameRulePayload(boolean value) implements CustomPayload {
    private static final Identifier booleanIdentifier = Identifier.of(MOD_ID, "washing_gamerule");
    public static final Id<WashingGameRulePayload> identifier = new Id<>(booleanIdentifier);

    public static final PacketCodec<RegistryByteBuf, WashingGameRulePayload> CODEC = PacketCodec.tuple(
        PacketCodecs.BOOLEAN,
        WashingGameRulePayload::value,
        WashingGameRulePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return identifier;
    }
}
