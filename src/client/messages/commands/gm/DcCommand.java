package client.messages.commands.gm;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;

public class DcCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
            victim.getClient().getSession().close();
            victim.getClient().disconnect(true, false);
        } else {
            c.getPlayer().dropMessage(6, "The victim does not exist.");
        }
    }
}