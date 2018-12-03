package itemrender.client.rendering;

import itemrender.ItemRenderMod;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class Renderer
{
  public static void renderEntity(EntityLivingBase entity, FBOHelper fbo, String filenameSuffix, boolean renderPlayer)
  {
    Minecraft minecraft = FMLClientHandler.instance().getClient();
    float scale = ItemRenderMod.renderScale;
    fbo.begin();
    
    AxisAlignedBB aabb = entity.getEntityBoundingBox();
    double minX = aabb.minX - entity.posX;
    double maxX = aabb.maxX - entity.posX;
    double minY = aabb.minY - entity.posY;
    double maxY = aabb.maxY - entity.posY;
    double minZ = aabb.minZ - entity.posZ;
    double maxZ = aabb.maxZ - entity.posZ;
    
    double minBound = Math.min(minX, Math.min(minY, minZ));
    double maxBound = Math.max(maxX, Math.max(maxY, maxZ));
    
    double boundLimit = Math.max(Math.abs(minBound), Math.abs(maxBound));
    
    GlStateManager.matrixMode(5889);
    GlStateManager.pushMatrix();
    GlStateManager.loadIdentity();
    GlStateManager.ortho(-boundLimit * 0.75D, boundLimit * 0.75D, -boundLimit * 1.25D, boundLimit * 0.25D, -100.0D, 100.0D);
    
    GlStateManager.matrixMode(5888);
    

    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0F, 0.0F, 50.0F);
    if (renderPlayer) {
      GlStateManager.scale(-1.0F, 1.0F, 1.0F);
    } else {
      GlStateManager.scale(-scale, scale, scale);
    }
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    float f2 = entity.renderYawOffset;
    float f3 = entity.rotationYaw;
    float f4 = entity.rotationPitch;
    float f5 = entity.prevRotationYawHead;
    float f6 = entity.rotationYawHead;
    GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
    
    GlStateManager.rotate((float)Math.toDegrees(Math.asin(Math.tan(Math.toRadians(30.0D)))), 1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
    
    entity.renderYawOffset = ((float)Math.atan(0.025000000372529D) * 20.0F);
    entity.rotationYaw = ((float)Math.atan(0.025000000372529D) * 40.0F);
    entity.rotationPitch = (-(float)Math.atan(0.025000000372529D) * 20.0F);
    entity.rotationYawHead = entity.rotationYaw;
    entity.prevRotationYawHead = entity.rotationYaw;
    GlStateManager.translate(0.0F, 0.0F, 0.0F);
    RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
    rendermanager.doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
    rendermanager.setRenderShadow(true);
    entity.renderYawOffset = f2;
    entity.rotationYaw = f3;
    entity.rotationPitch = f4;
    entity.prevRotationYawHead = f5;
    entity.rotationYawHead = f6;
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    
    GlStateManager.matrixMode(5889);
    GlStateManager.popMatrix();
    
    fbo.end();
    String name = EntityList.getEntityString(entity) == null ? entity.getName() : EntityList.getEntityString(entity);
    fbo.saveToFile(new File(minecraft.mcDataDir, renderPlayer ? "rendered/player.png" : String.format("rendered/entity_%s%s.png", new Object[] { name.replaceAll("[^A-Za-z0-9()\\[\\]]", ""), filenameSuffix })));
    fbo.restoreTexture();
  }
  
  public static void renderItem(ItemStack itemStack, FBOHelper fbo, String filenameSuffix, RenderItem itemRenderer)
  {
    Minecraft minecraft = FMLClientHandler.instance().getClient();
    float scale = ItemRenderMod.renderScale;
    fbo.begin();
    
    GlStateManager.matrixMode(5889);
    GlStateManager.pushMatrix();
    GlStateManager.loadIdentity();
    GlStateManager.ortho(0.0D, 16.0D, 0.0D, 16.0D, -150.0D, 150.0D);
    
    GlStateManager.matrixMode(5888);
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableColorMaterial();
    GlStateManager.enableLighting();
    
    GlStateManager.translate(8.0F * (1.0F - scale), 8.0F * (1.0F - scale), 0.0F);
    GlStateManager.scale(scale, scale, scale);
    
    itemRenderer.renderItemIntoGUI(itemStack, 0, 0);
    
    GlStateManager.disableLighting();
    RenderHelper.disableStandardItemLighting();
    
    GlStateManager.matrixMode(5889);
    GL11.glPopMatrix();
    
    fbo.end();
    fbo.saveToFile(new File(minecraft.mcDataDir, String.format("rendered/item_%s_%d%s.png", new Object[] { itemStack.getItem().getUnlocalizedName().replaceAll("[^A-Za-z0-9()\\[\\]]", ""), Integer.valueOf(itemStack.getItemDamage()), filenameSuffix })));
    fbo.restoreTexture();
  }
  
  public static String getItemBase64(ItemStack itemStack, FBOHelper fbo, RenderItem itemRenderer)
  {
    float scale = ItemRenderMod.renderScale;
    fbo.begin();
    
    GlStateManager.matrixMode(5889);
    GlStateManager.pushMatrix();
    GlStateManager.loadIdentity();
    GlStateManager.ortho(0.0D, 16.0D, 0.0D, 16.0D, -150.0D, 150.0D);
    
    GlStateManager.matrixMode(5888);
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableColorMaterial();
    GlStateManager.enableLighting();
    
    GlStateManager.translate(8.0F * (1.0F - scale), 8.0F * (1.0F - scale), 0.0F);
    GlStateManager.scale(scale, scale, scale);
    
    itemRenderer.renderItemIntoGUI(itemStack, 0, 0);
    
    GlStateManager.disableLighting();
    RenderHelper.disableStandardItemLighting();
    
    GlStateManager.matrixMode(5889);
    GL11.glPopMatrix();
    
    fbo.end();
    String base64 = fbo.getBase64();
    fbo.restoreTexture();
    return base64;
  }
}
