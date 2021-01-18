/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uni.lu.networks.finalproject.udpdata.nonexecs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 *
 * @author Gilles Chen
 */
public class UDPSender {

    private DatagramSocket socket;
    private InetAddress address;
    public static final int SERVICE_PORT = 21; //To which port the Data should be sent
    private File file;
    private FileInputStream fis = null;
    private int sequenceNumber = 0;
    private int ackSequence = 0;
    private long bytesSent = 0;
    //PLEASE PUT THE LOCATION TO THE FILE YOU WANT TO SEND HERE! 
    private final String FILE_LOCATION = "C:\\Users\\019161962A\\Desktop\\video.mp4"; 
    //THESE VARIABLES MUST HAVE THE SAME VALUE AS IN RECEIVER!
    private final static int PACKET_SIZE = 65507; //The Size of each Packet 
    private final static int INFORMATION_SIZE = 4; //The first X Bytes that will be used to transmit information(s) besides the Data.
    private final static int DATA_SIZE = PACKET_SIZE - INFORMATION_SIZE; //The Actual Size of the Data to be sent in each Packet

    public UDPSender(DatagramSocket socket) throws UnknownHostException, IOException {
        this.socket = socket;
        address = InetAddress.getByName("localhost");
        file = new File(FILE_LOCATION);
    }

    public void sendFile() throws IOException, InterruptedException {
        //Sending the NAME of the File to be sent.
        String fileName = file.getName();
        byte[] name = fileName.getBytes();
        DatagramPacket name_packet = new DatagramPacket(name, name.length, address, SERVICE_PORT);
        socket.send(name_packet);
        try {
            long length = file.length(); //length is used for last chunk size determination.
            fis = new FileInputStream(file);
            byte[] chunk = new byte[DATA_SIZE];
            int check = 0; //used for fileInputStream end of file read.
            int read = 0;
            while ((check = fis.read(chunk)) > 0) {
                send(length, chunk);
                if (check != -1) {
                    read = check;
                }
            }
            //If we are at the end of file, make sure to send the LAST packet!
            if (check == -1) {
                byte[] lastChunk = new byte[read];
                System.arraycopy(chunk, 0, lastChunk, 0, read);
                send(length, lastChunk);
            }
        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        }
        socket.close();
    }

    private void send(long fileLength, byte[] chunk) throws IOException, InterruptedException {
        System.out.println("Sending file");
        boolean eof; // To see if we got to the end of the file
        sequenceNumber++;
        boolean received; //is packet received? 

        byte[] message = new byte[PACKET_SIZE];
        //USE FIRST 3 BYTES TO STORE SEQUENCE NUMBER. UP TO (256 x 256 x 256) - 1 packages can be sent 
        //If you want to expand this to e.g. (256 x 256 x 256 x 256) - 1 packages, then use a 4th byte and shift by >> 24 bits.
        //Don't forget to change INFORMATION_SIZE accordindgly (in this case to 5) and change the eof accordingly aswell.
        //Use Long for sequenceNumber once you want to send more than 32 Bits - 1 of packages
        message[0] = (byte) (sequenceNumber >> 16);
        message[1] = (byte) (sequenceNumber >> 8);
        message[2] = (byte) (sequenceNumber);

        //Set EOF bit
        if ((bytesSent + DATA_SIZE) >= fileLength) {
            eof = true;
            message[3] = (byte) (1);
        } else {
            eof = false;
            bytesSent+= DATA_SIZE;
            message[3] = (byte) (0);
        }

        //Normal arraycopy if not end of file, otherwise use Length of the chunk parameter, as our InputStream will send a chunk
        //of exactly the remaining size when eof!
        if (!eof) {
            System.arraycopy(chunk, 0, message, INFORMATION_SIZE, DATA_SIZE);
        } else {
            byte[] lastMsg = new byte[chunk.length + INFORMATION_SIZE];
            lastMsg[0] = message[0];
            lastMsg[1] = message[1];
            lastMsg[2] = message[2];
            lastMsg[3] = message[3];
            System.arraycopy(chunk, 0, lastMsg, INFORMATION_SIZE, chunk.length);
            DatagramPacket lastPacket = new DatagramPacket(lastMsg, lastMsg.length, address, SERVICE_PORT); // The data to be sent
            socket.send(lastPacket); // Sending the last packet
            System.out.println("Last Packet sent");
            return;
        }

        DatagramPacket packet = new DatagramPacket(message, message.length, address, SERVICE_PORT);
        socket.send(packet);
        System.out.println("Packet Number: " + sequenceNumber + " sent. Currently at: " + bytesSent + "bytes");

        //Loop until acknowledgement is received before proceeding with further packets.
        while (true) {
            byte[] ack = new byte[3];
            DatagramPacket ack_packet = new DatagramPacket(ack, ack.length);

            try {
                socket.setSoTimeout(50);
                socket.receive(ack_packet);
                ackSequence = ((ack[0] & 0xff) << 16) + ((ack[1] & 0xff) << 8) + (ack[2] & 0xff); //get sequence number
                received = true;
            } catch (SocketTimeoutException ex) {
                System.out.println(ex);
                received = false;
            }

            if ((ackSequence == sequenceNumber) && (received)) {
                System.out.println("Packet was received!");
                break;
            } else {
                socket.send(packet);
                System.out.println("Packet must be send again. Packet Number: " + sequenceNumber);
            }

        }

    }

}
