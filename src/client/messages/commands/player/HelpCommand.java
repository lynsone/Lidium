package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;
import client.messages.CommandsExecutor;

public class HelpCommand extends Command {

    {
        setDescription("Show your available commands.");   
    }

    @Override
    public void execute(MapleClient c, String[] splitted) {
        CommandsExecutor.getInstance().getRegisteredCommands().forEach((commandStr, command) -> {
            int gmlevel = c.getPlayer().getGMLevel();
            if (command.getRank() <= gmlevel) {
                c.getPlayer().dropMessage(5, (gmlevel == 0 ? "@" : "!") + commandStr+" - "+command.getDescription());
            }
        });
       
    }
}