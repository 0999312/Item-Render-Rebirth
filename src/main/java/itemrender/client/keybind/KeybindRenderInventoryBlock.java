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
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

public class KeybindRenderInventoryBlock {

    public final KeyBinding key;
    public FBOHelper fbo;
    private String filenameSuffix = "";
    private RenderItem itemRenderer = new RenderItem();

    public KeybindRenderInventoryBlock(int textureSize, String filename_suffix, int keyVal, String des) {
        fbo = new FBOHelper(textureSize);
        filenameSuffix = filename_suffix;
        key = new KeyBinding(des, keyVal, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
            return;
        if (key.isPressed()) {
            Minecraft minecraft = FMLClientHandler.instance().getClient();
            if (minecraft.thePlayer != null) {
                ItemStack current = minecraft.thePlayer.getCurrentEquippedItem();
                if (current != null && current.getItem() != null) {
                    Renderer.renderItem(current, fbo, filenameSuffix, itemRenderer);
                }
            }
        }
    }
}
