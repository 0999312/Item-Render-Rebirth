package itemrender.client.export;

import itemrender.ItemRenderMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

public class ItemData
{
  private String name;
  private String englishName;
  private String registerName;
  private int metadata;
  private String OredictList;
  private String CreativeTabName;
  private String type;
  private int maxStackSize;
  private int maxDurability;
  private String smallIcon;
  private String largeIcon;
  private transient ItemStack itemStack;

  
  public ItemData(ItemStack itemStack)
  {
    if (ItemRenderMod.debugMode) {
      ItemRenderMod.instance.log.info("Processing " + itemStack.getUnlocalizedName() + "@" + itemStack.getItemDamage());
    }
    this.name = null;
    this.englishName = null;
    this.registerName = itemStack.getItem().getRegistryName();
    List<String> list = new ArrayList<String>();
    metadata=itemStack.getItemDamage();
    if(!isEmpty(itemStack)){
    for(int i : OreDictionary.getOreIDs(itemStack)){
    	String ore = OreDictionary.getOreName(i);
    	list.add(ore);
    }
    OredictList = list.toString();
    }
    this.CreativeTabName = null;
    this.type = ExportUtils.INSTANCE.getType(itemStack);
    this.maxStackSize = itemStack.getMaxStackSize();
    this.maxDurability = (itemStack.getMaxDamage() + 1);
    this.smallIcon = ExportUtils.INSTANCE.getSmallIcon(itemStack);
    this.largeIcon = ExportUtils.INSTANCE.getLargeIcon(itemStack);
    this.itemStack = itemStack;
  }
  
  public ItemStack getItemStack()
  {
    return this.itemStack;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setEnglishName(String englishName)
  {
    this.englishName = englishName;
  }
  public void setCreativeName(String name) {
      this.CreativeTabName = name;
  }
  private boolean isEmpty(ItemStack stack)
  {
      if (stack == new ItemStack((Item)null))
      {
          return true;
      }
      else if (stack.getItem() != null && stack.getItem() != Item.getItemFromBlock(Blocks.air))
      {
          if (stack.stackSize <= 0)
          {
              return true;
          }
          else
          {
              return stack.getItemDamage() < -32768 || stack.getItemDamage() > 65535;
          }
      }
      else
      {
          return true;
      }
  }
}
