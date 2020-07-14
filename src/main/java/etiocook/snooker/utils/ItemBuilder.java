package etiocook.snooker.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemBuilder(Material material) {

        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();

    }

    public ItemBuilder(Material material, int amount) {

        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();

    }

    public ItemBuilder name(String name) {
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return this;

    }

    public ItemBuilder lore(String... lore) {

        itemMeta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(itemMeta);

        return this;

    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemStack.setItemMeta(itemMeta);
        return this;
    }
    
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder durability(short data) {
        itemStack.setDurability(data);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

}
