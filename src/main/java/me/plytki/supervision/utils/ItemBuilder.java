package me.plytki.supervision.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemBuilder {
    private Material mat;
    private int amount;
    private short data;
    private String title;
    private List<String> lore;
    private HashMap<Enchantment, Integer> enchants;
    private Color color;
    private Potion potion;

    public ItemBuilder() {}

    public ItemBuilder(Material mat) {
        this(mat, 1);
    }

    public ItemBuilder(Material mat, int amount) {
        this(mat, amount, (short)0);
    }

    public ItemBuilder(Material mat, short data) {
        this(mat, 1, data);
    }

    public ItemBuilder(Material mat, int amount, short data) {
        this.title = null;
        this.lore = new ArrayList<String>();
        this.enchants = new HashMap<Enchantment, Integer>();
        this.mat = mat;
        this.amount = amount;
        this.data = data;
    }

    public ItemBuilder setType(Material mat) {
        this.mat = mat;
        return this;
    }

    public ItemBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ItemBuilder addLores(List<String> lores) {
        this.lore.addAll(lores);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchant, int level) {
        if (this.enchants.containsKey(enchant)) {
            this.enchants.remove(enchant);
        }
        this.enchants.put(enchant, level);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        if (!this.mat.name().contains("LEATHER_")) {
            throw new IllegalArgumentException("Can only dye leather armor!");
        }
        this.color = color;
        return this;
    }

    public ItemBuilder setPotion(Potion potion) {
        if (this.mat != Material.POTION) {
            this.mat = Material.POTION;
        }
        this.potion = potion;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build() {
        Material mat = this.mat;
        if (mat == null) {
            mat = Material.AIR;
            Bukkit.getLogger().warning("Null material!");
        }
        ItemStack item = new ItemStack(this.mat, this.amount, this.data);
        ItemMeta meta = item.getItemMeta();
        if (this.title != null) {
            meta.setDisplayName(this.title);
        }
        if (!this.lore.isEmpty()) {
            meta.setLore((List)this.lore);
        }
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)meta).setColor(this.color);
        }
        item.setItemMeta(meta);
        item.addUnsafeEnchantments((Map)this.enchants);
        if (this.potion != null) {
            this.potion.apply(item);
        }
        return item;
    }

    public ItemBuilder clone() {
        ItemBuilder newBuilder = new ItemBuilder(this.mat);
        newBuilder.setTitle(this.title);
        for (String lore : this.lore) {
            newBuilder.addLore(lore);
        }
        for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
            newBuilder.addEnchantment(entry.getKey(), entry.getValue());
        }
        newBuilder.setColor(this.color);
        newBuilder.potion = this.potion;
        return newBuilder;
    }

    public Material getType() {
        return this.mat;
    }

    public String getTitle() {
        return this.title;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean hasEnchantment(Enchantment enchant) {
        return this.enchants.containsKey(enchant);
    }

    public int getEnchantmentLevel(Enchantment enchant) {
        return this.enchants.get(enchant);
    }

    public HashMap<Enchantment, Integer> getAllEnchantments() {
        return this.enchants;
    }

    public boolean isItem(ItemStack item) {
        return this.isItem(item, false);
    }

    public boolean isItem(ItemStack item, boolean strictDataMatch) {
        ItemMeta meta = item.getItemMeta();
        if (item.getType() != this.getType()) {
            return false;
        }
        if (!meta.hasDisplayName() && this.getTitle() != null) {
            return false;
        }
        if (!meta.getDisplayName().equals(this.getTitle())) {
            return false;
        }
        if (!meta.hasLore() && !this.getLore().isEmpty()) {
            return false;
        }
        if (meta.hasLore()) {
            for (String lore : meta.getLore()) {
                if (!this.getLore().contains(lore)) {
                    return false;
                }
            }
        }
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            if (!this.hasEnchantment(enchant)) {
                return false;
            }
        }
        return true;
    }

    public int getItemAmount(Material material, Player gracz, short durability) {
        int am = 0;
        ItemStack[] eq;
        for (int dlugosc = (eq = gracz.getInventory().getContents()).length, i = 0; i < dlugosc; ++i) {
            ItemStack itemStack = eq[i];
            if(itemStack != null && itemStack.getType().equals(material) && itemStack.getDurability() == durability) {
                am += itemStack.getAmount();
            }
        }
        return am;
    }

    public boolean remove(ItemStack itemStack, Player player, int amount) {
        AtomicInteger actual = new AtomicInteger();
        if(player.getInventory().containsAtLeast(itemStack, amount)) {
            player.getInventory().all(itemStack.getType()).forEach((value, item) -> {
                if(item.getType() == itemStack.getType()) {
                    if(actual.get() > amount) {
                        ItemStack im = new ItemStack(itemStack.getType(), actual.get()-amount);
                        if(im.getAmount() != 0) {
                            player.getInventory().addItem(im);
                        }
                        return;
                    }
                    if(actual.get() <= amount) {
                        player.getInventory().remove(item);
                        actual.getAndAdd(item.getAmount());
                    }
                }
            });
            ItemStack im = new ItemStack(itemStack.getType(), actual.get()-amount);
            if(im.getAmount() != 0) {
                player.getInventory().addItem(im);
            }
            return true;
        }
        return false;
    }
}