package client.messages.commands.gm;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import tools.StringUtil;

public class BanCommand extends Command {
    protected boolean hellban = false, ipBan = false;

    private String getCommand() {
        if (hellban) {
            return "HellBan";
        } else {
            return "Ban";
        }
    }

    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length < 3) {
            c.getPlayer().dropMessage(5, "[Syntax] !" + getCommand() + " <IGN> <Reason>");
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (hellban) {
            sb.append("Banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
        } else {
            sb.append(c.getPlayer().getName()).append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
        }
        MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
        if (target != null) {
            if (c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().isAdmin()) {
                sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                if (target.ban(sb.toString(), hellban || ipBan, false, hellban)) {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] Successfully banned " + splitted[1] + ".");
                    return;
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] Failed to ban.");
                    return;
                }
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] May not ban GMs...");
                return;
            }
        } else {
            if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), hellban)) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] Successfully offline banned " + splitted[1] + ".");
                return;
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] Failed to ban " + splitted[1]);
                return;
            }
        }
    }
}
