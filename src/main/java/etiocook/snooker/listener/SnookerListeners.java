package etiocook.snooker.listener;

import etiocook.snooker.Main;
import etiocook.snooker.manager.SnookerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class SnookerListeners implements Listener {

    final Main main = Main.getInstance();
    final SnookerManager snookerManager = new SnookerManager();

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (main.getList().contains(player)) {
            if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER) {

                Location location = new Location(Bukkit.getWorld("AuraF"), 0, 190, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 2L, 2L);
                player.teleport(location);

                player.sendTitle("§e§lSnooker", "§7you were disqualified from the event");

            }
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (main.getList().contains(player)) {
                if (!snookerManager.isHappening()) {
                    event.setCancelled(true);

                }
            }
        }
    }

    @EventHandler
    public void commands(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (main.getList().contains(player) && snookerManager.isRunning()) {
            if (message.equals("tell") || message.equals("g")) {
            } else event.setCancelled(true);

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

}
