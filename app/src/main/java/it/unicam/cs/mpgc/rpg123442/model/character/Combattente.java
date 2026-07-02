package it.unicam.cs.mpgc.rpg123442.model.character;

/**
 * Astrazione di un'entita' capace di combattere (per esempio un eroe o un nemico).
 *
 * <p>E' un'<b>interfaccia</b>: descrive <i>cosa sa fare</i> un combattente, non
 * <i>come</i> lo fa. Il resto del gioco (combattimento, interfaccia grafica...)
 * dipendera' da questa astrazione e non dalle classi concrete: cosi' potremo
 * aggiungere nuovi tipi di combattente senza toccare il codice esistente
 * (principi DIP e Open/Closed di SOLID).
 */
public interface Combattente {

    /**
     * @return il nome del combattente (per messaggi e interfaccia)
     */
    String getNome();

    /**
     * @return le statistiche base (vita massima, attacco, difesa)
     */
    Statistiche getStatistiche();

    /**
     * @return i punti vita attuali (fra 0 e la vita massima)
     */
    int getVitaCorrente();

    /**
     * @return true se il combattente e' ancora in vita (vita corrente > 0)
     */
    boolean isVivo();

    /**
     * Applica del danno al combattente, riducendone la vita corrente.
     *
     * @param danno quantita' di danno da subire (non negativa)
     */
    void subisciDanno(int danno);
}
