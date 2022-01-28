package com.hasunemiku2015.inventorygui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class GUIRegistry {
    private static boolean isEnabled = false;
    protected Plugin resourcePlugin;

    /**
     * Creates a new GUIRegistry Object, this object registers IGUIExecutor classes and opens Inventory GUI for players.
     * GUIRegistry objects should be singletons.
     *
     * @param plugin The instance of your "main plugin class".
     * @throws IllegalArgumentException Error thrown when a GUIRegistry Object is already instantiated.
     */
    public GUIRegistry(Plugin plugin) throws IllegalArgumentException {
        resourcePlugin = plugin;
        if (!isEnabled) {
            Container.eventPlugin = plugin;
            Bukkit.getServer().getPluginManager().registerEvents(new Events(), Container.eventPlugin);
            isEnabled = true;
        }
    }

    /**
     * Register all classes containing methods that contain a IGUIExecutor annotation here.
     *
     * @param clazz The class containing IGUIExecutor methods (Obtainable by .class), the class should be a java bean.
     * @throws IllegalArgumentException Thrown when the class does not contain any IGUIExecutor methods.
     * @throws FileNotFoundException    Thrown when the methods within the class reference to an unknown yml file.
     */
    public void registerExecutors(Class<?> clazz) throws Exception {
        boolean hasAnnotation = false;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(IGUIExecutor.class)) {
                method.setAccessible(true);
                String name = method.getAnnotation(IGUIExecutor.class).name();
                if (!Container.guiFrameMap.containsKey(name)) {
                    String fileName = name.endsWith(".yml") ? name : name.concat(".yml");

                    if (resourcePlugin.getResource(fileName) == null) {
                        throw new FileNotFoundException("Cannot find yml file " + fileName + "in resources directory!");
                    } else {
                        Container.guiFrameMap.put(fileName, new GUIFrame(resourcePlugin, fileName));
                    }
                }
                hasAnnotation = true;
            }
        }
        if (hasAnnotation) Container.executorClassInstances.add(clazz.getConstructor(new Class[0]).newInstance());
        else
            throw new IllegalArgumentException("The class registered does not contain a IGUIExecutor annotated method!");
    }

    /**
     * Opens a GUI with specified fileName for player.
     *
     * @param player   The player to open the GUI for.
     * @param fileName File name of the yml that describes the GUI.
     * @return A GUIFrame object, expected to be used in a constructing GUIEditor and nothing else.
     */
    public GUIFrame openGUI(Player player, String fileName) {
        String name = fileName.endsWith(".yml") ? fileName : fileName.concat(".yml");
        GUIFrame frame = Container.guiFrameMap.get(fileName).copy();

        Container.activeFrames.add(frame);
        frame.open(player);
        return frame;
    }

    /**
     * Opens a GUI with specified fileName for player.
     *
     * @param player        The player to open the GUI for.
     * @param fileName      File name of the yml that describes the GUI.
     * @param preLaunchTask Actions that are to be done before opening the inventory for player.
     * @return A GUIFrame object, expected to be used in a constructing GUIEditor and nothing else.
     */
    public GUIFrame openGUI(Player player, String fileName, Consumer<GUIEditor> preLaunchTask) {
        String name = fileName.endsWith(".yml") ? fileName : fileName.concat(".yml");
        GUIFrame frame = Container.guiFrameMap.get(name).copy();
        GUIEditor editor = new GUIEditor(frame);
        preLaunchTask.accept(editor);

        Container.activeFrames.add(frame);
        frame.open(player);
        return frame;
    }
}
