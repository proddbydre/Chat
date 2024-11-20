package chat.server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatiCondivisi {
    ArrayList<String> utenti = new ArrayList<String>();
    ArrayList<Gruppo> gruppi = new ArrayList<Gruppo>();
    ArrayList<GestioneServizio> threads = new ArrayList<GestioneServizio>();
    ArrayList<Messaggio> messaggi = new ArrayList<Messaggio>();
    ArrayList<Messaggio> messaggiGlobali = new ArrayList<Messaggio>();

    public DatiCondivisi(){
    }

    public void salvaMessaggio(String mittente, String destinatario, String contenuto) {
        Messaggio msg = new Messaggio(mittente, contenuto);
        messaggi.add(msg);
    }

    public List<Messaggio> getCronologiaChat(String utente1, String utente2) {
        return messaggi.stream()
            .filter(msg -> 
                (msg.getMittente().equals(utente1) && msg.getDestinatario().equals(utente2)) ||
                (msg.getMittente().equals(utente2) && msg.getDestinatario().equals(utente1)))
            .collect(Collectors.toList());
    }

    public void salvaMGlobale(String mittente, String contenuto){
        Messaggio msg = new Messaggio(mittente, contenuto);
        messaggiGlobali.add(msg);
    }

    public ArrayList<Messaggio> getCronologiaGlobale(){
        return messaggiGlobali;
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
