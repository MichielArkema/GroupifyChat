package nl.michielarkema.groupifychat.objects;

import nl.michielarkema.groupifychat.GroupifyChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GroupInvitation {

    private final Player player;
    private final GroupChat groupChat;

    public GroupInvitation(Player player, GroupChat groupChat) {
        this.player = player;
        this.groupChat = groupChat;
    }
}
