package nl.michielarkema.groupifychat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.michielarkema.groupifychat.objects.GroupChat;
import nl.michielarkema.groupifychat.objects.GroupSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public final class GroupChatManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File dataFile;

    private HashSet<GroupChat> chatGroups = new HashSet<>();


    public boolean createGroup(String name, String description, UUID creatorUUID) {
        if(this.hasGroup(name))
            return false;

        GroupSettings settings = new GroupSettings(name, description, creatorUUID);
        GroupChat group = new GroupChat(settings);
        this.chatGroups.add(group);
        return true;
    }

    public boolean removeGroup(String name) {
        if(!this.hasGroup(name))
            return false;
        this.chatGroups.remove(this.getGroup(name));
        return true;
    }

    public GroupChat getGroup(String name) {
        return this.chatGroups.stream()
                .filter(x -> x.Settings.Name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean hasGroup(String name) {
        return this.chatGroups.stream().anyMatch(x -> x.Settings.Name.equals(name));
    }

    public boolean hasAccessTo(Player player, String permission) {
        if(GroupifyChat.getInstance().getConfig().getBoolean("allow-players")) {
            return true;
        }
        return player.isOp() || player.hasPermission(permission);
    }



    public void loadData() {
        try
        {
            dataFile = new File(GroupifyChat.getInstance().getDataFolder(), "chat-groups.json");
            if(!dataFile.exists()) {
                dataFile.createNewFile();
                //Lets save the empty hashset to avoid parsing errors later on.
                this.saveData();
                return;
            }
            String contents = Files.readString(dataFile.toPath());
            this.chatGroups = new HashSet<>(List.of(gson.fromJson(contents, GroupChat[].class)));
        }
        catch (Exception ex) {
            Bukkit.getLogger().warning("[GroupifyChat] Failed to load group data due to exception: " + ex);
        }
    }

    public void saveData() {
        try {
            String json = gson.toJson(this.chatGroups);
            Files.writeString(dataFile.toPath(), json);
        }
       catch (Exception ex) {
           Bukkit.getLogger().warning("[GroupifyChat] Failed to save group data due to exception: " + ex);
       }
    }
}
