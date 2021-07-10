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
package server.life;

public class MonsterDropEntry {

    private final int itemId, chance, minimum, maximum, questId;

    public MonsterDropEntry(int itemId, int chance, int minimum, int maximum, int questId) {
        this.itemId = itemId;
        this.chance = chance;
        this.minimum = minimum;
        this.maximum = maximum;
        this.questId = questId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getChance() {
        return chance;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public int getQuestId() {
        return questId;
    }

}
