package client.messages.commands.gm;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;

public class MonitorCommand extends Command {
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if (splitted.length <1){
            c.getPlayer().dropMessage(6, "Syntax: !monitor <Character name>");
            return;
        }
        MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        if (target != null) {
            if (target.getClient().isMonitored()) {
                target.getClient().setMonitored(false);
                c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
            } else {
                target.getClient().setMonitored(true);
                c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
            }
        } else {
            c.getPlayer().dropMessage(5, "Target not found on channel.");
        }
    }    
}