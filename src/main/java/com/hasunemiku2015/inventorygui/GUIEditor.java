package com.hasunemiku2015.inventorygui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIEditor {
    private final GUIFrame defaultFrame;
    private GUIFrame frame;

    public GUIEditor(GUIFrame frame) {
        this.frame = frame;
        this.defaultFrame = frame.copy();
    }

    /**
     * Changes the item in the specified slot of inventory.
     *
     * @param slot        The slot to be changed.
     * @param material    The item type of the new item.
     * @param displayName The name of the item. (Use & for color codes).
     * @param lore        List of lore to be added to the item
     * @param glint       Whether the item should have an enchantment effect.
     * @param childName   File name of the child frame to be opened when this item is clicked,
     *                    set to null to remove, set an empty string to keep it unchanged.
     */
    public void setItem(int slot, Material material, String displayName, List<String> lore, boolean glint, String childName) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itm = itemStack.getItemMeta();
        assert itm != null;
        itm.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        itm.setLore(lore);
        if (glint) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            itm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itm);
        frame.getInventory().setItem(slot, itemStack);

        if (childName == null) {
            frame.getChildren().remove(slot);
            return;
        }

        if (!childName.equalsIgnoreCase("")) {
            frame.getChildren().put(slot, childName);
        }
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public void reset() {
        frame = defaultFrame;
    }
}
