package rip.kits.regen.handlers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import rip.kits.regen.Regen;
import rip.kits.regen.util.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModModeHandler {
    public List<Player> inModMode;
    public List<Player> vanished;
    public List<Player> hidingStaff;
    public Map<Player, ItemStack[]> modInv;
    public Map<Player, ItemStack[]> modArmor;
    @Getter @Setter
    private String defaultPrefix;

    public ModModeHandler() {
        setDefaultPrefix("&f");
        inModMode = new ArrayList<>();
        vanished = new ArrayList<>();
        hidingStaff = new ArrayList<>();
        modInv = new HashMap<>();
        modArmor = new HashMap<>();
    }

    public boolean isInModMode(Player player) {
        return inModMode.contains(player);
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player);
    }

    public void setModMode(Player player, Boolean bool) {
        if (bool) {
            if (!inModMode.contains(player)) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(online);
                }

                setModInv(player, true);
                player.setAllowFlight(true);
                player.setGameMode(GameMode.SURVIVAL);
                inModMode.add(player);
                setVanished(player, true);
                player.setMetadata("modmode", new FixedMetadataValue(Regen.getPlugin(Regen.class), true));
            }
        } else {
            if (inModMode.contains(player)) {
                inModMode.remove(player);
                setModInv(player, false);
                setVanished(player, false);
                player.setAllowFlight(false);
                player.setGameMode(GameMode.SURVIVAL);
                player.removeMetadata("modmode", Regen.getInstance());
            }
        }
    }

    public void setModInv(Player player, Boolean bool) {
        if (bool) {
            modInv.put(player, player.getInventory().getContents());
            modArmor.put(player, player.getInventory().getArmorContents());
            player.getInventory().clear();
            player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS, 1).displayName("§bCompass").build());
            player.getInventory().setItem(1, new ItemBuilder(Material.BOOK, 1).displayName("§bInspection Book").build());
            if (player.hasPermission("worldedit.wand")) {
                player.getInventory().setItem(2, new ItemBuilder(Material.WOOD_AXE,1).displayName("§bWorldEdit Wand").build());
                player.getInventory().setItem(3, new ItemBuilder(Material.CARPET,1).displayName("§bBetter View").data((short)1).build());
            } else {
                player.getInventory().setItem(2, new ItemBuilder(Material.CARPET,1).displayName("§bBetter View").data((short)1).build());
            }
            player.getInventory().setItem(7, new ItemBuilder(Material.ICE, 1).displayName("§bFreeze").build());
            player.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK,1).displayName("§bBecome Visible").data((short) 8).build());
        } else {
            if (modInv.containsKey(player)) {
                player.getInventory().setContents(modInv.get(player));
            } else if (modArmor.containsKey(player)) {
                player.getInventory().setContents(modArmor.get(player));
            } else {
                player.getInventory().clear();
            }
        }
    }

    public void setVanished(Player player, Boolean bool) {
        if (bool) {
            if (!(vanished.contains(player))) vanished.add(player);

            for (Player online :  Bukkit.getOnlinePlayers()) {
                if (!isInModMode(online)) {
                    online.hidePlayer(player);
                } else if (hidingStaff.contains(online)) {
                    online.hidePlayer(player);
                }
            }
        } else {
            if (vanished.contains(player)) vanished.remove(player);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(player);
            }
        }
    }

    public boolean switchModMode(Player player) {
        setModMode(player, !inModMode.contains(player));
        return inModMode.contains(player);
    }
}
