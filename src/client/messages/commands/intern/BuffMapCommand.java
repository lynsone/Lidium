package client.messages.commands.intern;

import client.MapleClient;
import client.SkillFactory;
import client.messages.Command;

/**
 * Author: Manu
 */
public class BuffMapCommand extends Command {

    @Override
    public void execute(MapleClient client, String[] params) {
        int[] buffs = { 9101000, 9101001, 9101002, 9101003, 9101008, 1005, 2301002 };
        for (int b : buffs) {
            client.getPlayer().getMap().getCharacters().forEach((chr) -> {
                SkillFactory.getSkill(b).getEffect(SkillFactory.getSkill(b).getMaxLevel()).applyTo(chr);
            });
        }
    }

}