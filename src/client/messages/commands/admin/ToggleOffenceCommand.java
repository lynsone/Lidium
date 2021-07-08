package client.messages.commands.admin;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import client.messages.Command;

public class ToggleOffenceCommand extends Command {
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        try {
            CheatingOffense co = CheatingOffense.valueOf(splitted[0]);
            co.setEnabled(!co.isEnabled());
        } catch (IllegalArgumentException iae) {
            c.getPlayer().dropMessage(6, "Offense " + splitted[0] + " not found");
        }
    }  
}