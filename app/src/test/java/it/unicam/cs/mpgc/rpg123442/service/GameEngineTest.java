package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameEngine")
class GameEngineTest {

    private Eroe eroe;
    private Stanza atrio;
    private Stanza salaTrono;
    private Mondo mondo;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        eroe = new Eroe("Aragorn", new Statistiche(30, 8, 3));
        atrio = new Stanza("Atrio", "L'ingresso del castello");
        salaTrono = new Stanza("Sala del trono", "Un'ampia sala con un trono");
        atrio.collega(Direzione.NORD, salaTrono);

        mondo = new Mondo(atrio);
        mondo.aggiungiStanza(salaTrono);

        engine = new GameEngine(eroe, mondo);
    }

    @Test
    @DisplayName("la partita comincia nella stanza iniziale del mondo")
    void iniziaNellaStanzaIniziale() {
        assertEquals(atrio, engine.getStanzaCorrente());
        assertEquals(eroe, engine.getEroe());
    }

    @Test
    @DisplayName("eroe o mondo null -> eccezione")
    void costruttoreConNull() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(null, mondo));
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(eroe, null));
    }

    @Test
    @DisplayName("muoversi verso un'uscita esistente sposta l'eroe e restituisce true")
    void muoviVersoUscitaEsistente() {
        boolean spostato = engine.muovi(Direzione.NORD);

        assertTrue(spostato);
        assertEquals(salaTrono, engine.getStanzaCorrente());
    }

    @Test
    @DisplayName("muoversi verso un muro non sposta l'eroe e restituisce false")
    void muoviVersoUnMuro() {
        boolean spostato = engine.muovi(Direzione.EST);

        assertFalse(spostato);
        assertEquals(atrio, engine.getStanzaCorrente());
    }

    @Test
    @DisplayName("dopo essersi spostati si puo' tornare indietro (uscita bidirezionale)")
    void andataERitorno() {
        engine.muovi(Direzione.NORD);
        assertEquals(salaTrono, engine.getStanzaCorrente());

        boolean tornato = engine.muovi(Direzione.SUD);
        assertTrue(tornato);
        assertEquals(atrio, engine.getStanzaCorrente());
    }

    @Test
    @DisplayName("le direzioni possibili riflettono le uscite della stanza corrente")
    void direzioniPossibili() {
        assertEquals(1, engine.direzioniPossibili().size());
        assertTrue(engine.direzioniPossibili().contains(Direzione.NORD));
    }

    @Test
    @DisplayName("muovere con direzione null -> eccezione")
    void muoviConNull() {
        assertThrows(IllegalArgumentException.class, () -> engine.muovi(null));
    }

    @Test
    @DisplayName("combattere e vincere: eroe vivo, XP guadagnata, stanza liberata")
    void combattiVittoria() {
        Eroe eroeForte = new Eroe("Aragorn", new Statistiche(30, 8, 3));
        Nemico goblin = new Nemico("Goblin", new Statistiche(20, 5, 1), 10);
        Stanza tana = new Stanza("Tana", "Una tana buia", goblin);
        GameEngine engine = new GameEngine(eroeForte, new Mondo(tana));

        boolean vinto = engine.combatti();

        assertTrue(vinto);
        assertTrue(eroeForte.isVivo());
        assertEquals(10, eroeForte.getEsperienza());
        assertFalse(tana.haNemico());
    }

    @Test
    @DisplayName("combattere e perdere: eroe sconfitto, il nemico resta nella stanza")
    void combattiSconfitta() {
        Eroe novizio = new Eroe("Novizio", new Statistiche(10, 1, 0));
        Nemico drago = new Nemico("Drago", new Statistiche(50, 20, 5), 100);
        Stanza covo = new Stanza("Covo", "Il covo del drago", drago);
        GameEngine engine = new GameEngine(novizio, new Mondo(covo));

        boolean vinto = engine.combatti();

        assertFalse(vinto);
        assertFalse(novizio.isVivo());
        assertTrue(covo.haNemico());
    }

    @Test
    @DisplayName("combattere in una stanza senza nemico -> eccezione")
    void combattiSenzaNemico() {
        assertThrows(IllegalStateException.class, () -> engine.combatti());
    }

    @Test
    @DisplayName("con un nemico ancora vivo la partita e' IN_CORSO")
    void partitaInCorso() {
        Eroe eroe = new Eroe("Aragorn", new Statistiche(30, 8, 3));
        Nemico goblin = new Nemico("Goblin", new Statistiche(20, 5, 1), 10);
        Stanza tana = new Stanza("Tana", "Una tana buia", goblin);
        GameEngine engine = new GameEngine(eroe, new Mondo(tana));

        assertFalse(engine.tuttiNemiciSconfitti());
        assertEquals(StatoPartita.IN_CORSO, engine.statoPartita());
    }

    @Test
    @DisplayName("sconfitto l'ultimo nemico la partita e' VITTORIA")
    void partitaVinta() {
        Eroe eroe = new Eroe("Aragorn", new Statistiche(30, 8, 3));
        Nemico goblin = new Nemico("Goblin", new Statistiche(20, 5, 1), 10);
        Stanza tana = new Stanza("Tana", "Una tana buia", goblin);
        GameEngine engine = new GameEngine(eroe, new Mondo(tana));

        engine.combatti(); // l'eroe forte vince e libera la stanza

        assertTrue(engine.tuttiNemiciSconfitti());
        assertEquals(StatoPartita.VITTORIA, engine.statoPartita());
    }

    @Test
    @DisplayName("se l'eroe cade la partita e' SCONFITTA, anche con nemici rimasti")
    void partitaPersa() {
        Eroe novizio = new Eroe("Novizio", new Statistiche(10, 1, 0));
        Nemico drago = new Nemico("Drago", new Statistiche(50, 20, 5), 100);
        Stanza covo = new Stanza("Covo", "Il covo del drago", drago);
        GameEngine engine = new GameEngine(novizio, new Mondo(covo));

        engine.combatti(); // il novizio perde e muore

        assertFalse(novizio.isVivo());
        assertFalse(engine.tuttiNemiciSconfitti()); // il drago e' ancora li'
        assertEquals(StatoPartita.SCONFITTA, engine.statoPartita());
    }
}
