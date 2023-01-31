package rip.kits.regen.punish.menu;

import com.google.common.collect.Maps;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.kits.regen.punish.Punishment;
import rip.kits.regen.punish.util.PunishmentTypes;

import java.util.*;

public class PunishmentMenu extends PaginatedMenu {
    private UUID targetUUID;
    private String targetName;
    private PunishmentTypes type;
    private List<Punishment> punishments;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.RED + this.type.getName() + "s";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        Iterator<Punishment> iterator = punishments.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            buttons.put(index, new PunishmentButton(iterator.next()));
            index++;
        }

        return buttons;
    }

    public PunishmentMenu(UUID targetUUID, String targetName, PunishmentTypes type, List<Punishment> punishments) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.type = type;
        this.punishments = punishments;
    }
}
