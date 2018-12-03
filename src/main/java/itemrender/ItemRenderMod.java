/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender;


import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itemrender.client.RenderTickHandler;
import itemrender.client.export.ExportUtils;
import itemrender.client.export.ItemList;
import itemrender.client.keybind.*;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod(modid = ItemRenderMod.MODID, name = "Item Render", version = "@VERSION@",
        dependencies = "required-after:Forge@[10.12.2.1147,);", guiFactory = "itemrender.ItemRenderGuiFactory")
public class ItemRenderMod {

    public static final String MODID = "ItemRender";

    public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
    public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
    public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
    public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
    public static final int DEFAULT_PLAYER_SIZE = 1024;

    @Mod.Instance("ItemRender")
    public static ItemRenderMod instance;

    public static Configuration cfg;
    public static boolean gl32_enabled = false;

    public static int mainBlockSize = DEFAULT_MAIN_BLOCK_SIZE;
    public static int gridBlockSize = DEFAULT_GRID_BLOCK_SIZE;
    public static int mainEntitySize = DEFAULT_MAIN_ENTITY_SIZE;
    public static int gridEntitySize = DEFAULT_GRID_ENTITY_SIZE;
    public static int playerSize = DEFAULT_PLAYER_SIZE;
    public static boolean exportVanillaItems = false;
    public static boolean debugMode = false;
    public static List<String> blacklist = new ArrayList<String>();
    public static float renderScale = 1.0F;
    public Logger log;

    @SideOnly(Side.CLIENT)
    private RenderTickHandler renderTickHandler = new RenderTickHandler();

    public static void syncConfig() {
        mainBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockMain", DEFAULT_MAIN_BLOCK_SIZE, "Main size of export block image").getInt();
        gridBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockGrid", DEFAULT_GRID_BLOCK_SIZE, "Grid size of export block image").getInt();
        mainEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityMain", DEFAULT_MAIN_ENTITY_SIZE, "Main size of export entity image").getInt();
        gridEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityGrid", DEFAULT_GRID_ENTITY_SIZE, "Grid size of export entity image").getInt();
        playerSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderPlayer", DEFAULT_PLAYER_SIZE, "Size of export player image").getInt();
        exportVanillaItems = cfg.get(Configuration.CATEGORY_GENERAL, "ExportVanillaItems", false, "Export Vanilla Items").getBoolean();
        debugMode = cfg.get(Configuration.CATEGORY_GENERAL, "DebugMode", false, "Enable debug mode").getBoolean();
        blacklist = Arrays.asList(cfg.get(Configuration.CATEGORY_GENERAL, "BlackList", new String[]{}, "Export blacklist. Format: unlocalizedName@metadata").getStringList());
        if (cfg.hasChanged())
            cfg.save();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        if (event.getSide().isServer()) {
            log.error("Item Render is a client-only mod. Please remove this mod and restart your server.");
            return;
        }
        gl32_enabled = GLContext.getCapabilities().OpenGL32;

        // Config
        cfg = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandItemRender());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide().isServer()) {
            log.error("Item Render is a client-only mod. Please remove this mod and restart your server.");
            return;
        }

        FMLCommonHandler.instance().bus().register(instance);
        FMLCommonHandler.instance().bus().register(renderTickHandler);

        if (gl32_enabled) {
            ExportUtils.INSTANCE = new ExportUtils();

            KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(mainBlockSize, "", Keyboard.KEY_LBRACKET, "Render Block (" + mainBlockSize + ")");
            RenderTickHandler.keybindToRender = defaultRender;
            FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(mainEntitySize, "", Keyboard.KEY_SEMICOLON, "Render Entity (" + mainEntitySize + ")"));
            FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(gridEntitySize, "_grid", Keyboard.KEY_APOSTROPHE, "Render Entity (" + gridEntitySize + ")"));
            FMLCommonHandler.instance().bus().register(defaultRender);
            FMLCommonHandler.instance().bus().register(new KeybindRenderInventoryBlock(gridBlockSize, "_grid", Keyboard.KEY_RBRACKET, "Render Block (" + gridBlockSize + ")"));
            FMLCommonHandler.instance().bus().register(new KeybindToggleRender());
            FMLCommonHandler.instance().bus().register(new KeybindRenderCurrentPlayer(playerSize));
            FMLCommonHandler.instance().bus().register(new KeybindExport());
        } else {
            FMLCommonHandler.instance().bus().register(new KeybindWarn());
            log.error("[Item Render] OpenGL Error, please upgrade your drivers or system.");
        }
    }

    @Mod.EventHandler
    public void postInit(FMLInitializationEvent event) {
        if (event.getSide().isClient())
            ItemList.updateList();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ItemRenderMod.MODID))
            syncConfig();
    }
}
