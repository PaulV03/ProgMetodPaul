package it.unicam.cs.mpgc.rpg123442.repository.dto;

import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;

import java.util.List;
import java.util.Map;

/**
 * "Fotografia" piatta e serializzabile dello stato di una partita: e' il
 * <b>formato di salvataggio</b>, tenuto separato dalle classi di dominio.
 *
 * <p>Perche' non serializzare direttamente il dominio? Perche' il mondo di gioco
 * e' un <b>grafo con cicli</b> (le stanze si rimandano a vicenda): darlo in pasto
 * a un serializzatore JSON provocherebbe una ricorsione infinita. Qui invece il
 * grafo e' rappresentato come <b>nodi + archi</b>: ogni {@link StanzaDTO stanza}
 * indica le proprie uscite tramite il <i>nome</i> della stanza di destinazione
 * (un riferimento), non l'oggetto stanza. Niente cicli, e il file resta leggibile.
 *
 * <p>Sono tutti {@code record}: semplici contenitori immutabili di dati, senza
 * logica. La logica che traduce dominio &lt;-&gt; DTO vive nel mappatore dedicato.
 *
 * @param eroe           lo stato dell'eroe
 * @param stanzaIniziale il nome della stanza da cui parte il mondo
 * @param stanzaCorrente il nome della stanza in cui si trova l'eroe
 * @param stanze         tutte le stanze del mondo
 */
public record PartitaDTO(
        EroeDTO eroe,
        String stanzaIniziale,
        String stanzaCorrente,
        List<StanzaDTO> stanze) {

    /**
     * Stato dell'eroe: statistiche attuali, progressione e inventario.
     */
    public record EroeDTO(
            String nome,
            int vitaMassima,
            int vitaCorrente,
            int attacco,
            int difesa,
            int livello,
            int esperienza,
            List<PozioneDTO> inventario) {
    }

    /**
     * Stato di una stanza. Le {@code uscite} associano a ogni direzione il
     * <b>nome</b> della stanza raggiungibile (arco del grafo). Il {@code nemico}
     * e' {@code null} se la stanza e' libera.
     */
    public record StanzaDTO(
            String nome,
            String descrizione,
            NemicoDTO nemico,
            Map<Direzione, String> uscite) {
    }

    /**
     * Stato di un nemico che sorveglia una stanza.
     */
    public record NemicoDTO(
            String nome,
            int vitaMassima,
            int vitaCorrente,
            int attacco,
            int difesa,
            int esperienzaRilasciata) {
    }

    /**
     * Stato di una pozione di cura nell'inventario.
     */
    public record PozioneDTO(int quantitaCura) {
    }
}
