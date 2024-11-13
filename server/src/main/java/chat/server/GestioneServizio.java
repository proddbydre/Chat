package chat.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

public class GestioneServizio extends Thread{
    
    Socket s0;
    BufferedReader in;
    DataOutputStream out;

    @Override
    public void run(){
        try {
            System.out.println("Client connesso alla porta " + s0.getPort());

            String nome;
            Boolean flag;

            do {
                flag = true;
                nome = in.readLine();
                if (condition) {
                    
                }
                
            } while (condition);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
