//NPC Lea - Guild related NPC (GMS-like)
//NPC ID: 2010011
//author: Manu

var status = -1;

function action(mode, type, selection) {
    if (mode == -1) {
        if (selection == -1)
            cm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            cm.sendOk("Come talk to me again if you would like to move to the Hall of Headquarters.");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

    }

    if (status == 0) {
        cm.sendYesNo("Hello, I am Lea. I am in charge of guild support. For guild management, I can arrange transportation to the guild base, the Hall of Headquarters. Would you like to move to the Hall of Headquarters for your guild-related issues?");
    } else if (status == 1) {
        cm.sendNext("Well then, I will immediately transport you.");
    } else {
        cm.warp(200000301);
        cm.dispose();
    }

}
