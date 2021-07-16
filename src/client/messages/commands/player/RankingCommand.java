package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;
import java.util.List;
import server.RankingWorker;
import server.RankingWorker.RankingInformation;

public class RankingCommand extends Command{
    {
        setDescription("Show ranking.");
    }
    @Override
    public void execute(MapleClient c, String[] splitted){ 
        if(splitted.length < 4){
            c.getPlayer().dropMessage(5, "Use @ranking [job] [start number] [end number] where start and end are ranks of the players");
            final StringBuilder builder = new StringBuilder("JOBS: ");
            for(String b : RankingWorker.getJobCommands().keySet()){
                builder.append(b);
                builder.append(" ");
            }
            c.getPlayer().dropMessage(5, builder.toString());
        }else{
            int start = 1, end = 20;
            try{
                start = Integer.parseInt(splitted[2]);
                end = Integer.parseInt(splitted[3]);
            }catch(NumberFormatException e){
                c.getPlayer().dropMessage(5, "You didn't specify start and end number correctly, the default values of 1 and 20 will be used.");
            }
            if(end < start || end - start > 20){
                c.getPlayer().dropMessage(5, "End number must be greater, and end number must be within a range of 20 from the start number.");
            }else{
                final Integer job = RankingWorker.getJobCommand(splitted[1]);
                if(job == null){
                    c.getPlayer().dropMessage(5, "Please use @ranking to check the job names.");
                }else{
                    final List<RankingInformation> ranks = RankingWorker.getRankingInfo(job.intValue());
                    if(ranks == null || ranks.size() <= 0){
                        c.getPlayer().dropMessage(5, "Ranking not available. Please try again later.");
                    }else{
                        int num = 0;
                        for(RankingInformation rank : ranks){
                            if(rank.rank >= start && rank.rank <= end){
                                if(num == 0){
                                    c.getPlayer().dropMessage(6, "Rankings for " + splitted[1] + " - from " + start + " to " + end);
                                    c.getPlayer().dropMessage(6, "--------------------------------------");
                                }
                                c.getPlayer().dropMessage(6, rank.toString());
                                num++;
                            }
                        }
                        if(num == 0){
                            c.getPlayer().dropMessage(5, "No ranking was returned.");
                        }
                    }
                }
            }
        }
    }
}