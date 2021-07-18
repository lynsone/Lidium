package client.messages.commands.headgm;

import client.MapleClient;
import client.messages.Command;

public class LevelCommand extends Command {
    @Override
    public void execute(MapleClient c, String[] splitted){
        short maxLevel = 255;
        short level = c.getPlayer().getLevel();
        try {
            level = Short.parseShort(splitted[0]);
        } catch (NumberFormatException $Exception) { // out of range for short.
            level = maxLevel;
        }
        
        if (level > maxLevel) { // in range of short, but out of range for max level.
            level = maxLevel;
        }

        if (level != c.getPlayer().getLevel()) { // Only do this if target level is different from now.
            c.getPlayer().setLevel(level);
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
        }
    } 
}
