package itemrender.client.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import itemrender.ItemRenderMod;
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;

public class ExportUtils
{
  public static ExportUtils INSTANCE;
  private FBOHelper fboSmall;
  private FBOHelper fboLarge;
  private RenderItem itemRenderer = new RenderItem();
  private List<ItemData> itemDataList = new ArrayList();
  
  public ExportUtils()
  {
    this.fboSmall = new FBOHelper(32);
    this.fboLarge = new FBOHelper(128);
  }
  
  public String getLocalizedName(ItemStack itemStack)
  {
    return itemStack.getDisplayName();
  }
  public String getType(ItemStack itemStack)
  {
    return (itemStack.getItem() instanceof ItemBlock) ? "Block" : "Item";
  }
  
  public String getSmallIcon(ItemStack itemStack)
  {
    return Renderer.getItemBase64(itemStack, this.fboSmall, this.itemRenderer);
  }
  
  public String getLargeIcon(ItemStack itemStack)
  {
    return Renderer.getItemBase64(itemStack, this.fboLarge, this.itemRenderer);
  }
  
  public String getItemOwner(ItemStack itemStack)
  {
    GameRegistry.UniqueIdentifier uniqueIdentity = GameRegistry.findUniqueIdentifierFor(itemStack.getItem());
    return uniqueIdentity == null ? "unnamed" : uniqueIdentity.modId;
  }
  
  public void exportMods() throws IOException {
      Minecraft minecraft = FMLClientHandler.instance().getClient();
      itemDataList.clear();
      List<String> modList = new ArrayList<String>();
      Language lang = minecraft.getLanguageManager().getCurrentLanguage();
      
      Gson gson = new GsonBuilder().disableHtmlEscaping().create();
      ItemData itemData;
      String identifier;

      for (ItemStack itemStack : ItemList.items) {
          if (itemStack == null) continue;
          if (getItemOwner(itemStack).equals("minecraft") && !ItemRenderMod.exportVanillaItems) continue;

          identifier = itemStack.getUnlocalizedName() + "@" + itemStack.getItemDamage();
          if (ItemRenderMod.blacklist.contains(identifier)) continue;

          itemData = new ItemData(itemStack);
          itemDataList.add(itemData);
          if (!modList.contains(getItemOwner(itemStack))) modList.add(getItemOwner(itemStack));
      }
      // Since refreshResources takes a long time, only refresh once for all the items
      minecraft.getLanguageManager().setCurrentLanguage(new Language("zh_CN", "涓浗", "绠�浣撲腑鏂�", false));
      minecraft.gameSettings.language = "zh_CN";
      minecraft.refreshResources();
      minecraft.gameSettings.saveOptions();

      for (ItemData data : itemDataList) {
          if (ItemRenderMod.debugMode)
              ItemRenderMod.instance.log.info("Adding Chinese name for " + data.getItemStack().getUnlocalizedName() + "@" + data.getItemStack().getItemDamage());
          data.setName(this.getLocalizedName(data.getItemStack()));
          data.setCreativeName(getCreativeTabName(data));
      }

      minecraft.getLanguageManager().setCurrentLanguage(new Language("en_US", "US", "English", false));
      minecraft.gameSettings.language = "en_US";
      minecraft.refreshResources();
      minecraft.fontRenderer.setUnicodeFlag(false);
      minecraft.gameSettings.saveOptions();

      for (ItemData data : itemDataList) {
          if (ItemRenderMod.debugMode)
              ItemRenderMod.instance.log.info("Adding English name for " + data.getItemStack().getUnlocalizedName() + "@" + data.getItemStack().getItemDamage());
          data.setEnglishName(this.getLocalizedName(data.getItemStack()));
      }

      File export;

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
      
      minecraft.getLanguageManager().setCurrentLanguage(lang);
      minecraft.gameSettings.language = lang.getLanguageCode();
      minecraft.refreshResources();
      minecraft.gameSettings.saveOptions();
  }
	private String getCreativeTabName(ItemData data) {
		if(data.getItemStack().getItem().getCreativeTab()!=null){
		return I18n.format(data.getItemStack().getItem().getCreativeTab().getTranslatedTabLabel(), new Object[0]);
		}else{
		return "";
		}
	}
}
