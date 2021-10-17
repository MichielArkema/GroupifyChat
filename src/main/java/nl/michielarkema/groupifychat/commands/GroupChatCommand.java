package nl.michielarkema.groupifychat.commands;

import nl.michielarkema.groupifychat.GroupChatPermissions;
import nl.michielarkema.groupifychat.GroupifyChat;
import nl.michielarkema.groupifychat.objects.GroupChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class GroupChatCommand implements CommandExecutor {

    private final GroupifyChat plugin;
    private final FileConfiguration config;

    private final ConfigurationSection eventMessages;
    private final ConfigurationSection helpMessages;
    private final ConfigurationSection errorMessages;

    public GroupChatCommand(GroupifyChat plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.eventMessages = this.config.getConfigurationSection("event-messages");
        this.helpMessages = this.config.getConfigurationSection("help-messages");
        this.errorMessages = this.config.getConfigurationSection("error-messages");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        Player player = (Player)sender;
        if(args.length == 0) {
            this.showHelp(player);
            return true;
        }
        handleArguments(args, player);
        return true;
    }

    private void groupCreate(Player player, String[] args) {

        if(!this.plugin.getChatGroupsManager().hasAccessTo(player, GroupChatPermissions.GROUPCHAT_CREATE)) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("cannot-create-group")));
            return;
        }

        if(args.length < 3){
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-create")));
            return;
        }
        String groupName = args[1];
        String groupDescription = args[2];
        boolean created = this.plugin.getChatGroupsManager().createGroup(groupName, groupDescription, player.getUniqueId());
        if(!created) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("group-already-exists")
                    .replace("%group%", groupName)));
            return;
        }
        player.sendMessage(GroupifyChat.translateColor(this.eventMessages.getString("group-creation-success")
                .replace("%group%", groupName)));
        //Todo: Perhaps auto focus the player.
    }

    private void deleteGroup(Player player, String[] args) {

        if(!this.plugin.getChatGroupsManager().hasAccessTo(player, GroupChatPermissions.GROUPCHAT_DELETE)) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("not-allowed")));
            return;
        }

        if(args.length < 2) {
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-delete")));
            return;
        }
        String groupName = args[1];
        GroupChat groupChat = this.plugin.getChatGroupsManager().getGroup(groupName);
        if(groupChat == null)
        {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("group-not-exists")
                    .replace("%group%", groupName)));
            return;
        }
        if(!groupChat.canModify(player)) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("cannot-delete-group")
                    .replace("%group%", groupName)));
            return;
        }
        this.plugin.getChatGroupsManager().removeGroup(groupName);
        player.sendMessage(GroupifyChat.translateColor(this.eventMessages.getString("group-deleted")
                .replace("%group%", groupName)));
    }

    private void leaveGroup(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-leave")));
            return;
        }
        String groupName = args[1];
        GroupChat groupChat = this.plugin.getChatGroupsManager().getGroup(groupName);
        if(groupChat == null) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("group-not-exists")
                    .replace("%group%", groupName)));
            return;
        }
        if(!groupChat.hasMember(player.getUniqueId())) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("not-in-group")
                    .replace("%group%", groupName)));
            return;
        }
        if(groupChat.isOwner(player.getUniqueId())) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("cannot-leave-group-due-ownership")
                    .replace("%group%", groupName)));
            return;
        }
        this.plugin.getChatGroupsManager().removeGroup(groupName);
        player.sendMessage(GroupifyChat.translateColor(this.eventMessages.getString("group-member-left")
                .replace("%group%", groupName)));
        groupChat.sendGroupMessage(GroupifyChat.translateColor(this.eventMessages.getString("group-member-leave")
                .replace("%member%", player.getDisplayName())));
    }

    private void inviteGroup(Player player, String[] args) {
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

    private void acceptGroup(Player player, String[] args) {
    }

    private void promoteMember(Player player, String[] args) {
    }

    private void focusGroup(Player player, String[] args) {
    }

    private void unFocusGroup(Player player) {
    }

    private void listGroups(Player player) {
    }

    private void showHelp(Player player) {

        helpMessages.getValues(false).forEach((x, message) -> {
            player.sendMessage(message.toString());
        });
    }

    private void handleArguments(String[] args, Player player) {
        switch (args[0]) {
            case "create":
                this.groupCreate(player, args);
                break;
            case "invite":
                this.inviteGroup(player, args);
                break;
            case "accept":
                this.acceptGroup(player, args);
                break;
            case "delete":
                this.deleteGroup(player, args);
                break;
            case "leave":
                this.leaveGroup(player, args);
                break;
            case "promote":
                this.promoteMember(player, args);
                break;
            case "focus":
                this.focusGroup(player, args);
                break;
            case "unfocus":
                this.unFocusGroup(player);
                break;
            case "list":
                this.listGroups(player);
                break;
            default:
                player.sendMessage("Invalid command usage!");
                this.showHelp(player);
                break;
        }
    }
}

