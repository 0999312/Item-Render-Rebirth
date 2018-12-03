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


import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;

public class KeybindRenderCurrentPlayer {

    private final KeyBinding key;
    private FBOHelper fbo;

    public KeybindRenderCurrentPlayer(int textureSize) {
        fbo = new FBOHelper(textureSize);
        key = new KeyBinding(I18n.format("itemrender.key.currentplayer"), Keyboard.KEY_P, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
            return;
        if (key.isPressed()) {
            Minecraft minecraft = FMLClientHandler.instance().getClient();
            Entity player = ReflectionHelper.getPrivateValue(Minecraft.class, minecraft, "field_175622_Z", "renderViewEntity");
            if (player != null)
                Renderer.renderEntity((EntityLivingBase) player, fbo, "", true);
        }
    }
}
