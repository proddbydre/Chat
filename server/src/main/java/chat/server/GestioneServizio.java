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
                                inviato = true;
                            }
                        }
                        if (!inviato) {
                            out.writeBytes("NONE\n");
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