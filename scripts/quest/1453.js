//Job advance Way of the Arch Mage FP / Arch Mage IL / Bishop
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
    
    if (status == 0 && qm.haveItem(4031511, 1) && qm.haveItem(4031512, 1)){
        if (qm.getJob() == 211){
            qm.gainItem(4031511, -1);
            qm.gainItem(4031512, -1)
            qm.changeJob(212);
        }else if (qm.getJob() == 221){  
            qm.gainItem(4031511, -1);
            qm.gainItem(4031512, -1)
            qm.changeJob(222);           
        }else if (qm.getJob() == 231){
            qm.gainItem(4031511, -1);
            qm.gainItem(4031512, -1);
            qm.changeJob(232);
        }
        qm.sendNext("I will take these tokens of heroism from you, and grant you your 4th job skills."); 
        qm.forceCompleteQuest(); 
        qm.dispose();
    }else{
        qm.forceStartQuest(); 
        qm.dispose();
    }         
}