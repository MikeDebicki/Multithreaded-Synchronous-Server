/**
 * Client.java
 *
 * the client thread will send a message, print what it sent. Then it will wait for a reply and itll print that
 */

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client implements Runnable{

    private DatagramSocket socket = null;		// creating the socket and address so that this thread can send its message
    private InetAddress address = null;
    private PrintHelper printHelper;

    public Client(PrintHelper parentInput) throws Exception{
        socket = new DatagramSocket();
        address = InetAddress.getLocalHost();
        printHelper = parentInput;
    }


    public synchronized void run(){

        System.out.println("Client thread Running");
        int messageNumber = 0;				// i use a counter to help me terminate the thread
        while(messageNumber < 11) {//11
            try {
                String message = "text.txt";
                String modeString = "octet";
                byte zero = 0b0;					// setting a few variables to improve on sending a message
                byte one = 0b1;
                byte two = 0b10;
                byte[] data = message.getBytes();
                byte[] readRequest = {zero,one};
                byte[] writeRequest = {zero, two};
                byte[] mode = modeString.getBytes();


                /**
                 * the outputstream block below builds 10 correct requests, alternating between read and write.
                 */
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                if(messageNumber == 10){		// the 11th run of this thread will produce an incorrect format to send, resulting in a server exception
                    outputStream.write(readRequest);
                    outputStream.write(readRequest);
                    outputStream.write(data);

                }
                else if(messageNumber%2 == 0){
                    outputStream.write(readRequest);
                }
                else{
                    outputStream.write(writeRequest);
                }
                outputStream.write(data);
                outputStream.write(zero);
                outputStream.write(mode);
                outputStream.write(zero);

                byte[] buffer = outputStream.toByteArray( );
                byte[] bufferReply = new byte[256];
                outputStream.close();


                rpc_send(buffer,bufferReply);

                wait(100);


                if(messageNumber == 10) {		// this if helps terminate the client, since we wont get a response at the end from the server
                    break;
                }


                buffer = "".getBytes();

                rpc_send(buffer,bufferReply);


                messageNumber += 1;

            } catch (Exception E) {

            }
        }

    }

    private synchronized void rpc_send(byte[] buffer,byte[] bufferReply){

        try{
            printHelper.printHelper(buffer, "sending", "client");

            DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, address, 23);		// were sending our messages with port 23, and our address
            socket.send(packetToSend);

            DatagramPacket packetToReceive = new DatagramPacket(bufferReply, bufferReply.length);		// we get back a response from the server as ack.
            socket.receive(packetToReceive);

            byte[] recievedData = new byte[packetToReceive.getLength()];

            System.arraycopy(packetToReceive.getData(), packetToReceive.getOffset(), recievedData, 0, packetToReceive.getLength());

            printHelper.printHelper(recievedData, "receiving a reply with", "client");

        } catch(Exception e){

        }
    }




}
