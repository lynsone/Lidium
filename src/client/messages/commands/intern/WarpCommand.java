package client.messages.commands.intern;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;
import handling.world.World;
import server.MaplePortal;
import server.maps.MapleMap;

public class WarpCommand extends Command{

    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel() && !victim.inPVP() && !c.getPlayer().inPVP()) {
            if (splitted.length == 1) {
                c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getTruePosition()));
            } else {
                MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "Map does not exist");
                    return;
                }
                MaplePortal targetPortal = null;
                if (splitted.length > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                    } catch (IndexOutOfBoundsException e) {
                        // noop, assume the gm didn't know how many portals there are
                        c.getPlayer().dropMessage(5, "Invalid portal selected.");
                    } catch (NumberFormatException a) {
                        // noop, assume that the gm is drunk
                    }
                }
                if (targetPortal == null) {
                    targetPortal = target.getPortal(0);
                }
                victim.changeMap(target, targetPortal);
            }
        } else {
            try {
                victim = c.getPlayer();
                int ch = World.Find.findChannel(splitted[0]);
                if (ch < 0) {
                    MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[0]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "Map does not exist");
                        return;
                    }
                    MaplePortal targetPortal = null;
                    if (splitted.length > 2) {
                        try {
                            targetPortal = target.getPortal(Integer.parseInt(splitted[1]));
                        } catch (IndexOutOfBoundsException e) {
                            // noop, assume the gm didn't know how many portals there are
                            c.getPlayer().dropMessage(5, "Invalid portal selected.");
                        } catch (NumberFormatException a) {
                            // noop, assume that the gm is drunk
                        }
                    }
                    if (targetPortal == null) {
                        targetPortal = target.getPortal(0);
                    }
                    c.getPlayer().changeMap(target, targetPortal);
                } else {
                    victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[0]);
                    c.getPlayer().dropMessage(6, "Cross changing channel. Please wait.");
                    if (victim.getMapId() != c.getPlayer().getMapId()) {
                        final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                        c.getPlayer().changeMap(mapp, mapp.findClosestPortal(victim.getTruePosition()));
                    }
                    c.getPlayer().changeChannel(ch);
                }
            } catch (NumberFormatException e) {
                c.getPlayer().dropMessage(6, "Something went wrong " + e.getMessage());
            }
        }
    }
}