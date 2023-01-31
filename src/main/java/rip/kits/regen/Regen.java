package rip.kits.regen;

import lombok.Getter;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import rip.kits.regen.handlers.ModModeHandler;
import rip.kits.regen.listeners.Listeners;
import rip.kits.regen.punish.PunishBackend;
import rip.kits.regen.report.ReportBackend;
import rip.kits.regen.user.task.FreezeTask;

public final class Regen extends JavaPlugin {

    @Getter private static Regen instance;
    @Getter private JedisPool jedisPool;
    @Getter private ModModeHandler modModeHandler;
    @Getter private static PunishBackend punishBackend;
    @Getter private static ReportBackend reportBackend;

    private boolean redisConnected = false;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Bukkit.getConsoleSender().sendMessage("[Regen] Loading Commands");
        FrozenCommandHandler.registerAll(this);

        // Mod Mode Handler //
        modModeHandler = new ModModeHandler();
        (punishBackend = new PunishBackend()).initBackend(callback -> {
            if (!callback) {
                Bukkit.getConsoleSender().sendMessage("[Regen] Failed to load Punishment Backend.");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            } else {
                Bukkit.getConsoleSender().sendMessage("[Regen] Loaded Punishment Backend.");
                return;
            }
        });

        (reportBackend = new ReportBackend()).initBackend(callback -> {
            if (!callback) {
                Bukkit.getConsoleSender().sendMessage("[Regen] Failed to load Report Backend.");
                Bukkit.getPluginManager().disablePlugin(this);
            } else {
                Bukkit.getConsoleSender().sendMessage("[Regen] Loaded Report Backend.");
                return;
            }
        });
        punishBackend.loadFromDatabase();
        reportBackend.loadFromDatabase();
        Bukkit.getConsoleSender().sendMessage("[Regen] Loaded Commands");
        Bukkit.getConsoleSender().sendMessage("[Regen] Loading Listeners");
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getConsoleSender().sendMessage("[Regen] Loaded Listeners");
        Bukkit.getLogger().info("[Regen] Frozen Message Start..");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new FreezeTask(), 70L, 70L);
    }

    @Override
    public void onDisable() {
        instance = null;
        punishBackend.shutdown();
        reportBackend.shutdown();
    }
}
