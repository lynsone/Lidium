/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands.developer;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import server.MapleCarnivalChallenge;

public class JobPersonCommand extends Command{


    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleCharacter player = c.getPlayer();
        if(splitted.length == 1){
            int jobid = Integer.parseInt(splitted[0]);
            if (MapleCarnivalChallenge.getJobNameById(jobid).equals("")) {
                player.dropMessage(6, "Jobid " + jobid + " is not available.");
                return;
            }

            player.changeJob(jobid);
            player.equipChanged();
        }else if (splitted.length == 2){
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);

            if(victim != null){
                int jobid = Integer.parseInt(splitted[0]);
                if (MapleCarnivalChallenge.getJobNameById(jobid).equals("")) {
                    player.dropMessage(6, "Jobid " + jobid + " is not available.");
                    return;
                }

                victim.changeJob(jobid);
                player.equipChanged();
            }else{
                player.dropMessage(6, "Player '" + splitted[1] + "' could not be found.");
            }
        }else{
            player.dropMessage(6, "Syntax: !jobperson <job id> <opt: IGN of another person>");
        }
    }  
}