package client.messages.commands.developer;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.messages.Command;
import constants.GameConstants;
import server.MapleItemInformationProvider;

public class MultiDropCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length < 2){
            player.dropMessage(5, "Syntax: !dropmultiequip <itemid> <quantity>");
            return;
        }

        int itemId = Integer.parseInt(params[0]);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if(ii.getName(itemId) == null){
            player.dropMessage(5, "Item id '" + params[0] + "' does not exist.");
            return;
        }

        short quantity = 1;
        if(params.length >= 2) quantity = Short.parseShort(params[1]);

        if (quantity > 32){
            player.dropMessage(5, "You can only drop 32 equips per command to avoid server lag. Please change your quantity.");
            return;
        }

        if (GameConstants.isPet(itemId)){
            c.getPlayer().dropMessage(5, "Please purchase a pet from the cash shop instead.");
            return;
        }

        if (!GameConstants.isEquip(itemId)){

            player.dropMessage(5, "You can only drop equips with this command. Use the regular drop command for any other item type.");
            return;
        }

        for (int counter = 0; counter != quantity; counter++){
            Item newToDrop;
            newToDrop = ii.getEquipById(itemId);
            newToDrop.setOwner(player.getName());
            newToDrop.setGMLog(c.getPlayer().getName() + " used !dropmultiequip");
            c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), newToDrop, c.getPlayer().getPosition(), true, true);
        }
    }   
}