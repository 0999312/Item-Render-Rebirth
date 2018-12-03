package itemrender.client.keybind;

import itemrender.client.export.ExportUtils;
import itemrender.client.rendering.FBOHelper;
import java.io.IOException;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindExport
{
  public final KeyBinding key;
  public FBOHelper fbo;
  
  public KeybindExport()
  {
    this.key = new KeyBinding("Export Mods", 23, "Item Render");
    ClientRegistry.registerKeyBinding(this.key);
  }
  
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event)
  {
    if (FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
      return;
    }
    if (this.key.isPressed()) {
      try
      {
        ExportUtils.INSTANCE.exportMods();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
