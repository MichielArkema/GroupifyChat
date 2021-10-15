package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.commands.GroupChatCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class GroupifyChat extends JavaPlugin {

    private static GroupifyChat instance;

    public FileConfiguration Config;
    private GroupChatManager chatGroupsManager;
    private GroupInvitationManager groupInvitationManager;


    public static String translateColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.Config = this.getConfig();

        this.chatGroupsManager = new GroupChatManager();
        this.chatGroupsManager.loadData();

        this.groupInvitationManager = new GroupInvitationManager();

        this.getServer().getPluginManager().registerEvents(new Events(), this);

        this.getCommand("gc").setExecutor(new GroupChatCommand(this));
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

    public static GroupifyChat getInstance() {
        return instance;
    }
}
