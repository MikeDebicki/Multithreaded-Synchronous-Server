/**
 * Server.java
 *
 * the server thread will receive requests and parse the input in order to figure out what the request is
 * and if the request is valid
 */

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server implements Runnable{

    private DatagramSocket socket = null;
    private InetAddress address = null;
    private int port;
    private PrintHelper printHelper;

    public Server(PrintHelper parentInput) throws Exception{
        socket = new DatagramSocket();
        address = InetAddress.getLocalHost();
        printHelper = parentInput;

    }


    public synchronized void run(){

        int counter = 0;


        System.out.println("Server thread Running");
        while(true) {
            try {
                byte[] buffer = "".getBytes();
                byte[] bufferReply = new byte[256];

                rpc_send2(buffer, bufferReply);


                if(counter == 10) {		// this if helps terminate the thread, since we wont get a response at the end from the server
                    break;
                }

                counter += 1;
            } catch (Exception E) {
                System.out.println("Exception, The server thread has shut down");
                break;
            }
        }
    }

    private synchronized void rpc_send2(byte[] buffer,byte[] bufferReply){

        try{
            printHelper.printHelper(buffer, "sending", "server");

            DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, address, 24);		// were sending our messages with port 24, and our address
            socket.send(packetToSend);

            DatagramPacket packetToReceive = new DatagramPacket(bufferReply, bufferReply.length);		// we get back a response from the server as ack.
            socket.receive(packetToReceive);

            address = packetToReceive.getAddress();					// i remember the port and address to send back to the host
            port = packetToReceive.getPort();

            byte[] recievedData = new byte[packetToReceive.getLength()];

            System.arraycopy(packetToReceive.getData(), packetToReceive.getOffset(), recievedData, 0, packetToReceive.getLength());

            printHelper.printHelper(recievedData, "receiving a reply with", "server");

            String convertedMessage = new String(packetToReceive.getData(), 0, packetToReceive.getLength());

            buildReply(convertedMessage.getBytes());

        } catch(Exception e){

        }
    }

    private synchronized void buildReply(byte[] buffer){
        try{
            String requestBits = "";
            boolean firstZero = false;		// i have some flags and strings to help me with parsing and analyzing the data coming in
            boolean secondZero = false;
            String text1 = "";
            String text2 = "";

            if(buffer[0] != 0b0){				// i make sure my data starts with a 0 bit
                throw new Exception("E");
            }
            if(buffer[1] == 0b1){				// i check the next byte to see if it is a 1 bit
                requestBits = "0 1";
            }else if(buffer[1] == 0b10){		// else i check if the second byte is a 2 represented in bits
                requestBits = "0 2";
            }else{
                throw new Exception("E");		// if my message doesnt start with 0 1 or 0 2 i throw an exception
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( ); 	// i start a stream to build the text i could one day be using

            for(int i = 2; i < buffer.length; i += 1){			// i start my loop at 2 since i have checked [0] and [1] for the format part
                if(secondZero == true){							// this if statement will only run if i have reached a second 0 bit, but the message isn't over. This will throw E
                    throw new Exception("E");
                }
                if(buffer[i] == 0b0 && firstZero == false){		// if i reach my first 0 bit i save text1 as everything before the bit
                    text1 = outputStream.toString();
                    outputStream.reset();						// reset the stream for the rest of the message
                    firstZero = true;
                }else if(buffer[i] == 0b0 && secondZero == false){	// if i reach my second 0 bit i save text2 as everything before the bit
                    text2 = outputStream.toString();
                    outputStream.reset();
                    secondZero = true;
                }else if(buffer[i] == 0b0 && secondZero == true){	// this will run if i have too many 0 bits in my message. This will throw E
                    throw new Exception("E");
                }else{
                    outputStream.write(buffer[i]);				// this else statement is all other info in the message. I want to use this to write my texts
                }
            }
            outputStream.close();
            //System.out.print(requestBits);		// these prints i have commented out. but this will just show the end result of my string parsing
            //System.out.print(" " + text1);
            //System.out.print(" 0 ");
            //System.out.print(text2);
            //System.out.println(" 0");
            firstZero = false;
            secondZero = false;

            /**
             * the two if statements below will build a return message based on the second bit of the data that was received
             */

            if(buffer[1] == 0b10){						// write requested
                DatagramSocket socketToSend = new DatagramSocket();

                outputStream = new ByteArrayOutputStream( );
                outputStream.write(0b0);
                outputStream.write(0b100);
                outputStream.write(0b0);
                outputStream.write(0b0);

                buffer = outputStream.toByteArray( );
                outputStream.close();

                DatagramPacket packetToSend = new DatagramPacket(buffer, 4, address, 24);
                socketToSend.send(packetToSend);

                printHelper.printHelper(buffer, "sending", "server");

                DatagramPacket packetToReceive = new DatagramPacket(buffer, buffer.length);		// we get back a response from the server as ack.
                socketToSend.receive(packetToReceive);

                printHelper.printHelper(buffer, "receiving a reply with", "server");






                socketToSend.close();
            } else if(buffer[1] == 0b1){				// read requested
                DatagramSocket socketToSend = new DatagramSocket();

                outputStream = new ByteArrayOutputStream( );
                outputStream.write(0b0);
                outputStream.write(0b11);
                outputStream.write(0b0);
                outputStream.write(0b1);

                buffer = outputStream.toByteArray( );
                outputStream.close();

                DatagramPacket packetToSend = new DatagramPacket(buffer, 4, address, 24);
                socketToSend.send(packetToSend);

                printHelper.printHelper(buffer, "sendicwcwecwecwcng", "server");

                DatagramPacket packetToReceive = new DatagramPacket(buffer, buffer.length);		// we get back a response from the server as ack.
                socketToSend.receive(packetToReceive);




                socketToSend.close();
            }
        } catch (Exception e){

        }
    }



}
