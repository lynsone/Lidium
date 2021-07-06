package client.messages.commands.developer;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.messages.Command;
import constants.GameConstants;
import server.MapleItemInformationProvider;

public class DropCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if (splitted.length == 0){
            c.getPlayer().blueMessage("Syntax: !drop <itemid> <quantity>");
            return;
        }
        
        int itemId;
        short quantity;
        
        try{
            itemId = Integer.parseInt(splitted[0]);
            if (splitted.length == 2){
                quantity = (short) Integer.parseInt(splitted[1]);
            }
            else quantity = 1;
        }
        catch (NumberFormatException e){
            c.getPlayer().blueMessage("Your command could not run. Did you only enter numbers?");
            return;
        }

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.isPet(itemId)){
            c.getPlayer().dropMessage(5, "Please purchase a pet from the cash shop instead.");
        } 
        else if (!ii.itemExists(itemId)){
            c.getPlayer().dropMessage(5, itemId + " does not exist");
        }else{
            Item toDrop;
            if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP){
                toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
            }else{
                toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
            }
            if (!c.getPlayer().isAdmin()){
                toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                toDrop.setOwner(c.getPlayer().getName());
            }
            c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
        }
    }   
}