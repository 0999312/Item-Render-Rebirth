/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender;


import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * Created by Fang0716 on 6/27/2014.
 *
 * @author Meow J
 */
public class ItemRenderConfigGUI extends GuiConfig {
    public ItemRenderConfigGUI(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(ItemRenderMod.cfg.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), ItemRenderMod.MODID, false, true, GuiConfig.getAbridgedConfigPath(ItemRenderMod.cfg.toString()));
    }
}