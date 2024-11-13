package chat.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadClient extends Thread {
    Socket s0;
    BufferedReader in;
    DataOutputStream out;

    public ThreadClient(Socket s0) {
        this.s0 = s0;

    }

    @Override
    public void run() {
        try 
        {

            in = new BufferedReader(new InputStreamReader(s0.getInputStream()));
            out = new DataOutputStream(s0.getOutputStream());

            do 
            {
                System.out.println(in.readLine());
            } while (true);
            
        } catch (Exception e) {
            
        }

    }
}
