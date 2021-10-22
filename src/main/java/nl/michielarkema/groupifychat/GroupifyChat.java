package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.commands.GroupChatCommand;
import nl.michielarkema.groupifychat.managers.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class GroupifyChat extends JavaPlugin {

    private static GroupifyChat instance;

    public FileConfiguration Config;

    private GroupChatManager chatGroupsManager;
    private GroupInvitationManager groupInvitationManager;
    private GroupFocusManager groupFocusManager;

    public static String translateColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.Config = this.getConfig();

        this.chatGroupsManager = new GroupChatManager(this);
        this.chatGroupsManager.loadData();

        this.groupInvitationManager = new GroupInvitationManager(this);

        this.groupFocusManager = new GroupFocusManager(this);

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        Objects.requireNonNull(this.getCommand("gc")).setExecutor(new GroupChatCommand(this));
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public GroupChatManager getChatGroupsManager() {
        return chatGroupsManager;
    }
    public GroupInvitationManager getGroupInvitationManager() {
        return groupInvitationManager;
    }
    public GroupFocusManager getGroupFocusManager() {
        return groupFocusManager;
    }

    public static GroupifyChat getInstance() {
        return instance;
    }
}
