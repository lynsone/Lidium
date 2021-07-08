package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;

public class ReloadMapCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if (splitted.length < 2){
            c.getPlayer().dropMessage(6, "Syntax: !reloadmap <MapID>");
            return;
        }
        
        final int mapId = Integer.parseInt(splitted[0]);      
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
                c.getPlayer().dropMessage(5, "There exists characters on channel " + cserv.getChannel());
                return;
            }
        }
        
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            if (cserv.getMapFactory().isMapLoaded(mapId)) {
                cserv.getMapFactory().removeMap(mapId);
            }
        }
    }   
}