package it.unicam.cs.mpgc.rpg123442.model.character;

/**
 * Un avversario controllato dal gioco.
 *
 * <p>Oltre al comportamento base di combattente, un nemico rilascia una certa
 * quantita' di esperienza quando viene sconfitto: e' questo il motivo per cui e'
 * una classe distinta dall'{@link Eroe}. Servira' quando implementeremo la
 * progressione (l'eroe che sale di livello sconfiggendo nemici).
 */
public class Nemico extends AbstractCombattente {

    private final int esperienzaRilasciata;

    public Nemico(String nome, Statistiche statistiche, int esperienzaRilasciata) {
        super(nome, statistiche);
        if (esperienzaRilasciata < 0) {
            throw new IllegalArgumentException("L'esperienza rilasciata non puo' essere negativa");
        }
        this.esperienzaRilasciata = esperienzaRilasciata;
    }

    public int getEsperienzaRilasciata() {
        return esperienzaRilasciata;
    }
}
