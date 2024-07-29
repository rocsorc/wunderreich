package de.ambertation.wunderreich.network;

import de.ambertation.wunderlib.network.ServerBoundPacketHandler;

public abstract class ServerBoundNetworkHandlers {
    public static void register() {
        ServerBoundPacketHandler.register(AddRemoveWunderKisteMessage.HANDLER);
        ServerBoundPacketHandler.register(CycleTradesMessage.HANDLER);
        ServerBoundPacketHandler.register(SelectWhisperMessage.HANDLER);
    }
}
