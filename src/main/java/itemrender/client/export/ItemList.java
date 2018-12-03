package itemrender.client.export;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemList
{
  public static volatile List<ItemStack> items = new ArrayList();
  public static volatile ListMultimap<Item, ItemStack> itemMap = ArrayListMultimap.create();
  
  private static void damageSearch(Item item, List<ItemStack> permutations)
  {
    HashSet<String> damageIconSet = new HashSet();
    for (int damage = 0; damage < 16; damage++) {
      try
      {
        ItemStack stack = new ItemStack(item, 1, damage);
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        String name = concatenatedDisplayName(stack);
        String s = name + "@" + (model == null ? 0 : model.hashCode());
        if (!damageIconSet.contains(s))
        {
          damageIconSet.add(s);
          permutations.add(stack);
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public static String concatenatedDisplayName(ItemStack itemstack)
  {
    List<String> list = itemDisplayNameMultiline(itemstack);
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String name : list)
    {
      if (first) {
        first = false;
      } else {
        sb.append("#");
      }
      sb.append(name);
    }
    return TextFormatting.getTextWithoutFormattingCodes(sb.toString());
  }
  
  public static List<String> itemDisplayNameMultiline(ItemStack itemstack)
  {
    List<String> nameList = null;
    try
    {
      nameList = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
    }
    catch (Throwable localThrowable) {}
    if (nameList == null) {
      nameList = new ArrayList();
    }
    if (nameList.size() == 0) {
      nameList.add("Unnamed");
    }
    if ((nameList.get(0) == null) || (((String)nameList.get(0)).equals(""))) {
      nameList.set(0, "Unnamed");
    }
    nameList.set(0, itemstack.getRarity().rarityColor.toString() + (String)nameList.get(0));
    for (int i = 1; i < nameList.size(); i++) {
      nameList.set(i, "¡ì7" + (String)nameList.get(i));
    }
    return nameList;
  }
  
  public static void updateList()
  {
    LinkedList<ItemStack> items = new LinkedList();
    LinkedList<ItemStack> permutations = new LinkedList();
    ListMultimap<Item, ItemStack> itemMap = ArrayListMultimap.create();
    for (Item item : Item.REGISTRY) {
      if (item != null) {
        try
        {
          permutations.clear();
          if (permutations.isEmpty()) {
            item.getSubItems(item, null, permutations);
          }
          if (permutations.isEmpty()) {
            damageSearch(item, permutations);
          }
          items.addAll(permutations);
          itemMap.putAll(item, permutations);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    items = items;
    itemMap = itemMap;
  }
}
