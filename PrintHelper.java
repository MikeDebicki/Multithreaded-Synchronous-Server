
public class PrintHelper {
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
