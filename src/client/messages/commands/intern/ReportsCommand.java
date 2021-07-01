package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;
import handling.world.CheaterData;
import handling.world.World;
import java.util.List;

public class ReportsCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        List<CheaterData> cheaters = World.getReports();
        for (int x = cheaters.size() - 1; x >= 0; x--) {
            CheaterData cheater = cheaters.get(x);
            c.getPlayer().dropMessage(6, cheater.getInfo());
        }      
    }   
}