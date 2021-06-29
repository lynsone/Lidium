package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;
import server.MapleCarnivalChallenge;

public class JobCommand extends Command{

    @Override
    public void execute(MapleClient c, String[] splitted){
        if (MapleCarnivalChallenge.getJobNameById(Integer.parseInt(splitted[0])).length() == 0) {
            c.getPlayer().dropMessage(5, "Invalid Job");
            return;
        }
        c.getPlayer().changeJob(Integer.parseInt(splitted[0]));       
    }
}