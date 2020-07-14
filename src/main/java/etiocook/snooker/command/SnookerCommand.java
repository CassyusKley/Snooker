package etiocook.snooker.command;

import etiocook.snooker.Main;
import etiocook.snooker.manager.SnookerManager;
import etiocook.snooker.utils.CiberConfig;
import etiocook.snooker.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnookerCommand implements CommandExecutor {

    final Main main = Main.getInstance();
    final SnookerManager snookerManager = SnookerManager.getInstance();
    private int scheduler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInventory inventory = player.getInventory();

            if (args.length == 0) {
                if (!main.getList().contains(player)) {

                    if (!snookerManager.isState()) {
                        player.sendMessage(main.getconfigString("Messages.not-online"));
                        return false;
                    }

                        List<ItemStack> inventoryItems = Stream.of(new ItemStack[][]{inventory.getContents(), inventory.getArmorContents()}).flatMap(Stream::of).collect(Collectors.toList());
                        if (inventoryItems.stream().anyMatch(item -> (item != null && !item.getType().equals(Material.AIR)))) {
                            player.sendMessage(main.getconfigString("clear-inventory"));
                            return false;
                        }
                        if (!main.getList().contains(player)) {
                            main.getList().add(player);

                            Location location = new Location(Bukkit.getWorld("AuraF"), -26, 19, -7.281);
                            player.teleport(location);

                            ItemBuilder stick = new ItemBuilder(Material.STICK).unbreakable(true).enchant(Enchantment.KNOCKBACK, main.getConfigurations().getInt("stick-level"));
                            inventory.setItem(0, stick.build());
                            player.sendTitle("§e§lSnooker", main.getconfigString("teleported-waiting"));
                        }

                }
            }

            if (args.length == 1) {
                if ("camarote".equalsIgnoreCase(args[0])) {
                    if (snookerManager.isState()) {
                        if (!snookerManager.getCamaroteList().contains(player)) {
                            snookerManager.getCamaroteList().add(player);

                            Location location = new Location(Bukkit.getWorld("AuraF"), -26, 19, -7.281);
                            player.teleport(location);
                            player.sendTitle("§e§lSnooker", main.getconfigString("Messages.teleported-camarote"));
                            return false;
                        }
                        snookerManager.getCamaroteList().remove(player);
                        Location location = new Location(Bukkit.getWorld("AuraF"), -26, 19, -7.281);
                        player.teleport(location);
                        player.sendTitle("§e§lSnooker", main.getconfigString("Messages.quit-camarote"));
                    }

                }

                if (player.hasPermission("snooker.admin")) {
                    if ("start".equalsIgnoreCase(args[0]) || "iniciar".equalsIgnoreCase(args[0])) {

                        if (snookerManager.isState()) {
                            player.sendMessage("§cthe event is already started");
                            return false;
                        }

                        snookerManager.setState(true);
                        snookerManager.setRunning(true);
                        snookerManager.setHappening(false);
                        announce("start-message");
                        announcement();
                    }
                    if ("reload".equalsIgnoreCase(args[0]) || "recarregar".equalsIgnoreCase(args[0])) {
                        main.getConfigurations().reloadConfig();
                        main.getConfigurations().saveConfig();

                        player.sendTitle("§2§lSnooker", "§aa");

                    }
                }
            }
        }
        return false;
    }

    protected void announcement() {
        CiberConfig configurations = main.getConfigurations();
        AtomicInteger counter = new AtomicInteger();
        long time = configurations.getConfiguration().getInt("timer") * 20;
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        this.scheduler = scheduler.scheduleSyncRepeatingTask(main, () -> {
            if (counter.getAndIncrement() <= configurations.getInt("amount-messages")) {
                announce("start-message");
                return;
            }

            scheduler.cancelTask(this.scheduler);
            if (main.getList().size() <= 1) {
                snookerManager.setState(false);
                for (Player player : main.getList()) player.performCommand("spawn");
                announce("lack-of-player");
                main.getList().clear();
                return;
            }

            for (Player playerList : main.getList()) {
                Location location = new Location(Bukkit.getWorld("AuraF"), -1.905,4,-7.661);
                playerList.teleport(location);
                playerList.sendTitle("§e§lSnoooker", "§ayou were teleported to the event");
            }

            announce("quit-message");
            snookerManager.setHappening(true);

        }, time, time);
    }

    protected void announce(String configList) {
        CiberConfig configurations = main.getConfigurations();
        List<String> stringList = configurations.getConfiguration().getStringList(configList);
        stringList.forEach(msg -> Bukkit.broadcastMessage(
                ChatColor.translateAlternateColorCodes('&', msg).
                replace("<quantia>", "" + main.getList().size()))
        );
    }
}
