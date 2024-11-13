package chat.server;

import java.util.ArrayList;

public class Gruppo {
    String nome;
    ArrayList<String> partecipanti = new ArrayList<String>();
    
    public Gruppo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<String> getPartecipanti() {
        return partecipanti;
    }

    
}
