package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import server.MaplePortal;

public class LookPortalCommand extends Command{
        
    @Override
    public void execute(MapleClient c, String[] splitted) {
        for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
            c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
        }
    }
}
