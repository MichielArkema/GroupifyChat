package nl.michielarkema.groupifychat.objects;

import java.util.UUID;


public final class GroupSettings {
    
    public final String Name;
    public final String Description;
    public final UUID MasterAdminUUID;

    public GroupSettings(String name, String description, UUID masterAdminUUID) {
        this.Name = name;
        this.Description = description;
        this.MasterAdminUUID = masterAdminUUID;
    }
}
