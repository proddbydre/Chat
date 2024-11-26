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
        Socket s0 = new Socket("localhost", 3000); //socket che dice indirizzo e porta del server a cui connetersi

        BufferedReader in = new BufferedReader(new InputStreamReader(s0.getInputStream())); //stream dati in
        DataOutputStream out = new DataOutputStream(s0.getOutputStream()); //stream dati out

        ThreadClient tc; //il thread che ascolta i messaggi in entrata

        Scanner input = new Scanner(System.in); //scanner input da tastiera

        String username;

        String op="", v1="", v2="";

        //op=operazione, v1=destinatario v2=messaggio in sé

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
                    } while (!nome.startsWith("USERS:")); //si controlla che il messaggio arrivato contenga la lista di utenti

                    nome = nome.replace("USERS:", ""); //si toglie 'USERS:'

                    String nomi[] = nome.split(";"); //divido i nomi

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
                    out.writeBytes("SP- " + v1 + "; |||\n"); // mando un messaggio di test per capire se lo user esiste in base alla risposta del server
                    String esito = in.readLine();
                    if (esito.equals("NONE")) {
                        System.out.println("Destinatario non trovato, ritorno al menù");
                        break;
                    }
                    out.writeBytes("VP- " + v1 + "\n"); //invio la richiesta di invio della cronologia
                    String ris;
                    do {
                        ris = in.readLine();
                    } while (!ris.startsWith("CRON:") && (!ris.equals("NONE")));
                    if (ris.equals("NONE")) {
                        System.out.println("No msg precedenti");
                    } else{
                        ris = ris.replace("CRON:", ""); //elimino il prefisso 'CRON:'

                        String[] messaggi = ris.split("-++-"); //divido i messaggi
    
                        System.out.println("Cronologia messaggi:");
                        for (String messaggio : messaggi) {
                            if (!messaggio.trim().isEmpty()) {
                                messaggio = messaggio.replace("-++-", "\n"); //tolgo l'elemento di divisione dai messaggi
                                System.out.println(messaggio);
                            }
                        }
                    }
                    tc = new ThreadClient(s0, v1, Thread.currentThread().getName()); //creo il thread passando socket, il corrspondente ed il mio nome
                    tc.start();
                    do
                    {
                        do {
                            v2 = input.nextLine();
                        } while (v2.trim().isEmpty()); //controllo che il messaggi da inviare non sia vuoto o con solo spazi

                        if(!v2.equals("!")) //se il messaggio è diverso da '!', lo invio
                        {
                            op = "SP- "; 

                    
                            out.writeBytes(op + v1 + "; "+v2+"\n");
                        }
                    }while (!v2.equals("!"));
                    tc.setFlag(false); //imposto flag del thread a 0 e lo interrompo
                    tc.interrupt();

                    break;

                case "2":

                    out.writeBytes("VT- " + "\n"); // richiedo la cronologia dei messaggi
                    String risp;
                    do {
                        risp = in.readLine();
                    } while (!risp.startsWith("CRON:") && (!risp.equals("NONE")));
                    if (risp.equals("NONE")) {
                        System.out.println("No msg precedenti");
                    } else{
                        risp = risp.replace("CRON:", ""); //tolgo il prefisso 'CRON:'
                        String[] messaggi = risp.split("-++-");
    
                        System.out.println("Cronologia messaggi:");
                        for (String messaggio : messaggi) {
                            if (!messaggio.trim().isEmpty()) {
                                messaggio = messaggio.replace("-++-", "\n"); // divido i messaggi
                                System.out.println(messaggio);
                            }
                        }
                    }

                    tc = new ThreadClient(s0, "^", Thread.currentThread().getName()); //creo il thread passando la socket, il carattere che indica che tutti
                    tc.start();                                                                   //sono i corrispondenti ed il mio nome
                    do
                    {
                        do {
                            v2 = input.nextLine();
                            
                        } while (v2.trim().isEmpty()); // controllo che il messaggio inserito non sia vuoto o con solo spazi
                        if(!v2.equals("!"))
                        {
                            op = "ST- "; 

                            
                            out.writeBytes(op + v2 + "\n");
                        }
                    }while (!v2.equals("!"));
                    tc.setFlag(false); //imposto la flag del thread a false e lo interrompo
                    tc.interrupt();

                break;

                case "3":
                    out.writeBytes("EXIT\n"); // comunico al server di voler interrompere la connesione e attendo risposta
                    in.readLine();
                    System.out.println("Arrivederci");
                    azione = "0";
                    in.close(); //chiudo tutto
                    out.close();
                    s0.close();
                break;
            }


        }while(!azione.equals("0"));

    
        input.close();
        System.out.println("Client terminato");
    }
}