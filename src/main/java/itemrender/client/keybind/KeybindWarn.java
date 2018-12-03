package itemrender.client.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeybindWarn
{
  public KeybindWarn()
  {
    ClientRegistry.registerKeyBinding(new KeyBinding("OpenGL Error", 0, "Item Render"));
  }
}
