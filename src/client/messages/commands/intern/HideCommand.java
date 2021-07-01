package client.messages.commands.intern;

import client.MapleClient;
import client.SkillFactory;
import client.messages.Command;
import constants.GameConstants;

public class HideCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        SkillFactory.getSkill(GameConstants.GMS ? 9101004 : 9001004).getEffect(1).applyTo(c.getPlayer());
    }   
}