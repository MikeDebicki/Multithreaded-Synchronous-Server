public class TestClass {

    public static void main(String[] args){
        Thread client, server, intermediateHost1,intermediateHost2;
        PrintHelper printHelper;
        Storage serverStorage = new Storage();

        printHelper = new PrintHelper();

        try{
            System.out.println("Creating threads");
            client = new Thread(new Client(printHelper), "clientThread");
            System.out.println("Created client thread");

            server = new Thread(new Server(printHelper), "server");
            intermediateHost1 = new Thread(new IntermediateHost(printHelper, 23, serverStorage), "intermediateHost");
            intermediateHost2 = new Thread(new IntermediateHost(printHelper, 24, serverStorage), "intermediateHost");

            System.out.println("Created host thread");

            server.start();
            client.start();
            intermediateHost1.start();
            intermediateHost2.start();


        }catch(Exception IOException){
            System.out.println("Creating threads failed");
        }
    }

    public synchronized void printHelper(byte[] intputToPrint, String formating, String threadType){					// this function simply helps print out the responses
        String convertedMessage = new String(intputToPrint, 0, intputToPrint.length);
        System.out.println(threadType + " is " + formating + " this message " + convertedMessage);
        System.out.print(threadType + " is " + formating + " these bits ");
        for(int i = 0; i < intputToPrint.length; i += 1){
            System.out.print(Integer.toBinaryString(intputToPrint[i]) + " ");
        }
        System.out.println();
    }

}
