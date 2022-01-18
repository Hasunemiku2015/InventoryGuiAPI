package com.hasunemiku2015.inventorygui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GUIFrame {
    private final String fileName;
    private final HashMap<Integer, String> children;
    private Inventory inventory;
    private final boolean closable;

    protected GUIFrame(String fileName) throws NullPointerException {
        this.fileName = fileName;
        this.children = new HashMap<>();

        YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(
                new InputStreamReader(Objects.requireNonNull(Container.plugin.getResource(fileName))));

        inventory = Bukkit.createInventory(null, ymlFile.getInt("size") ,
                Objects.requireNonNull(ymlFile.getString("name")));
        closable = !ymlFile.contains("closable") || ymlFile.getBoolean("closable");

        ItemStack defaultItem = new ItemStack(Material.AIR, 1);
        String defaultChild = null;

        ConfigurationSection contents = ymlFile.getConfigurationSection("contents");
        if (contents == null) throw new NullPointerException("Cannot find contents of inventory GUI!");
        for(String key : contents.getKeys(false)){
            ConfigurationSection itemStackConfiguration = contents.getConfigurationSection(key);

            assert itemStackConfiguration != null;
            Material item = Material.valueOf(itemStackConfiguration.getString("item"));
            String name = itemStackConfiguration.getString("name") != null ? ChatColor.translateAlternateColorCodes
                    ('&', Objects.requireNonNull(itemStackConfiguration.getString("name"))): null;
            List<String> lore = itemStackConfiguration.getStringList("lore");
            boolean glint = itemStackConfiguration.getString("glint") != null && itemStackConfiguration.
                    getBoolean("glint");
            String child = itemStackConfiguration.getString("child");

            ItemStack itemStack = new ItemStack(item , 1);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(name);
            if(!lore.isEmpty()) meta.setLore(lore);
            if(glint){
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemStack.setItemMeta(meta);

            if(key.equalsIgnoreCase("default")){
                defaultItem = itemStack;
                defaultChild = child;
            } else {
                int slot = Integer.parseInt(key);
                inventory.setItem(slot, itemStack);
                if(child != null)
                    children.put(slot, child);
            }
        }

        for(int i=0;i<inventory.getSize();i++){
            if(inventory.getItem(i) == null){
                inventory.setItem(i, defaultItem);
                if(defaultChild != null){
                    children.put(i, defaultChild);
                }
            }
        }
    }
    private GUIFrame(String fileName, HashMap<Integer, String> children, Inventory inventory, boolean closable){
        this.fileName = fileName;
        this.children = children;
        this.inventory = inventory;
        this.closable = closable;
    }

    protected Inventory getInventory() {
        return inventory;
    }
    protected String getFileName() {
        return fileName;
    }
    protected boolean isClosable(){
        return closable;
    }
    protected HashMap<Integer, String> getChildren() {
        return children;
    }

    protected void setTitle(String title){
        Inventory newInventory = Bukkit.createInventory(null, 9, title);
        newInventory.setContents(inventory.getContents());
        this.inventory = newInventory;
    }

    protected void open(@NotNull Player player){
        player.openInventory(inventory);
    }
    protected GUIFrame copy(){
        Inventory inv = Bukkit.createInventory(null, inventory.getSize());
        inv.setContents(inventory.getContents());
        HashMap<Integer, String> childrenCopy = new HashMap<>(children);

        return new GUIFrame(this.fileName, childrenCopy, inv, closable);
    }
}
