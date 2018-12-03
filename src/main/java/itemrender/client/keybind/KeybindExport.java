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

import itemrender.client.export.ExportUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class KeybindExport {
    public final KeyBinding key;

    public KeybindExport() {
        key = new KeyBinding(I18n.format("itemrender.key.export"), Keyboard.KEY_I, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) throws IllegalAccessException, Throwable {
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
