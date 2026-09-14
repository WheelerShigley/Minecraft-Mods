package me.wheelershigley.www.window.api;

import com.mojang.serialization.Codec;

public enum LinkType {
    BIDIRECTIONAL,       // to <-> from
    MONODIRECTIONAL,     // to  -> from
    MONODIRECTIONAL_RTP, // to -> rtp(from)
    SINGLE;              // to -> specific_location

    public static final Codec<LinkType> CODEC =
        Codec.STRING.xmap(
            LinkType::valueOf,
            LinkType::name
        )
    ;

    public boolean overlaps(LinkType other) {
        return switch(this) {
            case BIDIRECTIONAL ->
                true
            ;
            case MONODIRECTIONAL, MONODIRECTIONAL_RTP ->
                other == MONODIRECTIONAL
                || other == MONODIRECTIONAL_RTP
                || other == SINGLE
            ;
            case SINGLE ->
                other == SINGLE
            ;
        };
    }

    @Override
    public String toString() {
        return switch(this) {
            case BIDIRECTIONAL -> "⇄";
            case MONODIRECTIONAL -> "⥤";
            case MONODIRECTIONAL_RTP -> "⥤*";
            case SINGLE -> "⇌";
        };
    }
}
