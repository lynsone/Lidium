package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import java.awt.Point;

public class MyNpcPosCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        Point pos = c.getPlayer().getPosition();
        c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFH());
    }
}
