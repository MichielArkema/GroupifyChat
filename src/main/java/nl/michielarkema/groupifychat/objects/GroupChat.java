package nl.michielarkema.groupifychat.objects;

import nl.michielarkema.groupifychat.GroupChatPermissions;
import nl.michielarkema.groupifychat.GroupifyChat;
import nl.michielarkema.groupifychat.managers.GroupFocusManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public final class GroupChat {

    public final GroupSettings Settings;
    public final HashSet<UUID> Members;

    public GroupChat(GroupSettings settings) {
        this.Settings = settings;
        this.Members = new HashSet<>(GroupifyChat.getInstance().Config.getInt("max-group-members"));
        this.Members.add(settings.MasterAdminUUID);
    }

    public boolean hasMember(UUID memberUUID) {
        return this.Members.contains(memberUUID);
    }

    public void addMember(UUID memberUUID) {
        this.Members.add(memberUUID);
    }

    public boolean removeMember(UUID memberUUID) {
        return this.Members.remove(memberUUID);
    }

    public boolean canDelete(Player player) {
        return player.isOp()
                || this.isOwner(player.getUniqueId())
                || player.hasPermission(GroupChatPermissions.GROUPCHAT_DELETE);
    }


    public void focusMember(UUID id) {
        if(!this.hasMember(id))
            return;
    }


    //Todo: This method contains new code that still needs to get tested.
    public void sendGroupMessage(String message) {
        GroupFocusManager focusManager = GroupifyChat.getInstance().getGroupFocusManager();

        for (UUID uuid : this.Members) {
            Player member = Bukkit.getPlayer(uuid);
            Bukkit.getLogger().info("sendGroupMessage 1.");
            if(member == null)
                continue;
            Bukkit.getLogger().info("sendGroupMessage 2.");
            if(!focusManager.isFocusedOn(uuid, this.Settings.Name))
                continue;

            member.sendMessage(message);
            Bukkit.getLogger().info("sendGroupMessage yesssss.");
        }
    }

    public boolean isOwner(UUID playerID) {
        return this.Settings.MasterAdminUUID.equals(playerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.Settings.Name);
    }

}

