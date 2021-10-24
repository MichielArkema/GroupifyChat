package nl.michielarkema.groupifychat.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.michielarkema.groupifychat.GroupChatPermissions;
import nl.michielarkema.groupifychat.GroupifyChat;
import nl.michielarkema.groupifychat.objects.GroupChat;
import nl.michielarkema.groupifychat.objects.GroupSettings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public final class GroupChatManager {

    private final GroupifyChat plugin;

    private final ConfigurationSection eventMessages;
    private final ConfigurationSection helpMessages;
    private final ConfigurationSection errorMessages;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File dataFile;

    private HashSet<GroupChat> chatGroups = new HashSet<>();

    public GroupChatManager(GroupifyChat plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();

        this.eventMessages = config.getConfigurationSection("event-messages");
        this.helpMessages = config.getConfigurationSection("help-messages");
        this.errorMessages = config.getConfigurationSection("error-messages");
    }

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

    public List<GroupChat> getPlayerGroups(UUID playerId) {
        return this.chatGroups.stream().filter(g ->g.isOwner(playerId)).collect(Collectors.toList());
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

    public void handleCreateCommand(Player player, String[] args) {
        if(!this.plugin.getChatGroupsManager().hasAccessTo(player, GroupChatPermissions.GROUPCHAT_CREATE)) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("cannot-create-group")));
            return;
        }

        if(args.length < 3){
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-create")));
            return;
        }

        if(this.getPlayerGroups(player.getUniqueId()).size() == this.plugin.getConfig().getInt("max-user-groups")) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("reached-max-groups")));
            return;
        }

        String groupName = args[1];
        String groupDescription = args[2];
        boolean created = this.plugin.getChatGroupsManager().createGroup(groupName, groupDescription, player.getUniqueId());
        if(!created) {
            player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.errorMessages.getString("group-already-exists"))
                    .replace("%group%", groupName)));
            return;
        }
        player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.eventMessages.getString("group-creation-success"))
                .replace("%group%", groupName)));
        //Todo: Perhaps auto focus the player.
    }

    public void handleDeleteCommand(Player player, String[] args) {
        if(!this.hasAccessTo(player, GroupChatPermissions.GROUPCHAT_DELETE)) {
            player.sendMessage(GroupifyChat.translateColor(this.errorMessages.getString("not-allowed")));
            return;
        }
        if(args.length < 2) {
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-delete")));
            return;
        }
        String groupName = args[1];
        GroupChat groupChat = this.getGroup(groupName);
        if(groupChat == null)
        {
            player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.errorMessages.getString("group-not-exists"))
                    .replace("%group%", groupName)));
            return;
        }
        if(!groupChat.canDelete(player)) {
            player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.errorMessages.getString("cannot-delete-group"))
                    .replace("%group%", groupName)));
            return;
        }
        this.removeGroup(groupName);
        player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.eventMessages.getString("group-deleted"))
                .replace("%group%", groupName)));
    }

    public void handleLeaveCommand(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(GroupifyChat.translateColor(this.helpMessages.getString("group-leave")));
            return;
        }
        String groupName = args[1];
        GroupChat groupChat = this.plugin.getChatGroupsManager().getGroup(groupName);
        if(groupChat == null) {
            player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.errorMessages.getString("group-not-exists"))
                    .replace("%group%", groupName)));
            return;
        }
        if(!groupChat.hasMember(player.getUniqueId())) {
            player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.errorMessages.getString("not-in-group"))
                    .replace("%group%", groupName)));
            return;
        }
        if(groupChat.isOwner(player.getUniqueId())) {
            player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.errorMessages.getString("cannot-leave-group-due-ownership"))
                    .replace("%group%", groupName)));
            return;
        }
        this.plugin.getChatGroupsManager().removeGroup(groupName);
        player.sendMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.eventMessages.getString("group-member-left"))
                .replace("%group%", groupName)));
        groupChat.sendGroupMessage(GroupifyChat.translateColor(Objects.requireNonNull(this.eventMessages.getString("group-member-leave"))
                .replace("%member%", player.getDisplayName())));
    }

    public void handleListCommand(Player player) {
        Object[] groups = this.chatGroups.stream()
                .filter(x -> x.hasMember(player.getUniqueId()))
                .toArray();

        player.sendMessage("You are in " + groups.length + " group(s).");
        for (int i = 0; i < groups.length; i++) {
            GroupChat group = (GroupChat)groups[i];
            int number = i + 1;
            String message = MessageFormat.format("{0} - {1} ({2})", number, group.Settings.Name, group.Settings.Description);
            player.sendMessage(message);
        }
    }
}
