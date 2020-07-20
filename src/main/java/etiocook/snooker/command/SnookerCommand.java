package etiocook.snooker.command;

import etiocook.snooker.Main;
import etiocook.snooker.manager.SnookerManager;
import etiocook.snooker.utils.CiberConfig;
import etiocook.snooker.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnookerCommand implements CommandExecutor {

    final Main main = Main.getInstance();
    final SnookerManager snookerManager = SnookerManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInventory inventory = player.getInventory();

            if (args.length == 0) {
                if (!main.getList().contains(player)) {

                    if (!snookerManager.isState()) {
                        player.sendMessage(main.colorize(main.getConfigurations().getString("Messages.not-online")));
                        return false;
                    }
                        List<ItemStack> inventoryItems = Stream.of(new ItemStack[][]{inventory.getContents(), inventory.getArmorContents()}).flatMap(Stream::of).collect(Collectors.toList());
                        if (inventoryItems.stream().anyMatch(item -> (item != null && !item.getType().equals(Material.AIR)))) {
                            player.sendMessage(main.colorize(main.getConfigurations().getString("Messages.clear-inventory")));
                            return false;
                        }
                        if (!main.getList().contains(player)) {
                            if (main.getList().size() == main.getConfigurations().getInt("players-limite")) {
                                player.sendMessage(main.colorize(main.getConfigurations().getString("Messages.players-limite")));
                                return false;
                            }

                            main.getList().add(player);

                            Location location = new Location(Bukkit.getWorld("Eventos"), 51,19,-67,(float)-88.2,(float)0.2);
                            player.teleport(location);
                            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

                            if (main.getConfigurations().getBoolean("set-effects")) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 90 * 9000, 2));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 90 * 9000, 2));
                            }

                            ItemBuilder stick = new ItemBuilder(Material.STICK).unbreakable(true).enchant(Enchantment.KNOCKBACK, main.getConfigurations().getInt("stick-level"));
                            inventory.setItem(0, stick.build());
                            player.sendTitle("§e§lSnooker", main.colorize("teleported-waiting"));
                        }

                }
            }

            if (args.length == 1) {
               if ("camarote".equalsIgnoreCase(args[0])) {
                    if (snookerManager.isCamarote()) {
                        if (!snookerManager.getCamaroteList().contains(player)) {
                            snookerManager.getCamaroteList().add(player);

                            Location location = new Location(Bukkit.getWorld("Eventos"),72,18,-68);
                            player.teleport(location);
                            player.sendTitle("§e§lSnooker", main.colorize(main.getConfigurations().getString("Messages.teleported-camarote")));
                            return false;
                        }
                        snookerManager.getCamaroteList().remove(player);

                        player.performCommand("spawn");
                        player.sendTitle("§e§lSnooker", main.colorize(main.getConfigurations().getString("Messages.quit-camarote")));

                        return false;
                    }
                    player.sendMessage(main.colorize(main.getConfigurations().getString("MEssages.camarote-off")));
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
                        main.announcement();
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

    protected void announce(String configList) {
        CiberConfig configurations = main.getConfigurations();
        List<String> stringList = configurations.getConfiguration().getStringList(configList);
        stringList.forEach(msg -> Bukkit.broadcastMessage(
                ChatColor.translateAlternateColorCodes('&', msg).
                replace("<amount>", "" + main.getList().size()).
                replace("<amountMax>", "" + main.getConfigurations().getInt("players-limite")))
        );
    }
}
