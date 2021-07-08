/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands.admin;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;

/**
 *
 * @author Administrator
 */
public class SetGMLevelCommand extends Command
{
    {
        setDescription("");
    }
    
    @Override
    public void execute(MapleClient c, String params[])
    {
        MapleCharacter player = c.getPlayer();
        if (params.length < 2) {
            c.getPlayer().dropMessage(6, "Syntax: !setgmlevel <playername> <newgmlevel>");
            return ;
        }

        int newLevel = Integer.parseInt(params[1]);
        MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
        if (target != null) 
        {
            target.setGMLevel(newLevel);
            target.getClient().setGMLevel(newLevel);

            target.dropMessage(6, "You are now a level " + newLevel + " GM. See !help for a list of available commands.");


            player.dropMessage(6, target.getName() + " is now a level " + newLevel + " GM.");

        } else {
            player.dropMessage(6, "Player '" + params[1] + "' was not found on this channel.");
        }
        return;
    }
}
