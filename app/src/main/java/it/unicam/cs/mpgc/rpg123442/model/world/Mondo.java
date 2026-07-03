package it.unicam.cs.mpgc.rpg123442.model.world;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * La mappa del gioco: l'insieme delle {@link Stanza stanze} che compongono il
 * mondo, con l'indicazione di quella iniziale (dove comincia l'avventura).
 *
 * <p>La sua responsabilita' (SRP) e' <b>solo</b> custodire la struttura del
 * mondo: quali stanze esistono e da dove si parte. NON tiene traccia di dove si
 * trova l'eroe in un dato momento ne' gestisce il movimento: quella e'
 * informazione di <i>stato di gioco</i> e verra' gestita dal motore di gioco.
 * Separare la mappa (statica) dallo stato (che cambia) mantiene le
 * responsabilita' distinte.
 */
public class Mondo {

    /**
     * Le stanze del mondo, indicizzate per nome per poterle ritrovare facilmente.
     * Uso una {@link LinkedHashMap} per mantenere l'ordine di inserimento: rende
     * prevedibile l'iterazione (utile per test e futura persistenza).
     */
    private final Map<String, Stanza> stanze = new LinkedHashMap<>();

    private final Stanza stanzaIniziale;

    /**
     * Crea un mondo a partire dalla sua stanza iniziale, che viene subito
     * registrata fra le stanze del mondo.
     *
     * @param stanzaIniziale la stanza da cui parte l'avventura (non null)
     */
    public Mondo(Stanza stanzaIniziale) {
        if (stanzaIniziale == null) {
            throw new IllegalArgumentException("La stanza iniziale non puo' essere null");
        }
        this.stanzaIniziale = stanzaIniziale;
        registra(stanzaIniziale);
    }

    /**
     * Aggiunge una stanza al mondo. I nomi devono essere univoci: due stanze con
     * lo stesso nome sarebbero indistinguibili nella ricerca.
     *
     * @param stanza la stanza da aggiungere (non null, nome non gia' presente)
     */
    public void aggiungiStanza(Stanza stanza) {
        registra(stanza);
    }

    /**
     * Registrazione interna, usata anche dal costruttore. E' {@code private} per
     * non essere sovrascritta: chiamare un metodo sovrascrivibile dal costruttore
     * sarebbe una cattiva pratica.
     */
    private void registra(Stanza stanza) {
        if (stanza == null) {
            throw new IllegalArgumentException("Non si puo' aggiungere una stanza null");
        }
        if (stanze.containsKey(stanza.getNome())) {
            throw new IllegalArgumentException("Esiste gia' una stanza di nome: " + stanza.getNome());
        }
        stanze.put(stanza.getNome(), stanza);
    }

    /**
     * @return la stanza da cui parte l'avventura
     */
    public Stanza getStanzaIniziale() {
        return stanzaIniziale;
    }

    /**
     * @param nome il nome della stanza cercata
     * @return la stanza con quel nome, se presente nel mondo
     */
    public Optional<Stanza> getStanza(String nome) {
        return Optional.ofNullable(stanze.get(nome));
    }

    /**
     * @return tutte le stanze del mondo, come collezione non modificabile
     *         (copia difensiva: non si puo' alterare il mondo dall'esterno)
     */
    public Collection<Stanza> getStanze() {
        return List.copyOf(stanze.values());
    }

    /**
     * @return il numero di stanze presenti nel mondo
     */
    public int numeroStanze() {
        return stanze.size();
    }
}
