//Author: Manu
//This script is GMS-Like (Match v223.4) (July, 2021);

var status = -1;
var price = 2000;
var towns = [
    ["Ellinia", 101000000],
    ["Henesys", 100000000],
    ["Perion", 102000000],
    ["Kerning City",103000000]
];
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (mode == 0 && status < 0) {
            cm.dispose();
        } else {
            status--;
        }
    }
    if (status == 0) {
        var text = "(If you don't have any business in Elluel, you can take this Meso-powered Mysterious Portal" +
            " to other towns for " + price + " Mesos a trip. Where to?)\r\n";
        for (var i = 0; i < towns.length; i++){
            text += "#L" + i + "##b" + towns[i][0] + "#k#l\r\n";
        }
        cm.sendNext(text);
    } else if(status==1){
        if (cm.getMeso() < price) {
            cm.sendNext("(You don't have enough Mesos...)");
            cm.dispose();
        } else {
            var town = towns[selection][1];
            cm.gainMeso(-price);
            cm.warp(town, 0);
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}