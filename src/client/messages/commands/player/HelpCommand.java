package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;

public class HelpCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        c.getPlayer().dropMessage(5, "@str, @dex, @int, @luk <amount to add>");
        c.getPlayer().dropMessage(5, "@mob < Information on the closest monster >");
        c.getPlayer().dropMessage(5, "@check < Displays various information >");
        c.getPlayer().dropMessage(5, "@fm < Warp to FM >");
        c.getPlayer().dropMessage(5, "@togglesmega < Toggle super megaphone on/off >");
        c.getPlayer().dropMessage(5, "@dispose < If you are unable to attack or talk to NPC >");
        c.getPlayer().dropMessage(5, "@clearslot < Cleanup that trash in your inventory >");
        c.getPlayer().dropMessage(5, "@ranking < Use @ranking for more details >");
        c.getPlayer().dropMessage(5, "@checkdrops < Use @checkdrops for more details >");  
        c.getPlayer().dropMessage(5, "@enablepic < Toggle pic on/off >");  
        c.getPlayer().dropMessage(5, "@uptime < Shows howlong the server has been up for >");
        //c.getPlayer().dropMessage(5, "@npc < Universal Town Warp / Event NPC>"); to be done
        //c.getPlayer().dropMessage(5, "@dcash < Universal Cash Item Dropper >");????  
    }   
}