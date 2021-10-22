package nl.michielarkema.groupifychat.managers;

import nl.michielarkema.groupifychat.GroupifyChat;
import nl.michielarkema.groupifychat.objects.GroupChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GroupFocusManager {

    private final GroupifyChat plugin;
    private final FileConfiguration config;
    private final Map<UUID, String> focusedMap = new HashMap<UUID, String>();

    private final ConfigurationSection eventMessages;
    private final ConfigurationSection helpMessages;
    private final ConfigurationSection errorMessages;

    public GroupFocusManager(GroupifyChat plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.eventMessages = this.config.getConfigurationSection("event-messages");
        this.helpMessages = this.config.getConfigurationSection("help-messages");
        this.errorMessages = this.config.getConfigurationSection("error-messages");
    }

    public void focusGroup(UUID playerId, String groupName) {
        this.focusedMap.put(playerId, groupName);
    }

    public void unFocusGroup(UUID playerId) {
        this.focusedMap.remove(playerId);
    }

    public boolean isFocused(UUID playerId) {
        return this.focusedMap.containsKey(playerId);
    }

    public GroupChat getFocusedGroupChat(UUID playerId) {
        if(!this.isFocused(playerId)) return null;
        String groupName = this.focusedMap.get(playerId);
        return GroupifyChat.getInstance().getChatGroupsManager().getGroup(groupName);
    }


    public void handleFocusCommand(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-focus")));
            return;
        }
        UUID id = player.getUniqueId();
        String groupName = args[1];
        GroupChat groupChat = this.plugin.getChatGroupsManager().getGroup(groupName);
        if(groupChat == null) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("group-not-exists")));
            return;
        }

        if(!groupChat.hasMember(id)) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("not-in-group")));
            return;
        }
        this.focusedMap.put(id, groupName);
        player.sendMessage(GroupifyChat.translateColor(this.eventMessages.getString("group-focused")
                .replace("%group%", groupName)));
    }
    public void handleUnFocusCommand(Player player) {

    }
}
