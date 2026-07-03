package it.unicam.cs.mpgc.rpg123442.model.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stanza")
class StanzaTest {

    @Test
    @DisplayName("una stanza nuova espone nome e descrizione e non ha uscite")
    void stanzaNuova() {
        Stanza atrio = new Stanza("Atrio", "L'ingresso del castello");

        assertEquals("Atrio", atrio.getNome());
        assertEquals("L'ingresso del castello", atrio.getDescrizione());
        assertTrue(atrio.getDirezioniDisponibili().isEmpty());
        assertFalse(atrio.haUscita(Direzione.NORD));
        assertTrue(atrio.getUscita(Direzione.NORD).isEmpty());
    }

    @Test
    @DisplayName("nome o descrizione vuoti -> eccezione")
    void nomeODescrizioneVuoti() {
        assertThrows(IllegalArgumentException.class, () -> new Stanza("  ", "descrizione"));
        assertThrows(IllegalArgumentException.class, () -> new Stanza("Atrio", ""));
        assertThrows(IllegalArgumentException.class, () -> new Stanza(null, "descrizione"));
        assertThrows(IllegalArgumentException.class, () -> new Stanza("Atrio", null));
    }

    @Test
    @DisplayName("collegare due stanze crea anche il passaggio di ritorno (bidirezionale)")
    void collegamentoBidirezionale() {
        Stanza atrio = new Stanza("Atrio", "L'ingresso del castello");
        Stanza salaTrono = new Stanza("Sala del trono", "Un'ampia sala con un trono");

        atrio.collega(Direzione.NORD, salaTrono);

        // andata: da atrio a nord si arriva alla sala del trono
        assertTrue(atrio.haUscita(Direzione.NORD));
        assertEquals(salaTrono, atrio.getUscita(Direzione.NORD).orElseThrow());

        // ritorno automatico: dalla sala del trono a sud si torna nell'atrio
        assertTrue(salaTrono.haUscita(Direzione.SUD));
        assertEquals(atrio, salaTrono.getUscita(Direzione.SUD).orElseThrow());
    }

    @Test
    @DisplayName("le direzioni disponibili riflettono le uscite create")
    void direzioniDisponibili() {
        Stanza atrio = new Stanza("Atrio", "L'ingresso del castello");
        atrio.collega(Direzione.NORD, new Stanza("Sala", "Una sala"));
        atrio.collega(Direzione.EST, new Stanza("Cucina", "Una cucina"));

        assertEquals(2, atrio.getDirezioniDisponibili().size());
        assertTrue(atrio.getDirezioniDisponibili().contains(Direzione.NORD));
        assertTrue(atrio.getDirezioniDisponibili().contains(Direzione.EST));
    }

    @Test
    @DisplayName("collegare con direzione o destinazione null -> eccezione")
    void collegaConNull() {
        Stanza atrio = new Stanza("Atrio", "L'ingresso del castello");
        assertThrows(IllegalArgumentException.class, () -> atrio.collega(null, atrio));
        assertThrows(IllegalArgumentException.class, () -> atrio.collega(Direzione.NORD, null));
    }

    @Test
    @DisplayName("l'insieme delle direzioni disponibili e' una copia non modificabile")
    void direzioniNonModificabili() {
        Stanza atrio = new Stanza("Atrio", "L'ingresso del castello");
        atrio.collega(Direzione.NORD, new Stanza("Sala", "Una sala"));

        assertThrows(UnsupportedOperationException.class,
                () -> atrio.getDirezioniDisponibili().add(Direzione.SUD));
    }
}
