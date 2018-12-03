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
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

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
    public static volatile List<ItemStack> items = new ArrayList<ItemStack>();
    /**
     * Fields are replaced atomically and contents never modified.
     */
    public static volatile ListMultimap<Item, ItemStack> itemMap = ArrayListMultimap.create();

    private static void damageSearch(Item item, List<ItemStack> permutations) {
        HashSet<String> damageIconSet = new HashSet<String>();
        for (int damage = 0; damage < 16; damage++) {
            ItemStack itemstack = new ItemStack(item, 1, damage);
            IIcon icon = item.getIconIndex(itemstack);
            String name = concatenatedDisplayName(itemstack);
            String s = name + "@" + (icon == null ? 0 : icon.hashCode());
            if (!damageIconSet.contains(s)) {
                damageIconSet.add(s);
                permutations.add(itemstack);
            }
        }
    }

    public static String concatenatedDisplayName(ItemStack itemstack) {
        List<String> list = itemDisplayNameMultiline(itemstack, null);
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
        return EnumChatFormatting.getTextWithoutFormattingCodes(sb.toString());
    }

    @SuppressWarnings("unchecked")
    public static List<String> itemDisplayNameMultiline(ItemStack itemstack, GuiContainer gui) {
        List<String> nameList = null;

        nameList = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, false);

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
        LinkedList<ItemStack> permutations = new LinkedList<ItemStack>();
        ListMultimap<Item, ItemStack> itemMap = ArrayListMultimap.create();

        for (Item item : (Iterable<Item>) Item.itemRegistry) {

            if (item == null)
                continue;

            try {
                permutations.clear();

                if (permutations.isEmpty())
                    item.getSubItems(item, null, permutations);

                if (permutations.isEmpty())
                    damageSearch(item, permutations);


                items.addAll(permutations);
                itemMap.putAll(item, permutations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ItemList.items = items;
        ItemList.itemMap = itemMap;
    }
}
