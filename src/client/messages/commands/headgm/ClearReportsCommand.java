package client.messages.commands.headgm;

import client.MapleCharacter;
import client.MapleClient;
import client.anticheat.ReportType;
import client.messages.Command;

public class ClearReportsCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if(splitted.length < 2){
            StringBuilder ret = new StringBuilder("report [ign] [all/");
            for(ReportType type : ReportType.values()){
                ret.append(type.theId).append('/');
            }
            ret.setLength(ret.length() - 1);
            c.getPlayer().dropMessage(6, ret.append(']').toString());
            return;
        }
        final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        if(victim == null){
            c.getPlayer().dropMessage(5, "Does not exist");
            return;
        }
        final ReportType type = ReportType.getByString(splitted[1]);
        if(type != null){
            victim.clearReports(type);
        }else{
            victim.clearReports();
        }
        c.getPlayer().dropMessage(5, "Done.");
    }   
}