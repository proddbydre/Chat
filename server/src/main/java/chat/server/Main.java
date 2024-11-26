package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Server in ascolto sulla porta 3000");
        @SuppressWarnings("resource")
        ServerSocket sS0 = new ServerSocket(3000); //Porta dove il server aspetta richiesta
        DatiCondivisi dC = new DatiCondivisi();
        do {
            Socket s0 = sS0.accept(); //quando arriva una connessione, viene accettata e rende la nuova porta su cui avverra il vero passaggio di dati
            System.out.println("Un client si è connesso");   
            GestioneServizio gS = new GestioneServizio(s0, dC);
            gS.start();
        } while(true);
    }
}