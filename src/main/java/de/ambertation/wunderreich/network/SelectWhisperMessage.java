package de.ambertation.wunderreich.network;

import de.ambertation.wunderlib.network.ServerBoundNetworkPayload;
import de.ambertation.wunderlib.network.ServerBoundPacketHandler;
import de.ambertation.wunderreich.Wunderreich;
import de.ambertation.wunderreich.gui.whisperer.WhispererMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

public class SelectWhisperMessage extends ServerBoundNetworkPayload<SelectWhisperMessage> {
    public static final ServerBoundPacketHandler<SelectWhisperMessage> HANDLER = new ServerBoundPacketHandler<>(
            Wunderreich.ID("select_whisper"),
            SelectWhisperMessage::new
    );
    public final int itemIndex;

    protected SelectWhisperMessage(FriendlyByteBuf buf) {
        super(HANDLER);
        this.itemIndex = buf.readVarInt();
    }

    protected SelectWhisperMessage(int itemIndex) {
        super(HANDLER);
        this.itemIndex = itemIndex;
    }

    public static void send(int itemIndex) {
        ServerBoundPacketHandler.sendToServer(new SelectWhisperMessage(itemIndex));
    }

    @Override
    protected void prepareOnClient() {

    }

    @Override
    protected void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.itemIndex);
    }

    @Override
    protected void processOnServer(ServerPlayer player, PacketSender responseSender) {

    }

    @Override
    protected void processOnGameThread(MinecraftServer server, ServerPlayer player) {
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;

        if (abstractContainerMenu instanceof WhispererMenu menu) {
            menu.setSelectionHint(this.itemIndex);
            menu.tryMoveItems(this.itemIndex);
        }
    }
}
