package itemrender.client.keybind;

import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class KeybindRenderCurrentPlayer
{
  public final KeyBinding key;
  public FBOHelper fbo;
  
  public KeybindRenderCurrentPlayer(int textureSize)
  {
    this.fbo = new FBOHelper(textureSize);
    this.key = new KeyBinding("Render Current Player", 25, "Item Render");
    ClientRegistry.registerKeyBinding(this.key);
  }
  
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event)
  {
    if (FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
      return;
    }
    if (this.key.isPressed())
    {
      Minecraft minecraft = FMLClientHandler.instance().getClient();
      Entity player = (Entity)ReflectionHelper.getPrivateValue(Minecraft.class, minecraft, new String[] { "field_175622_Z", "renderViewEntity" });
      if (player != null) {
        Renderer.renderEntity((EntityLivingBase)player, this.fbo, "", true);
      }
    }
  }
}
