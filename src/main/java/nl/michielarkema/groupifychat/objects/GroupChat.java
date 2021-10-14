package nl.michielarkema.groupifychat.objects;

import nl.michielarkema.groupifychat.GroupifyChat;
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
        if(this.hasMember(memberUUID))
            return;
        this.Members.add(memberUUID);
    }

    public boolean canModify(Player player) {
        return player.isOp()
                || this.isOwner(player.getUniqueId());
                //|| GroupifyChat.getInstance().getChatGroupsManager().canDelete(this, player);
    }

    public boolean isOwner(UUID playerID) {
        return this.Settings.MasterAdminUUID == playerID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.Settings.Name);
    }


}

