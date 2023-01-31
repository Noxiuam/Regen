package rip.kits.regen.user.task;

import org.bukkit.Bukkit;
import rip.kits.regen.user.User;
import rip.kits.regen.user.UserManager;
import rip.kits.regen.util.CC;

public class FreezeTask implements Runnable {
    @Override
    public void run() {
        UserManager.INSTANCE.getUsers().stream().filter(User::isFrozen).map(User::getPlayerUUID).map(Bukkit::getPlayer).forEach(player -> {
            player.sendMessage(" ");
            player.sendMessage(CC.s("&f████&c█&f████"));
            player.sendMessage(CC.s("&f███&c█&6█&c█&f███ &4&lDo NOT log out!"));
            player.sendMessage(CC.s("&f██&c█&6█&0█&6█&c█&f██ &cIf you do, you will be banned!"));
            player.sendMessage(CC.s("&f██&c█&6█&0█&6█&c█&f██ &3Please download &b&lAnydesk &3& &b&lDiscord."));
            player.sendMessage(CC.s("&f█&c█&6██&0█&6██&c█&f█"));
            player.sendMessage(CC.s("&f█&c█&6█████&c█&f█ &3&ldiscord.gg/hvGDqYPZzt"));
            player.sendMessage(CC.s("&c█&6███&0█&6███&c█ &3&lanydesk.com/en"));
            player.sendMessage(CC.s("&c█████████"));
            player.sendMessage(" ");
        });
    }
}
