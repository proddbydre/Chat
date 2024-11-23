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
    DatiCondivisi dC;

    


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
                if (dC.getUtenti().contains(nome)) {
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
                System.out.println("Messaggio: " + msg);

                String op = msg.split("-")[0];
                System.out.println("op: " + op);

                String cont = msg.split("-")[1].trim();
                System.out.println("cont: " + cont);

                String lista;
                String altroUtente;

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

                    case "G":

                        lista = "";
                        System.out.println("raccolgo nomiG");
                        for (int i = 0; i < dC.getGruppi().size(); i++) {
                                lista += dC.getGruppi().get(i).getNome() + ";";
                        }

                        System.out.println("invio nomiG");

                        out.writeBytes(lista + "\n");
                        
                        break;

                    case "SP":
                        
                        String vals[] = cont.split(";");

                        boolean inviato = false;

                        for (int i = 0; i < dC.getThreads().size(); i++) {
                            if (dC.getThreads().get(i).getName().equals(vals[0].trim())) {
                                dC.getThreads().get(i).inviaClient(this.getName() + ": " + vals[1].trim());
                                if (!vals[1].equals(" |||")) {
                                    dC.salvaMessaggio(this.getName(), vals[0].trim(), vals[1].trim());    
                                }
                                out.writeBytes("OK\n");
                                inviato = true;
                            }
                        }
                        if (!inviato) {
                            out.writeBytes("NONE\n");
                        }
                        break;

                    case "ST":
                        
                        for (int i = 0; i < dC.getThreads().size(); i++) {
                            if (!dC.getThreads().get(i).equals(this.getName())) {
                                dC.getThreads().get(i).inviaClient(this.getName() + ": " + cont.trim());
                            }
                            out.writeBytes("OK\n");
                        }
                        dC.salvaMGlobale(this.getName(), cont);
                        break;

                    case "VP":
                        // Ottiene la cronologia con l'utente specificato
                        altroUtente = cont.trim();
                        List<Messaggio> cronologia = dC.getCronologiaChat(this.getName(), altroUtente);
                        
                        // Costruisce la stringa di risposta
                        StringBuilder cronologiaStr = new StringBuilder();
                        for (Messaggio m : cronologia) {
                            cronologiaStr.append(m.getMittente())
                                       .append(": ")
                                       .append(m.getContenuto())
                                       .append("\\|\\|\\|");
                        }
                        
                        // Se non ci sono messaggi, invia "NONE"
                        if (cronologia.isEmpty()) {
                            out.writeBytes("NONE\n");
                        } else {
                            out.writeBytes(cronologiaStr.toString() + "\n");
                        }
                        break;

                    case "VT":
                        altroUtente = cont.trim();
                        ArrayList<Messaggio> cronologiaGlobale = dC.getCronologiaGlobale();
                        if (cronologiaGlobale.isEmpty()) {
                            out.writeBytes("NONE\n");
                        } else {
                            StringBuilder cronGlobStr = new StringBuilder();
                            for (Messaggio m : cronologiaGlobale) {
                                cronGlobStr.append(m.getMittente())
                                .append(": ")
                                .append(m.getContenuto())
                                .append("\\|\\|\\|");
                            }

                            out.writeBytes(cronGlobStr.toString() + "\n");
                        }
                        break;
                
                    default:
                        break;
                }
            } while (!msg.equals("EXIT"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inviaClient(String msg){

        try {
            out.writeBytes(msg + "\n");
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}