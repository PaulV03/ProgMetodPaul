package it.unicam.cs.mpgc.rpg123442.model.world;

import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Un luogo del mondo di gioco, collegato ad altre stanze tramite delle uscite e
 * che puo' essere sorvegliato da un nemico.
 *
 * <p>La sua responsabilita' (SRP) e' coesa: rappresentare un luogo (nome e
 * descrizione), conoscere le proprie <i>uscite</i> (verso quali altre stanze si
 * puo' andare e in che {@link Direzione}) ed eventualmente ospitare un
 * {@link Nemico}. Non gestisce il combattimento ne' l'interfaccia grafica: e' un
 * semplice nodo del "grafo" che forma la mappa del mondo.
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

    /** Il nemico che sorveglia la stanza, oppure null se la stanza e' libera. */
    private Nemico nemico;

    /**
     * Crea una stanza libera, senza nemico.
     */
    public Stanza(String nome, String descrizione) {
        this(nome, descrizione, null);
    }

    /**
     * Crea una stanza, eventualmente sorvegliata da un nemico.
     *
     * @param nome        il nome della stanza (non vuoto)
     * @param descrizione la descrizione della stanza (non vuota)
     * @param nemico      il nemico che la sorveglia, oppure null se e' libera
     */
    public Stanza(String nome, String descrizione, Nemico nemico) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della stanza non puo' essere vuoto");
        }
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione della stanza non puo' essere vuota");
        }
        this.nome = nome;
        this.descrizione = descrizione;
        this.nemico = nemico;
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

    /**
     * @return il nemico che sorveglia la stanza, se presente.
     *         Restituisco un {@link Optional} perche' una stanza puo' essere
     *         libera: chi chiama e' cosi' costretto a gestire questo caso.
     */
    public Optional<Nemico> getNemico() {
        return Optional.ofNullable(nemico);
    }

    /**
     * @return true se la stanza e' sorvegliata da un nemico
     */
    public boolean haNemico() {
        return nemico != null;
    }

    /**
     * Toglie il nemico dalla stanza (per esempio dopo che e' stato sconfitto).
     * Dopo questa chiamata la stanza risulta libera.
     */
    public void rimuoviNemico() {
        this.nemico = null;
    }
}
