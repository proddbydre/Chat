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
        Socket s0 = new Socket("172.21.231.92", 3000); //socker che dice indirizzo e porta del server a cui connetersi

        BufferedReader in = new BufferedReader(new InputStreamReader(s0.getInputStream())); //stream dati in
        DataOutputStream out = new DataOutputStream(s0.getOutputStream()); //stream dati out

        ThreadClient tc = new ThreadClient(s0);
        tc.start();

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
                                "2 => Visualizzare i GRUPPI presenti" + "\n" +
                                "3 => Visualizza chat GLOBALE");

            azione = input.nextLine();

            switch (azione) 
            {
                case "1":

                    out.writeBytes("P- " + "\n");
                    String nome = in.readLine();
                    String[] nomi = nome.split(";");

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
                                System.out.println(nomi[i]); 
                            }
                        }
                    }

                    System.out.println("Scrivi il nome della persona con cui vuoi comunicare");
                    v1 = input.nextLine();
                    System.out.println("Inserisci il messaggio da scrivere");
                    do
                    {
                        v2 = input.nextLine();
                        if(!v2.equals("!"))
                        {
                            op = "SP- "; 

                    
                            out.writeBytes(op + v1 + "; "+v2+"\n");
                        String r = in.readLine();


                        if(r.equals("KO"))
                        {
                            System.out.println("ci sono stati dei problemi nello scrivere il messaggio");
                        }
                        else if(r.equals("NONE"))
                        {
                            System.out.println("non è stato trovato l'utente desiderato");
                        }
                        else
                        {
                            System.out.println("daje");
                        
                        }
                        }
                    }while (!v2.equals("!"));

                    break;

                case "2":

                    out.writeBytes("G-" + "\n");

                break;

                case "3":

                    out.writeBytes("VT-" + "\n");

                break;
            }


        }while(!azione.equals("0"));

    
        input.close();
        s0.close();
        System.out.println("Client terminato");
    }
}