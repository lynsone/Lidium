package client.messages.commands.player;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import server.MapleInventoryManipulator;

public class StarterKitCommand extends Command {
    {
        setDescription("Claim your starter kit.");
    }

    @Override
    public void execute(MapleClient c, String[] splitted) { 
        MapleCharacter player = c.getPlayer();
        if (player.hasClaimedStarterKit()) {
            player.dropMessage(5, "You've already claimed your starter kit for this character.");
            return;
        }

        // Starter kit items and mesos.
        MapleInventoryManipulator.addById(c, 2000000, (short) 200, "Gained from starter kit."); // 100 red pot
        MapleInventoryManipulator.addById(c, 2000003, (short) 200, "Gained from starter kit."); // 100 blue pot
        MapleInventoryManipulator.addById(c, 1002602, (short) 1, "Gained from starter kit."); // 1 blue maple bandana
        player.gainMeso(50000, true); // small loan of 50,000.

        player.setClaimedStarterKit(true);
        player.dropMessage(6, "You've successfully claimed your starter pack.");
    }
}
