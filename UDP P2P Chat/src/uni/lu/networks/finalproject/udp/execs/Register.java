package uni.lu.networks.finalproject.udp.execs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * START THIS CLASS FIRST OR YOUR CLIENTS WONT BE REGISTERED
 * This Class registers Clients upon creation, holds onto them in an ARrayList, and sends them to all Clients
 * once the correct number of Clients has been reached.
 */
public class Register extends Thread {

    public static final int SERVICE_PORT = 20;
    private DatagramSocket socket;
    private ArrayList<InetSocketAddress> clients;
    private static final int CLIENTS_TO_BE_REGISTERED_COUNT = 3; //Three and only Three Clients can be registered. Change this to expand.
    private int count;

    public Register() throws SocketException {
        socket = new DatagramSocket(SERVICE_PORT);
        clients = new ArrayList();
        count = 0;
    }

    public static void main(String[] args) throws SocketException {
        Register server = new Register();
        server.start();
    }

    public void run() {
        byte[] buffer = new byte[512];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                //Creating a client with Information from the Packet he sent.
                InetSocketAddress client = new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort());

                //Adding unique Clients to Arraylist and increasing Count.
                if(!clients.contains(client)){ 
                    System.out.println("Received registration: " + message);
                    clients.add(client);
                    count++;
                }
                
                if (count == CLIENTS_TO_BE_REGISTERED_COUNT) {
                    for (int i = 0; i < clients.size(); i++) {
                        byte[] socketAddress = (clients.get(i).getAddress().getHostAddress() + ";" + clients.get(i).getPort()).getBytes();
                        for (int j = 0; j < clients.size(); j++) {
                            if (j != i) { //don't need to know your own socketAddress..
                                packet = new DatagramPacket(socketAddress, socketAddress.length, clients.get(j).getAddress(), clients.get(j).getPort());
                                //Sending message in form of: ADDRESS;Port e.g. 127.0.0.1;52324
                                socket.send(packet);
                            }
                        }
                    }
                    //No more Clients need to be registered, so stop.
                    break;
                }

            } catch (IOException ex) {
                System.out.println(ex);
            }

        }
    }

}
