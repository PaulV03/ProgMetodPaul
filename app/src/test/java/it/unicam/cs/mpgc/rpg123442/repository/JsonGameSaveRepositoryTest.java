package it.unicam.cs.mpgc.rpg123442.repository;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.item.Oggetto;
import it.unicam.cs.mpgc.rpg123442.model.item.PozioneCura;
import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonGameSaveRepository (salvataggio/caricamento JSON)")
class JsonGameSaveRepositoryTest {

    @TempDir
    Path cartellaTemporanea;

    private final GameSaveRepository repository = new JsonGameSaveRepository();

    private Eroe eroe;
    private Stanza atrio;
    private Stanza salaTrono;
    private Nemico goblin;
    private GameEngine partita;

    @BeforeEach
    void setUp() {
        // Un eroe gia' progredito e ferito, con una pozione nell'inventario.
        eroe = new Eroe("Aragorn", new Statistiche(40, 12, 5), 2, 30);
        eroe.subisciDanno(15); // vita 40 -> 25
        eroe.getInventario().aggiungi(new PozioneCura(10));

        // Un goblin ferito che sorveglia la sala del trono.
        goblin = new Nemico("Goblin", new Statistiche(20, 7, 2), 15);
        goblin.subisciDanno(4); // vita 20 -> 16

        atrio = new Stanza("Atrio", "L'ingresso del castello");
        salaTrono = new Stanza("Sala del trono", "Un'ampia sala con un trono", goblin);
        atrio.collega(Direzione.NORD, salaTrono);

        Mondo mondo = new Mondo(atrio);
        mondo.aggiungiStanza(salaTrono);

        partita = new GameEngine(eroe, mondo);
        partita.muovi(Direzione.NORD); // l'eroe non e' piu' nella stanza iniziale
    }

    @Test
    @DisplayName("salvare crea un file JSON leggibile")
    void salvareCreaIlFile() throws IOException {
        Path file = cartellaTemporanea.resolve("partita.json");

        repository.salva(partita, file);

        assertTrue(Files.exists(file));
        String contenuto = Files.readString(file);
        assertTrue(contenuto.contains("Aragorn"));
        assertTrue(contenuto.contains("Sala del trono"));
    }

    @Test
    @DisplayName("caricare ricostruisce fedelmente lo stato dell'eroe")
    void roundTripEroe() {
        Path file = cartellaTemporanea.resolve("partita.json");
        repository.salva(partita, file);

        Eroe caricato = repository.carica(file).getEroe();

        assertAll(
                () -> assertEquals("Aragorn", caricato.getNome()),
                () -> assertEquals(40, caricato.getStatistiche().getVitaMassima()),
                () -> assertEquals(12, caricato.getStatistiche().getAttacco()),
                () -> assertEquals(5, caricato.getStatistiche().getDifesa()),
                () -> assertEquals(25, caricato.getVitaCorrente()),
                () -> assertEquals(2, caricato.getLivello()),
                () -> assertEquals(30, caricato.getEsperienza()),
                () -> assertEquals(1, caricato.getInventario().dimensione())
        );

        List<Oggetto> oggetti = caricato.getInventario().getOggetti();
        assertInstanceOf(PozioneCura.class, oggetti.get(0));
        assertEquals(10, ((PozioneCura) oggetti.get(0)).getQuantitaCura());
    }

    @Test
    @DisplayName("caricare ricostruisce il mondo, la posizione e il nemico")
    void roundTripMondo() {
        Path file = cartellaTemporanea.resolve("partita.json");
        repository.salva(partita, file);

        GameEngine caricata = repository.carica(file);

        assertAll(
                () -> assertEquals(2, caricata.getMondo().numeroStanze()),
                () -> assertEquals("Sala del trono", caricata.getStanzaCorrente().getNome()),
                () -> assertEquals("Atrio", caricata.getMondo().getStanzaIniziale().getNome())
        );

        Stanza salaCaricata = caricata.getMondo().getStanza("Sala del trono").orElseThrow();
        Stanza atrioCaricato = caricata.getMondo().getStanza("Atrio").orElseThrow();
        assertAll(
                // il nemico e' tornato con la sua vita parziale
                () -> assertTrue(salaCaricata.haNemico()),
                () -> assertEquals(16, salaCaricata.getNemico().orElseThrow().getVitaCorrente()),
                () -> assertFalse(atrioCaricato.haNemico()),
                // i collegamenti bidirezionali sono stati ricostruiti
                () -> assertEquals(salaCaricata, atrioCaricato.getUscita(Direzione.NORD).orElseThrow()),
                () -> assertEquals(atrioCaricato, salaCaricata.getUscita(Direzione.SUD).orElseThrow())
        );
    }

    @Test
    @DisplayName("una partita caricata e' di nuovo giocabile (muoversi e combattere)")
    void partitaCaricataEGiocabile() {
        Path file = cartellaTemporanea.resolve("partita.json");
        repository.salva(partita, file);

        GameEngine caricata = repository.carica(file);
        boolean tornatoIndietro = caricata.muovi(Direzione.SUD);

        assertTrue(tornatoIndietro);
        assertEquals("Atrio", caricata.getStanzaCorrente().getNome());
    }

    @Test
    @DisplayName("caricare un file inesistente -> PersistenzaException")
    void caricareFileInesistente() {
        Path mancante = cartellaTemporanea.resolve("non-esiste.json");
        assertThrows(PersistenzaException.class, () -> repository.carica(mancante));
    }

    @Test
    @DisplayName("caricare un JSON malformato -> PersistenzaException")
    void caricareJsonMalformato() throws IOException {
        Path file = cartellaTemporanea.resolve("rotto.json");
        Files.writeString(file, "{ questo non e' json valido ]");

        assertThrows(PersistenzaException.class, () -> repository.carica(file));
    }
}
