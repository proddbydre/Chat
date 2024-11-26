package chat.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadClient extends Thread {
    Socket s0;
    String mioNome;
    String altroUtente;
    BufferedReader in;
    DataOutputStream out;
    Boolean flag;

    public ThreadClient(Socket s0, String altroUtente, String mioNome) {
        this.s0 = s0;
        this.altroUtente = altroUtente;
        this.mioNome = mioNome;
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
            flag = true; // indica se il thread deve attendere messaggi


            while (flag && !Thread.interrupted()) {
                if (in.ready()) { // Verifica se ci sono dati disponibili per la lettura
                    String msg = in.readLine();
                    if (msg.equals("OK") || msg.startsWith("CRON:")) { //scarto il messaggio se è una conferma un invio di cronologia
                        //NULLA
                    } else if (msg.split(":")[1].equals(" |||--P--")) { //scarto il messaggio se è di prova
                        //NULLA
                    } else{
                        if ((msg.split(":")[0].equals(altroUtente) && (msg.contains("--P--"))) || (altroUtente.equals("^")) && (!msg.contains("--P--"))) {
                            System.out.println(msg.replace("--P--", "")); //stampo il messaggio solo se me lo ha inviato il mio corrspondente ed è privato
                                                                                             //o se il mittente passato al thread è '^' e il messaggio non è privato
                        }
                    }
                } else {
                    // Se non ci sono dati, aggiungi un breve ritardo per evitare cicli di polling ad alta intensità (che il thread simetta in attesa prima di essere interrotto)
                    Thread.sleep(100);
                }
            }
            
            
         } catch (IOException e) {
            if (!flag) {
                //NULLA
            } else {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            //NULLA
        }

    }
}
