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

import java.util.ArrayList;
import java.util.List;

import itemrender.ItemRenderMod;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ItemData {
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


    public ItemData(ItemStack itemStack) {
        if (ItemRenderMod.debugMode)
            ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.processing", itemStack.getItem().getUnlocalizedName() + "@" + itemStack.getMetadata()));
        name = null;
        englishName = null;
        registerName = itemStack.getItem().getRegistryName().toString();
        metadata=itemStack.getMetadata();
        List<String> list = new ArrayList<String>();
        if(!itemStack.isEmpty()){
        for(int i : OreDictionary.getOreIDs(itemStack)){
        	String ore = OreDictionary.getOreName(i);
        	list.add(ore);
        }
        OredictList = list.toString();
        }
        CreativeTabName=null;
        type = ExportUtils.INSTANCE.getType(itemStack);
        maxStackSize = itemStack.getMaxStackSize();
        maxDurability = itemStack.getMaxDamage() + 1;
        smallIcon = ExportUtils.INSTANCE.getSmallIcon(itemStack);
        largeIcon = ExportUtils.INSTANCE.getLargeIcon(itemStack);
        
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setCreativeName(String name) {
        this.CreativeTabName = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

}
