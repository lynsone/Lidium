package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;
import tools.packet.CField;

public class SongCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
    }  
}