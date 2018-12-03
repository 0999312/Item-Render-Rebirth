/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.client.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import itemrender.ItemRenderMod;
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ExportUtils {
	private Thread item,entity;
    public static ExportUtils INSTANCE;
    private int progress=1,size;
    private int progress1=1,size1;
    private FBOHelper fboSmall;
    private FBOHelper fboLarge;
    private FBOHelper fboEntity;
    private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
    private List<ItemData> itemDataList = new ArrayList<ItemData>();
    private List<MobData> mobDataList = new ArrayList<MobData>();
    
    public ExportUtils() {
        // Hardcoded value for mcmod.cn only, don't change this unless the website updates
        fboSmall = new FBOHelper(32);
        fboLarge = new FBOHelper(128);
        fboEntity = new FBOHelper(200);
    }


    public String getLocalizedName(ItemStack itemStack) {
        return itemStack.getDisplayName();
    }
    
    public String getType(ItemStack itemStack) {
        return (itemStack.getItem() instanceof ItemBlock) ? "Block" : "Item";
    }

    public String getSmallIcon(ItemStack itemStack) {
        return Renderer.getItemBase64(itemStack, fboSmall, itemRenderer);
    }

    public String getLargeIcon(ItemStack itemStack) {
        return Renderer.getItemBase64(itemStack, fboLarge, itemRenderer);
    }

    public String getEntityIcon(EntityEntry Entitymob){
        return Renderer.getEntityBase64(Entitymob, fboEntity);
    }
    
    private String getItemOwner(ItemStack itemStack) {
        ResourceLocation registryName = itemStack.getItem().getRegistryName();
        return registryName == null ? "unnamed" : registryName.getResourceDomain();
    }
    private String getEntityOwner(EntityEntry Entitymob) {
        ResourceLocation registryName = Entitymob.getRegistryName();
        return registryName == null ? "unnamed" : registryName.getResourceDomain();
    }
    
    public void exportMods() throws IOException{
        Minecraft minecraft = FMLClientHandler.instance().getClient();
        itemDataList.clear();
        mobDataList.clear();
        List<String> modList = new ArrayList<String>();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        ItemData itemData;
        MobData mobData;
        String identifier;

        for (ItemStack itemStack : ItemList.items) {
            if (itemStack == null) continue;
            if (getItemOwner(itemStack).equals("minecraft") && !ItemRenderMod.exportVanillaItems) continue;

            identifier = itemStack.getItem().getUnlocalizedName() + "@" + itemStack.getMetadata();
            if (ItemRenderMod.blacklist.contains(identifier)) continue;

            itemData = new ItemData(itemStack);
            itemDataList.add(itemData);
            if (!modList.contains(getItemOwner(itemStack))) modList.add(getItemOwner(itemStack));
        }
        for (EntityEntry Entity : ForgeRegistries.ENTITIES) {
            if (Entity == null) continue;
//            if (!(Entity.newInstance(minecraft.world) instanceof EntityLivingBase)||!(Entity.newInstance(minecraft.world) instanceof EntityMob)) continue;
            if (getEntityOwner(Entity).equals("minecraft") && !ItemRenderMod.exportVanillaItems) continue;

            mobData = new MobData(Entity);
            mobDataList.add(mobData);
            if (!modList.contains(getEntityOwner(Entity))) modList.add(getEntityOwner(Entity));
        }
        // Since refreshResources takes a long time, only refresh once for all the items
        minecraft.getLanguageManager().setCurrentLanguage(new Language("zh_CN", "涓浗", "绠�浣撲腑鏂�", false));
        minecraft.gameSettings.language = "zh_CN";
        minecraft.refreshResources();
        minecraft.gameSettings.saveOptions();

        for (ItemData data : itemDataList) {
            if (ItemRenderMod.debugMode)
                ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.addCN", data.getItemStack().getItem().getUnlocalizedName() + "@" + data.getItemStack().getMetadata()));
            data.setName(this.getLocalizedName(data.getItemStack()));
            data.setCreativeName(getCreativeTabName(data));
        }
        for (MobData data : mobDataList) {
            if (ItemRenderMod.debugMode)
                ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.addCN", data.getMob().getRegistryName()));
            data.setName(new TextComponentTranslation("entity." + data.getMob().getName() + ".name", new Object[0]).getFormattedText());
        }

        minecraft.getLanguageManager().setCurrentLanguage(new Language("en_US", "US", "English", false));
        minecraft.gameSettings.language = "en_US";
        minecraft.refreshResources();
        minecraft.fontRendererObj.setUnicodeFlag(false);
        minecraft.gameSettings.saveOptions();

        for (ItemData data : itemDataList) {
            if (ItemRenderMod.debugMode)
                ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.addEN", data.getItemStack().getItem().getUnlocalizedName() + "@" + data.getItemStack().getMetadata()));
            data.setEnglishName(this.getLocalizedName(data.getItemStack()));
        }
        
        for (MobData data : mobDataList) {
            if (ItemRenderMod.debugMode)
                ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.addEN", data.getMob().getRegistryName()));
            data.setEnglishname(new TextComponentTranslation("entity." + data.getMob().getName() + ".name", new Object[0]).getFormattedText());
        }
        
        File export;
        File export1;
        for (String modid : modList) {
            export = new File(minecraft.mcDataDir, String.format("export/"+modid+"_item.json", modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "")));
            if (!export.getParentFile().exists()) export.getParentFile().mkdirs();
            if (!export.exists()) export.createNewFile();
            PrintWriter pw = new PrintWriter(export, "UTF-8");

            for (ItemData data : itemDataList) {
                if (modid.equals(getItemOwner(data.getItemStack())))
                    pw.println(gson.toJson(data));
            }
            pw.close();

        }
        for (String modid : modList) {
        export1 = new File(minecraft.mcDataDir, String.format("export/"+modid+"_entity.json", modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "")));
        if (!export1.getParentFile().exists()) export1.getParentFile().mkdirs();
        if (!export1.exists()) export1.createNewFile();
        PrintWriter pw1 = new PrintWriter(export1, "UTF-8");

        for (MobData data : mobDataList) {
            if (modid.equals(getEntityOwner(data.getMob())))
                pw1.println(gson.toJson(data));
        }
        pw1.close();
        }
    }

	private String getCreativeTabName(ItemData data) {
		if(data.getItemStack().getItem().getCreativeTab()!=null){
		return new TextComponentTranslation(data.getItemStack().getItem().getCreativeTab().getTranslatedTabLabel(), new Object[0]).getFormattedText();
		}else{
		return "";
		}
	}
}
