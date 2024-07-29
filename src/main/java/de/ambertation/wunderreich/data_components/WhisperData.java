package de.ambertation.wunderreich.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import io.netty.buffer.ByteBuf;

public record WhisperData(ResourceKey<Enchantment> enchantmentKey) {
    private static final Codec<ResourceKey<Enchantment>> KEY_CODEC = ResourceKey.codec(Registries.ENCHANTMENT);
    private static final StreamCodec<ByteBuf, ResourceKey<Enchantment>> STREAM_KEY_CODEC = ResourceKey.streamCodec(Registries.ENCHANTMENT);

    public static final Codec<WhisperData> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                    KEY_CODEC
                            .fieldOf("id")
                            .forGetter(WhisperData::enchantmentKey)
            ).apply(instance, WhisperData::new)
    );

    public static final StreamCodec<ByteBuf, WhisperData> STREAM_CODEC = StreamCodec.of(WhisperData::toNetwork, WhisperData::fromNetwork);

    private static WhisperData fromNetwork(ByteBuf byteBuf) {
        return new WhisperData(STREAM_KEY_CODEC.decode(byteBuf));
    }

    private static void toNetwork(ByteBuf byteBuf, WhisperData whisperData) {
        STREAM_KEY_CODEC.encode(byteBuf, whisperData.enchantmentKey);
    }
}
