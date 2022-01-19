package com.hasunemiku2015.inventorygui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

final class Events implements Listener {
    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        for (GUIFrame frame : new ArrayList<>(Container.activeFrames)) {
            if (event.getInventory().equals(frame.getInventory())) {
                if (!frame.isClosable()) {
                    frame.open(player);
                } else {
                    Container.inheritObjects.remove(player);
                    Container.activeFrames.remove(frame);
                }
            }
        }
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        for (GUIFrame frame : Container.guiFrameMap.values()) {
            if (event.getInventory().equals(frame.getInventory())) event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Container.inheritObjects.remove(player);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) throws InvalidChildException {
        Player player = (Player) event.getWhoClicked();

        for (GUIFrame frame : new ArrayList<>(Container.activeFrames)) {
            if (event.getInventory().equals(frame.getInventory())) {
                event.setCancelled(true);

                Object returnData = null;
                for (Object obj : Container.executorClassInstances) {
                    Class<?> cls = obj.getClass();
                    for (Method mth : cls.getDeclaredMethods()) {
                        if (mth.isAnnotationPresent(IGUIExecutor.class)) {
                            String name = mth.getAnnotation(IGUIExecutor.class).name();
                            int[] slots = mth.getAnnotation(IGUIExecutor.class).slots();

                            if (frame.getFileName().equalsIgnoreCase(name) && IntStream.of(slots).anyMatch(x -> x == event.getRawSlot())) {
                                List<Object[]> inputs = new ArrayList<>();
                                inputs.add(new Object[]{event.getRawSlot(), player, Container.inheritObjects.get(player)});
                                inputs.add(new Object[]{player, event.getRawSlot(), Container.inheritObjects.get(player)});
                                inputs.add(new Object[]{Container.inheritObjects.get(player), event.getRawSlot(), player});
                                inputs.add(new Object[]{Container.inheritObjects.get(player), player, event.getRawSlot()});
                                inputs.add(new Object[]{player, Container.inheritObjects.get(player), event.getRawSlot()});
                                inputs.add(new Object[]{event.getRawSlot(), Container.inheritObjects.get(player), player});

                                inputs.add(new Object[]{event.getRawSlot(), player});
                                inputs.add(new Object[]{player, event.getRawSlot()});
                                inputs.add(new Object[]{Container.inheritObjects.get(player), event.getRawSlot()});
                                inputs.add(new Object[]{event.getRawSlot(), Container.inheritObjects.get(player)});
                                inputs.add(new Object[]{Container.inheritObjects.get(player), player});
                                inputs.add(new Object[]{player, Container.inheritObjects.get(player)});

                                inputs.add(new Object[]{event.getRawSlot()});
                                inputs.add(new Object[]{player});
                                inputs.add(new Object[]{Container.inheritObjects.get(player)});
                                inputs.add(new Object[]{});

                                for (Object[] input : inputs) {
                                    try {
                                        returnData = mth.invoke(obj, input);
                                    } catch (Exception ex) {
                                        continue;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                if (frame.getChildren().containsKey(event.getRawSlot())) {
                    GUIFrame childFrameTemplate = Container.guiFrameMap.get(frame.getChildren().get(event.getRawSlot()));

                    if (childFrameTemplate == null) {
                        throw new InvalidChildException("The specified child GUI does not exist!");
                    } else if (returnData instanceof Object[]) {
                        Container.inheritObjects.put(player, (Object[]) returnData);
                        GUIFrame childFrame = childFrameTemplate.copy();
                        player.openInventory(childFrame.getInventory());
                        Container.activeFrames.add(childFrame);
                    }
                }
            }
        }
    }
}