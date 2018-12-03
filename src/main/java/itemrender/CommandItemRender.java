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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandItemRender extends CommandBase {


    @Override
    public String getCommandName() {
        return "itemrender";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/itemrender scale [value]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/itemrender scale [value]"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Execute this command to control entity rendering scale."));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Scale Range: (0.0, 2.0]. Default: 1.0. Current: " + ItemRenderMod.renderScale));
        } else if (args[0].equalsIgnoreCase("scale")) {
            if (args.length == 2) {
                float value = Float.valueOf(args[1]);
                if (value > 0.0F && value <= 2.0F) {
                    ItemRenderMod.renderScale = Float.valueOf(args[1]);
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Scale: " + value));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Scale Range: (0.0, 2.0]"));
                }
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Current Scale: " + ItemRenderMod.renderScale));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Execute /itemrender scale [value] to control entity rendering " + EnumChatFormatting.RED + "scale."));
            }
        } else
            throw new CommandException("/itemrender scale [value]", 0);
    }
}