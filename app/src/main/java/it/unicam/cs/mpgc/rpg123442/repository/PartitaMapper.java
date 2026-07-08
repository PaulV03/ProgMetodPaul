package it.unicam.cs.mpgc.rpg123442.repository;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;
import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.item.Oggetto;
import it.unicam.cs.mpgc.rpg123442.model.item.PozioneCura;
import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;
import it.unicam.cs.mpgc.rpg123442.repository.dto.PartitaDTO;
import it.unicam.cs.mpgc.rpg123442.repository.dto.PartitaDTO.EroeDTO;
import it.unicam.cs.mpgc.rpg123442.repository.dto.PartitaDTO.NemicoDTO;
import it.unicam.cs.mpgc.rpg123442.repository.dto.PartitaDTO.PozioneDTO;
import it.unicam.cs.mpgc.rpg123442.repository.dto.PartitaDTO.StanzaDTO;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Traduce fra il dominio (oggetti ricchi di comportamento) e i {@link PartitaDTO
 * DTO} (dati piatti da serializzare), in entrambe le direzioni.
 *
 * <p>Concentra qui, in un solo posto, la parte "delicata" della persistenza: come
 * appiattire il grafo del mondo in nodi + archi e come ricostruirlo. Tenere questa
 * logica fuori sia dal dominio sia dal formato di file mantiene le responsabilita'
 * separate (SRP): il dominio non sa nulla del salvataggio, i DTO non hanno logica.
 *
 * <p>E' una classe di sole utilita' statiche: il costruttore privato impedisce di
 * istanziarla.
 */
public final class PartitaMapper {

    private PartitaMapper() {
    }

    // ---------------------------------------------------------------------
    // Dominio  ->  DTO  (per il salvataggio)
    // ---------------------------------------------------------------------

    /**
     * Crea la fotografia serializzabile di una partita.
     */
    public static PartitaDTO toDTO(GameEngine partita) {
        Mondo mondo = partita.getMondo();
        List<StanzaDTO> stanze = new ArrayList<>();
        for (Stanza stanza : mondo.getStanze()) {
            stanze.add(stanzaToDTO(stanza));
        }
        return new PartitaDTO(
                eroeToDTO(partita.getEroe()),
                mondo.getStanzaIniziale().getNome(),
                partita.getStanzaCorrente().getNome(),
                stanze);
    }

    private static EroeDTO eroeToDTO(Eroe eroe) {
        Statistiche s = eroe.getStatistiche();
        List<PozioneDTO> inventario = new ArrayList<>();
        for (Oggetto oggetto : eroe.getInventario().getOggetti()) {
            if (oggetto instanceof PozioneCura pozione) {
                inventario.add(new PozioneDTO(pozione.getQuantitaCura()));
            }
        }
        return new EroeDTO(
                eroe.getNome(),
                s.getVitaMassima(),
                eroe.getVitaCorrente(),
                s.getAttacco(),
                s.getDifesa(),
                eroe.getLivello(),
                eroe.getEsperienza(),
                inventario);
    }

    private static StanzaDTO stanzaToDTO(Stanza stanza) {
        NemicoDTO nemico = stanza.getNemico().map(PartitaMapper::nemicoToDTO).orElse(null);
        Map<Direzione, String> uscite = new EnumMap<>(Direzione.class);
        for (Direzione direzione : stanza.getDirezioniDisponibili()) {
            uscite.put(direzione, stanza.getUscita(direzione).orElseThrow().getNome());
        }
        return new StanzaDTO(stanza.getNome(), stanza.getDescrizione(), nemico, uscite);
    }

    private static NemicoDTO nemicoToDTO(Nemico nemico) {
        Statistiche s = nemico.getStatistiche();
        return new NemicoDTO(
                nemico.getNome(),
                s.getVitaMassima(),
                nemico.getVitaCorrente(),
                s.getAttacco(),
                s.getDifesa(),
                nemico.getEsperienzaRilasciata());
    }

    // ---------------------------------------------------------------------
    // DTO  ->  Dominio  (per il caricamento)
    // ---------------------------------------------------------------------

