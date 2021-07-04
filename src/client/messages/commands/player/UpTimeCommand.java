package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;
import tools.StringUtil;

public class UpTimeCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        c.getPlayer().dropMessage(6, "Server has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
    }   
}
