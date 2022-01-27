package com.hasunemiku2015.inventorygui;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class Container {
    static Plugin eventPlugin;
    static final List<GUIFrame> activeFrames = new ArrayList<>();
    static final List<Object> executorClassInstances = new ArrayList<>();
    static final HashMap<String, GUIFrame> guiFrameMap = new HashMap<>();
    static final HashMap<Player, Object[]> inheritObjects = new HashMap<>();
}