package client.messages.commands.player;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import constants.GameConstants;
import tools.FileoutputUtil;

public class CheckCommand extends Command{
    {
        setDescription("Show NX and points information.");
    }

    @Override
    public void execute(MapleClient c, String[] splitted){ 
        c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getCSPoints(MapleCharacter.CashShopType.NX_PREPAID) + " Cash.");
        c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getPoints() + " donation points.");
        c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getVPoints() + " voting points.");
        c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getIntNoRecord(GameConstants.BOSS_PQ) + " Boss Party Quest points.");
        c.getPlayer().dropMessage(6, "The time is currently " + FileoutputUtil.CurrentReadable_TimeGMT() + " GMT.");  
    }    
}