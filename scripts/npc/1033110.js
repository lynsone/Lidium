//Author Manu
//This script is GMS-Like (Match v223.4) (July, 2021);
var status = -1;
var songs = [
    ["RisingStar", "BgmEvent2.img/risingStar"],
    ["MoonlightShadow", "Bgm01/MoonlightShadow"],
    ["When the morning comes", "BgmJp/WhenTheMorningComes"],
    ["Flying In A Blue Dream", "Bgm06/FlyingInABlueDream"],
    ["Fantasia", "Bgm07/Fantasia"],
    ["FairyTalediffvers", "Bgm09/FairyTalediffvers"],
    ["Minar'sDream","Bgm13/Minar'sDream"],
    ["ElinForest", "Bgm15/ElinForest"],
    ["TimeTemple", "Bgm16/TimeTemple"],
    ["QueensGarden","Bgm18/QueensGarden"]
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
        var text = "A beautiful, flower-shaped Orgel manufactured in Elluel. " +
            "You can play a variety of music with this Orgel.\r\nNote: To fully appreciate the wonders " +
            "this music has to offer, you will need your SFX Sound Option enabled.\r\n\r\n";
            for (var i = 0; i < songs.length; i++){
                text += "#L" + i + "##b" + songs[i][0] + "#k#l\r\n";
        }
        cm.sendNext(text);
    } else if (status == 1) {
       
        cm.getClient().getSession().write(Packages.tools.packet.CField.musicChange(songs[selection][1]));
        cm.dispose();
    }
}