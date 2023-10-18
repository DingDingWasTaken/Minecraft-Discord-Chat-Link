package dingdingwastaken.discordchatlink.event;

import dingdingwastaken.discordchatlink.ServerInit;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class EndTickHandler implements ServerTickEvents.EndTick {
    @Override
    public void onEndTick(MinecraftServer server) {
        if (ServerInit.SERVER == null) ServerInit.SERVER = server;
    }
}
