package it.unicam.cs.mpgc.rpg123442.model.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mondo")
class MondoTest {

    @Test
    @DisplayName("un mondo nuovo contiene la sola stanza iniziale")
    void mondoNuovo() {
        Stanza atrio = new Stanza("Atrio", "L'ingresso del castello");
        Mondo mondo = new Mondo(atrio);

        assertEquals(atrio, mondo.getStanzaIniziale());
        assertEquals(1, mondo.numeroStanze());
        assertEquals(atrio, mondo.getStanza("Atrio").orElseThrow());
    }

    @Test
    @DisplayName("stanza iniziale null -> eccezione")
    void stanzaInizialeNull() {
        assertThrows(IllegalArgumentException.class, () -> new Mondo(null));
    }

    @Test
    @DisplayName("aggiungere stanze le rende ricercabili per nome")
    void aggiungiERicerca() {
        Mondo mondo = new Mondo(new Stanza("Atrio", "L'ingresso"));
        Stanza cucina = new Stanza("Cucina", "Una cucina fumosa");

        mondo.aggiungiStanza(cucina);

        assertEquals(2, mondo.numeroStanze());
        assertEquals(cucina, mondo.getStanza("Cucina").orElseThrow());
    }

    @Test
    @DisplayName("cercare una stanza inesistente restituisce Optional vuoto")
    void ricercaStanzaAssente() {
        Mondo mondo = new Mondo(new Stanza("Atrio", "L'ingresso"));
        assertTrue(mondo.getStanza("Segreta").isEmpty());
    }

    @Test
    @DisplayName("aggiungere una stanza con nome gia' presente -> eccezione")
    void nomeDuplicato() {
        Mondo mondo = new Mondo(new Stanza("Atrio", "L'ingresso"));
        assertThrows(IllegalArgumentException.class,
                () -> mondo.aggiungiStanza(new Stanza("Atrio", "Un altro atrio")));
    }

    @Test
    @DisplayName("aggiungere una stanza null -> eccezione")
    void aggiungiNull() {
        Mondo mondo = new Mondo(new Stanza("Atrio", "L'ingresso"));
        assertThrows(IllegalArgumentException.class, () -> mondo.aggiungiStanza(null));
    }

    @Test
    @DisplayName("la collezione delle stanze e' una copia non modificabile")
    void stanzeNonModificabili() {
        Mondo mondo = new Mondo(new Stanza("Atrio", "L'ingresso"));
        assertThrows(UnsupportedOperationException.class,
                () -> mondo.getStanze().add(new Stanza("Intrusa", "Non dovrebbe entrare")));
    }
}
