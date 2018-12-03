package itemrender.client.keybind;

import itemrender.client.RenderTickHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindToggleRender
{
  public final KeyBinding key;
  
  public KeybindToggleRender()
  {
    this.key = new KeyBinding("Toggle Render", 24, "Item Render");
    ClientRegistry.registerKeyBinding(this.key);
  }
  
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event)
  {
    if (FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
      return;
    }
    if (this.key.isPressed()) {
      RenderTickHandler.renderPreview = !RenderTickHandler.renderPreview;
    }
  }
}
