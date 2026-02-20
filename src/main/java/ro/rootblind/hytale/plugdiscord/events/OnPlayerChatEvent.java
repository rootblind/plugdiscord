package ro.rootblind.hytale.plugdiscord.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class OnPlayerChatEvent {
    public static void onPlayerChat(PlayerChatEvent event) {
        PlayerRef sender = event.getSender();
        // sender.sendMessage(Message.raw(event.getContent()));
    }
}
