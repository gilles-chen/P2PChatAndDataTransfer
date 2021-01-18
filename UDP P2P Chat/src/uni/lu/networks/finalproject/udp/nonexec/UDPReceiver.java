package uni.lu.networks.finalproject.udp.nonexec;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
/**
 * Runnable for receiving packets.
 */
public class UDPReceiver implements Runnable {

    private DatagramSocket socket;
    private byte[] buffer;
    private boolean listen;
    private HashMap<Integer, InetAddress> clients; //store unique clients
    int maxClients;

    public UDPReceiver(DatagramSocket socket, int maxClients) throws SocketException {
        this.socket = socket;
        buffer = new byte[512];
        clients = new HashMap();
        listen = false;
        this.maxClients = maxClients;
    }

    private void receive() throws IOException {
        if (clients.size() < maxClients) { //Receive information about other clients
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String client = new String(packet.getData(), 0, packet.getLength());
            String[] clientData = client.split(";"); //Split Message of format "IP;Port" 
            clients.put(Integer.valueOf(clientData[1]), InetAddress.getByName(clientData[0])); //Put as Key value Pair <Port, Address>
        } else { // Clients are now registered
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println(message);
            if (message.contains("STOP")) {
                listen = true;
                System.out.println("Listen-Mode activated.");
            }
        }
    }

    public boolean getListenMode() {
        return listen;
    }

    public HashMap<Integer, InetAddress> getClients() {
        return clients;
    }

    @Override
    public void run() {
        while (true) {
            try {
                receive();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

}
