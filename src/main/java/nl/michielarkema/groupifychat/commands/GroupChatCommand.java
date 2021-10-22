package nl.michielarkema.groupifychat.commands;

import nl.michielarkema.groupifychat.GroupifyChat;
import nl.michielarkema.groupifychat.managers.GroupChatManager;
import nl.michielarkema.groupifychat.managers.GroupFocusManager;
import nl.michielarkema.groupifychat.managers.GroupInvitationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class GroupChatCommand implements CommandExecutor {

    private final GroupifyChat plugin;

    private final ConfigurationSection helpMessages;
    private final ConfigurationSection errorMessages;

    public GroupChatCommand(GroupifyChat plugin)
    {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();

        this.helpMessages = config.getConfigurationSection("help-messages");
        this.errorMessages = config.getConfigurationSection("error-messages");
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

    private void showHelp(Player player) {

        helpMessages.getValues(false).forEach((x, message)
                -> player.sendMessage(message.toString()));
    }

    private void handleArguments(String[] args, Player player) {
        GroupChatManager chatGroupsManager = this.plugin.getChatGroupsManager();
        GroupInvitationManager groupInvitationManager = this.plugin.getGroupInvitationManager();
        GroupFocusManager groupFocusManager = this.plugin.getGroupFocusManager();

        switch (args[0]) {
            case "create":
                chatGroupsManager.handleCreateCommand(player, args);
                break;
            case "delete":
                chatGroupsManager.handleDeleteCommand(player, args);
                break;
            case "leave":
                chatGroupsManager.handleLeaveCommand(player, args);
                break;
            case "invite":
                groupInvitationManager.handleInvitationCommand(player, args);
                break;
            case "accept":
                groupInvitationManager.handleAcceptCommand(player, args);
                break;
            case "focus":
                groupFocusManager.handleFocusCommand(player, args);
                break;
            case "unfocus":
                groupFocusManager.handleUnFocusCommand(player);
                break;
            case "list":
                chatGroupsManager.handleListCommand(player);
                break;
            case "disband":
                chatGroupsManager.getGroupAdministrationManager().handleDisbandCommand(player);
                break;
            default:
                player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("invalid-command-usage")));
                this.showHelp(player);
                break;
        }
    }
}

