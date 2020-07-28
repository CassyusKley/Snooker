package etiocook.snooker;

import etiocook.snooker.command.SnookerCommand;
import etiocook.snooker.listener.SnookerListeners;
import etiocook.snooker.manager.SnookerManager;
import etiocook.snooker.utils.CiberConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class Main extends JavaPlugin {

    private CiberConfig configurations;
    final SnookerManager snookerManager = SnookerManager.getInstance();
    private final List<Player> list = new LinkedList<>();
    public static Main getInstance() {
        return getPlugin(Main.class);
    }

    public CiberConfig getConfigurations() {
        return configurations;
    }
    public void setConfigurations(CiberConfig configurations) {
        this.configurations = configurations;
    }
    public List<Player> getList() {
        return list;
    }
    private int scheduler;

    @Override
    public void onEnable() {
        setConfigurations(new CiberConfig(this,"configurations.yml"));
        configurations.saveDefaultConfig();

        getCommand("sinuca").setExecutor(new SnookerCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new SnookerListeners(), this);
        new SnookerCommand();


        if (!getConfigurations().getBoolean("autostart")) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (snookerManager.isState()) {
                    Bukkit.getConsoleSender().sendMessage("§cthe event is already started");
                    return;
                }
                    announcement();
            }
        }.runTaskTimer(
                this,
                20 * getConfigurations().getConfiguration().getInt("autostart-timer"),
                20 * getConfigurations().getConfiguration().getInt("autostart-timer")
        );

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public String colorize(String path) {
       return ChatColor.translateAlternateColorCodes('&', path);
    }

    public void announcement() {
        CiberConfig configurations = this.getConfigurations();
        AtomicInteger counter = new AtomicInteger();
        long time = configurations.getConfiguration().getInt("timer") * 20;
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        this.scheduler = scheduler.scheduleSyncRepeatingTask(this, () -> {
            if (counter.getAndIncrement() <= configurations.getInt("amount-messages")) {
                announce("start-message");
                return;
            }
            scheduler.cancelTask(this.scheduler);
            if ( this.getList().size() <= 1) {
                snookerManager.setState(false);
                for (Player player : this.getList()) {
                    player.performCommand("spawn");
                    player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                }
                announce("lack-of-player");
                this.getList().clear();
                return;
            }
            for (Player playerList :  this.getList()) {
                Location location = new Location(Bukkit.getWorld("Eventos"), 70, 4, -67);
                playerList.teleport(location);
                playerList.sendTitle("§e§lSnoooker", "§ayou were teleported to the event");
            }
            announce("quit-message");
            snookerManager.setHappening(true);
            snookerManager.setState(false);
            snookerManager.setCamarote(true);

        }, time, time);
    }

    protected void announce(String configList) {
        CiberConfig configurations =  this.getConfigurations();
        List<String> stringList = configurations.getConfiguration().getStringList(configList);
        stringList.forEach(msg -> Bukkit.broadcastMessage(
                ChatColor.translateAlternateColorCodes('&', msg).
                        replace("<amount>", "" +  this.getList().size()).
                        replace("<amountMax>", "" + this.getConfigurations().getInt("players-limite")))
        );
    }
 }
