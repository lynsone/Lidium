package client.messages.commands.headgm;

import client.MapleClient;
import client.messages.Command;

public class UnbanCommand extends Command {
    protected boolean hellban = false;

    private String getCommand() {
        if (hellban) {
            return "UnHellBan";
        } else {
            return "UnBan";
        }
    }

    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length < 1) {
            c.getPlayer().dropMessage(6, "[Syntax] !" + getCommand() + " <IGN>");
            return;
        }

        byte ret;
        if (hellban) {
            ret = MapleClient.unHellban(splitted[0]);
        } else {
            ret = MapleClient.unban(splitted[0]);
        }

        if (ret == -2) {
            c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL error.");
            return;
        } else if (ret == -1) {
            c.getPlayer().dropMessage(6, "[" + getCommand() + "] The character does not exist.");
            return;
        } else {
            c.getPlayer().dropMessage(6, "[" + getCommand() + "] Successfully unbanned!");
        }
    
        byte ret_ = MapleClient.unbanIPMacs(splitted[0]);
        if (ret_ == -2) {
            c.getPlayer().dropMessage(6, "[UnbanIP] SQL error.");
        } else if (ret_ == -1) {
            c.getPlayer().dropMessage(6, "[UnbanIP] The character does not exist.");
        } else if (ret_ == 0) {
            c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
        } else if (ret_ == 1) {
            c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
        } else if (ret_ == 2) {
            c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
        }
    }
}
