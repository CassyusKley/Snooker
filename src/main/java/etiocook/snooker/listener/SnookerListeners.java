package etiocook.snooker.listener;

import etiocook.snooker.Main;
import etiocook.snooker.manager.SnookerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class SnookerListeners implements Listener {

    final Main main = Main.getInstance();
    final SnookerManager snookerManager = SnookerManager.getInstance();

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (main.getList().contains(player)) {
            if (player.getLocation().getBlock().isLiquid()) {
                main.getList().remove(player);

                player.performCommand("spawn");
                player.getInventory().clear();
                player.sendTitle("§e§lSnooker", main.getconfigString("Messages.disqualified"));

                if (main.getList().size() > 1) {
                    Bukkit.broadcastMessage(main.getconfigString("Messages.players-disqualified").replace(
                            "<player>", player.getName())
                    );
                    return;
                }
                for (Player winner : main.getList()) {
                    snookerManager.setState(false);
                    main.getList().clear();
                    Bukkit.broadcastMessage(main.getconfigString("Messages.winner").replace("<player>", winner.getName()));
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
    public void commands(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (main.getList().contains(player) && snookerManager.isRunning()) {
            if (message.equals("tell") || message.equals("g")) {

            } else {
                player.sendMessage(main.getconfigString("Messages.commands-waiting"));
                main.getconfigString("Messages.commands-waiting");
                event.setCancelled(true);
            }

        }

    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (main.getList().contains(player)) {
            main.getList().remove(player);

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack.getType() == Material.STICK) player.getInventory().remove(itemStack);

            }
        }
    }

    @EventHandler
    public void hiy(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity(), damager = (Player) event.getDamager();

            if (main.getList().contains(player) && main.getList().contains(damager)) {
                if (!snookerManager.isHappening()) {
                    damager.sendMessage(main.getconfigString("Messages.pvp-waiting"));
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

    @EventHandler
    public void health(EntityRegainHealthEvent event) {
        Player player = (Player) event.getEntity();
        if (main.getList().contains(player)) {
            event.setAmount(20.0);

        }

    }

}
