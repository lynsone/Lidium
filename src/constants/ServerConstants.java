/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package constants;

import server.ServerProperties;

public class ServerConstants {
    // 159.89.87.254

    public static boolean TESPIA = false; // true = uses GMS test server, for MSEA it does nothing though
    public static final byte[] Gateway_IP = new byte[] { (byte) 127, (byte) 0, (byte) 0, (byte) 1 };
    // public static final byte[] Gateway_IP = new byte[]{(byte) 5, (byte) 180,
    // (byte) 9, (byte) 16};
    // Inject a DLL that hooks SetupDiGetClassDevsExA and returns 0.

    /*
     * Specifics which job gives an additional EXP to party returns the percentage
     * of EXP to increase
     */
    public static final byte Class_Bonus_EXP(final int job) {
        switch (job) {
            case 501, 530, 531, 532, 2300, 2310, 2311, 2312, 3100, 3110, 3111, 3112, 800, 900, 910 -> {
                return 10;
            }
        }
        return 0;
    }

    // Start of Poll
    public static final boolean PollEnabled = false;
    public static final String Poll_Question = "Are you mudkiz?";
    public static final String[] Poll_Answers = { "test1", "test2", "test3" };
    // End of Poll
    public static final short MAPLE_VERSION = (short) 111;
    public static final String MAPLE_PATCH = "1";
    public static boolean Use_Fixed_IV = true; // true = disable sniffing, false = server can connect to itself
    public static boolean Use_Localhost = false; // true = packets are logged, false = others can connect to server
    public static final int MIN_MTS = 100; // lowest amount an item can be, GMS = 110
    public static final int MTS_BASE = 0; // +amount to everything, GMS = 500, MSEA = 1000
    public static final int MTS_TAX = 5; // +% to everything, GMS = 10
    public static final int MTS_MESO = 10000; // mesos needed, GMS = 5000
    public static final String SQL_USER = "root", SQL_PASSWORD = "";
    // master login is only used in GMS: fake account for localhost only
    // master and master2 is to bypass all accounts passwords only if you are under
    // the IPs below

    public static byte[] getServerIP() {
        byte[] bip = null;

        final String serverip = ServerProperties.getProperty("net.sf.odinms.world.host").trim();
        String[] split = serverip.split("\\.");
        bip = new byte[] { (byte) Integer.parseInt(split[0]), (byte) Integer.parseInt(split[1]),
                (byte) Integer.parseInt(split[2]), (byte) Integer.parseInt(split[3]) };
        return bip;
    }
}
