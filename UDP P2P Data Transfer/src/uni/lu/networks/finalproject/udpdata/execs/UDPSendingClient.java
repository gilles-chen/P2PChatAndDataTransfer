/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uni.lu.networks.finalproject.udpdata.execs;

import java.io.IOException;
import uni.lu.networks.finalproject.udpdata.nonexecs.UDPSender;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Gilles Chen
 */
public class UDPSendingClient {
    
    public static void main(String[] args) throws SocketException, IOException, InterruptedException{
        DatagramSocket socket = new DatagramSocket(20);
        UDPSender sender = new UDPSender(socket);
        sender.sendFile();
    }
    
}
