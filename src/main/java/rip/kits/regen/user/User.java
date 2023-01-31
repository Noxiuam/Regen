package rip.kits.regen.user;

import lombok.Getter;
import lombok.Setter;
import rip.kits.regen.util.Cooldown;

import java.util.UUID;

@Getter
@Setter
public class User {
    private UUID playerUUID;

    private Cooldown requestCooldown = new Cooldown(0); // Request Cooldowns...
    private boolean soundsEnabled = true, privateMessagesEnabled = true, frozen = false;
    private UUID messagingPlayer = null; // Null since when joining the server he hasnt messaged a player...

    public User(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
}

