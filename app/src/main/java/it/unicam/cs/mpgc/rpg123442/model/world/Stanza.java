package it.unicam.cs.mpgc.rpg123442.model.world;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Un luogo del mondo di gioco, collegato ad altre stanze tramite delle uscite.
 *
 * <p>La sua responsabilita' (SRP) e' duplice ma coesa: rappresentare un luogo
 * (nome e descrizione) e conoscere le proprie <i>uscite</i>, cioe' verso quali
 * altre stanze si puo' andare e in che {@link Direzione}. Non sa nulla di eroi,
 * combattimenti o interfaccia grafica: e' un semplice nodo del "grafo" che forma
 * la mappa del mondo.
 *
 * <p>I collegamenti sono <b>bidirezionali</b>: quando collego questa stanza a
 * un'altra verso una direzione, viene creato automaticamente anche il passaggio
 * di ritorno nella direzione {@link Direzione#opposta() opposta}.
 */
public class Stanza {

    private final String nome;
    private final String descrizione;

    /**
     * Le uscite: per ogni direzione, la stanza che si raggiunge.
     * Uso una {@link EnumMap} perche' le chiavi sono valori di un enum: e' piu'
     * efficiente e compatta di una HashMap in questo caso.
     */
    private final Map<Direzione, Stanza> uscite = new EnumMap<>(Direzione.class);

    public Stanza(String nome, String descrizione) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della stanza non puo' essere vuoto");
        }
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione della stanza non puo' essere vuota");
        }
        this.nome = nome;
        this.descrizione = descrizione;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Collega questa stanza a un'altra in una certa direzione, creando anche il
     * passaggio di ritorno nella direzione opposta.
     *
     * @param direzione   la direzione verso cui si trova la stanza di destinazione
     * @param destinazione la stanza raggiungibile in quella direzione
     */
    public void collega(Direzione direzione, Stanza destinazione) {
        if (direzione == null || destinazione == null) {
            throw new IllegalArgumentException("Direzione e destinazione non possono essere null");
        }
        this.uscite.put(direzione, destinazione);
        destinazione.uscite.put(direzione.opposta(), this);
    }

    /**
     * @param direzione la direzione da controllare
     * @return la stanza raggiungibile in quella direzione, se esiste un'uscita.
     *         Restituisco un {@link Optional} invece di null per rendere esplicito
     *         che l'uscita potrebbe non esserci, evitando errori a runtime.
     */
    public Optional<Stanza> getUscita(Direzione direzione) {
        return Optional.ofNullable(uscite.get(direzione));
    }

    /**
     * @param direzione la direzione da controllare
     * @return true se da questa stanza si puo' andare in quella direzione
     */
    public boolean haUscita(Direzione direzione) {
        return uscite.containsKey(direzione);
    }

    /**
     * @return l'insieme delle direzioni in cui esiste un'uscita.
     *         E' una copia non modificabile: chi la riceve non puo' alterare
     *         le uscite reali della stanza.
     */
    public Set<Direzione> getDirezioniDisponibili() {
        return Set.copyOf(uscite.keySet());
    }
}
