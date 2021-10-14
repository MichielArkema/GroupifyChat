package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.commands.GroupChatCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class GroupifyChat extends JavaPlugin {

    private static GroupifyChat instance;

    public FileConfiguration Config = this.getConfig();
    private GroupChatManager chatGroupsManager;

    public static GroupifyChat getInstance() {
        return instance;
    }


    public static String translateColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.chatGroupsManager = new GroupChatManager();
        this.chatGroupsManager.loadData();

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
}
