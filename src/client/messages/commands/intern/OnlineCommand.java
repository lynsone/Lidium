package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;

public class OnlineCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        c.getPlayer().dropMessage(6, "Characters connected to channel " + c.getChannel() + ":");
        c.getPlayer().dropMessage(6, c.getChannelServer().getPlayerStorage().getOnlinePlayers(true));
    }
}