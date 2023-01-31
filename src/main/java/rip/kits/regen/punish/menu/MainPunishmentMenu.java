package rip.kits.regen.punish.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.kits.regen.Regen;
import rip.kits.regen.punish.Punishment;
import rip.kits.regen.punish.util.PunishmentTypes;

import java.util.*;

public class MainPunishmentMenu extends Menu {
    private UUID targetUUID;
    private String targetName;

    public String getTitle(Player player) {
        return ChatColor.BLUE + "Punishments - " + this.targetName;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        if (player.hasPermission("regen.admin")) {
            buttons.put(1, this.button(PunishmentTypes.WARN));
            buttons.put(3, this.button(PunishmentTypes.MUTE));
            buttons.put(5, this.button(PunishmentTypes.BAN));
            buttons.put(7, this.button(PunishmentTypes.BLACKLIST));
        } else {
            buttons.put(1, this.button(PunishmentTypes.WARN));
            buttons.put(4, this.button(PunishmentTypes.MUTE));
            buttons.put(7, this.button(PunishmentTypes.BAN));
        }
        return buttons;
    }

    private Button button(PunishmentTypes type) {
        return new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + type.getName() + "s";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> desc = Lists.newArrayList();
                List<Punishment> punish = Regen.getPunishBackend().getPunishmentsByTypes(Bukkit.getOfflinePlayer(targetUUID).getUniqueId(), type);

                desc.add(ChatColor.GRAY.toString() + punish.size() + " " + type.getName() + " on record");

                return desc;
            }


            @Override
            public Material getMaterial(Player player) {

                return Material.WOOL;
            }

            @Override
            public byte getDamageValue(Player player) {
                switch(type) {
                    case WARN: {
                        return DyeColor.YELLOW.getWoolData();
                    }
                    case MUTE: {
                        return DyeColor.ORANGE.getWoolData();
                    }
                    case BAN: {
                        return DyeColor.RED.getWoolData();
                    }
                }
                return DyeColor.BLACK.getWoolData();
            }

            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Loading " + MainPunishmentMenu.this.targetName + "'s " + type.getName() + "s...");
                List<Punishment> punish = Regen.getPunishBackend().getPunishmentsByTypes(Bukkit.getOfflinePlayer(targetUUID).getUniqueId(), type);

                punish.stream().sorted((first, second) -> Longs.compare(second.getTime(), first.getTime()));
                punish.sort((r1, r2) -> (int) (r2.getTime() - r1.getTime())); // Sorting by time...

                Bukkit.getScheduler().runTaskAsynchronously(Regen.getInstance(), () -> new PunishmentMenu(MainPunishmentMenu.this.targetUUID, MainPunishmentMenu.this.targetName, type, punish).openMenu(player));
            }
        };
    }

    public MainPunishmentMenu(UUID targetUUID, String targetName) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
    }
}
