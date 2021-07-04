package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import database.DatabaseConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemInfo.CashModInfo;

public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private final static int[] bestItems = new int[]{10003055, 10003090, 10103464, 10002960, 10103363};
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();
    private final Map<Integer, List<Integer>> itemPackage = new HashMap<Integer, List<Integer>>();
    private final Map<Integer, CashModInfo> itemMods = new HashMap<Integer, CashModInfo>();
    private final Map<Integer, List<Integer>> openBox = new HashMap<>();
    private final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
    private static List<Integer> blacklist = new ArrayList<>();

    public static final CashItemFactory getInstance() {
        return instance;
    }

    public void initialize() {
        ThreadManager.getInstance().newTask(() -> {

            long start = System.currentTimeMillis();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("CashShopBlackList.ini"));
                String line = reader.readLine();
                while (line != null) {
                    try {
                        if (!line.isEmpty() && !line.split(",")[0].startsWith("#")) {
                            getBlacklist().add(Integer.parseInt(line.split(",")[0]));
                        }
                    } catch (Exception ex) {
                        System.err.println("Error while loading Black listed Cash Item.\r\n" + ex.getMessage());
                    }
                    line = reader.readLine();
                }
            } catch (IOException | NumberFormatException ex) {
                System.err.println("Error while loading cash black list.\r\n" + ex.getMessage());
            }
            final List<MapleData> cccc = data.getData("Commodity.img").getChildren();

            cccc.forEach(field -> {
                final int SN = MapleDataTool.getIntConvert("SN", field, 0);
                int ID = MapleDataTool.getIntConvert("ItemId", field, 0);
                final int Count = MapleDataTool.getIntConvert("Count", field, 1);
                final int Price = MapleDataTool.getIntConvert("Price", field, 0);
                final int Period = MapleDataTool.getIntConvert("Period", field, 0);
                final int Gender = MapleDataTool.getIntConvert("Gender", field, 2);
                final boolean OnSale = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && Price > 0;
                if (getBlacklist().contains(ID) || ((Period == 0 && OnSale))) { // Block black listed item from CS
                    if (!itemMods.containsKey(SN)) {
                        itemMods.put(SN, new CashModInfo(SN, 0, -1, false, ID, 100, false, 0, Gender, 0, 0, 0, 0, 0, 40000));
                    }
                }
                final CashItemInfo stats = new CashItemInfo(ID, Count, Price, SN, Period, Gender, OnSale);
                if (SN > 0) {
                    itemStats.put(SN, stats);
                }
            });
            final MapleData b = data.getData("CashPackage.img");
            for (MapleData c : b.getChildren()) {
                if (c.getChildByPath("SN") == null) {
                    continue;
                }
                final int packageID = Integer.parseInt(c.getName());
                final List<Integer> packageItems = new ArrayList<Integer>();
                for (MapleData d : c.getChildByPath("SN").getChildren()) {
                    try {
                        packageItems.add(MapleDataTool.getIntConvert(d));
                    } catch (Exception e) {
                    }
                }
                for (int pi : packageItems) { // Block Cash Package if contain item in black list
                    if (getBlacklist().contains((itemStats.containsKey(pi) ? itemStats.get(pi).getId() : 0))) {
                        for (int _SN : getSN(packageID)) {
                            if (!itemMods.containsKey(_SN)) {
                                itemMods.put(_SN, new CashModInfo(_SN, 0, -1, false, 0, 100, false, 0, 2, 0, 0, 0, 0, 0, 40000));
                            }
                        }
                    }
                }
                itemPackage.put(Integer.parseInt(c.getName()), packageItems);
            }

            for (int i = 0; i < getBlacklist().size(); i++) {
                if (getBlacklist().get(i) / 100000 == 9) {
                    for (int _SN : getSN(getBlacklist().get(i))) {
                        if (itemMods.containsKey(_SN)) {
                            itemMods.put(_SN, new CashModInfo(_SN, 0, -1, false, 0, 100, false, 0, 2, 0, 0, 0, 0, 0, 40000));
                        }
                    }
                }
            }

            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    CashModInfo ret = new CashModInfo(rs.getInt("serial"), rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                    itemMods.put(ret.sn, ret);
                    if (ret.showUp) {
                        final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                        if (cc != null) {
                            ret.toCItem(cc); //init
                        }
                    }
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Integer> availableSN = new LinkedList<>();
            availableSN.add(20001141);
            availableSN.add(20001142);
            availableSN.add(20001143);
            availableSN.add(20001144);
            availableSN.add(20001145);
            availableSN.add(20001146);
            availableSN.add(20001147);
            openBox.put(5533003, availableSN); // Rainbow Visor Box

            availableSN = new LinkedList<>();
            availableSN.add(20000462);
            availableSN.add(20000463);
            availableSN.add(20000464);
            availableSN.add(20000465);
            availableSN.add(20000466);
            availableSN.add(20000467);
            availableSN.add(20000468);
            availableSN.add(20000469);
            openBox.put(5533000, availableSN); // Korean stuffs..

            availableSN = new LinkedList<>();
            availableSN.add(20800259);
            availableSN.add(20800260);
            availableSN.add(20800263);
            availableSN.add(20800264);
            availableSN.add(20800265);
            availableSN.add(20800267);
            openBox.put(5533001, availableSN); // Angelic Beam Weapon Box

            availableSN = new LinkedList<>();
            availableSN.add(20800270);
            availableSN.add(20800271);
            availableSN.add(20800272);
            availableSN.add(20800273);
            availableSN.add(20800274);
            openBox.put(5533002, availableSN); // Chief Knight Weapon Box
            System.out.println("Cash Item Factory loaded in " + (System.currentTimeMillis() - start) + "ms.");

        });
      
    }

    public final CashItemInfo getSimpleItem(int sn) {
        return itemStats.get(sn);
    }

    public final CashItemInfo getItem(int sn) {
        final CashItemInfo stats = itemStats.get(Integer.valueOf(sn));
        final CashModInfo z = getModInfo(sn);
        if (z != null && z.showUp) {
            return z.toCItem(stats); //null doesnt matter
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }
        //hmm
        return stats;
    }

    public final List<Integer> getPackageItems(int itemId) {
        return itemPackage.get(itemId);
    }

    public final CashModInfo getModInfo(int sn) {
        return itemMods.get(sn);
    }

    public final Collection<CashModInfo> getAllModInfo() {
        return itemMods.values();
    }

    public final Map<Integer, List<Integer>> getRandomItemInfo() {
        return openBox;
    }

    public final int[] getBestItems() {
        return bestItems;
    }

    public static List<Integer> getBlacklist() {
        return blacklist;
    }

    public static void setBlacklist(List<Integer> aBlacklist) {
        blacklist = aBlacklist;
    }

    private List<Integer> getSN(int ItemId) {
        final List<Integer> SN = new ArrayList<>();
        for (Entry<Integer, CashItemInfo> item : itemStats.entrySet()) {
            if (ItemId == item.getValue().getId()) {
                SN.add(item.getKey());
            }
        }
        return SN;
    }
}
