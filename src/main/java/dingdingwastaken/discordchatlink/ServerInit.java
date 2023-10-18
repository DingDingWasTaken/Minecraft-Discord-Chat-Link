package dingdingwastaken.discordchatlink;

import dingdingwastaken.discordchatlink.commands.RegisterBotClass;
import dingdingwastaken.discordchatlink.event.EndTickHandler;
import dingdingwastaken.discordchatlink.event.ServerMessageHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;

public class ServerInit implements ModInitializer {
    public static MinecraftServer SERVER;
    @Override
    public void onInitialize() {
        ServerMessageEvents.CHAT_MESSAGE.register(new ServerMessageHandler());
        ServerTickEvents.END_SERVER_TICK.register(new EndTickHandler());
        CommandRegistrationCallback.EVENT.register(RegisterBotClass::register);
    }
}
