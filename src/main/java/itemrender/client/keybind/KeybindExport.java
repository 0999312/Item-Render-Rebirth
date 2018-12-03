/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.client.keybind;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import itemrender.client.export.ExportUtils;
import itemrender.client.rendering.FBOHelper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class KeybindExport {
    public final KeyBinding key;
    public FBOHelper fbo;

    public KeybindExport() {
        key = new KeyBinding("Export Mods", Keyboard.KEY_I, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
            return;
        if (key.isPressed()) {
            try {
                ExportUtils.INSTANCE.exportMods();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
