package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;

public class EnablePicCommand extends Command {

    {
        setDescription("Enable or disable your PIC.");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        boolean picEnable = c.isPicEnable();
        picEnable = !picEnable;
        c.setPicEnable(picEnable);
        c.getPlayer().dropMessage(-1, "Your PIC has been " + (picEnable ? "enabled." : "deactivated."));
    }
}