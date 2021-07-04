package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;
import java.util.Map.Entry;
import server.MapleSquad;

public class ListAllSquadsCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (Entry<MapleSquad.MapleSquadType, MapleSquad> squads : cserv.getAllSquads().entrySet()) {
                c.getPlayer().dropMessage(5, "[Channel " + cserv.getChannel() + "] TYPE: " + squads.getKey().name() + ", Leader: " + squads.getValue().getLeader().getName() + ", status: " + squads.getValue().getStatus() + ", numMembers: " + squads.getValue().getSquadSize() + ", numBanned: " + squads.getValue().getBannedMemberSize());
            }
        }
    }   
}