//Quest ID: 10579
var status = -1;
function start(mode, type, selection) {
    qm.openUI(21);
    qm.sendOk("The new #bParty Search#k feature will help you search for a party, party members or find an expedition. You can find it by pressing the P key and clicking the #bsearch button#k. \r\nHave a nice day.");
    qm.forceCompleteQuest();
    qm.dispose();
}
function end(mode, type, selection) {
    qm.dispose();
}
