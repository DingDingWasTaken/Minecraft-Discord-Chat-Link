package dingdingwastaken.discordchatlink.event;

import dingdingwastaken.discordchatlink.DiscordBot;
import discord4j.core.object.entity.channel.MessageChannel;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;

public class ServerMessageHandler implements ServerMessageEvents.ChatMessage {

    @Override
    public void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        HashSet<MessageChannel> keys = new HashSet<>();
        for (MessageChannel c : DiscordBot.channels.keySet()) {
            if (DiscordBot.channels.get(c) == null || !DiscordBot.channels.get(c)) {
                try {
                    c.createMessage("__" + sender.getName().getString() + "__: " + message.getContent().getString()).block();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    keys.add(c);
                }
            }
        }
        keys.forEach(key -> DiscordBot.channels.remove(key));
    }
}