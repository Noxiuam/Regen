package rip.kits.regen.report.menu;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.kits.regen.report.Report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportButton  extends Button {
    private Report report;

    public String getName(Player player) {
        return ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(this.report.getTime()));
    }

    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
        description.add(ChatColor.YELLOW + "By: " + ChatColor.RED + this.report.getExecutorName());
        description.add(ChatColor.YELLOW + "Added on: " + ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(this.report.getTime())));
        description.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + this.report.getReason());
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    public ReportButton(Report report) {
        this.report = report;
    }
}

