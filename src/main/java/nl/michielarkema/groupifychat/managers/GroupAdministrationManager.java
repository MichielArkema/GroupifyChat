package nl.michielarkema.groupifychat.managers;

import nl.michielarkema.groupifychat.GroupifyChat;
import org.bukkit.entity.Player;

public final class GroupAdministrationManager {

    private final GroupifyChat plugin;
    private final GroupChatManager groupChatManager;

    public GroupAdministrationManager( GroupifyChat plugin, GroupChatManager groupChatManager) {
        this.plugin = plugin;
        this.groupChatManager = groupChatManager;
    }

    public void handleDisbandCommand(Player player) {
        //Todo Handle group disband command.
        GroupFocusManager focusManager = this.plugin.getGroupFocusManager();
        if(!focusManager.isFocused(player.getUniqueId())) {

            return;
        }
    }
}
