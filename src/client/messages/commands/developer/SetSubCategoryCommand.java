package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;

public class SetSubCategoryCommand extends Command {
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        c.getPlayer().setSubcategory(Byte.parseByte(splitted[0]));
    }   
}