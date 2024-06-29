package de.ambertation.wunderreich.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import org.jetbrains.annotations.NotNull;

public abstract class NetworkPayload<T extends NetworkPayload<T>> implements CustomPacketPayload {
    protected final PacketHandler<T> descriptor;

    protected NetworkPayload(PacketHandler<T> desc) {
        this.descriptor = desc;
    }

    protected abstract void write(FriendlyByteBuf buf);

    @Override
    public final @NotNull Type<T> type() {
        return this.descriptor.CHANNEL;
    }
}
