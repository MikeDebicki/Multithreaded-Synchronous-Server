public class Storage {

    private byte[] clientData;
    private byte[] serverData;
    private boolean serverDataEmpty = true;
    private boolean clientDataEmpty = true;

    public synchronized void storeClientData(byte[] inputData){ // checks for an empty spot and stores when ready
        try{
            while(clientDataEmpty == false){
                wait();
            }
            clientDataEmpty = false;
            clientData = inputData;
            notifyAll();
        } catch (Exception e){

        }
    }

    public synchronized void storeServerData(byte[] inputData){
        try{
            while(serverDataEmpty == false){
                wait();
            }
            serverDataEmpty = false;
            serverData = inputData;
            notifyAll();
        } catch (Exception e){

        }

    }

    public synchronized byte[] fetchClientData(){   // checks for data, and provides when ready
        try{
            while(clientDataEmpty == true){
                wait();
            }
            clientDataEmpty = true;
            notifyAll();
            return clientData;
        } catch (Exception e){

        }

        return null;
    }

    public synchronized byte[] fetchServerData(){
        try{
            while(serverDataEmpty == true){
                wait();
            }
            serverDataEmpty = true;
            notifyAll();
            return serverData;

        } catch (Exception e){

        }

        return null;
    }
}
