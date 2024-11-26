package chat.server;

public class Messaggio {
    private String mittente;
    private String destinatario;
    private String contenuto;
    private long timestamp;

    public Messaggio(String mittente, String destinatario, String contenuto) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.contenuto = contenuto;
        this.timestamp = System.currentTimeMillis(); //ora dell'invio
    }

    

    public Messaggio(String mittente, String contenuto) {
        this.mittente = mittente;
        this.contenuto = contenuto;
    }



    public String getMittente() {
        return mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getContenuto() {
        return contenuto;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
