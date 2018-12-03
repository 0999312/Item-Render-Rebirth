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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Code from NEI
 *
 * @author Chickenbones
 */
public class ItemList {
    /**
     * Fields are replaced atomically and contents never modified.
     */
    static volatile List<ItemStack> items = new ArrayList<ItemStack>();

    private static void damageSearch(Item item, List<ItemStack> permutations) {
        HashSet<String> damageIconSet = new HashSet<String>();
        for (int damage = 0; damage < 16; damage++)
            try {
                ItemStack stack = new ItemStack(item, 1, damage);
                IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
                String name = concatenatedDisplayName(stack);
                String s = name + "@" + (model == null ? 0 : model.hashCode());
                if (!damageIconSet.contains(s)) {
                    damageIconSet.add(s);
                    permutations.add(stack);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private static String concatenatedDisplayName(ItemStack itemstack) {
        List<String> list = itemDisplayNameMultiline(itemstack);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String name : list) {
            if (first) {
                first = false;
            } else {
                sb.append("#");
            }
            sb.append(name);
        }
        return TextFormatting.getTextWithoutFormattingCodes(sb.toString());
    }

    @SuppressWarnings("unchecked")
    private static List<String> itemDisplayNameMultiline(ItemStack itemstack) {
        List<String> nameList = null;
        try {
            nameList = itemstack.getTooltip(Minecraft.getMinecraft().player,true);
        } catch (Throwable ignored) {
        }

        if (nameList == null)
            nameList = new ArrayList<String>();

        if (nameList.size() == 0)
            nameList.add("Unnamed");

        if (nameList.get(0) == null || nameList.get(0).equals(""))
            nameList.set(0, "Unnamed");

        nameList.set(0, itemstack.getRarity().rarityColor.toString() + nameList.get(0));
        for (int i = 1; i < nameList.size(); i++)
            nameList.set(i, "\u00a77" + nameList.get(i));

        return nameList;
    }

    @SuppressWarnings("unchecked")
    public static void updateList() {
        LinkedList<ItemStack> items = new LinkedList<ItemStack>();
        NonNullList<ItemStack> permutations = NonNullList.create();
        ListMultimap<Item, ItemStack> itemMap = ArrayListMultimap.create();

        for (Item item : Item.REGISTRY) {

            if (item == null)
                continue;

            try {
                permutations.clear();

                if (permutations.isEmpty())
//                    item.getSubItems(null, permutations);
                    for (CreativeTabs tab : item.getCreativeTabs())
                        item.getSubItems(item, tab, permutations);

                if (permutations.isEmpty())
                    damageSearch(item, permutations);


                items.addAll(permutations);
                itemMap.putAll(item, permutations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ItemList.items = items;
    }
}
