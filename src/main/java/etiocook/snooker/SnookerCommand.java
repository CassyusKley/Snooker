package etiocook.snooker;

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
    private int scheduler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInventory inventory = player.getInventory();
            SnookerManager snookerManager = new SnookerManager();

            if (args.length == 0) {

                if (!main.getList().contains(player)) {
                    List<ItemStack> inventoryItems = Stream.of(new ItemStack[][]{inventory.getContents(), inventory.getArmorContents()}).flatMap(Stream::of).collect(Collectors.toList());
                    if (inventoryItems.stream().anyMatch(item -> (item != null && !item.getType().equals(Material.AIR)))) {
                        player.sendMessage("§cLimpe seu inventario para participar do evento");
                        return false;
                    }
                    if (!main.getList().contains(player)) {
                        main.getList().add(player);

                        Location location = new Location(Bukkit.getWorld("AuraF"), 0, 190, 0);
                        player.teleport(location);

                        ItemBuilder stick = new ItemBuilder(Material.STICK).unbreakable(true).enchant(Enchantment.KNOCKBACK, 2);
                        inventory.setItem(0, stick.build());
                        player.sendTitle("§e§lSnooker", "have been sucessfully teleported");
                    }
                }

            }

            if (args.length == 1 && player.hasPermission("snooker.admin")) {
                if ("start".equalsIgnoreCase(args[0]) || "iniciar".equalsIgnoreCase(args[0])) {

                    if (snookerManager.isState()) {
                        player.sendMessage("§cthe event is already started");
                        return false;
                    }

                    snookerManager.setState(true);
                    snookerManager.setRunning(true);
                    announcement();
                }
                if ("reload".equalsIgnoreCase(args[0]) || "recarregar".equalsIgnoreCase(args[0])) {
                    main.getConfigurations().reloadConfig();
                    main.getConfigurations().saveConfig();

                    player.sendTitle("§2§lSnooker", "§aa");

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
        SnookerManager snookerManager = new SnookerManager();

        this.scheduler = scheduler.scheduleSyncRepeatingTask(main, () -> {
            if (counter.getAndIncrement() <= configurations.getInt("amount-messages")) {

                List<String> stringList = configurations.getConfiguration().getStringList("start-message");
                stringList.forEach(msg -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg)));
                return;
            }

            for (Player playerList : main.getList()) {
                Location location = new Location(Bukkit.getWorld("AuraF"), 0, 190, 0);
                playerList.teleport(location);

                playerList.sendTitle("§e§lSnoooker", "§ayou were teleported to the event");
            }

            scheduler.cancelTask(this.scheduler);
            snookerManager.setHappening(true);

        }, time, time);
    }

}
