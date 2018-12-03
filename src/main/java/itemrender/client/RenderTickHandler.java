/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender.client;


import itemrender.client.keybind.KeybindRenderInventoryBlock;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class RenderTickHandler {
    public static boolean renderPreview = false;
    public static KeybindRenderInventoryBlock keybindToRender;

    public RenderTickHandler() {
    }

    @SubscribeEvent
    public void tick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            if (keybindToRender != null && renderPreview) {
                int originalTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

                // Bind framebuffer texture
                keybindToRender.fbo.bind();
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2f(0, 0);
                GL11.glVertex2i(0, 0);
                GL11.glTexCoord2f(0, 1);
                GL11.glVertex2i(0, 128);
                GL11.glTexCoord2f(1, 1);
                GL11.glVertex2i(128, 128);
                GL11.glTexCoord2f(1, 0);
                GL11.glVertex2i(128, 0);
                GL11.glEnd();

                // Restore old texture
                GlStateManager.bindTexture(originalTexture);
            }
    }
}