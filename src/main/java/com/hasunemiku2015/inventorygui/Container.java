package com.hasunemiku2015.inventorygui;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class Container {
    static Plugin plugin;
    static List<GUIFrame> activeFrames = new ArrayList<>();
    static HashMap<String, GUIFrame> guiFrameMap = new HashMap<>();
    static HashMap<Class<?>, Object> executorClassMap = new HashMap<>();
    static HashMap<Player, Object[]> inheritObjects = new HashMap<>();
}