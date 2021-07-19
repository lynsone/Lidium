/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.life;

/**
 *
 * @author TheWayMan
 */
public class MonsterLevelDropEntry {
    
       public MonsterLevelDropEntry(int itemId, int chance, int moblevel, byte dropType, int Minimum, int Maximum, int questid) {
        this.itemId = itemId;
        this.chance = chance;
        this.dropType = dropType;
        this.moblevel = moblevel;
        this.questid = questid;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
       }
       
        public MonsterLevelDropEntry(int itemId, int chance, int moblevel, byte dropType, int Minimum, int Maximum, int questid, boolean onlySelf) {
        this.itemId = itemId;
        this.chance = chance;
        this.dropType = dropType;
        this.moblevel = moblevel;
        this.questid = questid;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
        this.onlySelf = onlySelf;
    }
         public byte dropType;
    public int itemId, chance, Minimum, Maximum, moblevel, questid;
    public boolean onlySelf = false;
    
}
