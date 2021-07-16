package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;
import constants.GameConstants;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.SavedLocationType;

public class FMCommand extends Command{

    {
        setDescription("Warps you to Free Market.");
    }
    
    @Override
    public void execute(MapleClient c, String[] params){ 
        for(int i : GameConstants.blockedMaps){           
            if(c.getPlayer().getMapId() == i){
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return;
            }
        }
        
        if(c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200){
            c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
            return;
        }
        
        if(c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000|| FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())){
            c.getPlayer().dropMessage(5, "You may not use this command here.");
            return;
        }
        
        if((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)){
            c.getPlayer().dropMessage(5, "You may not use this command here.");
            return;
        }
        
        c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
        MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
        c.getPlayer().changeMap(map, map.getPortal(0));   
    }   
}