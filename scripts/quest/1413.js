//Dances with balrog Spearman job advancement
var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.sendOk("You cannot stay a Swordman. You #bwill#k have to face up to the test.\r\n Talk to me when you are ready");
			qm.dispose(); 
			return;
		}
	}
	if (status == 0){                  
	    qm.sendNext("Before I teach you the ways of the Spearman, you will have to accomplish a very difficult test.");
        }else if (status == 1) {
            qm.sendNext("I will warp you into a special map, in which I require you to defeat #bSkeledogs#k and return 30 #i4031013# to me.");  
	}else if (status == 2){
            qm.sendNext("Once you enter the map, you #rcannot#k return without the #b#t4031013#s#k, if you die you will lose your experience.");
        }else if (status == 3){
            qm.sendYesNo("Are you ready ?");    
	} else if(status == 4){
            qm.forceStartQuest();
            qm.warp(910230000, 0);
            qm.dispose();            
        }
}

function end(mode, type, selection) {    
    status++;
	if (mode != 1){            
	    if(type == 1 && mode == 0)
                status -= 2;
            else{
                qm.dispose();
		return;
            }
	}
	if (status == 0 && qm.haveItem(4031013, 30)){
            qm.sendNext("I am impressed, you surpassed the test. Only few are talented enough.");
        }else if (status == 1){
            qm.sendNext("You have proven yourself to be worthy, I shall mold your body into a #bSpearman#k.");       
        }else if (status == 2){
            qm.gainItem(4031013, -30);
	    qm.changeJob(120);
	    qm.sendOk("You are now a #bSpearman#k");
	    qm.forceCompleteQuest(); 
	    qm.dispose();
        }else{
            qm.sendOk("Come back when you've collected 30 #b#t4031013#s#k");
            qm.dispose();
        }
           
}