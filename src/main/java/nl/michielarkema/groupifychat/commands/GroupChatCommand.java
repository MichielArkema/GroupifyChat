package nl.michielarkema.groupifychat.commands;

import nl.michielarkema.groupifychat.GroupifyChat;
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

    private void showHelp(Player player) {

        helpMessages.getValues(false).forEach((x, message) -> {
            player.sendMessage(message.toString());
        });
    }

    private void handleArguments(String[] args, Player player) {
        switch (args[0]) {
            case "create":
                this.plugin.getChatGroupsManager().handleCreateCommand(player, args);
                break;
            case "delete":
                this.plugin.getChatGroupsManager().handleDeleteCommand(player, args);
                break;
            case "leave":
                this.plugin.getChatGroupsManager().handleLeaveCommand(player, args);
                break;
            case "invite":
                this.plugin.getGroupInvitationManager().handleInvitationCommand(player, args);
                break;
            case "accept":
                this.plugin.getGroupInvitationManager().handleAcceptCommand(player, args);
                break;
            case "focus":
                this.plugin.getGroupFocusManager().handleFocusCommand(player, args);
                break;
            case "unfocus":
                this.plugin.getGroupFocusManager().handleUnFocusCommand(player);
                break;
            case "list":
                this.plugin.getChatGroupsManager().handleListCommand(player);
                break;
            case "disband":
                this.plugin.getChatGroupsManager().getGroupAdministrationManager().handleDisbandCommand(player);
                break;
            default:
                player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("invalid-command-usage")));
                this.showHelp(player);
                break;
        }
    }
}

