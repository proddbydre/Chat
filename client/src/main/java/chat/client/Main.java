package chat.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException {
        System.out.println("client partito");
        Socket s0 = new Socket("localhost", 3000); //socker che dice indirizzo e porta del server a cui connetersi

        BufferedReader in = new BufferedReader(new InputStreamReader(s0.getInputStream())); //stream dati in
        DataOutputStream out = new DataOutputStream(s0.getOutputStream()); //stream dati out

        ThreadClient tc;

        Scanner input = new Scanner(System.in); //scanner input da tastiera

        String username;

        String op="", v1="", v2="";

        do
        {   
            System.out.println("Inserisci il tuo username");
            username = input.nextLine();
            out.writeBytes(username + "\n");
            
            String ris = in.readLine();
            if(ris.equals("KO"))
            {
                System.out.println("username non disponibile");
            }
            else
            {
                System.out.println("Benvenuto " + username);
                break;
            }
        }while(true);

        String azione = "";

        do
        {
            System.out.println("Benvenuto "+  username +  ", inserisci l'operazione desiderata: " + "\n" +
                                "1 => Visualizzare gli UTENTI connessi" + "\n" +
                                "2 => Visualizza chat GLOBALE\n" + 
                                "3 => Esci");

            azione = input.nextLine();

            switch (azione) 
            {
                case "1":

                    out.writeBytes("P- " + "\n");
                    String nome;
                    do {
                        nome = in.readLine();
                    } while (!nome.startsWith("USERS:"));

                    nome = nome.replace("USERS:", "");

                    System.out.println("Nomi ricevuti: " + nome);
                    String nomi[] = nome.split(";");

                    if (nomi.length == 1) 
                    {
                        System.out.println("Non sono presenti utenti");
                        break;
                    }
                    else
                    {
                        for(int i=0; i<nomi.length; i++)
                        {
                            if(!nomi[i].equals(username))
                            {
                                System.out.println(nomi[i] + "\n"); 
                            }
                        }
                    }

                    System.out.println("Scrivi il nome della persona con cui vuoi comunicare");
                    v1 = input.nextLine();
                    out.writeBytes("SP- " + v1 + "; |||\n");
                    String esito = in.readLine();
                    if (esito.equals("NONE")) {
                        System.out.println("Destinatario non trovato, ritorno al menù");
                        break;
                    }
                    out.writeBytes("VP- " + v1 + "\n");
                    String ris = in.readLine();
                    if (ris.equals("NONE")) {
                        System.out.println("No msg precedenti");
                    } else{
                        ris = ris.replace("CRON:", "");

                        String[] messaggi = ris.split("\\|\\|\\|");
    
                        System.out.println("Cronologia messaggi:");
                        for (String messaggio : messaggi) {
                            if (!messaggio.trim().isEmpty()) {
                                messaggio = messaggio.replace("\\|\\|\\|", "\n");
                                System.out.println(messaggio);
                            }
                        }
                    }
                    System.out.println("Inserisci il messaggio da scrivere");
                    System.out.println("Apro thread");
                    tc = new ThreadClient(s0, v1, Thread.currentThread().getName());
                    tc.start();
                    do
                    {
                        v2 = input.nextLine();
                        if(!v2.equals("!"))
                        {
                            op = "SP- "; 

                    
                            out.writeBytes(op + v1 + "; "+v2+"\n");
                        }
                    }while (!v2.equals("!"));
                    System.out.println("chiudo thread");
                    tc.setFlag(false);
                    tc.interrupt();

                    break;

                case "2":

                    out.writeBytes("VT- " + "\n");

                    String risp = in.readLine();
                    if (risp.equals("NONE")) {
                        System.out.println("No msg precedenti");
                    } else{
                        risp = risp.replace("CRON:", "");
                        String[] messaggi = risp.split("\\|\\|\\|");
    
                        System.out.println("Cronologia messaggi:");
                        for (String messaggio : messaggi) {
                            if (!messaggio.trim().isEmpty()) {
                                messaggio = messaggio.replace("\\|\\|\\|", "\n");
                                System.out.println(messaggio);
                            }
                        }
                    }

                    System.out.println("Inserisci il messaggio da scrivere");
                    System.out.println("Apro thread");
                    tc = new ThreadClient(s0, "^", Thread.currentThread().getName());
                    tc.start();
                    do
                    {
                        v2 = input.nextLine();
                        if(!v2.equals("!"))
                        {
                            op = "ST- "; 

                            
                            out.writeBytes(op + v2 + "\n");
                        }
                    }while (!v2.equals("!"));
                    System.out.println("chiudo thread");
                    tc.setFlag(false);
                    tc.interrupt();

                break;

                case "3":
                    out.writeBytes("EXIT\n");
                    in.readLine();
                    System.out.println("Arrivederci");
                    azione = "0";
                    in.close();
                    out.close();
                    s0.close();
                break;
            }


        }while(!azione.equals("0"));

    
        input.close();
        s0.close();
        System.out.println("Client terminato");
    }
}