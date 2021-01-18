/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uni.lu.networks.finalproject.udpdata.execs;

import uni.lu.networks.finalproject.udpdata.nonexecs.UDPReceiver;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Gilles Chen
 */
public class UDPReceivingClient {
    
    public static void main(String[] args) throws SocketException, UnknownHostException, FileNotFoundException, IOException{
        DatagramSocket socket = new DatagramSocket(21);
        UDPReceiver receiver = new UDPReceiver(socket);
        receiver.createFile();
    }
}
