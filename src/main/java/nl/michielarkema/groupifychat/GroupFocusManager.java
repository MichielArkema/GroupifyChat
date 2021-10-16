package nl.michielarkema.groupifychat;

import nl.michielarkema.groupifychat.objects.GroupChat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GroupFocusManager {

    private final Map<UUID, String> focusedMap = new HashMap<UUID, String>();

    public void focusGroup(UUID playerId, String groupName) {
        this.focusedMap.put(playerId, groupName);
    }

    public void unFocusGroup(UUID playerId) {
        this.focusedMap.remove(playerId);
    }

    public boolean isFocused(UUID playerId) {
        return this.focusedMap.containsKey(playerId);
    }

    public GroupChat getFocusedGroupChat(UUID playerId) {
        if(!this.isFocused(playerId)) return null;
        String groupName = this.focusedMap.get(playerId);
        return GroupifyChat.getInstance().getChatGroupsManager().getGroup(groupName);
    }
}
