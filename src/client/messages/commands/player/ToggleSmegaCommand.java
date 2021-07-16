package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;

public class ToggleSmegaCommand extends Command {

    {
        setDescription("Enable or disable smegas.");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        c.getPlayer().setSmega();
    }
}