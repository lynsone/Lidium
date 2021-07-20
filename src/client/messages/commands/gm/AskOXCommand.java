package client.messages.commands.gm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import server.TimerManager;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class AskOXCommand extends Command {
    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (c.getPlayer().getMapId() != 109020001) {
            c.getPlayer().changeMap(109020001, 0);
            c.getPlayer().dropMessage(6, "Warp other players to this map to begin the event");
            return;
        }
        final MapleCharacter asker = c.getPlayer();
        final MapleMap thisMap =  asker.getMap();        
        String[] trueCmd = {"true", "t", "left", "l", "o", "1"};
        String[] falseCmd = {"false", "f", "right", "r", "x", "0"};
        boolean tempAns;
        if (splitted.length > 0 && Arrays.asList(trueCmd).contains(splitted[0].toLowerCase())) {
            tempAns = true;
        } else if (splitted.length > 0 && Arrays.asList(falseCmd).contains(splitted[0].toLowerCase())) {   
            tempAns = false;
        } else {
            c.getPlayer().dropMessage(6, "Syntax: !AskOX <True/False> <Time> <Question>");
            return;
        }
        final boolean ans = tempAns;
        final int timeLimit = Math.min(Math.max(Integer.parseInt(splitted[1]), 1), 60);
        final String question = "Question: " + StringUtil.joinStringFrom(splitted, 2);

        thisMap.broadcastMessage(CField.MapEff("SportsDay/EndMessage/Start"));
        thisMap.broadcastMessage(CField.playSound("Dojang/start"));
        TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                thisMap.broadcastMessage(CWvsContext.serverNotice(1, question));
                thisMap.broadcastMessage(CWvsContext.serverNotice(0, question));
                thisMap.broadcastMessage(CField.getClock(timeLimit));
                TimerManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        List<MapleCharacter> winners = new ArrayList<MapleCharacter>();
                        for (final MapleCharacter victim : thisMap.getCharacters()) {
                            if (victim == asker) {
                                continue;
                            }
                            if ((victim.getPosition().x > -308 && victim.getPosition().x < -142) || victim.getPosition().y <= -206) {
                                // NO ANSWER
                                victim.getClient().getSession().write(CField.MapEff("SportsDay/EndMessage/TimeOver"));
                                victim.getClient().getSession().write(CField.playSound("phantom/skaia"));
                            } else if (victim.getPosition().x > -308 ^ ans) {
                                // CORRECT ANSWER
                                winners.add(victim);
                                victim.getClient().getSession().write(CField.MapEff("SportsDay/EndMessage/Win"));
                                victim.getClient().getSession().write(CField.playSound("Party1/Clear"));  
                            } else {
                                // WRONG ANSWER
                                victim.getClient().getSession().write(CField.MapEff("SportsDay/EndMessage/Lose"));
                                victim.getClient().getSession().write(CField.playSound("Party1/Failed"));
                            }
                        }
                        if (winners.size() > 0) {
                            for (final MapleCharacter victim : thisMap.getCharacters()) {
                                if (victim != asker && !winners.contains(victim)) {
                                    victim.dropMessage(5, "Unfortunately you picked the wrong answer. You will be warped out in 5 secs.");
                                    // WARP OUT IN 5 SEC
                                    TimerManager.getInstance().schedule(new Runnable() {
                                    @Override
                                        public void run() {
                                            int map = victim.getSavedLocation(SavedLocationType.EVENT);
                                            if (map <= -1) {
                                                map = 104000000;
                                            }
                                            final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(map);
                                            victim.changeMap(mapp, mapp.getPortal(0));
                                        }
                                        }, 5000);
                                }
                            }
                        } else {
                            thisMap.broadcastMessage(CWvsContext.serverNotice(6, "Since there are no winners for this round, all participants will proceed on to the next round"));
                        }
                    }
                }, timeLimit * 1000);
            }
            }, 2000);
        return;
    }
}
