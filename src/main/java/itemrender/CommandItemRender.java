package itemrender;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandItemRender
  extends CommandBase
{
  public String getCommandName()
  {
    return "itemrender";
  }
  
  public String getCommandUsage(ICommandSender sender)
  {
    return "/itemrender scale [value]";
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    throws CommandException
  {
    if (args.length == 0)
    {
      sender.addChatMessage(new TextComponentString(TextFormatting.RED + "/itemrender scale [value]"));
      sender.addChatMessage(new TextComponentString(TextFormatting.AQUA + "Execute this command to control entity/item rendering scale."));
      sender.addChatMessage(new TextComponentString(TextFormatting.AQUA + "Scale Range: (0.0, 2.0]. Default: 1.0. Current: " + ItemRenderMod.renderScale));
    }
    else if (args[0].equalsIgnoreCase("scale"))
    {
      if (args.length == 2)
      {
        float value = Float.valueOf(args[1]).floatValue();
        if ((value > 0.0F) && (value <= 2.0F))
        {
          ItemRenderMod.renderScale = Float.valueOf(args[1]).floatValue();
          sender.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Scale: " + value));
        }
        else
        {
          sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Scale Range: (0.0, 2.0]"));
        }
      }
      else
      {
        sender.addChatMessage(new TextComponentString(TextFormatting.AQUA + "Current Scale: " + ItemRenderMod.renderScale));
        sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Execute /itemrender scale [value] to control entity/item rendering " + TextFormatting.RED + "scale."));
      }
    }
    else
    {
      throw new CommandException("/itemrender scale [value]", new Object[] { Integer.valueOf(0) });
    }
  }
}
