/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.client.rendering;

import itemrender.ItemRenderMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;

import org.lwjgl.opengl.GL11;

import java.io.File;

/**
 * Created by Jerrell Fang on 2/23/2015.
 *
 * @author Meow J
 */
public class Renderer {

    public static void renderEntity(EntityLivingBase entity, FBOHelper fbo, String filenameSuffix, boolean renderPlayer) {
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

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(-boundLimit * 0.75, boundLimit * 0.75, -boundLimit * 1.25, boundLimit * 0.25, -100.0, 100.0);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        // Render entity
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 50.0F);

        if (renderPlayer)
            GlStateManager.scale(-1F, 1F, 1F);
        else
            GlStateManager.scale(-scale, scale, scale);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.rotate((float) Math.toDegrees(Math.asin(Math.tan(Math.toRadians(30)))), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-45, 0.0F, 1.0F, 0.0F);

        entity.renderYawOffset = (float) Math.atan((double) (1 / 40.0F)) * 20.0F;
        entity.rotationYaw = (float) Math.atan((double) (1 / 40.0F)) * 40.0F;
        entity.rotationPitch = -((float) Math.atan((double) (1 / 40.0F))) * 20.0F;
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

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();

        fbo.end();
        String name = EntityList.getEntityString(entity) == null ? entity.getName() : EntityList.getEntityString(entity);
        fbo.saveToFile(new File(minecraft.mcDataDir, renderPlayer ? "rendered/player.png" : String.format("rendered/entity_%s%s.png", name.replaceAll("[^A-Za-z0-9()\\[\\]]", ""), filenameSuffix)));
        fbo.restoreTexture();
    }

    public static void renderItem(ItemStack itemStack, FBOHelper fbo, String filenameSuffix, RenderItem itemRenderer) {
        Minecraft minecraft = FMLClientHandler.instance().getClient();
        float scale = ItemRenderMod.renderScale;
        fbo.begin();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, 16, 0, 16, -150.0F, 150.0F);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();

        GlStateManager.translate(8 * (1 - scale), 8 * (1 - scale), 0);
        GlStateManager.scale(scale, scale, scale);

        itemRenderer.renderItemIntoGUI(itemStack, 0, 0);

        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        fbo.end();
        fbo.saveToFile(new File(minecraft.mcDataDir, String.format("rendered/item_%s_%d%s.png", itemStack.getItem().getUnlocalizedName().replaceAll("[^A-Za-z0-9()\\[\\]]", ""), itemStack.getItemDamage(), filenameSuffix)));
        fbo.restoreTexture();
    }

    public static String getItemBase64(ItemStack itemStack, FBOHelper fbo, RenderItem itemRenderer) {
        String base64;
        float scale = ItemRenderMod.renderScale;
        fbo.begin();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, 16, 0, 16, -150.0F, 150.0F);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();

        GlStateManager.translate(8 * (1 - scale), 8 * (1 - scale), 0);
        GlStateManager.scale(scale, scale, scale);

        itemRenderer.renderItemIntoGUI(itemStack, 0, 0);

        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        fbo.end();
        base64 = fbo.getBase64();
        fbo.restoreTexture();
        return base64;
    }
    public static String getEntityBase64(EntityEntry Entitymob, FBOHelper fbo){
    	 String base64;
    	Minecraft minecraft = FMLClientHandler.instance().getClient();
    	if(!(Entitymob.newInstance(minecraft.world) instanceof EntityLivingBase)){
    		return "";
    	}else{
        float scale = ItemRenderMod.renderScale;
        fbo.begin();
        EntityLivingBase entity = (EntityLivingBase) Entitymob.newInstance(minecraft.world);
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

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(-boundLimit * 0.75, boundLimit * 0.75, -boundLimit * 1.25, boundLimit * 0.25, -100.0, 100.0);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        // Render entity
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 50.0F);
        GlStateManager.scale(-scale, scale, scale);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.rotate((float) Math.toDegrees(Math.asin(Math.tan(Math.toRadians(30)))), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-45, 0.0F, 1.0F, 0.0F);

        entity.renderYawOffset = (float) Math.atan((double) (1 / 40.0F)) * 20.0F;
        entity.rotationYaw = (float) Math.atan((double) (1 / 40.0F)) * 40.0F;
        entity.rotationPitch = -((float) Math.atan((double) (1 / 40.0F))) * 20.0F;
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

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();

        fbo.end();
        base64 = fbo.getBase64();
        fbo.restoreTexture();
        return base64;
        }
    }
}
