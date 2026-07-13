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
     * Avvia una <b>nuova</b> partita ponendo l'eroe nella stanza iniziale del mondo.
     *
     * @param eroe  il personaggio guidato dal giocatore (non null)
     * @param mondo la mappa in cui si svolge l'avventura (non null)
     */
    public GameEngine(Eroe eroe, Mondo mondo) {
        this(eroe, mondo, mondo == null ? null : mondo.getStanzaIniziale());
    }

    /**
     * Riprende una partita con l'eroe in una stanza qualsiasi del mondo.
     *
     * <p>Rispetto al costruttore che parte dalla stanza iniziale, questo permette
     * di <b>riprendere una partita in corso</b> (per esempio dopo aver caricato un
     * salvataggio) collocando l'eroe dove si trovava. La stanza indicata deve
     * appartenere al mondo: cosi' evitiamo stati incoerenti (un eroe "fuori mappa").
     *
     * @param eroe           il personaggio guidato dal giocatore (non null)
     * @param mondo          la mappa in cui si svolge l'avventura (non null)
     * @param stanzaCorrente la stanza in cui si trova l'eroe (non null, deve essere del mondo)
     */
    public GameEngine(Eroe eroe, Mondo mondo, Stanza stanzaCorrente) {
        if (eroe == null || mondo == null || stanzaCorrente == null) {
            throw new IllegalArgumentException("Eroe, mondo e stanza corrente non possono essere null");
        }
        if (mondo.getStanza(stanzaCorrente.getNome()).orElse(null) != stanzaCorrente) {
            throw new IllegalArgumentException(
                    "La stanza corrente non appartiene al mondo: " + stanzaCorrente.getNome());
        }
        this.eroe = eroe;
        this.mondo = mondo;
        this.stanzaCorrente = stanzaCorrente;
    }

    /**
     * @return il personaggio guidato dal giocatore
     */
    public Eroe getEroe() {
        return eroe;
    }

    /**
     * @return la mappa del mondo in cui si svolge la partita.
     *         Serve, fra l'altro, a chi deve salvare l'intero stato di gioco.
     */
    public Mondo getMondo() {
        return mondo;
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

    /**
     * Apre uno scontro con il nemico che sorveglia la stanza corrente, da giocare
     * <b>un'azione alla volta</b>.
     *
     * <p>E' la via che usa l'interfaccia grafica: restituisce lo scontro in corso e
     * lascia che sia il giocatore a decidere, turno dopo turno, se attaccare o
     * usare un oggetto.
     *
     * @return lo scontro appena iniziato
     * @throws IllegalStateException se nella stanza corrente non c'e' nessun nemico
     */
    public SessioneCombattimento iniziaCombattimento() {
        return new SessioneCombattimento(eroe, stanzaCorrente);
    }

    /**
     * Fa combattere l'eroe contro il nemico della stanza corrente risolvendo lo
     * scontro <b>per intero</b>, come se l'eroe attaccasse a oltranza senza usare
     * oggetti. Se vince, guadagna l'esperienza del nemico e la stanza resta libera.
     *
     * <p>E' la scorciatoia per chi non vuole giocare i turni a mano (uno script,
     * un test, una futura modalita' automatica). Non ripete le regole dello scontro:
     * le chiede alla stessa {@link SessioneCombattimento} usata dall'interfaccia
     * grafica, cosi' le due strade non possono divergere.
     *
     * @return true se l'eroe ha vinto, false se e' stato sconfitto
     * @throws IllegalStateException se nella stanza corrente non c'e' nessun nemico
     */
    public boolean combatti() {
        SessioneCombattimento scontro = iniziaCombattimento();
        while (!scontro.isFinito()) {
            scontro.attacca();
        }
        return scontro.eroeVincitore();
    }

    /**
     * @return true se nel mondo non e' rimasto piu' alcun nemico da sconfiggere
     */
    public boolean tuttiNemiciSconfitti() {
        return mondo.getStanze().stream().noneMatch(Stanza::haNemico);
    }

    /**
     * Determina lo stato complessivo della partita.
     *
     * <p>L'ordine dei controlli conta: se l'eroe e' morto la partita e' persa,
     * anche se restassero nemici. Solo se l'eroe e' vivo ha senso chiedersi se ha
     * gia' ripulito il mondo.
     *
     * @return {@link StatoPartita#SCONFITTA} se l'eroe e' caduto,
     *         {@link StatoPartita#VITTORIA} se ha sconfitto tutti i nemici,
     *         {@link StatoPartita#IN_CORSO} altrimenti
     */
    public StatoPartita statoPartita() {
        if (!eroe.isVivo()) {
            return StatoPartita.SCONFITTA;
        }
        if (tuttiNemiciSconfitti()) {
            return StatoPartita.VITTORIA;
        }
        return StatoPartita.IN_CORSO;
    }
}
