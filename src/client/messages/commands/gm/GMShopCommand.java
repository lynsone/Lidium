package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;
import server.MapleShopFactory;

public class GMShopCommand extends Command {
    protected int gmShopId = 1337;

    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleShopFactory shop = MapleShopFactory.getInstance();

        if (shop.getShop(gmShopId) == null) {
            c.getPlayer().dropMessage(5, "GM Shop not found, contact a developer.");
            return;
        }

        shop.getShop(gmShopId).sendShop(c);
    }
}
