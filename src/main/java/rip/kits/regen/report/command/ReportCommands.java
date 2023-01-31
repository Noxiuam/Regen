package rip.kits.regen.report.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.kits.regen.Regen;
import rip.kits.regen.report.Report;
import rip.kits.regen.report.menu.ReportMenu;

import java.util.List;
import java.util.UUID;

public class ReportCommands {
    @Command(names={"reports"}, permission = "regen.reports")
    public static void onReports(Player sender, @Param(name="Player") Player player) {
        List<Report> reports = Regen.getReportBackend().getReportOf(player.getUniqueId());
        reports.sort((r1, r2) -> (int) (r2.getTime() - r1.getTime())); // Sorts by time...

        new ReportMenu(player.getUniqueId(), player.getName(), reports).openMenu(sender);
    }
}
