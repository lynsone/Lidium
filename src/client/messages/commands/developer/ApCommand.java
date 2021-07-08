package client.messages.commands.developer;

import client.messages.Command;
import client.MapleClient;
import client.MapleCharacter;

public class ApCommand extends Command {
 
    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleCharacter player = c.getPlayer();
        int amount;
        
        if (splitted.length < 1){
            player.blueMessage("Syntax: !ap <amount> -- The amount can be negative." );
            return;
        }
        
        try{
            amount = Math.min(Integer.parseInt(splitted[0]), 32000 - c.getPlayer().getRemainingAp());
        }
        catch (NumberFormatException e){
            player.dropMessage(6, "That is not a valid number!");
            return;
        }
        
        if (amount + player.getRemainingAp() > 32000){
            player.dropMessage(6, "You can't have more than 32000 AP.");
        }else{
            player.gainAp((short) amount);
            player.dropMessage(6, "You gained " + amount + " AP.");
        }
    }
}