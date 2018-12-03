package itemrender;

import itemrender.client.RenderTickHandler;
import itemrender.client.export.ExportUtils;
import itemrender.client.export.ItemList;
import itemrender.client.keybind.KeybindExport;
import itemrender.client.keybind.KeybindRenderCurrentPlayer;
import itemrender.client.keybind.KeybindRenderEntity;
import itemrender.client.keybind.KeybindRenderInventoryBlock;
import itemrender.client.keybind.KeybindToggleRender;
import itemrender.client.keybind.KeybindWarn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

@Mod(modid="ItemRender", name="Item Render", version="3.0", dependencies="required-after:Forge@[10.12.2.1147,);", guiFactory="itemrender.ItemRenderGuiFactory", acceptedMinecraftVersions="[1.9, 1.10.2]")
public class ItemRenderMod
{
  public static final String MODID = "ItemRender";
  public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
  public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
  public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
  public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
  public static final int DEFAULT_PLAYER_SIZE = 1024;
  public static float renderScale = 1.0F;
  @Mod.Instance("ItemRender")
  public static ItemRenderMod instance;
  public static Configuration cfg;
  public static boolean gl32_enabled = false;
  public static int mainBlockSize = 128;
  public static int gridBlockSize = 32;
  public static int mainEntitySize = 512;
  public static int gridEntitySize = 128;
  public static int playerSize = 1024;
  public static boolean exportVanillaItems = false;
  public static boolean debugMode = false;
  public static List<String> blacklist = new ArrayList();
  public Logger log;
  @SideOnly(Side.CLIENT)
  private RenderTickHandler renderTickHandler = new RenderTickHandler();
  
  public static void syncConfig()
  {
    mainBlockSize = cfg.get("general", "RenderBlockMain", 128, "Main size of export block image").getInt();
    gridBlockSize = cfg.get("general", "RenderBlockGrid", 32, "Grid size of export block image").getInt();
    mainEntitySize = cfg.get("general", "RenderEntityMain", 512, "Main size of export entity image").getInt();
    gridEntitySize = cfg.get("general", "RenderEntityGrid", 128, "Grid size of export entity image").getInt();
    playerSize = cfg.get("general", "RenderPlayer", 1024, "Size of export player image").getInt();
    exportVanillaItems = cfg.get("general", "ExportVanillaItems", false, "Export Vanilla Items").getBoolean();
    debugMode = cfg.get("general", "DebugMode", false, "Enable debug mode").getBoolean();
    blacklist = Arrays.asList(cfg.get("general", "BlackList", new String[0], "Export blacklist. Format: unlocalizedName@metadata").getStringList());
    if (cfg.hasChanged()) {
      cfg.save();
    }
  }
  
  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event)
  {
    this.log = event.getModLog();
    if (event.getSide().isServer())
    {
      this.log.error("Item Render is a client-only mod. Please remove this mod and restart your server.");
      return;
    }
    gl32_enabled = GLContext.getCapabilities().OpenGL32;
    

    cfg = new Configuration(event.getSuggestedConfigurationFile());
    syncConfig();
  }
  
  @Mod.EventHandler
  public void serverStarting(FMLServerStartingEvent event)
  {
    event.registerServerCommand(new CommandItemRender());
  }
  
  @Mod.EventHandler
  public void init(FMLInitializationEvent event)
  {
    if (event.getSide().isServer())
    {
      this.log.error("Item Render is a client-only mod. Please remove this mod and restart your server.");
      return;
    }
    MinecraftForge.EVENT_BUS.register(instance);
    MinecraftForge.EVENT_BUS.register(this.renderTickHandler);
    if (gl32_enabled)
    {
      ExportUtils.INSTANCE = new ExportUtils();
      
      KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(mainBlockSize, "", 26, "Render Block (" + mainBlockSize + ")");
      RenderTickHandler.keybindToRender = defaultRender;
      MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(mainEntitySize, "", 39, "Render Entity (" + mainEntitySize + ")"));
      MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(gridEntitySize, "_grid", 40, "Render Entity (" + gridEntitySize + ")"));
      MinecraftForge.EVENT_BUS.register(defaultRender);
      MinecraftForge.EVENT_BUS.register(new KeybindRenderInventoryBlock(gridBlockSize, "_grid", 27, "Render Block (" + gridBlockSize + ")"));
      MinecraftForge.EVENT_BUS.register(new KeybindToggleRender());
      MinecraftForge.EVENT_BUS.register(new KeybindRenderCurrentPlayer(playerSize));
      MinecraftForge.EVENT_BUS.register(new KeybindExport());
    }
    else
    {
      MinecraftForge.EVENT_BUS.register(new KeybindWarn());
      this.log.error("[Item Render] OpenGL Error, please upgrade your drivers or system.");
    }
  }
  
  @Mod.EventHandler
  public void postInit(FMLInitializationEvent event)
  {
    if (event.getSide().isClient()) {
      ItemList.updateList();
    }
  }
  
  @SubscribeEvent
  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
  {
    if (event.getModID().equals("ItemRender")) {
      syncConfig();
    }
  }
}
