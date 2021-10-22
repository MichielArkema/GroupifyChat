package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.managers.GroupFocusManager;
import nl.michielarkema.groupifychat.objects.GroupChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.text.MessageFormat;
import java.util.UUID;

public class EventListener implements Listener {

    private final GroupifyChat plugin;

    public EventListener(GroupifyChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWordSave(WorldSaveEvent ev) {
        Bukkit.getLogger().info("[GroupifyChat] World save event detected.");
        GroupifyChat.getInstance().getChatGroupsManager().saveData();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent ev) {
        Player player = ev.getPlayer();
        UUID id = player.getUniqueId();

        GroupFocusManager groupFocusManager = this.plugin.getGroupFocusManager();
        if(!groupFocusManager.isFocused(id))
            return;

        GroupChat groupChat = groupFocusManager.getFocusedGroupChat(id);
        if(groupChat == null) {
            groupFocusManager.unFocusGroup(id);
            return;
        }
        String message = MessageFormat.format("[{0}] {1}: {2}", groupChat.Settings.Name, player.getDisplayName(), ev.getMessage());
        groupChat.sendGroupMessage(message);

        ev.setCancelled(true);
    }
}
