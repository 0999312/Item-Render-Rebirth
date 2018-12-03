package itemrender.client.keybind;

import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindRenderEntity
{
  public final KeyBinding key;
  public FBOHelper fbo;
  private String filenameSuffix = "";
  
  public KeybindRenderEntity(int textureSize, String filename_suffix, int keyVal, String des)
  {
    this.fbo = new FBOHelper(textureSize);
    this.filenameSuffix = filename_suffix;
    this.key = new KeyBinding(des, keyVal, "Item Render");
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
      if (minecraft.pointedEntity != null) {
        Renderer.renderEntity((EntityLivingBase)minecraft.pointedEntity, this.fbo, this.filenameSuffix, false);
      }
    }
  }
}
