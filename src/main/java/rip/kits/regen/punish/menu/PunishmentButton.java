package rip.kits.regen.punish.menu;

import com.google.common.collect.Lists;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import rip.kits.regen.Regen;
import rip.kits.regen.punish.Punishment;
import rip.kits.regen.util.TimeUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PunishmentButton extends Button {
    private Punishment punishment;

    public String getName(Player player) {
        return ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(this.punishment.getTime()));
    }

    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(Regen.getInstance(), () -> {
            description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
            description.add(ChatColor.YELLOW + "By: " + ChatColor.RED + this.punishment.getExecutorName());
            description.add(ChatColor.YELLOW + "Added on: " + ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(this.punishment.getTime())));
            description.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + this.punishment.getReason());
            if (this.punishment.isActive()) {
                if (this.punishment.getTime() != 0L) {
                    description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
                    description.add(ChatColor.YELLOW + "Time remaining: " + ChatColor.RED + this.punishment.getDurationString());
                } else if (this.punishment.isPermanent()) {
                    description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
                    description.add(ChatColor.YELLOW + "This is a permanent punishment.");
                }
            } else if (this.punishment.isPardoned()) {
                description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
                description.add(ChatColor.RED + "Removed:");
                description.add(ChatColor.YELLOW + this.punishment.getPardonedByName() + ": " + ChatColor.RED + this.punishment.getPardonedReason());
                description.add(ChatColor.RED + "at " + ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(this.punishment.getPardonedAt())));

                if (this.punishment.getDuration() >= 0L) {
                    description.add("");
                    description.add(ChatColor.YELLOW + "Duration: " + this.punishment.getDurString());
                }
            } else if (!(this.punishment.isActive() && this.punishment.isPardoned())) {
                description.add("");
                description.add(ChatColor.YELLOW + "Duration: " + this.punishment.getDurString());
                description.add(ChatColor.GREEN + "Expired");
            }
            description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
        });
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return this.punishment.isActive() ? DyeColor.RED.getWoolData() : DyeColor.LIME.getWoolData();
    }

    public PunishmentButton(final Punishment punishment) {
        this.punishment = punishment;
    }
}
