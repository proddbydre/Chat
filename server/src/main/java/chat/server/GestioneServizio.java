package chat.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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
            System.out.println("Nome aggiunto: " + this.getName());

            String msg;
            do {
                msg = in.readLine();

                String op = msg.split("-")[0];
                String cont = msg.split("-")[1];

                String lista;

                switch (op) {
                    case "P":

                        lista = "";
                        System.out.println("raccolgo nomi");
                        for (int i = 0; i < dC.getThreads().size(); i++) {
                                lista += dC.getThreads().get(i).getName() + ";";
                        }

                        System.out.println("invio nomi");

                        out.writeBytes(lista + "\n");
                        
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
                
                    default:
                        break;
                }
            } while (!msg.equals("EXIT"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
