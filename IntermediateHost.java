/**
 * IntermediateHost.java
 *
 * the intermediatehost thread will just act as a middle man in the connection between the client and the server. it doesn't do too much
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class IntermediateHost implements Runnable {

    private DatagramSocket socket = null;
    private DatagramSocket socketToSendReceive = null;
    private InetAddress address = null;
    private PrintHelper printHelper;
    private Storage serverStorage;
    private int threadType;


    public IntermediateHost(PrintHelper parentInput,int socketPort, Storage serverStorageLocation) throws Exception{
        System.out.println(socketPort);
        socket = new DatagramSocket(socketPort);
        threadType = socketPort;
        socketToSendReceive = new DatagramSocket();
        address = InetAddress.getLocalHost();
        printHelper = parentInput;
        serverStorage = serverStorageLocation;

    }

    public synchronized void run(){
        int counter = 0;					// counter used to help terminate the thread, since i don't want it to run forever right now.
        System.out.println("Intermediate Host Running");
        while(true) {
            /**
             * the code below just accepts messages from the client, sends them to the server, accepts replies from the server, and sends them back to the client
             */
            try {
                byte[] buffer = new byte[256];
                byte[] bufferReply = "".getBytes();

                DatagramPacket packetToReceive = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetToReceive);							// received from client or server

                byte[] recievedData = new byte[packetToReceive.getLength()];

                System.arraycopy(packetToReceive.getData(), packetToReceive.getOffset(), recievedData, 0, packetToReceive.getLength());

                buffer = recievedData;

                printHelper.printHelper(recievedData, "receiving", "Intermediate Host");



                if(packetToReceive.getLength() == 0 && threadType == 23){
                    bufferReply = serverStorage.fetchServerData();
                }else if(packetToReceive.getLength() == 0 && threadType == 24){
                    bufferReply = serverStorage.fetchClientData();
                }else if(threadType == 23){

                    serverStorage.storeClientData(buffer);

                }else if(threadType == 24){
                    serverStorage.storeServerData(buffer);
                }



                String convertedMessage = new String(packetToReceive.getData(), 0, packetToReceive.getLength());
                buffer = convertedMessage.getBytes();



                int port = packetToReceive.getPort();
                InetAddress backAddress = packetToReceive.getAddress();

                DatagramPacket packetToSend = new DatagramPacket(bufferReply, bufferReply.length, backAddress, port); // just sending an ack. or sending data back
                socketToSendReceive.send(packetToSend);

                printHelper.printHelper(bufferReply, "sending back", "Intermediate Host");


                if(counter == 20) {			// this if statement is placed here since i am expecting no return on my 11th run on this thread. i want this thread to end
                    break;
                }

                counter += 1;

            } catch (Exception E) {

            }
        }
    }
}
