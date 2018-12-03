package itemrender.client.keybind;

import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindRenderInventoryBlock
{
  public final KeyBinding key;
  public FBOHelper fbo;
  private String filenameSuffix = "";
  private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
  
  public KeybindRenderInventoryBlock(int textureSize, String filename_suffix, int keyVal, String des)
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
      if (minecraft.thePlayer != null)
      {
        ItemStack current = minecraft.thePlayer.getCurrentEquippedItem();
        if ((current != null) && (current.getItem() != null)) {
          Renderer.renderItem(current, this.fbo, this.filenameSuffix, this.itemRenderer);
        }
      }
    }
  }
}
