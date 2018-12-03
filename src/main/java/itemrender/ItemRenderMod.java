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


import itemrender.client.RenderTickHandler;
import itemrender.client.export.ExportUtils;
import itemrender.client.export.ItemList;
import itemrender.client.keybind.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod(modid = ItemRenderMod.MODID, name = "Item Render", version = "@VERSION@", guiFactory = "itemrender.ItemRenderGuiFactory", acceptedMinecraftVersions = "[1.12, 1.12.10]")
public class ItemRenderMod {

    static final String MODID = "itemrender";

    public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
    public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
    public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
    public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
    public static final int DEFAULT_PLAYER_SIZE = 1024;
    public static float renderScale = 1.0F;

    @Mod.Instance(MODID)
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
    public Logger log;

    @SideOnly(Side.CLIENT)
    private RenderTickHandler renderTickHandler = new RenderTickHandler();

    private static void syncConfig() {
        mainBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockMain", DEFAULT_MAIN_BLOCK_SIZE, I18n.format("itemrender.cfg.mainblock")).getInt();
        gridBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockGrid", DEFAULT_GRID_BLOCK_SIZE, I18n.format("itemrender.cfg.gridblock")).getInt();
        mainEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityMain", DEFAULT_MAIN_ENTITY_SIZE, I18n.format("itemrender.cfg.mainentity")).getInt();
        gridEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityGrid", DEFAULT_GRID_ENTITY_SIZE, I18n.format("itemrender.cfg.gridentity")).getInt();
        playerSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderPlayer", DEFAULT_PLAYER_SIZE, I18n.format("itemrender.cfg.player")).getInt();
        exportVanillaItems = cfg.get(Configuration.CATEGORY_GENERAL, "ExportVanillaItems", false, I18n.format("itemrender.cfg.vanilla")).getBoolean();
        debugMode = cfg.get(Configuration.CATEGORY_GENERAL, "DebugMode", false, I18n.format("itemrender.cfg.debug")).getBoolean();
        blacklist = Arrays.asList(cfg.get(Configuration.CATEGORY_GENERAL, "BlackList", new String[]{}, I18n.format("itemrender.cfg.blacklist")).getStringList());
        if (cfg.hasChanged())
            cfg.save();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        if (event.getSide().isServer()) {
            log.error(I18n.format("itemrender.msg.clientonly"));
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
            log.error(I18n.format("itemrender.msg.clientonly"));
            return;
        }

        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.EVENT_BUS.register(renderTickHandler);

        if (gl32_enabled) {
            ExportUtils.INSTANCE = new ExportUtils();

            KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(mainBlockSize, "", Keyboard.KEY_LBRACKET, I18n.format("itemrender.key.block", mainBlockSize));
            RenderTickHandler.keybindToRender = defaultRender;
            MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(mainEntitySize, "", Keyboard.KEY_SEMICOLON, I18n.format("itemrender.key.entity", mainEntitySize)));
            MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(gridEntitySize, "_grid", Keyboard.KEY_APOSTROPHE, I18n.format("itemrender.key.entity", gridEntitySize)));
            MinecraftForge.EVENT_BUS.register(defaultRender);
            MinecraftForge.EVENT_BUS.register(new KeybindRenderInventoryBlock(gridBlockSize, "_grid", Keyboard.KEY_RBRACKET, I18n.format("itemrender.key.block", gridBlockSize)));
            MinecraftForge.EVENT_BUS.register(new KeybindToggleRender());
            MinecraftForge.EVENT_BUS.register(new KeybindRenderCurrentPlayer(playerSize));
            MinecraftForge.EVENT_BUS.register(new KeybindExport());
        } else {
            MinecraftForge.EVENT_BUS.register(new KeybindWarn());
            log.error(I18n.format("itemrender.msg.openglerror"));
        }
    }

    @Mod.EventHandler
    public void postInit(FMLInitializationEvent event) {
        if (event.getSide().isClient())
            ItemList.updateList();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ItemRenderMod.MODID))
            syncConfig();
    }
}
