package chat.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadClient extends Thread {
    Socket s0;
    BufferedReader in;
    DataOutputStream out;
    Boolean flag;

    public ThreadClient(Socket s0) {
        this.s0 = s0;

    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }



    @Override
    public void run() {
        try 
        {

            in = new BufferedReader(new InputStreamReader(s0.getInputStream()));
            out = new DataOutputStream(s0.getOutputStream());
            flag = true;


            while (flag && !Thread.interrupted()) {
                if (in.ready()) { // Verifica se ci sono dati disponibili per la lettura
                    String msg = in.readLine();
                    if (msg.equals("NONE")) {
                        System.out.println("Destinatario non trovato, uscire dalla chat digitando '!'");
                    }else{
                        System.out.println(msg);
                    }
                } else {
                    // Se non ci sono dati, aggiungi un breve ritardo per evitare cicli di polling ad alta intensità
                    Thread.sleep(100);
                }
            }
            
            
         } catch (IOException e) {
            if (!flag) {
                System.out.println("Thread terminato in modo sicuro.");
            } else {
            e.printStackTrace(); // Stampa il problema se si verifica in circostanze inattese
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrotto.");
        }

    }
}
