package etiocook.snooker;

import etiocook.snooker.command.SnookerCommand;
import etiocook.snooker.listener.SnookerListeners;
import etiocook.snooker.utils.CiberConfig;
import etiocook.snooker.utils.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public final class Main extends JavaPlugin {

    private CiberConfig configurations;
    private boolean state;
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

    CommandManager commandManager;

    @Override
    public void onEnable() {
        setConfigurations(new CiberConfig(this,"configurations.yml"));
        configurations.saveDefaultConfig();

        getCommand("sinuca").setExecutor(new SnookerCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new SnookerListeners(), this);
        commandManager = new CommandManager();
        new SnookerCommand();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public String colorize(String path) {
       return ChatColor.translateAlternateColorCodes('&', path);
    }
 }
