package rip.kits.regen.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Flag;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.kits.regen.Regen;
import rip.kits.regen.punish.menu.MainPunishmentMenu;
import rip.kits.regen.punish.Punishment;
import rip.kits.regen.punish.util.PunishmentTypes;
import rip.kits.regen.util.TimeUtil;
import rip.kits.ruby.profiles.Profile;
import rip.kits.ruby.ranks.Rank;

import java.util.UUID;

public class ModCommands {
    @Command(names={"checkpunishments", "cp", "c"}, permission = "")
    public static void checkPunish(Player sender, @Param(name="target") Player player) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.MOD)) {
            Bukkit.getScheduler().runTaskAsynchronously(Regen.getInstance(), () -> new MainPunishmentMenu(player.getUniqueId(), player.getName()).openMenu(sender)); // Run Async because we need async lol i could just make the command async but didnt.
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names={"mute"}, permission = "")
    public static void mute(Player sender, @Flag(value = {"s", "silent"}) boolean silent, @Param(name="player") OfflinePlayer player, @Param(name="duration", defaultValue = "perm") String time, @Param(name = "reason", wildcard = true, defaultValue = "No reason provided") String reason) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {

            String senderName = (sender instanceof Player) ? ChatColor.DARK_AQUA + sender.getName() : "§4§lConsole";
            long length = TimeUtil.parseTime(time);
            if (length == -1L) length = Long.MAX_VALUE;

            if (Regen.getPunishBackend().isCurrentlyPunishedByTypes(player.getUniqueId(), PunishmentTypes.MUTE)) {
                sender.sendMessage(ChatColor.RED + "This player is already muted.");
                return;
            }

            String ip = player.isOnline() ? player.getPlayer().getAddress().getHostString() : "None";
            Punishment punishment = new Punishment(UUID.randomUUID(), player.getUniqueId(), PunishmentTypes.MUTE, System.currentTimeMillis(), reason, length, ip);
            if (sender instanceof Player) {
                punishment.setExecutor(sender.getUniqueId());
                punishment.setExecutorName(sender.getName());
            } else {
                punishment.setExecutorName("Console");
            }

            Regen.getPunishBackend().insertPunishment(punishment);
            if (player.isOnline())
                player.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have been muted, by " + senderName));
            punishment.broadcastPunish(senderName, ChatColor.translateAlternateColorCodes('&', "&3" + player.getName()), reason, (length == Long.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(length)), silent);
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }

    }

    @Command(names = {"ban"}, permission = "")
    public static void ban(Player sender, @Flag (value = { "s", "silent" }) boolean silent, @Param(name="player") OfflinePlayer player, @Param(name = "duration", defaultValue = "perm") String time, @Param(name="reason", wildcard = true, defaultValue = "No reason provided") String reason) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.MOD)) {

            String senderName = (sender instanceof Player) ? ChatColor.DARK_AQUA + sender.getName() : "§4§lConsole";
            long length = TimeUtil.parseTime(time);
            if (length == -1L) {
                length = Long.MAX_VALUE;
            }

            if (Regen.getPunishBackend().isCurrentlyPunishedByTypes(player.getUniqueId(), PunishmentTypes.BAN)) {
                sender.sendMessage(ChatColor.RED + "This player is already banned.");
                return;
            }
            String ip = player.isOnline() ? player.getPlayer().getAddress().getHostString() : "None";
            Punishment punishment = new Punishment(UUID.randomUUID(), player.getUniqueId(), PunishmentTypes.BAN, System.currentTimeMillis(), reason, length, ip);
            if (sender instanceof Player) {
                punishment.setExecutor(sender.getUniqueId());
                punishment.setExecutorName(sender.getName());
            } else {
                punishment.setExecutorName("Console");
            }


            Regen.getPunishBackend().insertPunishment(punishment);
            punishment.broadcastPunish(senderName, ChatColor.translateAlternateColorCodes('&', "&3" + player.getName()), reason, (length == Long.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(length)), silent);

            if (player.isOnline()) {
                new BukkitRunnable() {
                    public void run() {
                        player.getPlayer().kickPlayer(punishment.getKickMessage());
                    }
                }.runTask(Regen.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names = {"blacklist"}, permission = "")
    public static void blacklist(Player sender, @Flag (value = { "s", "silent" }) boolean silent, @Param(name="player") OfflinePlayer player, @Param(name="reason", wildcard = true, defaultValue = "No reason provided") String reason) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.ADMIN)) {

            String senderName = (sender instanceof Player) ? ChatColor.DARK_AQUA + sender.getName() : "§4§lConsole";

            StringBuilder reasonBuilder = new StringBuilder();
            if (Regen.getPunishBackend().isCurrentlyPunishedByTypes(player.getUniqueId(), PunishmentTypes.BLACKLIST)) {
                sender.sendMessage(ChatColor.RED + "This player is already blacklisted.");
                return;
            }
            String ip = player.isOnline() ? player.getPlayer().getAddress().getHostString() : "None";
            Punishment punishment = new Punishment(UUID.randomUUID(), player.getUniqueId(), PunishmentTypes.BLACKLIST, System.currentTimeMillis(), reason, Long.MAX_VALUE, ip);
            if (sender instanceof Player) {
                punishment.setExecutor(sender.getUniqueId());
                punishment.setExecutorName(sender.getName());
            } else {
                punishment.setExecutorName("Console");
            }

            if (ip.equalsIgnoreCase("None")) {
                sender.sendMessage(ChatColor.RED + "Cannot blacklist this player as they have never logged on.");
                return;
            }

            Regen.getPunishBackend().insertPunishment(punishment);
            punishment.broadcastPunish(senderName, ChatColor.translateAlternateColorCodes('&', "&3" + player.getName()), reason, null, silent);

            if (player.isOnline()) {
                new BukkitRunnable() {
                    public void run() {
                        player.getPlayer().kickPlayer(punishment.getKickMessage());
                    }
                }.runTask(Regen.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names={"unban"}, permission = "", async = true)
    public static void unban(Player sender, @Flag(value = "s") boolean silent, @Param(name="player") String player, @Param(name = "reason", defaultValue = "No reason provided", wildcard = true) String reason) {
        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.MOD)) {


            OfflinePlayer targetPlayer;
            if (Bukkit.getPlayer(player) != null) {
                targetPlayer = Bukkit.getPlayer(player);
            } else {
                targetPlayer = Bukkit.getOfflinePlayer(player);
            }

            String senderName = (sender instanceof Player) ? ChatColor.DARK_AQUA + sender.getName() : "§4§lConsole";
            if (!(Regen.getPunishBackend().isCurrentlyPunishedByTypes(targetPlayer.getUniqueId(), PunishmentTypes.BAN))) {
                sender.sendMessage(ChatColor.RED + "This player is not currently banned.");
                return;
            }
            Punishment punishment = (Punishment) Regen.getPunishBackend().getActivePunishmentsByTypes(targetPlayer.getUniqueId(), PunishmentTypes.BAN).toArray()[0];
            punishment.setPardoned(true);
            punishment.setPardonedAt(System.currentTimeMillis());
            if (sender instanceof Player) {
                punishment.setPardonedBy(sender.getUniqueId());
                punishment.setPardonedByName(sender.getName());
            } else {
                punishment.setPardonedByName("Console");
            }
            punishment.setPardonedReason(reason);
            Regen.getPunishBackend().updatePunishment(targetPlayer.getUniqueId(), punishment.getPunishID(), punishment);
            punishment.broadcastPunish(senderName, ChatColor.translateAlternateColorCodes('&', "&3" + targetPlayer.getName()), reason, null, silent);

        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @Command(names={"unblacklist"}, permission = "", async = true)
    public static void unblacklist(Player sender, @Flag(value = "s") boolean silent, @Param(name="player") String player, @Param(name = "reason", defaultValue = "No reason provided", wildcard = true) String reason) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.ADMIN)) {

            OfflinePlayer targetPlayer;
            if (Bukkit.getPlayer(player) != null) {
                targetPlayer = Bukkit.getPlayer(player);
            } else {
                targetPlayer = Bukkit.getOfflinePlayer(player);
            }

            String senderName = (sender instanceof Player) ? ChatColor.DARK_AQUA + sender.getName() : "§4§lConsole";
            if (!(Regen.getPunishBackend().isCurrentlyPunishedByTypes(targetPlayer.getUniqueId(), PunishmentTypes.BLACKLIST))) {
                sender.sendMessage(ChatColor.RED + "This player is not currently blacklisted.");
                return;
            }
            Punishment punishment = (Punishment) Regen.getPunishBackend().getActivePunishmentsByTypes(targetPlayer.getUniqueId(), PunishmentTypes.BLACKLIST).toArray()[0];
            punishment.setPardoned(true);
            punishment.setPardonedAt(System.currentTimeMillis());
            if (sender instanceof Player) {
                punishment.setPardonedBy(sender.getUniqueId());
                punishment.setPardonedByName(sender.getName());
            } else {
                punishment.setPardonedByName("Console");
            }

            punishment.setPardonedReason(reason);
            Regen.getPunishBackend().updatePunishment(targetPlayer.getUniqueId(), punishment.getPunishID(), punishment);
            punishment.broadcastPunish(senderName, ChatColor.translateAlternateColorCodes('&', "&3" + targetPlayer.getName()), reason, null, silent);
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }

    }

    @Command(names={"unmute"}, permission = "")
    public static void unmute(Player sender, @Flag(value = "s") boolean silent, @Param(name="player") String player, @Param(name = "reason", defaultValue = "No reason provided", wildcard = true) String reason) {

        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.MOD)) {

            OfflinePlayer targetPlayer;
            if (Bukkit.getPlayer(player) != null) {
                targetPlayer = Bukkit.getPlayer(player);
            } else {
                targetPlayer = Bukkit.getOfflinePlayer(player);
            }

            String senderName = (sender instanceof Player) ? ChatColor.DARK_AQUA + sender.getName() : "§4§lConsole";
            if (!(Regen.getPunishBackend().isCurrentlyPunishedByTypes(targetPlayer.getUniqueId(), PunishmentTypes.MUTE))) {
                sender.sendMessage(ChatColor.RED + "This player is not currently muted.");
                return;
            }
            Punishment punishment = (Punishment) Regen.getPunishBackend().getActivePunishmentsByTypes(targetPlayer.getUniqueId(), PunishmentTypes.MUTE).toArray()[0];
            punishment.setPardoned(true);
            punishment.setPardonedAt(System.currentTimeMillis());
            if (sender instanceof Player) {
                punishment.setPardonedBy(sender.getUniqueId());
                punishment.setPardonedByName(sender.getName());
            } else {
                punishment.setPardonedByName("Console");
            }

            punishment.setPardonedReason(reason);
            Regen.getPunishBackend().updatePunishment(targetPlayer.getUniqueId(), punishment.getPunishID(), punishment);
            punishment.broadcastPunish(senderName, ChatColor.translateAlternateColorCodes('&', "&3" + targetPlayer.getName()), reason, null, silent);
        } else {
            sender.sendMessage(ChatColor.RED + "No permission.");
        }
    }
}
