/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uni.lu.networks.finalproject.udpdata.nonexecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Gilles Chen
 */
public class UDPReceiver {

    private DatagramSocket socket;
    String save_location = "C:\\Users\\019161962A\\Desktop\\TransferReceiver";
    private final static int PACKET_SIZE = 65507; //The Size of each Packet
    private final static int INFORMATION_SIZE = 4; //The first X Bytes that will be used to transmit information(s) besides the Data.
    private final static int DATA_SIZE = PACKET_SIZE - INFORMATION_SIZE; //The Actual Size of the Data to be sent in each Packet

    public UDPReceiver(DatagramSocket socket) throws FileNotFoundException {
        System.out.println("Ready to receive data.");
        this.socket = socket;
    }
    
    public void createFile() throws IOException{
            byte[] fileNameInBytes = new byte[PACKET_SIZE];
            DatagramPacket name_packet = new DatagramPacket(fileNameInBytes, fileNameInBytes.length);
            socket.receive(name_packet);
            System.out.println("File name received.");
            byte[] name = name_packet.getData();
            String filename = new String(name, 0, name_packet.getLength());
            String location = save_location + "\\" + filename;
            File f = new File(location);
            FileOutputStream out = new FileOutputStream(f);
            
            receive(out);
    }

    private void receive(FileOutputStream out) throws IOException {
        System.out.println("Receiving packet.");
        boolean eof; //end of file boolean
        int sequenceNumber = 0; //current sequence
        int lastSequence = 0; //the last (previous) sequence
        while(true) {
            byte[] message = new byte[PACKET_SIZE];
            byte[] fileByteArray = new byte[DATA_SIZE];
            
            DatagramPacket packet = new DatagramPacket(message, message.length);
            socket.receive(packet);
            message = packet.getData();
            
            //24Bit Number Assembly
            sequenceNumber = ((message[0] & 0xff) << 16) + ((message[1] & 0xff) << 8) + (message[2] & 0xff);
            //Checking whether end of file has been reached.
            eof = (message[3] & 0xff) == 1;
            
            if(sequenceNumber == (lastSequence + 1)){
                lastSequence = sequenceNumber;
                
                System.arraycopy(message, INFORMATION_SIZE, fileByteArray, 0, message.length-INFORMATION_SIZE);
                
                out.write(fileByteArray);
                System.out.println("Received packet number: " + lastSequence);
                
                sendAcknowledgement(lastSequence);
                
            } else {
                System.out.println("Waiting for packet number: " + (lastSequence + 1) + " , but received Packet number" + sequenceNumber);
                sendAcknowledgement(lastSequence);
            }
            if(eof){
                out.close();
                System.out.println("Data Transfer closed.");
                break;
            }
        }
    }

    private void sendAcknowledgement(int lastSequence) throws UnknownHostException, IOException {
                byte[] ackPacket = new byte[3];
                //Store sequence number in the first three bytes.
                ackPacket[0] = (byte) (lastSequence >> 16);
                ackPacket[1] = (byte) (lastSequence >> 8);
                ackPacket[2] = (byte) (lastSequence); 
                
                DatagramPacket acknowledge = new DatagramPacket(ackPacket, ackPacket.length, InetAddress.getLocalHost(), 20);
                socket.send(acknowledge);
                System.out.println("Sending acknowledgement of Packet number: " + lastSequence);
    }

}
