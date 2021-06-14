var status = -1;
var job = 310;
var darkMarble = 4031013;
function start(mode, type, selection) {
	if (qm.haveItem(darkMarble, 30)) {
	qm.sendNext("I am impressed, you surpassed the test. Only few are talented enough. You have proven yourself to be worthy, I shall mold your body into a #bHunter#k."");
	qm.gainItem(darkMarble, -30)
	qm.completeQuest(1419);
	qm.changeJob(job);
	qm.sendNext("You are now a #bHunter#k.")
	qm.dispose();	
	} else {
	qm.sendOk("You have not retrieved the Dark Marbles yet, I will be waiting.");
	qm.dispose();
	}
}

function end(mode, type, selection) {
    /* if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	if (status == 0) {
	    if (qm.getPlayerStat("HP") < 50) {
		qm.sendNext("Hey, your HP is not fully recovered yet. Did you take all the Roger's Apple that I gave you? Are you sure?");
		qm.dispose();
	    } else {
		qm.sendNext("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.");
	    }
	} else if (status == 1) {
	    qm.sendNextPrev("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!");
	} else if (status == 2) {
	    qm.sendNextPrev("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care if yourself and Good luck my friend!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v2010000# 3 #t2010000#\r\n#v2010009# 3 #t2010009#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10 exp");
	} else if (status == 3) {
	    qm.gainExp(10);
	    qm.gainItem(2010000, 3);
	    qm.gainItem(2010009, 3);
	    qm.forceCompleteQuest();
	    qm.dispose();
	}
    } */
}
