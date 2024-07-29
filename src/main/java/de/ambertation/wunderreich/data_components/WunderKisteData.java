package de.ambertation.wunderreich.data_components;

import de.ambertation.wunderreich.blocks.WunderKisteBlock;
import de.ambertation.wunderreich.utils.WunderKisteDomain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

public record WunderKisteData(WunderKisteDomain domain) {
    public static final Codec<WunderKisteData> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                    WunderKisteDomain.CODEC
                            .optionalFieldOf("domain", WunderKisteBlock.DEFAULT_DOMAIN)
                            .forGetter(WunderKisteData::domain)
            ).apply(instance, WunderKisteData::new)
    );

    public static final StreamCodec<ByteBuf, WunderKisteData> STREAM_CODEC = StreamCodec.of(WunderKisteData::toNetwork, WunderKisteData::fromNetwork);

    private static WunderKisteData fromNetwork(ByteBuf byteBuf) {
        final var domain = WunderKisteDomain.STREAM_CODEC.decode(byteBuf);
        return new WunderKisteData(domain);
    }

    private static void toNetwork(ByteBuf byteBuf, WunderKisteData wunderKisteDomain) {
        WunderKisteDomain.STREAM_CODEC.encode(byteBuf, wunderKisteDomain.domain);
    }
}
