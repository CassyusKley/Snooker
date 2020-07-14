package etiocook.snooker;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public final class Main extends JavaPlugin {

    private final List<Player> list = new LinkedList<>();
    public static Main getInstance() {
        return getPlugin(Main.class);
    }
    public List<Player> getList() {
        return list;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
