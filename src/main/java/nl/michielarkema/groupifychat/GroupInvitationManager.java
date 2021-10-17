package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.objects.GroupChat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

public final class GroupInvitationManager {

    private final GroupifyChat plugin;
    private final FileConfiguration config;

    private final ConfigurationSection eventMessages;
    private final ConfigurationSection helpMessages;
    private final ConfigurationSection errorMessages;

    private final Dictionary<UUID, String> groupInvitations = new Hashtable<>();

    public GroupInvitationManager(GroupifyChat plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.eventMessages = this.config.getConfigurationSection("event-messages");
        this.helpMessages = this.config.getConfigurationSection("help-messages");
        this.errorMessages = this.config.getConfigurationSection("error-messages");
    }


    public void addInvitation(UUID playerId, String groupName) {
        this.groupInvitations.put(playerId, groupName);
    }
    public void removeInvitation(UUID playerId) {
        this.groupInvitations.remove(playerId);
    }

    public boolean hasInvitation(UUID playerId) {
        return this.groupInvitations.get(playerId) != null;
    }

    public GroupChat getInvitationGroup(UUID playerId) {
        return GroupifyChat.getInstance().getChatGroupsManager().getGroup(this.groupInvitations.get(playerId));
    }


    public void handleInvitationCommand(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-invite")));
            return;
        }
        GroupChat groupChat = this.plugin.getGroupFocusManager().getFocusedGroupChat(player.getUniqueId());
        if(groupChat == null) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("not-focused")));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if(target == null) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("player-not-found")));
            return;
        }
        if(target.getUniqueId().equals(player.getUniqueId()))
            return;

        if(this.plugin.getGroupInvitationManager().hasInvitation(target.getUniqueId())) {
            //Todo: Send a message that the target player already has incoming invitation.
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("player-has-invitation")
                    .replace("%group%", target.getDisplayName())));
            return;
        }
        //Todo: Handle the group invitation code.
        player.sendMessage(GroupifyChat.translateColor(this.eventMessages.getString("group-invite-sent")
                .replace("%target%", target.getDisplayName())
                .replace("%group%", groupChat.Settings.Name)));

        target.sendMessage(GroupifyChat.translateColor(this.eventMessages.getString("group-invite-received")));
        this.plugin.getGroupInvitationManager().addInvitation(target.getUniqueId(), groupChat.Settings.Name);
    }
}
