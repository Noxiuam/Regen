package rip.kits.regen.report;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.kits.ruby.profiles.Profile;
import rip.kits.ruby.ranks.Rank;

import java.util.UUID;

@Setter @Getter
public class Report {
    private UUID reportID;
    private UUID playerID;
    private UUID executorID;
    private String playerName;
    private String executorName;
    private String reason;
    private long time;

    public Report(UUID reportID, UUID playerID, UUID executorID, String playerName, String executorName, String reason, long time) {
        this.reportID = reportID;
        this.playerID = playerID;
        this.executorID = executorID;
        this.playerName = playerName;
        this.executorName = executorName;
        this.reason = reason;
        this.time = time;
    }

    public void broadCastReportToStaff(String sender, String target, String reason) {
        String message = ChatColor.translateAlternateColorCodes('&', "&3[Report] &b" + sender + " &freported &b" + target + " &fwith the reason:\n    &b" + reason);
        Bukkit.getConsoleSender().sendMessage(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile staff = Profile.getByUuid(player.getUniqueId());
            if (staff.getRank().isAboveOrEqual(Rank.MOD)) {
                player.sendMessage(message);
            }
        }
    }
}
