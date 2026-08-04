package me.wheelershigley.www.charged.gamerules;

import static me.wheelershigley.www.charged.Charged.MOD_ID;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WashingGameRulePayload(boolean value) implements CustomPacketPayload {
    private static final Identifier booleanIdentifier = Identifier.fromNamespaceAndPath(MOD_ID, "washing_gamerule");
    public static final Type<WashingGameRulePayload> identifier = new Type<>(booleanIdentifier);

    public static final StreamCodec<RegistryFriendlyByteBuf, WashingGameRulePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        WashingGameRulePayload::value,
        WashingGameRulePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return identifier;
    }
}
