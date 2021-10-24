package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.managers.GroupFocusManager;
import nl.michielarkema.groupifychat.objects.GroupChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.UUID;

public class EventListener implements Listener {

    private final GroupifyChat plugin;
    private final String groupMessage;

    public EventListener(GroupifyChat plugin) {
        this.plugin = plugin;
        this.groupMessage = plugin.getConfig().getConfigurationSection("event-messages").getString("group-message");
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
        String message = GroupifyChat.translateColor(this.groupMessage
                .replace("%group%", groupChat.Settings.Name)
                .replace("%player%", player.getDisplayName())
                .replace("%message%", ev.getMessage()));
        groupChat.sendGroupMessage(message);

        Bukkit.getLogger().info("onPlayerChat yesssss.");
        ev.setCancelled(true);
    }
}
