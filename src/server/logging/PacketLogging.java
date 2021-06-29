/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logging;

public class PacketLogging {
    
    public static boolean Log_Packet_Sends = false;
    public static boolean Log_Packet_Receives = false;
    public static boolean Log_Packet_Unhandled_Receives = false;
    
    public static void enableSendPacketLogging() {       
        Log_Packet_Sends = true;
    }
    
    public static void disableSendPacketLogging() {
        Log_Packet_Sends  = false;
    }
    
    public static void enableReceivePacketLogging() {        
        Log_Packet_Receives = true;
    }
    
    public static void disableReceivePacketLogging() {
        Log_Packet_Receives  = false;
    }
    
    public static void enableUnhandledReceiveLogging() {        
        Log_Packet_Unhandled_Receives = true;
    }
    
    public static void disableUnhandledReceiveLogging() {
        Log_Packet_Unhandled_Receives  = false;
    }
    
    public static void enableBothDirectionsPacketLogging() {        
        Log_Packet_Sends = true;
        Log_Packet_Receives  = true;
    }
    
    public static void disableBothDirectionsPacketLogging() {
        Log_Packet_Sends = false;
        Log_Packet_Receives  = false;
    }
}