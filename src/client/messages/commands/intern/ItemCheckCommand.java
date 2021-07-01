package client.messages.commands.intern;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;

public class ItemCheckCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length < 2 || splitted[0] == null || splitted[0].equals("") || splitted[1] == null || splitted[1].equals("")) {
            c.getPlayer().dropMessage(6, "!itemcheck <playername> <itemid>");
        } else {
            int item = Integer.parseInt(splitted[1]);
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
            int itemamount = chr.getItemQuantity(item, true);
            if (itemamount > 0) {
                c.getPlayer().dropMessage(6, chr.getName() + " has " + itemamount + " (" + item + ").");
            } else {
                c.getPlayer().dropMessage(6, chr.getName() + " doesn't have (" + item + ")");
            }
        }
    }   
}