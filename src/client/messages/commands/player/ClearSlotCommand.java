package client.messages.commands.player;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.messages.Command;
import server.MapleInventoryManipulator;

public class ClearSlotCommand extends Command{
    {
        setDescription("Remove all items from a tab of your inventory.");
    }
    
    private static MapleInventoryType[] invs = {
            MapleInventoryType.EQUIP,
            MapleInventoryType.USE,
            MapleInventoryType.SETUP,
            MapleInventoryType.ETC,
            MapleInventoryType.CASH,};
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleCharacter player = c.getPlayer();
        if (splitted.length < 2 || player.hasBlockedInventory()){
            c.getPlayer().dropMessage(5, "@clearslot <eq/use/setup/etc/cash/all>");
        }else{
            MapleInventoryType type;
            if(splitted[1].equalsIgnoreCase("eq")){
                type = MapleInventoryType.EQUIP;
            }else if(splitted[1].equalsIgnoreCase("use")){
                type = MapleInventoryType.USE;
            }else if(splitted[1].equalsIgnoreCase("setup")){
                type = MapleInventoryType.SETUP;
            }else if(splitted[1].equalsIgnoreCase("etc")){
                type = MapleInventoryType.ETC;
            }else if(splitted[1].equalsIgnoreCase("cash")){
                type = MapleInventoryType.CASH;
            }else if(splitted[1].equalsIgnoreCase("all")){
                type = null;
            }else{              
                c.getPlayer().dropMessage(5, "Invalid. @clearslot <eq/use/setup/etc/cash/all>");
                return;
            }
            
            if(type == null){
                for(MapleInventoryType t : invs){
                    type = t;
                    MapleInventory inv = c.getPlayer().getInventory(type);
                    byte start = -1;
                    for(byte i = 0; i < inv.getSlotLimit(); i++){
                        if(inv.getItem(i) != null){
                            start = i;
                            break;
                        }
                    }               
                    if(start == -1){
                        c.getPlayer().dropMessage(5, "There are no items in that inventory.");
                        return;
                    }       
                    int end = 0;
                    for(byte i = start; i < inv.getSlotLimit(); i++){
                        if(inv.getItem(i) != null){
                            MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                        }else{
                           end = i;
                            break;
                        }
                    }
                    c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
                }
            }else{
                MapleInventory inv = c.getPlayer().getInventory(type);
                byte start = -1;
                for(byte i = 0; i < inv.getSlotLimit(); i++){
                    if(inv.getItem(i) != null){
                        start = i;
                        break;
                    }
                }
                if(start == -1){  
                    c.getPlayer().dropMessage(5, "There are no items in that inventory.");
                    return;
                }       
                byte end = 0;
                for(byte i = start; i < inv.getSlotLimit(); i++){
                    if(inv.getItem(i) != null){
                        MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                    }else{                    
                        end = i;
                        break;
                    }
                }
                c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
            }
        }
    }
}