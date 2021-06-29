package client.messages.commands.admin;

import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;
import tools.StringUtil;

public class ServerMessageCommand extends Command {
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if (splitted.length <1){
            c.getPlayer().dropMessage(6, "Syntax: !servermessage <message>");
            return;
        }
        String outputMessage = StringUtil.joinStringFrom(splitted, 0);
        for (var cserv : ChannelServer.getAllInstances()) {
            cserv.setServerMessage(outputMessage);
        }       
    } 
}