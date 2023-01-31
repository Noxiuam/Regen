package rip.kits.regen.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.kits.regen.Regen;
import rip.kits.regen.report.Report;
import rip.kits.regen.user.User;
import rip.kits.regen.user.UserManager;
import rip.kits.regen.util.CC;
import rip.kits.regen.util.Cooldown;
import rip.kits.ruby.Ruby;
import rip.kits.ruby.profiles.Profile;
import rip.kits.ruby.ranks.Rank;

import java.util.UUID;

public class PlayerCommands {

    @Command(names={"helpop"}, permission = "")
    public static void helpop(Player sender, @Param(name="Reason", wildcard = true) String s) {
        User user = UserManager.INSTANCE.getUser(sender.getUniqueId());
        Profile staff = Profile.getByUuid(sender.getUniqueId());

        if (!(user.getRequestCooldown().hasExpired())) {
            sender.sendMessage(CC.s("&cYou are still on cooldown for &e" + user.getRequestCooldown().getTimeLeft() + " &cseconds"));
            return;
        }

        user.setRequestCooldown(new Cooldown(300_00L));
        sender.sendMessage(CC.s("&aWe received your request."));

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {
                players.sendMessage(CC.s("&3[Request] &b" + sender.getName() + " &fhas requested help: &b" + s)); // Go To Staff sender.getName(); target.getName();
            }
        }
    }

    @Command(names={"report"}, permission = "")
    public static void report(Player sender, @Param(name="Player") Player target, @Param(name="Reason", wildcard = true, defaultValue = "no reason provided") String s) {
        if (sender.equals(target)) {
            sender.sendMessage(CC.s("&cYou cant report yourself."));
            return;
        }
        User user = UserManager.INSTANCE.getUser(sender.getUniqueId());

        if (!(user.getRequestCooldown().hasExpired())) {
            sender.sendMessage(CC.s("&cYou are still on cooldown for &e" + user.getRequestCooldown().getTimeLeft() + " &cseconds"));
            return;
        }

        Report report = new Report(UUID.randomUUID(), target.getUniqueId(), sender.getUniqueId(), target.getName(), sender.getName(), s, target.getPlayerTime());
        Regen.getReportBackend().insertReport(report);
        report.broadCastReportToStaff(sender.getName(), target.getName(), s);

        user.setRequestCooldown(new Cooldown(300_00L)); // 30 seconds...
        sender.sendMessage(CC.s("&bWe have received your report."));
    }

    @Command(names={"message", "msg", "m", "t", "tell"}, permission = "")
    public static void onMessage(Player sender, @Param(name="Player") Player player, @Param(name="Message", wildcard = true) String message) {
        User target = UserManager.INSTANCE.getUser(player.getUniqueId());
        Profile targetPlayer = Profile.getByUuid(player.getUniqueId());
        Profile targetPlayer2 = Profile.getByUuid(sender.getUniqueId());
        User messageSender = UserManager.INSTANCE.getUser(sender.getUniqueId());

        if (!(target.isPrivateMessagesEnabled())) {
            sender.sendMessage(targetPlayer.getRank().getColor() + player.getName() + " has messages turned off.");
            return;
        } else if (!(messageSender.isPrivateMessagesEnabled())) {
            sender.sendMessage(ChatColor.RED + "You have messages toggled off.");
            return;
        } else {
            player.sendMessage("§7(From " + targetPlayer2.getRank().getColor() + sender.getName() + "§7) " + message);
            sender.sendMessage("§7(To " + targetPlayer.getRank().getColor() + player.getName() + "§7) " + message);
            if (target.isSoundsEnabled()) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
            }
            target.setMessagingPlayer(sender.getUniqueId());
            messageSender.setMessagingPlayer(player.getUniqueId());
        }
    }

    @Command(names={"reply", "r"}, permission = "")
    public static void onReply(Player sender, @Param(name="Message", wildcard = true, defaultValue = "whoisitalkingto?") String message) {
        User user = UserManager.INSTANCE.getUser(sender.getUniqueId());
        Profile targetUser = Profile.getByUuid(Bukkit.getPlayer(user.getMessagingPlayer()).getUniqueId());
        Profile targetPlayer2 = Profile.getByUuid(sender.getUniqueId());

        if (user.getMessagingPlayer() == null || Bukkit.getPlayer(user.getMessagingPlayer()) == null || !Bukkit.getPlayer(user.getMessagingPlayer()).isOnline()) {
            sender.sendMessage("§cThat player has logged out.");
            return;
        } else if (message.equalsIgnoreCase("whoisitalkingto?")) {
            sender.sendMessage("§bYou are in a conversation with " + targetUser.getRank().getColor() + Bukkit.getPlayer(user.getMessagingPlayer()).getName() + "§b.");
        } else if (!(UserManager.INSTANCE.getUser(user.getMessagingPlayer()).isPrivateMessagesEnabled())) {
            sender.sendMessage(targetUser.getRank().getColor() + Bukkit.getPlayer(user.getMessagingPlayer()).getName() + " has messages turned off.");
            return;
        } else if (!(user.isPrivateMessagesEnabled())) {
            sender.sendMessage("§cYou have messages toggled off.");
            return;
        } else {
            Bukkit.getPlayer(user.getMessagingPlayer()).sendMessage("§7(From " + targetPlayer2.getRank().getColor() + sender.getDisplayName() + "§7) " + message);
            sender.sendMessage("§7(To " + targetUser.getRank().getColor() + Bukkit.getPlayer(user.getMessagingPlayer()).getName() + "§7) " + message);
            if (UserManager.INSTANCE.getUser(user.getMessagingPlayer()).isSoundsEnabled()) Bukkit.getPlayer(user.getMessagingPlayer()).playSound(Bukkit.getPlayer(user.getMessagingPlayer()).getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
            UserManager.INSTANCE.getUser(user.getMessagingPlayer()).setMessagingPlayer(sender.getUniqueId());
        }
    }
}
