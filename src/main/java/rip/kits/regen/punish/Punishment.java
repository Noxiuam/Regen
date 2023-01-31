package rip.kits.regen.punish;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.kits.regen.punish.util.PunishmentTypes;
import rip.kits.regen.util.TimeUtil;
import rip.kits.ruby.profiles.Profile;
import rip.kits.ruby.ranks.Rank;

import java.util.UUID;

@Setter @Getter
public class Punishment {
    private UUID punishID;
    private UUID uuid;
    private UUID executor;
    private UUID pardonedBy;
    private PunishmentTypes punishmentTypes;
    private String pardonedByName;
    private String executorName;
    private String reason;
    private String pardonedReason;
    private String IP;
    private long time;
    private long duration;
    private long pardonedAt;
    private boolean pardoned;

    public Punishment(UUID punishmentID, UUID uuid, PunishmentTypes type, long time, String reason, long duration, String playerIP) {
        this.punishID = punishmentID;
        this.uuid = uuid;
        this.punishmentTypes = type;
        this.time = time;
        this.reason = reason;
        this.duration = duration;
        this.IP = playerIP;
    }

    public Punishment(UUID punishmentID, UUID uuid, UUID executor, UUID pardonedBy, PunishmentTypes type, String reason, String executorName, String pardonedByName, String pardonedReason, long time, long duration, long pardonedAt, boolean pardoned, String playerIP) {
        this.punishID = punishmentID;
        this.uuid = uuid;
        this.executor = executor;
        this.pardonedBy = pardonedBy;
        this.punishmentTypes = type;
        this.reason = reason;
        this.executorName = executorName;
        this.pardonedByName = pardonedByName;
        this.pardonedReason = pardonedReason;
        this.time = time;
        this.duration = duration;
        this.pardonedAt = pardonedAt;
        this.pardoned = pardoned;
        this.IP = playerIP;
    }

    public boolean isPermanent() {
        return this.punishmentTypes == PunishmentTypes.BLACKLIST || this.duration == Long.MAX_VALUE;
    }

    public boolean isActive() {
        return !this.pardoned && (this.isPermanent() || this.getRemainingTime() < 0L);
    }

    public long getRemainingTime() {
        return System.currentTimeMillis() - (this.time + this.duration);
    }

    public String getRemainingString() {
        if (this.pardoned) return "Pardoned";
        if (this.isPermanent()) return "Permanent";
        if (!this.isActive()) return "Expired";

        return "Active";
    }

    public String getDurationString() {
        if (this.isPermanent()) return "Permanent";

        return TimeUtil.millisToRoundedTime((this.time + this.duration) - System.currentTimeMillis());
    }
    public String getDurString() {
        if (this.isPermanent()) {
            return "Permanent";
        }
        return TimeUtil.millisToRoundedTime(this.duration);
    }


    public String getKickMessage() {
        switch (this.punishmentTypes) {
            case BAN: {
                String message = ChatColor.translateAlternateColorCodes('&', "&cYou are permanently banned from the &3&lAero Network \n \n&cappeal at &3www.aeroclient.net/network-appeal");
                if (!this.isPermanent()) {
                    message = ChatColor.translateAlternateColorCodes('&', "&cYou are temporarily banned from the &3&lAero Network &cfor &b" + this.getDurationString() + " \n \n&cappeal at www.aeroclient.net/network-appeal"); // this.getDurationString(); <- Better shows time counting down...
                }
                return message;
            }
            case MUTE: {
                String message = ChatColor.translateAlternateColorCodes('&', "&cYou are permanently muted from the &3&lAero Network\n&c(&cappeal at &3www.aeroclient.net/network-appeal&c)");

                if (!this.isPermanent()) {
                    message = ChatColor.translateAlternateColorCodes('&', "&cYou are temporarily muted from the &3&lAero Network &cfor &b" + this.getDurationString() + "\n(&cappeal at aeroclient.net/network-appeal)");
                }
                return message;
            }
            case BLACKLIST: {
                return ChatColor.translateAlternateColorCodes('&', "&cYou are blacklisted from the &3&lAero Network\n \n&cThis type of punishment cannot be appealed.");
            }
            case KICK: {
                return ChatColor.translateAlternateColorCodes('&', "&cYou were kicked from the &3&lAero Network &cfor &b" + this.reason); // Kick
            }
            default: {
                return ChatColor.RED + "ERR ID: 0x01";
            }
        }
    }

    public void broadcastPunish(String sender, String target, String reason, String duration, boolean silent) {
        String message = ChatColor.translateAlternateColorCodes('&', (silent ? "&7(Silent) " : "") + target + " &awas "  + this.getDisplayString() + " by &f" + sender); // (silent ? "Silent.Color silenly + display
        Bukkit.getConsoleSender().sendMessage(message);

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {

            Profile staff = Profile.getByUuid(player.getUniqueId());

            if (silent) {

                if (staff.getRank().isAboveOrEqual(Rank.CHATMOD)) {
                    player.sendMessage(message);
                    return;
                }

            }

            player.sendMessage(message);
        }
    }

    public String getDisplayString() {
        if (this.punishmentTypes != PunishmentTypes.BAN && this.punishmentTypes != PunishmentTypes.MUTE) {
            return this.pardoned ? this.punishmentTypes.getUndoText() : this.punishmentTypes.getExecuteText();
        } else if (this.isPermanent()) {
            return this.pardoned ? this.punishmentTypes.getUndoText() : (this.punishmentTypes.getExecuteText());
        }
        return this.pardoned ? this.punishmentTypes.getUndoText() : ("temporarily " + this.punishmentTypes.getExecuteText());
    }
}