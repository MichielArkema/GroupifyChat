package nl.michielarkema.groupifychat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class Events implements Listener {


    @EventHandler
    public void onWordSave(WorldSaveEvent ev) {
        Bukkit.getLogger().info("[GroupifyChat] World save event detected.");
        GroupifyChat.getInstance().getChatGroupsManager().saveData();
    }
}
