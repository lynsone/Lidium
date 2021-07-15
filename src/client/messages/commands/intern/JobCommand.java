package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;
import server.MapleCarnivalChallenge;

public class JobCommand extends Command{

    @Override
    public void execute(MapleClient c, String[] splitted) {
        int jobId;
        try {
            jobId = Integer.parseInt(splitted[0]);
        } catch (NumberFormatException $Exception) {
            c.getPlayer().dropMessage(5, "You should use jobID.");
            return;
        } catch (IndexOutOfBoundsException $Exception) {
            c.getPlayer().dropMessage(5, "!job <jobID>");
            return;
        }

        if (MapleCarnivalChallenge.getJobNameById(jobId).length() == 0) {
            c.getPlayer().dropMessage(5, "Invalid Job ID.");
            return;
        }
        c.getPlayer().changeJob(jobId);       
    }
}