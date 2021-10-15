package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.objects.GroupChat;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

public final class GroupInvitationManager {

    private final Dictionary<UUID, String> groupInvitations = new Hashtable<>();

    public void addInvitation(UUID playerId, String groupName) {
        this.groupInvitations.put(playerId, groupName);
    }
    public void removeInvitation(UUID playerId) {
        this.groupInvitations.remove(playerId);
    }

    public boolean hasInvitation(UUID playerId) {
        return this.groupInvitations.get(playerId) != null;
    }

    public GroupChat getInvitationGroup(UUID playerId) {
        return GroupifyChat.getInstance().getChatGroupsManager().getGroup(this.groupInvitations.get(playerId));
    }
}
