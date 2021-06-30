/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands.headgm;

import client.MapleClient;
import client.messages.Command;
import java.util.HashMap;
import server.MaplePortal;
import server.maps.MapleMap;

/**
 *
 * @author Manu
 */
public class GoToCommand extends Command {

    private static final HashMap<String, Integer> gotomaps = new HashMap<String, Integer>();

    static {
        gotomaps.put("gmmap", 180000000);
        gotomaps.put("southperry", 2000000);
        gotomaps.put("amherst", 1010000);
        gotomaps.put("henesys", 100000000);
        gotomaps.put("ellinia", 101000000);
        gotomaps.put("perion", 102000000);
        gotomaps.put("kerning", 103000000);
        gotomaps.put("harbor", 104000000);
        gotomaps.put("sleepywood", 105000000);
        gotomaps.put("florina", 120000300);
        gotomaps.put("orbis", 200000000);
        gotomaps.put("happyville", 209000000);
        gotomaps.put("elnath", 211000000);
        gotomaps.put("ludibrium", 220000000);
        gotomaps.put("aquaroad", 230000000);
        gotomaps.put("leafre", 240000000);
        gotomaps.put("mulung", 250000000);
        gotomaps.put("herbtown", 251000000);
        gotomaps.put("omegasector", 221000000);
        gotomaps.put("koreanfolktown", 222000000);
        gotomaps.put("newleafcity", 600000000);
        gotomaps.put("sharenian", 990000000);
        gotomaps.put("pianus", 230040420);
        gotomaps.put("horntail", 240060200);
        gotomaps.put("chorntail", 240060201);
        gotomaps.put("griffey", 240020101);
        gotomaps.put("manon", 240020401);
        gotomaps.put("zakum", 280030000);
        gotomaps.put("czakum", 280030001);
        gotomaps.put("papulatus", 220080001);
        gotomaps.put("showatown", 801000000);
        gotomaps.put("zipangu", 800000000);
        gotomaps.put("ariant", 260000100);
        gotomaps.put("nautilus", 120000000);
        gotomaps.put("boatquay", 541000000);
        gotomaps.put("malaysia", 550000000);
        gotomaps.put("erev", 130000000);
        gotomaps.put("ellin", 300000000);
        gotomaps.put("kampung", 551000000);
        gotomaps.put("singapore", 540000000);
        gotomaps.put("amoria", 680000000);
        gotomaps.put("timetemple", 270000000);
        gotomaps.put("pinkbean", 270050100);
        gotomaps.put("fm", 910000000);
        gotomaps.put("freemarket", 910000000);
        gotomaps.put("oxquiz", 109020001);
        gotomaps.put("ola", 109030101);
        gotomaps.put("fitness", 109040000);
        gotomaps.put("snowball", 109060000);
        gotomaps.put("golden", 950100000);
        gotomaps.put("phantom", 610010000);
        gotomaps.put("cwk", 610030000);
        gotomaps.put("rien", 140000000);
        gotomaps.put("edel", 310000000);
        gotomaps.put("ardent", 910001000);
        gotomaps.put("craft", 910001000);
        gotomaps.put("pvp", 960000000);
        gotomaps.put("future", 271000000);
    }

    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length < 1) {
            c.getPlayer().dropMessage(6, "Syntax: !goto <mapname>");
        } else {
            if (gotomaps.containsKey(splitted[0])) {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[0]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "Map does not exist");
                    return;
                }
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else {
                if (splitted[0].equals("locations")) {
                    c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
                    StringBuilder sb = new StringBuilder();
                    for (String s : gotomaps.keySet()) {
                        sb.append(s).append(", ");
                    }
                    c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));

                } else {
                    c.getPlayer().dropMessage(6, "Invalid command syntax - Use !goto <location>. For a list of locations, use !goto locations.");
                }
            }
        }
        return;
    }
}
