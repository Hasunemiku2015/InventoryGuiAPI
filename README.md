# Hatsunemiku2015's InventoryGUI API
> ### This API is created by a thrid-party and has no realtion with Spigot.
&nbsp;

This API allows the creation of InventoryGUI in spigot plugins at ease. It also support creating and wiring sub-guis that open after certain slot is clicked in main GUI. **No more spaghetti code for wiring!**

## How to import?
    1. By importing external jar
Download the jar file from github and import into your ide.
&nbsp;

    2. Using maven
Copy this into your pom.xml:
- Between repository tags:
```xml
  <repository>
    Work in progress!
  </repository>
```
- Between dependency tags:
```xml
  <dependency>
    <groupId>com.hasunemiku2015</groupId>
    <artifactId>inventorygui</artifactId>
    <version>0.1</version>
    <scope>compile</scope>
  </dependency>
```
## How to use?
    To create a InventoryGUI, create a .yml file in your plugin's resource directory

- The resource directory is where your plugin.yml is located.
- Rename the file to the YOUR_GUI_NAME.yml , where YOUR_GUI_NAME is the executor reference you will wire to in your plugin
- The yml should have the following format (use & for color code):
```yml
name: '&0gui-display-name' #Mandatory, controls the title of your gui
size: 9                    #Mandatory, controls the size of your gui. (multiple of 9)
closable: true             #Optional,  configure if the gui is closable by player.
contents:
  default:                 #Optional,  this defines the item in slots with nothing configured.
    item: AIR              #Mandatory, this defines the item type in slot
    name: 'name'           #Optional,  this controls the display name of the item
    lore:                  #Optional,  this controls the lore of the item
      - 'lore line 1'
      - 'lore line 2'
    glint: false           #Optional,  configures if the item should have a enchantment glint.
    child: ''              #Optional,  this configures the child gui of this slot.

  1:                       #"Optional", this is the itemslot config.
    item: AIR
    name: '&0name2'
    lore:
      - 'lore line 1'
      - 'lore line 2'
    glint: false
    child: ''
```
&nbsp;

    Now you created the gui, time to wire it to executors in your code.

- To create the executor in your code, create classes and executor methods similar to the example code below.

```java
public class MyClass {
    // You can create multiple executors in a class.
    // The class should be public and have no constructors

    //Annotate with suppress warning if you don't want your IDE to throw error.
    @SuppressWarnings("unused")
    // name: The file name of your gui (.yml file)
    // slots: an array of slots that this executor will respond to
    @IGUIExecutor(name="GUINAME", slots={0})
    // Any of the follow input parameter could be supplied
    // int slot: The slot number clicked.
    // Player player: The player who clicked in the InventoryGUI.
    // Object[] args: Data supplied after executor of parent InventoryGUI.
    public Object[] someMethod(int slots, Player player, Object[] args) {
        return null;
    }

    // Here is another example
    @SuppressWarnings("unused")
    @IGUIExecutor(name="GUINAME2", slots={0, 1, 2, 3})
    public Object[] someMethod(int slots, Player player, Object[] args) {
      if(slot == 2){
        player.sendMessage("Hello World!");
      }
      return null;
    }
}
```
- After that, remember to register your GUIExecutor class in onEnable.
```java
    public class MyPlugin extends JavaPlugin {
        public static GUIRegistry registry;

        public void onEnable() throws FileNotFoundException {
          registry = new GUIRegistry(this);

          try {
            // Remember it is .class not a object of the class
            registry.registerExecutors(Executor.class);
            registry.registerExecutors(Executor2.class);
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        }
    }
```
- You can open the inventory gui for a player with the following method.
```java
  registry.openGUI(player, "fileName");
```

> Last but not least, be sure to check out the docstring of various methods for a more detailed description.