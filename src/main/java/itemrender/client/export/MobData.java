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

import itemrender.ItemRenderMod;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class MobData {
    private String name;
    private String Englishname;
    private String mod;
    private String registerName;
    private String Icon;
    private transient EntityEntry mob;
   
    public MobData(EntityEntry Entitymob){

        if (ItemRenderMod.debugMode)
            ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.processing", Entitymob.getName()));
        name = null;
        Englishname=null;
        mod = Entitymob.getRegistryName().getResourceDomain();
        registerName = Entitymob.getRegistryName().toString();    
        Icon = ExportUtils.INSTANCE.getEntityIcon(Entitymob);
        this.mob = Entitymob;
    }

    public EntityEntry getMob() {
        return mob;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEnglishname(String name) {
        this.Englishname = name;
    }
}
