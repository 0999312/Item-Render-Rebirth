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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandItemRender extends CommandBase {

    @Override
    public String getName() {
        return "itemrender";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/itemrender scale [value]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "/itemrender scale [value]"));
            sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Execute this command to control entity/item rendering scale."));
            sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Scale Range: (0.0, 2.0]. Default: 1.0. Current: " + ItemRenderMod.renderScale));
        } else if (args[0].equalsIgnoreCase("scale")) {
            if (args.length == 2) {
                float value = Float.valueOf(args[1]);
                if (value > 0.0F && value <= 2.0F) {
                    ItemRenderMod.renderScale = Float.valueOf(args[1]);
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Scale: " + value));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Scale Range: (0.0, 2.0]"));
                }
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Current Scale: " + ItemRenderMod.renderScale));
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Execute /itemrender scale [value] to control entity/item rendering " + TextFormatting.RED + "scale."));
            }
        } else
            throw new CommandException("/itemrender scale [value]", 0);
    }
}