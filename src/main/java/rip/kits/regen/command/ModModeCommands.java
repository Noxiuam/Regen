package rip.kits.regen.command;

import com.cheatbreaker.api.CheatBreakerAPI;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import rip.kits.regen.Regen;
import rip.kits.regen.user.User;
import rip.kits.regen.user.UserManager;
import rip.kits.regen.util.CC;
import rip.kits.ruby.profiles.Profile;
import rip.kits.ruby.ranks.Rank;

public class ModModeCommands {

    @Command(names={"staff", "modmode", "mod"}, permission = "")
    public static void staff(Player sender) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {

            if (Regen.getInstance().getModModeHandler().switchModMode(sender)) {
                sender.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.GREEN + "Enabled");
                CheatBreakerAPI.getInstance().giveAllStaffModules(sender);
            } else {
                CheatBreakerAPI.getInstance().disableAllStaffModules(sender);
                sender.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.RED + "Disabled");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names = "freezeserver", permission = "")
    public static void freezeserver(Player sender) {
        Profile staff = Profile.getByUuid(sender.getUniqueId());
        if (staff.getRank().isAboveOrEqual(Rank.OWNER)) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = UserManager.INSTANCE.getUser(player.getUniqueId());
                sender.sendMessage(CC.s("&c&lThe server has been frozen."));
                user.setFrozen(true);
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No permission: Only owners can run this command.");
        }


    }

    @Command(names = "unfreezeserver", permission = "")
    public static void unfreezeserver(Player sender) {
        Profile staff = Profile.getByUuid(sender.getUniqueId());
        if (staff.getRank().isAboveOrEqual(Rank.OWNER)) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = UserManager.INSTANCE.getUser(player.getUniqueId());
                sender.sendMessage(CC.s("&c&lThe server has been unfrozen."));
                user.setFrozen(false);
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No permission: Only owners can run this command.");
        }


    }

    @Command(names = "panic", permission = "")
    public static void panic(Player sender) {
        Profile player = Profile.getByUuid(sender.getUniqueId());

        if (player.getRank().isAboveOrEqual(Rank.YOUTUBE)) {
            User user = UserManager.INSTANCE.getUser(sender.getUniqueId());
            sender.sendMessage(CC.s("&c&lThe server has been unfrozen."));
            user.setFrozen(false);
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }

    }

    @Command(names={"ss", "freeze"}, permission = "")
    public static void ss(Player sender, @Param(name="Player") Player player) {
        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.MOD)) {

            if (sender != player) {

                if (UserManager.INSTANCE.getUser(player.getUniqueId()) != null) {
                    User user = UserManager.INSTANCE.getUser(player.getUniqueId());

                    if (!(player.hasPermission("regen.ss.bypass"))) {
                        if (user.isFrozen()) {
                            user.setFrozen(false);
                            sender.sendMessage(CC.s("&cYou unfroze " + player.getName() + "."));
                            player.sendMessage(CC.s("&cYou are unfrozen."));
                        } else {
                            user.setFrozen(true);
                            sender.sendMessage(CC.s("&cYou froze " + player.getName() + "."));
                            player.sendMessage(CC.s("&cYou are currently frozen."));
                        }
                    } else {
                        sender.sendMessage(CC.s("&cYou cannot freeze this player."));
                    }
                } else {
                    sender.sendMessage(CC.s("&cAn error happened when running this command..."));
                }
            } else {
                sender.sendMessage(CC.s("&cYou cannot freeze yourself."));
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }

    }

    @Command(names={"tp", "teleport"}, permission = "")
    public static void tp(Player sender, @Param(name="player") OfflinePlayer player) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {

            if (player.isOnline()) {
                sender.teleport(player.getPlayer().getLocation());
                sender.sendMessage("§6Teleporting you to §f" + player.getName() + "§6.");
            } else {
                sender.teleport(player.getPlayer().getLocation());
                sender.sendMessage("§6Teleporting you to offline location of " + player.getName() + "§6.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names={"tphere", "s"}, permission = "")
    public static void tphere(Player sender, @Param(name="player") Player player) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.ADMIN)) {

            if (player != sender) {
                player.teleport(sender.getLocation());
                sender.sendMessage("§6Teleporting §f" + player.getName() + "§6 to you.");
                player.sendMessage("§6Teleporting you to §f" + sender.getName() + "§6.");
            } else {
                sender.sendMessage(ChatColor.RED + "You cannot teleport yourself to yourself.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names={"inventorysee", "invsee"}, permission = "")
    public static void invsee(Player sender, @Param(name="player") Player player) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.MOD)) {

            Inventory i = Bukkit.createInventory(null, 45, "§1§7Inventory: " + player.getName());
            i.setContents(player.getInventory().getContents());
            i.setItem(36, player.getInventory().getArmorContents()[3]);
            i.setItem(36, player.getInventory().getArmorContents()[2]);
            i.setItem(36, player.getInventory().getArmorContents()[1]);
            i.setItem(36, player.getInventory().getArmorContents()[0]);
            sender.openInventory(i);
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }

    }

    @Command(names={"clear", "ci"}, permission = "")
    public static void clear(Player sender, @Param(name="player", defaultValue = "self") Player player) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.ADMIN)) {

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.updateInventory();
            player.sendMessage("§6Your inventory has been cleared.");
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names={"head", "skull"}, permission = "")
    public static void head(Player sender, @Param(name="Players Head") String headName) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.ADMIN)) {

            ItemStack skull = new ItemStack(Material.SKULL_ITEM);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            meta.setOwner(headName);
            meta.setDisplayName(headName + "'s Head");
            skull.setDurability((short) 3);
            skull.setItemMeta(meta);

            sender.getInventory().addItem(skull);
            sender.sendMessage("§6You were given §f" + headName + "§6's head.");

        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names={"more"}, permission = "")
    public static void more(Player sender) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.ADMIN)) {

            if (sender.getItemInHand().getType() != Material.AIR) {
                sender.getItemInHand().setAmount(64);
                sender.sendMessage("§6There you go.");
            } else {
                sender.sendMessage("§aAir isn't a item.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }
}
