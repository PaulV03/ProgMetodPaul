package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;

import java.util.Set;

/**
 * Il motore che gestisce lo stato di una partita: tiene insieme l'eroe e il
 * mondo, e sa dove si trova l'eroe in un dato momento.
 *
 * <p>La sua responsabilita' (SRP) e' <b>orchestrare</b>: mette in relazione i
 * mattoni del dominio (eroe, mondo, stanze) e ne coordina l'evoluzione durante
 * il gioco. Non ridisegna la mappa (compito di {@link Mondo}) ne' calcola i
 * danni (compito del calcolatore di danno): si limita a coordinare.
 *
 * <p>Questa prima versione copre l'<b>esplorazione</b>: la posizione corrente
 * dell'eroe e il suo spostamento fra le stanze. Combattimento e condizioni di
 * fine gioco verranno aggiunti come passi successivi, senza dover modificare
 * cio' che c'e' gia' (Open/Closed).
 */
public class GameEngine {

    private final Eroe eroe;
    private final Mondo mondo;

    /** La stanza in cui si trova l'eroe adesso: e' l'unico stato che cambia. */
    private Stanza stanzaCorrente;

    /**
     * Avvia una partita ponendo l'eroe nella stanza iniziale del mondo.
     *
     * @param eroe  il personaggio guidato dal giocatore (non null)
     * @param mondo la mappa in cui si svolge l'avventura (non null)
     */
    public GameEngine(Eroe eroe, Mondo mondo) {
        if (eroe == null || mondo == null) {
            throw new IllegalArgumentException("Eroe e mondo non possono essere null");
        }
        this.eroe = eroe;
        this.mondo = mondo;
        this.stanzaCorrente = mondo.getStanzaIniziale();
    }

    /**
     * @return il personaggio guidato dal giocatore
     */
    public Eroe getEroe() {
        return eroe;
    }

    /**
     * @return la stanza in cui l'eroe si trova in questo momento
     */
    public Stanza getStanzaCorrente() {
        return stanzaCorrente;
    }

    /**
     * @return le direzioni verso cui l'eroe puo' spostarsi dalla stanza corrente
     */
    public Set<Direzione> direzioniPossibili() {
        return stanzaCorrente.getDirezioniDisponibili();
    }

    /**
     * Sposta l'eroe nella stanza adiacente nella direzione indicata, se esiste
     * un'uscita in quella direzione.
     *
     * <p>Provare a muoversi verso un muro non e' un errore ma una normale mossa
     * di gioco: per questo il metodo restituisce semplicemente {@code false}
     * invece di sollevare un'eccezione.
     *
     * @param direzione la direzione verso cui muoversi (non null)
     * @return true se lo spostamento e' avvenuto, false se in quella direzione
     *         non c'e' un'uscita
     */
    public boolean muovi(Direzione direzione) {
        if (direzione == null) {
            throw new IllegalArgumentException("La direzione non puo' essere null");
        }
        return stanzaCorrente.getUscita(direzione)
                .map(destinazione -> {
                    this.stanzaCorrente = destinazione;
                    return true;
                })
                .orElse(false);
    }
}
