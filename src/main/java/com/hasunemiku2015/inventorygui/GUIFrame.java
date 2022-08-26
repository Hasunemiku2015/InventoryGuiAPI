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
import org.bukkit.plugin.Plugin;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class GUIFrame {
    private final String fileName;
    private String displayName;
    private final HashMap<Integer, String> children;
    private Inventory inventory;
    private final boolean closable;

    protected GUIFrame(Plugin resourcePlugin, String fileName) throws NullPointerException {
        this.fileName = fileName;
        this.children = new HashMap<>();

        YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(
                new InputStreamReader(Objects.requireNonNull(resourcePlugin.getResource(fileName))));
        inventory = createInventory(ymlFile.getString("name"), ymlFile.getInt("size"));
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
            List<String> lore = itemStackConfiguration.getStringList("lore").stream().map
                    (str -> ChatColor.translateAlternateColorCodes('&', str)).collect(Collectors.toList());
            boolean glint = itemStackConfiguration.getBoolean("glint");
            String ymlChild = itemStackConfiguration.getString("child");
            String child = ymlChild == null ? null : ymlChild.endsWith(".yml") ? ymlChild : ymlChild.concat(".yml");

            ItemStack itemStack = new ItemStack(item , 1);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(name);
            if(!lore.isEmpty()) meta.setLore(lore);
            if(glint){
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
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

    protected void open(Player player){
        player.openInventory(inventory);
    }
    protected GUIFrame copy(){
        Inventory inv = createInventory(displayName, this.inventory.getSize());
        inv.setContents(inventory.getContents());
        HashMap<Integer, String> childrenCopy = new HashMap<>(children);

        return new GUIFrame(this.fileName, childrenCopy, inv, closable);
    }

    private Inventory createInventory(String name, int size){
        displayName = name;
        return name == null ? Bukkit.createInventory(null, size) : Bukkit.createInventory(null, size,
                ChatColor.translateAlternateColorCodes('&', name));
    }
}
