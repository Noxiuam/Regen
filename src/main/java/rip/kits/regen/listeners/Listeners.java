package rip.kits.regen.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import rip.kits.regen.Regen;
import rip.kits.regen.punish.Punishment;
import rip.kits.regen.punish.util.PunishmentTypes;
import rip.kits.regen.user.User;
import rip.kits.regen.user.UserManager;
import rip.kits.regen.util.CC;
import rip.kits.regen.util.ItemBuilder;
import rip.kits.ruby.profiles.Profile;
import rip.kits.ruby.ranks.Rank;

import java.util.Set;
import java.util.UUID;

public class Listeners implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Profile staff = Profile.getByUuid(event.getPlayer().getUniqueId());

        try {
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                Profile received = Profile.getByUuid(players.getUniqueId());
                if (received.getRank().isAboveOrEqual(Rank.TRIALMOD) && staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {
                    players.sendMessage(CC.s("&3[Staff] &b" + event.getPlayer().getName() + " &3has joined the network."));
                }
            }
        } catch (Exception ignored){}

        event.getPlayer().sendMessage(CC.s("&7&l&m------------------------------------"));
        event.getPlayer().sendMessage(CC.s("&7Welcome to the &b&lAero Network&7!"));
        event.getPlayer().sendMessage("");
        event.getPlayer().sendMessage(CC.s("&b&lDiscord &3- &odiscord.gg/hvGDqYPZzt"));
        event.getPlayer().sendMessage(CC.s("&b&lStore &3- &oComing Soon"));/* aeroclient.net/network-store */
        event.getPlayer().sendMessage(CC.s("&7&l&m------------------------------------"));

        if (staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {
            Regen.getInstance().getModModeHandler().setModMode(event.getPlayer(), true);
        }

        User user = new User(event.getPlayer().getUniqueId());
        UserManager.INSTANCE.register(user);

        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Player vanished : Regen.getInstance().getModModeHandler().vanished) {
                    if (player != event.getPlayer() && !(staff.getRank().isAboveOrEqual(Rank.TRIALMOD))) player.hidePlayer(vanished);
                }
            }
        } catch (Exception ignored) {}
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Profile staff = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) Regen.getInstance().getModModeHandler().setModMode(event.getPlayer(), false);

        User user = UserManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            Profile received = Profile.getByUuid(players.getUniqueId());
            if (received.getRank().isAboveOrEqual(Rank.TRIALMOD) && staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {
                players.sendMessage(CC.s("&3[Staff] &b" + event.getPlayer().getName() + " &3has left the network."));
            }
        }



        if (user.isFrozen()) {
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                Profile received = Profile.getByUuid(players.getUniqueId());
                if (received.getRank().isAboveOrEqual(Rank.TRIALMOD) && staff.getRank().isAboveOrEqual(Rank.TRIALMOD)) {
                    players.sendMessage(CC.s("&3[Staff] &b" + event.getPlayer().getName() + " &4has logged out while frozen."));
                }
            }
        }
    }

    @EventHandler
    public void onDamageTaken(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Regen.getInstance().getModModeHandler().isInModMode(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (Regen.getInstance().getModModeHandler().isVanished(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot hit other's in mod mode.");
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (Regen.getPunishBackend().isCurrentlyPunishedByTypes(event.getPlayer().getUniqueId(), PunishmentTypes.MUTE)) {
            event.getPlayer().sendMessage(CC.s("&cYou are currently muted."));
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void fixTabComplete(PlayerChatTabCompleteEvent event) {
        if (event.getChatMessage().startsWith("/")) {
            if (!event.getChatMessage().startsWith("/msg") || !event.getChatMessage().startsWith("/m") || !event.getChatMessage().startsWith("/tell") || !event.getChatMessage().startsWith("/t") || !event.getChatMessage().startsWith("/message")) {
                event.getTabCompletions().clear();
            }
        }

    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInHand();
        Player clicked = (Player) event.getRightClicked();
        if (itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null && Regen.getInstance().getModModeHandler().isInModMode(event.getPlayer())) {
            if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains("inspection book")) {
                if (event.getRightClicked() != null && event.getRightClicked() instanceof Player) {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Opening inventory of: " + ChatColor.RED + clicked.getName());
                    event.getPlayer().openInventory(clicked.getInventory());
                }
            } else if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains("freeze")) {
                User user = UserManager.INSTANCE.getUser(clicked.getUniqueId());

                if (!user.isFrozen()) {
                    user.setFrozen(true);
                } else {
                    user.setFrozen(false);
                }

            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getDisplayName() != null && Regen.getInstance().getModModeHandler().isInModMode(event.getPlayer())) {
            String name = event.getItem().getItemMeta().getDisplayName();

            if (name.toLowerCase().contains("become visible")) {
                Regen.getInstance().getModModeHandler().setVanished(event.getPlayer(), false);
                event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), new ItemBuilder(Material.INK_SACK,1).displayName("§bBecome Invisible").data((short) 10).build());
            } else if (name.toLowerCase().contains("become invisible")) {
                Regen.getInstance().getModModeHandler().setVanished(event.getPlayer(), true);
                event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), new ItemBuilder(Material.INK_SACK,1).displayName("§bBecome Visible").data((short) 8).build());
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (Regen.getInstance().getModModeHandler().isInModMode(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Player player = Bukkit.getServer().getPlayer(event.getUniqueId());

        if (player != null && player.isOnline()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cPlease wait a few seconds before reconnecting...");
            return;
        }

        UUID uuid = event.getUniqueId();
        String ip = event.getAddress().getHostAddress();
        if (Regen.getPunishBackend().isCurrentlyPunishedByTypes(uuid, PunishmentTypes.BAN)) {
            Set<Punishment> punish = Regen.getPunishBackend().getActivePunishmentsByTypes(event.getUniqueId(), PunishmentTypes.BAN);
            if (punish.size() > 0) {
                Punishment disallow = (Punishment) punish.toArray()[0];
                if (disallow != null) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, disallow.getKickMessage());
                    return;
                }
            }
        }

        if (Regen.getPunishBackend().isCurrentlyIPPunishedByTypes(ip, PunishmentTypes.BLACKLIST)) {
            Set<Punishment> punish = Regen.getPunishBackend().getActiveIPPunishmentsByTypes(ip, PunishmentTypes.BLACKLIST);

            if (punish.size() > 0) {
                Punishment disallow = (Punishment) punish.toArray()[0];
                if (disallow != null) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, disallow.getKickMessage());
                }
            }
        }
    }

    /* Basic */

    /*
    * On request from Jegox.
    * @returns skulls owner [ie: ArcaneCC, Jegox, Yegox, ST0RM]
    */
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            BlockState block = event.getClickedBlock().getState();
            if (block instanceof Skull) {
                Skull skull = (Skull) block;
                String owner = skull.getOwner();
                player.sendMessage("§eThis is the head of: " + owner);
            }
        }
    }

    /* Frozen */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandProcess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        Profile staff = Profile.getByUuid(event.getPlayer().getUniqueId());

        boolean whitelistedCommands = command.startsWith("/msg") || command.startsWith("/unfreezeserver");

        User user = UserManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        if (!whitelistedCommands && user.isFrozen()) {
            event.getPlayer().sendMessage(CC.s("&cYou cannot run commands while frozen.."));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        User user = UserManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        if (user.isFrozen()) {
            event.getPlayer().sendMessage(CC.s("&cYou cannot chat while frozen.."));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        User user = UserManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        if (user.isFrozen()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        User user = UserManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        if (user.isFrozen()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        User user = UserManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        if (user.isFrozen()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            User user = UserManager.INSTANCE.getUser(event.getDamager().getUniqueId());

            if (user.isFrozen()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        User user = UserManager.INSTANCE.getUser(event.getWhoClicked().getUniqueId());

        if (user.isFrozen()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        User user = UserManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        if (user.isFrozen()) {
            event.setTo(event.getFrom());
        }
    }
}
