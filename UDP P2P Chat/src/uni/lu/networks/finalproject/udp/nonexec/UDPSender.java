/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uni.lu.networks.finalproject.udp.nonexec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Runnable for sending Packets.
 */
public class UDPSender implements Runnable {

    private DatagramSocket socket;
    private InetAddress address;
    private BufferedReader stdin;
    private static final int SERVICE_PORT = 20; //The Register we send our initial Registering Packet to.
    private HashMap<Integer, InetAddress> clients; // store unique clients
    private UDPReceiver receiver;
    private String name;
    private static final int CLIENTS_COUNT = 2; //The amount of clients we will send messages to.

    public UDPSender(DatagramSocket socket) throws UnknownHostException, SocketException {
        this.socket = socket;
        address = InetAddress.getByName("localhost");
        receiver = new UDPReceiver(socket,CLIENTS_COUNT);
        Thread receivingThread = new Thread(receiver);
        receivingThread.start();
        clients = receiver.getClients();
        stdin = new BufferedReader(new InputStreamReader(System.in));
        //Registering yourself upon creation
        try {
            System.out.println("Please enter a Name: ");
            name = stdin.readLine();
            send(name + " registering to Network..");
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void send(String message) throws IOException {
        //This first if clause is solely used to send the Registration to the Register.
        if (clients.size() < CLIENTS_COUNT) {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVICE_PORT);
            socket.send(packet);
        } else { // Clients are now registered, so now you are able to send messages to each other
            if (receiver.getListenMode()) { //Are we in Listenmode?
                System.out.println("SYSTEM: You can't send messages. You are in Listen-Mode and only able to receive messages");
            } else { //Not Listenmode
                System.out.println("You: " + message);
                byte[] buffer = (name + ": " + message).getBytes();
                for (Map.Entry<Integer, InetAddress> set : clients.entrySet()) { //Sent packet to each Client
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, set.getValue(), set.getKey());
                    socket.send(packet);
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Enter the message to be sent
                String message = stdin.readLine();
                //Send the message
                send(message);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

}
