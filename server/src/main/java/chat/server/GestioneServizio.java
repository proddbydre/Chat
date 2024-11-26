package chat.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GestioneServizio extends Thread{
    
    Socket s0;
    BufferedReader in;
    DataOutputStream out;
    DatiCondivisi dC; //dati a cui accedono tutti i thread

    


    public GestioneServizio(Socket s0, DatiCondivisi dC) {
        this.s0 = s0;
        this.dC = dC;
    }

    @Override
    public void run(){
        try {
            System.out.println("Client connesso alla porta " + s0.getPort());

            String nome;
            Boolean flag;

            in = new BufferedReader(new InputStreamReader(s0.getInputStream()));
            out = new DataOutputStream(s0.getOutputStream());

            do {
                flag = true;
                nome = in.readLine();
                if (dC.getUtenti().contains(nome) ||(nome.trim().isEmpty())) { //se il nome è gia presente o nonn è regolare invio 'KO'
                    flag = false;
                    out.writeBytes("KO\n");
                }
            } while (!flag);
            out.writeBytes("OK\n");
            this.setName(nome);
            dC.getThreads().add(this);
            dC.getUtenti().add(nome);
            System.out.println("Nome aggiunto: " + this.getName());

            String msg;
            do {
                msg = in.readLine();
                String op; //operazione
                String cont; //contenuto
                if (!msg.equals("EXIT")) { //se il messaggio non è exit, lo si analizza più a fondo
                    System.out.println("Messaggio: " + msg);
    
                    op = msg.split("-")[0];
                    System.out.println("op: " + op);
    
                    cont = msg.split("-")[1].trim();
                    System.out.println("cont: " + cont);
                    
                } else {
                    op = msg;
                    cont = "";
                }

                String lista; //lista nomi utenti
                String altroUtente; //nome corrispondende del caso

                switch (op) {
                    case "P":

                        lista = "";
                        System.out.println("raccolgo nomi");
                        for (int i = 0; i < dC.getThreads().size(); i++) {
                                lista += dC.getThreads().get(i).getName() + ";";
                        }

                        System.out.println("invio nomi");

                        out.writeBytes("USERS:" + lista + "\n");
                        
                        break;

                    case "SP":
                        
                        String vals[] = cont.split(";"); //divido il contenuto in destinatario[0] e messaggio in sé[1]

                        boolean inviato = false;

                        for (int i = 0; i < dC.getThreads().size(); i++) {
                            if (dC.getThreads().get(i).getName().equals(vals[0].trim())) {
                                dC.getThreads().get(i).inviaClient(this.getName() + ": " + vals[1].trim() + "--P--"); //chiamo il metodo del thread associato alla socket
                                if (!vals[1].equals(" |||")) {                                               //del destinatario per inviare al client il messaggio
                                    dC.salvaMessaggio(this.getName(), vals[0].trim(), vals[1].trim());  //se il messaggio non è di test, lo salvo nella cronologia della chat   
                                }
                                out.writeBytes("OK\n"); //invio al mio client la conferma di invio
                                inviato = true;
                            }
                        }
                        if (!inviato) {
                            out.writeBytes("NONE\n"); // se non ho trovato il destinatario, invio 'NONE'
                        }
                        break;

                    case "ST":
                        
                        for (int i = 0; i < dC.getThreads().size(); i++) {
                            if (!dC.getThreads().get(i).getName().equals(this.getName())) {
                                dC.getThreads().get(i).inviaClient(this.getName() + ": " + cont.trim()); //chiamo il metodo di tutti i thread associati alle socket
                            }                                                                            //del destinatario per inviare ai client il messaggio
                            out.writeBytes("OK\n"); //invio al mio client la conferma di invio
                        }
                        dC.salvaMGlobale(this.getName(), cont); //salvo il messaggio in cronologia
                        break;

                    case "VP":
                        // Ottiene la cronologia con l'utente specificato
                        altroUtente = cont.trim();
                        List<Messaggio> cronologia = dC.getCronologiaChat(this.getName(), altroUtente); //richiedo la cronologia dei messaggi tra me ed il corrispondente
                        
                        // Costruisco la stringa di risposta
                        StringBuilder cronologiaStr = new StringBuilder();
                        for (Messaggio m : cronologia) {
                            cronologiaStr.append(m.getMittente())
                                       .append(": ")
                                       .append(m.getContenuto())
                                       .append("-++-"); //per dividere i messaggi
                        }
                        
                        // Se non ci sono messaggi, invio "NONE"
                        if (cronologia.isEmpty()) {
                            out.writeBytes("NONE\n");
                        } else {
                            out.writeBytes("CRON:" + cronologiaStr.toString() + "\n"); //invio la cronologia con il prefisso 'CRON:'
                        }
                        break;

                    case "VT":
                        altroUtente = cont.trim();
                        ArrayList<Messaggio> cronologiaGlobale = dC.getCronologiaGlobale(); //richiedo la cronologia
                        if (cronologiaGlobale.isEmpty()) {
                            out.writeBytes("NONE\n"); //se è vuota, mando 'NONE'
                        } else {
                            //costruisco la cronologia
                            StringBuilder cronGlobStr = new StringBuilder();
                            for (Messaggio m : cronologiaGlobale) {
                                cronGlobStr.append(m.getMittente())
                                .append(": ")
                                .append(m.getContenuto())
                                .append("-++-");
                            }

                            out.writeBytes("CRON:" + cronGlobStr.toString() + "\n"); //invio la cronologia con il prefisso 'CRON:'
                        }
                        break;

                    case "EXIT": 
                        System.out.println("Il client si è disconnesso, chiudo");
                        out.writeBytes("OK\n"); //invio al client conferma disconnessione
                        break;
                    default:
                        break;
                }
            } while (!msg.equals("EXIT"));
            dC.getUtenti().remove(nome); //tolgo il nome dell'utente dalla lista nomi
            dC.getThreads().remove(this); //tolgo il thread (this) dalla lista dei thread
            for (int i = 0; i < dC.getMessaggi().size(); i++) { //cancello le cronologie di messaggi di cui il mio utente fa parte
                if ((dC.getMessaggi().get(i).getMittente().equals(this.getName())) || (dC.getMessaggi().get(i).getDestinatario().equals(this.getName()))) {
                    dC.getMessaggi().remove(i);
                }
            }
            in.close(); //chiudo tutto
            out.close();
            s0.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //metodo per inviare messaggio al proprio client
    public void inviaClient(String msg){

        try {
            out.writeBytes(msg + "\n");
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}