package de.ambertation.wunderreich.network;

import de.ambertation.wunderlib.network.ServerBoundNetworkPayload;
import de.ambertation.wunderlib.network.ServerBoundPacketHandler;
import de.ambertation.wunderreich.Wunderreich;
import de.ambertation.wunderreich.gui.whisperer.WhispererMenu;
import de.ambertation.wunderreich.recipes.ImprinterRecipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

public class SelectWhisperMessage extends ServerBoundNetworkPayload<SelectWhisperMessage> {
    public static final ServerBoundPacketHandler<SelectWhisperMessage> HANDLER = new ServerBoundPacketHandler<>(
            Wunderreich.ID("select_whisper"),
            SelectWhisperMessage::new
    );
    public final ResourceLocation ruleID;

    protected SelectWhisperMessage(FriendlyByteBuf buf) {
        super(HANDLER);
        final boolean isNull = buf.readBoolean();
        this.ruleID = isNull ? null : ResourceLocation.STREAM_CODEC.decode(buf);
    }

    protected SelectWhisperMessage(ResourceLocation ruleID) {
        super(HANDLER);
        this.ruleID = ruleID;
    }

    public static void send(ResourceLocation ruleID) {
        ServerBoundPacketHandler.sendToServer(new SelectWhisperMessage(ruleID));
    }

    public static void send(ImprinterRecipe rule) {
        send(rule == null ? null : rule.id);
    }

    @Override
    protected void prepareOnClient() {

    }

    @Override
    protected void write(FriendlyByteBuf buf) {
        final boolean isNull = this.ruleID == null;
        buf.writeBoolean(isNull);
        if (!isNull) {
            ResourceLocation.STREAM_CODEC.encode(buf, this.ruleID);
        }
    }

    @Override
    protected void processOnServer(ServerPlayer player, PacketSender responseSender) {

    }

    @Override
    protected void processOnGameThread(MinecraftServer server, ServerPlayer player) {
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;

        if (abstractContainerMenu instanceof WhispererMenu menu) {
            Wunderreich.LOGGER.info("Selecting whisperer recipe: " + this.ruleID);
            ImprinterRecipe selected = menu.selectByID(this.ruleID);
            menu.tryMoveItems(selected);
        }
    }
}
