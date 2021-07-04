package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;
import handling.world.World;
import tools.StringUtil;
import tools.packet.CWvsContext;

public class SayCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            if (c.getPlayer().isIntern()){
                sb.append("Intern ");
            }else if (c.getPlayer().isGM()) {
                sb.append("GM ");
            }else if (c.getPlayer().isHeadGM()) {
                sb.append("Head-GM ");
            }else if (c.getPlayer().isDeveloper()) {
                sb.append("Developer ");
            }else if (c.getPlayer().isAdmin()) {
                sb.append("Admin ");
            }
            sb.append(c.getPlayer().getName());
            sb.append("] ");
            sb.append(StringUtil.joinStringFrom(splitted, 0));
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(c.getPlayer().isGM() ? 6 : 5, sb.toString()));
        } else {
            c.getPlayer().dropMessage(6, "Syntax: say <message>");
        }
    }   
}