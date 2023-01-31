package rip.kits.regen.report.menu;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.kits.regen.report.Report;

import java.util.*;

public class ReportMenu extends PaginatedMenu {
    private UUID targetUUID;
    private String targetName;
    private List<Report> reports;


    public ReportMenu(UUID targetUUID, String targetName, List<Report> reports) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.reports = reports;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.RED + this.targetName + "s";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Iterator<Report> iterator = this.reports.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            buttons.put(index, new ReportButton(iterator.next()));
            ++index;
        }
        return buttons;
    }
}

