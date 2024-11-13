package chat.server;

import java.util.ArrayList;

public class DatiCondivisi {
    ArrayList<String> utenti = new ArrayList<String>();
    ArrayList<Gruppo> gruppi = new ArrayList<Gruppo>();
    ArrayList<GestioneServizio> threads = new ArrayList<GestioneServizio>();

    public DatiCondivisi(){
    }

    public ArrayList<String> getUtenti() {
        return utenti;
    }

    public ArrayList<Gruppo> getGruppi() {
        return gruppi;
    }

    public ArrayList<GestioneServizio> getThreads() {
        return threads;
    }

    
}
