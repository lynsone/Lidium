package client.messages.commands.gm;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;

public class EndEventCommand extends Command {
    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        c.getChannelServer().setEvent(-1);
        player.dropMessage(5, "You have ended the event. No more players may join.");
    }
}
