package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;

public class ReloadOpsCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        SendPacketOpcode.reloadValues();
        RecvPacketOpcode.reloadValues();
        c.getPlayer().dropMessage(6, "Ops reloaded");
    }  
}