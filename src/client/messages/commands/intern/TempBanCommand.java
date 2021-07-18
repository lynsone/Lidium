package client.messages.commands.intern;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import java.text.DateFormat;
import java.util.Calendar;

public class TempBanCommand extends Command {
    protected boolean ipBan = false;
    private String[] types = {"HACK", "BOT", "AD", "HARASS", "CURSE", "SCAM", "MISCONDUCT", "SELL", "ICASH", "TEMP", "GM", "IPROGRAM", "MEGAPHONE"};

    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length < 3) {
            c.getPlayer().dropMessage(6, "Tempban [name] [REASON] [days]");
            StringBuilder s = new StringBuilder("Tempban reasons: ");
            for (int i = 0; i < types.length; i++) {
                s.append(i + 1).append(" - ").append(types[i]).append(", ");
            }
            c.getPlayer().dropMessage(6, s.toString());
            return;
        }
        final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        
        final int numDay;
        try {
            numDay = Integer.parseInt(splitted[2]);
        } catch (NumberFormatException $Exception) {
            c.getPlayer().dropMessage(6, "Amount of days should be a number.");
            return;
        }
        
        final int reason;
        try {
            reason = Integer.parseInt(splitted[1]);
        } catch (NumberFormatException $Exception) {
            c.getPlayer().dropMessage(6, "Please select the reason by inputting the corresponding number.");
            return;
        }

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, numDay);
        final DateFormat df = DateFormat.getInstance();

        if (victim == null || reason < 0 || reason >= types.length) {
            c.getPlayer().dropMessage(6, "Unable to find character or reason was not valid, type tempban to see reasons");
            return;
        }
        victim.tempban("Temp banned by " + c.getPlayer().getName() + " for " + types[reason] + " reason", cal, reason, ipBan);
        c.getPlayer().dropMessage(6, "The character " + splitted[0] + " has been successfully tempbanned till " + df.format(cal.getTime()));
    }
}
