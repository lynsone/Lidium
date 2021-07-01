//Job Advance White Knight
var status = -1;
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
    
    if (status == 0 && qm.haveItem(4031059, 1)){
        qm.sendNext("I am impressed, you surpassed the test.\r\nOnly few are talented enough.");
    }else if (status == 1){
        qm.sendNext("You have proven yourself to be worthy, I shall mold your body into a #bWhite Knight#k.");       
    }else if (status == 2){
        qm.gainItem(4031059, -1);
        qm.changeJob(121);
        qm.sendOk("You are now a #bWhite Knight#k");
        qm.forceCompleteQuest(); 
        qm.dispose();
    }else{
        qm.forceStartQuest(); 
        qm.dispose();
    }         
}