    /**
     * Ricostruisce una partita giocabile a partire dalla sua fotografia.
     *
     * @throws PersistenzaException se il salvataggio e' incoerente (per esempio
     *                              un'uscita verso una stanza che non esiste)
     */
    public static GameEngine fromDTO(PartitaDTO dto) {
        Eroe eroe = eroeFromDTO(dto.eroe());

        // 1) creo tutte le stanze (senza uscite), indicizzate per nome.
        Map<String, Stanza> perNome = new LinkedHashMap<>();
        for (StanzaDTO stanzaDTO : dto.stanze()) {
            perNome.put(stanzaDTO.nome(), stanzaFromDTO(stanzaDTO));
        }

        // 2) collego le stanze. collega() e' bidirezionale, quindi ogni arco va
        //    creato una sola volta: tengo traccia di quelli gia' fatti.
        Set<String> giaCollegate = new HashSet<>();
        for (StanzaDTO stanzaDTO : dto.stanze()) {
            Stanza origine = perNome.get(stanzaDTO.nome());
            if (stanzaDTO.uscite() == null) {
                continue;
            }
            for (Map.Entry<Direzione, String> uscita : stanzaDTO.uscite().entrySet()) {
                Direzione direzione = uscita.getKey();
                String chiave = stanzaDTO.nome() + "|" + direzione;
                if (giaCollegate.contains(chiave)) {
                    continue;
                }
                Stanza destinazione = richiediStanza(perNome, uscita.getValue(), "collegata a " + stanzaDTO.nome());
                origine.collega(direzione, destinazione);
                giaCollegate.add(chiave);
                giaCollegate.add(uscita.getValue() + "|" + direzione.opposta());
            }
        }

        // 3) costruisco il mondo e ci colloco l'eroe dove era rimasto.
        Mondo mondo = new Mondo(richiediStanza(perNome, dto.stanzaIniziale(), "iniziale"));
        for (StanzaDTO stanzaDTO : dto.stanze()) {
            if (!stanzaDTO.nome().equals(dto.stanzaIniziale())) {
                mondo.aggiungiStanza(perNome.get(stanzaDTO.nome()));
            }
        }
        Stanza corrente = richiediStanza(perNome, dto.stanzaCorrente(), "corrente");
        return new GameEngine(eroe, mondo, corrente);
    }

    private static Eroe eroeFromDTO(EroeDTO dto) {
        Statistiche statistiche = new Statistiche(dto.vitaMassima(), dto.attacco(), dto.difesa());
        Eroe eroe = new Eroe(dto.nome(), statistiche, dto.livello(), dto.esperienza());
        applicaVitaCorrente(eroe, dto.vitaCorrente(), dto.vitaMassima());
        if (dto.inventario() != null) {
            for (PozioneDTO pozione : dto.inventario()) {
                eroe.getInventario().aggiungi(new PozioneCura(pozione.quantitaCura()));
            }
        }
        return eroe;
    }

    private static Stanza stanzaFromDTO(StanzaDTO dto) {
        Nemico nemico = dto.nemico() == null ? null : nemicoFromDTO(dto.nemico());
        return new Stanza(dto.nome(), dto.descrizione(), nemico);
    }

    private static Nemico nemicoFromDTO(NemicoDTO dto) {
        Statistiche statistiche = new Statistiche(dto.vitaMassima(), dto.attacco(), dto.difesa());
        Nemico nemico = new Nemico(dto.nome(), statistiche, dto.esperienzaRilasciata());
        applicaVitaCorrente(nemico, dto.vitaCorrente(), dto.vitaMassima());
        return nemico;
    }

    /**
     * Riporta un combattente alla vita corrente salvata. Alla nascita parte con la
     * vita al massimo, quindi per riottenere una vita parziale gli applico il danno
     * mancante usando la sola API pubblica (nessuna scorciatoia sul dominio).
     */
    private static void applicaVitaCorrente(Combattente combattente, int vitaCorrente, int vitaMassima) {
        int danno = vitaMassima - vitaCorrente;
        if (danno > 0) {
            combattente.subisciDanno(danno);
        }
    }

    private static Stanza richiediStanza(Map<String, Stanza> perNome, String nome, String ruolo) {
        Stanza stanza = perNome.get(nome);
        if (stanza == null) {
            throw new PersistenzaException(
                    "Il salvataggio fa riferimento a una stanza inesistente (" + ruolo + "): " + nome);
        }
        return stanza;
    }
}
