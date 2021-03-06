package etiocook.snooker.listener;

import etiocook.snooker.Main;
import etiocook.snooker.manager.SnookerManager;
import etiocook.snooker.utils.CiberConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;


public class SnookerListeners implements Listener {

    final Main main = Main.getInstance();
    final SnookerManager snookerManager = SnookerManager.getInstance();

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CiberConfig config = main.getConfigurations();

        if (main.getList().contains(player)) {
            if (player.getLocation().getBlock().isLiquid()) {
                main.getList().remove(player);
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

                player.performCommand("spawn");
                player.getInventory().clear();
                player.sendTitle("§e§lSnooker", main.colorize(config.getString("Messages.disqualified")));

                if (main.getList().size() > 1) {
                    main.getList().forEach(playersList -> playersList.sendMessage(
                            main.colorize(main.getConfigurations().getString(
                                    "Messages.players-disqualified").replace(
                            "<player>", player.getName())))
                    );
                    return;
                }
                for (Player winner : main.getList()) {
                    snookerManager.setState(false);
                    snookerManager.setCamarote(false);
                    Bukkit.broadcastMessage(main.colorize(config.getString("Messages.winner").replace("<player>", winner.getName())));

                    winner.getInventory().clear();
                    winner.getActivePotionEffects().forEach(potionEffect -> winner.removePotionEffect(potionEffect.getType()));
                    snookerManager.getCamaroteList().forEach(playerCamarote -> playerCamarote.performCommand("spawn"));
                    snookerManager.getCamaroteList().clear();

                    winner.sendMessage("§aVocê será teleportado para o spawn em 15 segundos");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            winner.performCommand("spawn");

                            List<String> rewards = config.getConfiguration().getStringList("winnerReward");
                            rewards.forEach(reward -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("<winner>", winner.getName())));

                            main.getList().clear();
                            cancel();
                        }
                    }.runTaskLaterAsynchronously(main,20 * 15);
                }

            }
        }
    }

 /*   @EventHandler
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (main.getList().contains(player)) {
                if (!snookerManager.isHappening()) {

                    player.sendMessage(main.getConfigurations().getString("pvp-waiting"));
                    event.setCancelled(true);
                }
            }
        }
    }*/
 @EventHandler
 public void click(InventoryClickEvent e) {

     Player player = (Player) e.getWhoClicked();
     if (main.getList().contains(player)) e.setCancelled(true);

 }

    @EventHandler
    public void click(PlayerDropItemEvent e) {

        Player player = e.getPlayer();
        if (main.getList().contains(player)) e.setCancelled(true);

    }
    @EventHandler
    public void commands(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (main.getList().contains(player) && snookerManager.isRunning()) {
            if (message.equals("tell") || message.equals("g")) {

            } else {
                player.sendMessage(main.colorize(main.getConfigurations().getString("Messages.commands-waiting")));
                event.setCancelled(true);
            }

        }

    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (snookerManager.getCamaroteList().contains(player)) snookerManager.getCamaroteList().remove(player);
        if (main.getList().contains(player)) {
            main.getList().remove(player);

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack.getType() == Material.STICK) player.getInventory().remove(itemStack);

            }
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
    }

    @EventHandler
    public void hiy(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity(), damager = (Player) event.getDamager();

            if (main.getList().contains(player) && main.getList().contains(damager)) {
                if (!snookerManager.isHappening()) {
                    damager.sendMessage(main.colorize(main.getConfigurations().getString("Messages.pvp-waiting")));
                    event.setCancelled(true);
                    return;
                }

                if (damager.getItemInHand().getType() == Material.STICK) event.setDamage(0);
            }
        }

    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (main.getList().contains(player)) {
            event.setFoodLevel(20);

        }
    }

 /*   @EventHandler
    public void health(EntityRegainHealthEvent event) {
        Player player = (Player) event.getEntity();
        if (main.getList().contains(player)) {
            event.setAmount(20.0);

        }

    }*/

}
