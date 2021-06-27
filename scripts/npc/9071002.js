//Mary Monster Park ticket exchanger
var status;

function start(){
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection){
    if (mode != 1) {
        cm.dispose();
        return;
    }else{
        status++;
    }

    if (status == 0){
        cm.sendSimple("Hello! If you want to enjoy the Monster Park then you came to the right person! So what can i do for you?\r\n" +
                      "#L0##bExchange Zebra Stripe Ticket Piece#k#l\r\n" +
                      "#L1##bExchange Leopard Stripe Ticket Piece#k#l\r\n" +
                      "#L2##bExchange Tiger Stripe Ticket Piece#k#l");
    }else if (status == 1) {
        if(selection == 0){
            cm.sendGetNumber("How many Zebra Strip Tickets would you like to exchange ?\r\n\r\n Your currently have #b" + cm.getPlayer().getItemQuantity(4001513, false) + " #kZebra Stripe Ticket Pieces.", 1, 1, 1000);          
        }else if(selection == 1){
            status = 2;
            cm.sendGetNumber("How many Leopard Strip Tickets would you like to exchange ?\r\n\r\n Your currently have #b" + cm.getPlayer().getItemQuantity(4001515, false) + " #kLeopard Stripe Ticket Pieces.", 1, 1, 1000);
        }else if(selection == 2){
            status = 3;
            cm.sendGetNumber("How many Tiger Strip Tickets would you like to exchange ?\r\n\r\n Your currently have #b" + cm.getPlayer().getItemQuantity(4001522, false) + " #kTiger Stripe Ticket Pieces.", 1, 1, 1000);
        }
    }else if (status == 2){
        if(cm.haveItem(4001513, 10 * selection)){
            if(cm.canHold(4001514, selection)) {
                cm.gainItem(4001513, -(10 * selection));
                cm.gainItem(4001514, selection);
                cm.sendOk("You have recieved #b" + selection + "#k Zebra Stripe Ticket(s).");
                cm.dispose();
            }else{
                cm.sendOk("Please make sure you have enoughe room to hold #b" + selection + " Zebra Stripe Ticket(s).");
                cm.dispose();
            }            
        }else{
            cm.sendOk("You dont have enoughe Zebra Stripe Ticket Pieces.");
            cm.dispose();
        }
    }else if (status == 3){
        if(cm.haveItem(4001515, 10 * selection)){
            if(cm.canHold(4001516, selection)) {
                cm.gainItem(4001515, -(10 * selection));
                cm.gainItem(4001516, selection);
                cm.sendOk("You have recieved #b" + selection + "#k Leopard Stripe Ticket(s).");
                cm.dispose();
            }else{
                cm.sendOk("Please make sure you have enoughe room to hold #b" + selection + " Leopard Stripe Ticket(s).");
                cm.dispose();
            }            
        }else{
            cm.sendOk("You dont have enoughe Leopard Stripe Ticket Pieces.");
            cm.dispose();
        }
    }else if (status == 4){
        if(cm.haveItem(4001521, 10 * selection)){
            if(cm.canHold(4001522, selection)) {
                cm.gainItem(4001521, -(10 * selection));
                cm.gainItem(4001522, selection);
                cm.sendOk("You have recieved #b" + selection + "#k Tiger Stripe Ticket(s).");
                cm.dispose();
            }else{
                cm.sendOk("Please make sure you have enoughe room to hold #b" + selection + " Tiger Stripe Ticket(s).");
                cm.dispose();
            }            
        }else{
            cm.sendOk("You dont have enoughe Tiger Stripe Ticket Pieces.");
            cm.dispose();
        }
    }
} 