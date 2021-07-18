package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;
import server.MapleShopFactory;

public class ShopCommand extends Command {
    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleShopFactory shop = MapleShopFactory.getInstance();

        int shopId;
        try {
            shopId = Integer.parseInt(splitted[0]);
        } catch (NumberFormatException $Exception) {
            c.getPlayer().dropMessage(5, "You should use shop ID");
            return;
        } catch (IndexOutOfBoundsException $BoundsException) {
            c.getPlayer().dropMessage(5, "!shop <shopID>");
            return;
        }

        if (shop.getShop(shopId) == null) {
            c.getPlayer().dropMessage(5, "Couldn't find a shop with that ID.");
        }

        shop.getShop(shopId).sendShop(c);
    }
}
