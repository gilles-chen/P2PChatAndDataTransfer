package uni.lu.networks.finalproject.udp.execs;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import uni.lu.networks.finalproject.udp.nonexec.UDPSender;

/**
 * Class to create Clients. Start this AFTER the Register.
 */
public class UDPClient {
    
    public static void main(String[] args) throws SocketException, UnknownHostException{
        DatagramSocket socket = new DatagramSocket();
        UDPSender sender = new UDPSender(socket);
        Thread sendingThread = new Thread(sender);
        sendingThread.start();
    }
    
}
