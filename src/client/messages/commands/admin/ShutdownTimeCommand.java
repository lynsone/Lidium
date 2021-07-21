package client.messages.commands.admin;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import server.ShutdownServer;
import server.TimerManager;

public class ShutdownTimeCommand extends Command {
    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleCharacter player = c.getPlayer();
        if (splitted.length < 1){
            player.dropMessage(6, "Syntax: !shutdown [<time>|NOW]");
            return;
        }
        
        int time = 60000;
        if (splitted[0].equalsIgnoreCase("now")){
            time = 1;
        } else {
            time *= Integer.parseInt(splitted[0]);
        }

        if (time > 1) {
            int seconds = (int) (time / 1000) % 60;
            int minutes = (int) ((time / (1000 * 60)) % 60);
            int hours = (int) ((time / (1000 * 60 * 60)) % 24);
            int days = (int) ((time / (1000 * 60 * 60 * 24)));

            String strTime = "";
            if (days > 0) strTime += days + " days, ";
            if (hours > 0) strTime += hours + " hours, ";
            strTime += minutes + " minutes, ";
            strTime += seconds + " seconds";

            for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                chr.dropMessage(6, "Server is undergoing maintenance process, and will be shutdown in " + strTime + ". Prepare yourself to quit safely in the mean time.");
            }
            
        }
        
        Runnable shutdown = new Runnable() {
            @Override
            public void run() {
                ShutdownServer.getInstance().shutdown();
            }
        };

        TimerManager.getInstance().schedule(shutdown, time);
    }
}
