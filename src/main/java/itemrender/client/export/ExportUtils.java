package itemrender.client.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import itemrender.ItemRenderMod;
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

public class ExportUtils
{
	private Thread item;
    public static ExportUtils INSTANCE;
    private int progress=1,size;
  private FBOHelper fboSmall;
  private FBOHelper fboLarge;
  private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
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
  
  private String getItemOwner(ItemStack itemStack)
  {
    ResourceLocation registryName = itemStack.getItem().getRegistryName();
    return registryName == null ? "unnamed" : registryName.getResourceDomain();
  }
  public void exportMods()
    throws IOException
  {
    Minecraft minecraft = FMLClientHandler.instance().getClient();
    this.itemDataList.clear();
    List<String> modList = new ArrayList();
    
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    for (ItemStack itemStack : ItemList.items) {
      if ((itemStack != null) && (
        (!getItemOwner(itemStack).equals("minecraft")) || (ItemRenderMod.exportVanillaItems)))
      {
        String identifier = itemStack.getItem().getUnlocalizedName() + "@" + itemStack.getMetadata();
        if (!ItemRenderMod.blacklist.contains(identifier))
        {
          ItemData itemData = new ItemData(itemStack);
          this.itemDataList.add(itemData);
          if (!modList.contains(getItemOwner(itemStack))) {
            modList.add(getItemOwner(itemStack));
          }
        }
      }
    }
    minecraft.getLanguageManager().setCurrentLanguage(new Language("zh_CN", "中国", "简体中文", false));
    minecraft.gameSettings.language = "zh_CN";
    minecraft.refreshResources();
    minecraft.gameSettings.saveOptions();
    for (ItemData data : this.itemDataList)
    {
      if (ItemRenderMod.debugMode) {
        ItemRenderMod.instance.log.info("Adding Chinese name for " + data.getItemStack().getItem().getUnlocalizedName() + "@" + data.getItemStack().getMetadata());
      }
      data.setName(getLocalizedName(data.getItemStack()));
      data.setCreativeName(getCreativeTabName(data));
    }
    minecraft.getLanguageManager().setCurrentLanguage(new Language("en_US", "US", "English", false));
    minecraft.gameSettings.language = "en_US";
    minecraft.refreshResources();
    minecraft.fontRendererObj.setUnicodeFlag(false);
    minecraft.gameSettings.saveOptions();
    
    for (ItemData data : itemDataList) {
        if (ItemRenderMod.debugMode)
            ItemRenderMod.instance.log.info("Adding English name for " + data.getItemStack().getUnlocalizedName() + "@" + data.getItemStack().getItemDamage());
        data.setEnglishName(this.getLocalizedName(data.getItemStack()));
    }
    ItemData data;
    for (String modid : modList)
    {
      File export = new File(minecraft.mcDataDir, String.format("export/"+modid+"_item.json", new Object[] { modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "") }));
      if (!export.getParentFile().exists()) {
        export.getParentFile().mkdirs();
      }
      if (!export.exists()) {
        export.createNewFile();
      }
      PrintWriter pw = new PrintWriter(export, "UTF-8");
      for (ItemData data1 : this.itemDataList) {
        if (modid.equals(getItemOwner(data1.getItemStack()))) {
          pw.println(gson.toJson(data1));
        }
      }
      pw.close();
    }
  }
  

  
	private String getCreativeTabName(ItemData data) {
		if(data.getItemStack().getItem().getCreativeTab()!=null){
		return I18n.format(data.getItemStack().getItem().getCreativeTab().getTranslatedTabLabel(), new Object[0]);
		}else{
		return "";
		}
	}
}
