package itemrender.client.rendering;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.relauncher.ReflectionHelper;
import itemrender.ItemRenderMod;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class Renderer
{
  public static void renderEntity(EntityLivingBase entity, FBOHelper fbo, String filenameSuffix, boolean renderPlayer)
  {
    Minecraft minecraft = FMLClientHandler.instance().getClient();
    float scale = ItemRenderMod.renderScale;
    fbo.begin();
    
    AxisAlignedBB aabb = entity.boundingBox;
    double minX = aabb.minX - entity.posX;
    double maxX = aabb.maxX - entity.posX;
    double minY = aabb.minY - entity.posY;
    double maxY = aabb.maxY - entity.posY;
    double minZ = aabb.minZ - entity.posZ;
    double maxZ = aabb.maxZ - entity.posZ;
    
    double minBound = Math.min(minX, Math.min(minY, minZ));
    double maxBound = Math.max(maxX, Math.max(maxY, maxZ));
    
    double boundLimit = Math.max(Math.abs(minBound), Math.abs(maxBound));
    
    GL11.glMatrixMode(5889);
    GL11.glPushMatrix();
    GL11.glLoadIdentity();
    GL11.glOrtho(-boundLimit * 0.75D, boundLimit * 0.75D, -boundLimit * 1.25D, boundLimit * 0.25D, -100.0D, 100.0D);
    
    GL11.glMatrixMode(5888);
    
    GL11.glEnable(2903);
    GL11.glPushMatrix();
    GL11.glTranslatef(0.0F, 0.0F, 50.0F);
    if (renderPlayer) {
      GL11.glScalef(-1.0F, 1.0F, 1.0F);
    } else {
      GL11.glScalef(-scale, scale, scale);
    }
    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
    float f2 = entity.renderYawOffset;
    float f3 = entity.rotationYaw;
    float f4 = entity.rotationPitch;
    float f5 = entity.prevRotationYawHead;
    float f6 = entity.rotationYawHead;
    GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef((float)Math.toDegrees(Math.asin(Math.tan(Math.toRadians(30.0D)))), 1.0F, 0.0F, 0.0F);
    GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
    
    entity.renderYawOffset = ((float)Math.atan(0.025000000372529D) * 20.0F);
    entity.rotationYaw = ((float)Math.atan(0.025000000372529D) * 40.0F);
    entity.rotationPitch = (-(float)Math.atan(0.025000000372529D) * 20.0F);
    entity.rotationYawHead = entity.rotationYaw;
    entity.prevRotationYawHead = entity.rotationYaw;
    GL11.glTranslatef(0.0F, 0.0F, 0.0F);
    RenderManager.instance.playerViewY = 180.0F;
    RenderManager.instance.func_147939_a(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
    entity.renderYawOffset = f2;
    entity.rotationYaw = f3;
    entity.rotationPitch = f4;
    entity.prevRotationYawHead = f5;
    entity.rotationYawHead = f6;
    GL11.glPopMatrix();
    RenderHelper.disableStandardItemLighting();
    GL11.glDisable(32826);
    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glDisable(3553);
    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    
    GL11.glMatrixMode(5889);
    GL11.glPopMatrix();
    
    fbo.end();
    String name = EntityList.getEntityString(entity) == null ? entity.getCommandSenderName() : EntityList.getEntityString(entity);
    fbo.saveToFile(new File(minecraft.mcDataDir, renderPlayer ? "rendered/player.png" : String.format("rendered/entity_%s%s.png", new Object[] { name.replaceAll("[^A-Za-z0-9()\\[\\]]", ""), filenameSuffix })));
    fbo.restoreTexture();
  }
  
  public static void renderItem(ItemStack itemStack, FBOHelper fbo, String filenameSuffix, RenderItem itemRenderer)
  {
    Minecraft minecraft = FMLClientHandler.instance().getClient();
    float scale = ItemRenderMod.renderScale;
    fbo.begin();
    
    GL11.glMatrixMode(5889);
    GL11.glPushMatrix();
    GL11.glLoadIdentity();
    GL11.glOrtho(0.0D, 16.0D, 0.0D, 16.0D, -150.0D, 150.0D);
    
    GL11.glMatrixMode(5888);
    RenderHelper.enableGUIStandardItemLighting();
    
    GL11.glTranslatef(8.0F * (1.0F - scale), 8.0F * (1.0F - scale), 0.0F);
    GL11.glScalef(scale, scale, scale);
    
    RenderBlocks renderBlocks = (RenderBlocks)ReflectionHelper.getPrivateValue(Render.class, itemRenderer, new String[] { "field_147909_c", "renderBlocks" });
    if (!ForgeHooksClient.renderInventoryItem(renderBlocks, minecraft.renderEngine, itemStack, true, 0.0F, 0.0F, 0.0F)) {
      itemRenderer.renderItemIntoGUI(null, minecraft.renderEngine, itemStack, 0, 0);
    }
    GL11.glMatrixMode(5889);
    RenderHelper.disableStandardItemLighting();
    GL11.glPopMatrix();
    
    fbo.end();
    fbo.saveToFile(new File(minecraft.mcDataDir, String.format("rendered/item_%s_%d%s.png", new Object[] { itemStack.getItem().getUnlocalizedName().replaceAll("[^A-Za-z0-9()\\[\\]]", ""), Integer.valueOf(itemStack.getItemDamageForDisplay()), filenameSuffix })));
    fbo.restoreTexture();
  }
  
  public static String getItemBase64(ItemStack itemStack, FBOHelper fbo, RenderItem itemRenderer)
  {
    Minecraft minecraft = FMLClientHandler.instance().getClient();
    
    float scale = ItemRenderMod.renderScale;
    fbo.begin();
    
    GL11.glMatrixMode(5889);
    GL11.glPushMatrix();
    GL11.glLoadIdentity();
    GL11.glOrtho(0.0D, 16.0D, 0.0D, 16.0D, -150.0D, 150.0D);
    
    GL11.glMatrixMode(5888);
    RenderHelper.enableGUIStandardItemLighting();
    
    GL11.glTranslatef(8.0F * (1.0F - scale), 8.0F * (1.0F - scale), 0.0F);
    GL11.glScalef(scale, scale, scale);
    
    RenderBlocks renderBlocks = (RenderBlocks)ReflectionHelper.getPrivateValue(Render.class, itemRenderer, new String[] { "field_147909_c", "renderBlocks" });
    if (!ForgeHooksClient.renderInventoryItem(renderBlocks, minecraft.renderEngine, itemStack, true, 0.0F, 0.0F, 0.0F)) {
      itemRenderer.renderItemIntoGUI(null, minecraft.renderEngine, itemStack, 0, 0);
    }
    GL11.glMatrixMode(5889);
    RenderHelper.disableStandardItemLighting();
    GL11.glPopMatrix();
    
    fbo.end();
    String base64 = fbo.getBase64();
    fbo.restoreTexture();
    return base64;
  }
}
