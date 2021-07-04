package client.messages.commands.gm;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;
import handling.world.World;
import server.maps.MapleMap;

public class WarpHereCommand extends Command{
    

    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        if (victim != null) {
            if (c.getPlayer().inPVP() || (!c.getPlayer().isGM() && (victim.isInBlockedMap()))) {
                c.getPlayer().dropMessage(5, "Try again later.");
                return;
            }
            victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition()));
        } else {
            int ch = World.Find.findChannel(splitted[0]);
            if (ch < 0) {
                c.getPlayer().dropMessage(5, "Not found.");
                return;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[0]);
            if (victim == null || victim.inPVP() || (!c.getPlayer().isGM() && (victim.isInBlockedMap() || victim.isGM()))) {
                c.getPlayer().dropMessage(5, "Try again later.");
                return;
            }
            c.getPlayer().dropMessage(5, "Victim is cross changing channel.");
            victim.dropMessage(5, "Cross changing channel.");
            if (victim.getMapId() != c.getPlayer().getMapId()) {
                final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                victim.changeMap(mapp, mapp.findClosestPortal(c.getPlayer().getTruePosition()));
            }
            victim.changeChannel(c.getChannel());
        }
    }
}