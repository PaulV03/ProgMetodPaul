package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MondoFactory")
class MondoFactoryTest {

    @Test
    @DisplayName("il mondo di default ha quattro stanze e parte dall'Ingresso")
    void struttura() {
        Mondo mondo = MondoFactory.creaMondoDiDefault();

        assertEquals(4, mondo.numeroStanze());
        assertEquals("Ingresso", mondo.getStanzaIniziale().getNome());
    }

    @Test
    @DisplayName("l'Ingresso e' libero, le altre tre stanze hanno un nemico")
    void nemici() {
        Mondo mondo = MondoFactory.creaMondoDiDefault();

        assertFalse(mondo.getStanza("Ingresso").orElseThrow().haNemico());
        assertTrue(mondo.getStanza("Corridoio").orElseThrow().haNemico());
        assertTrue(mondo.getStanza("Armeria").orElseThrow().haNemico());
        assertTrue(mondo.getStanza("Sala del Trono").orElseThrow().haNemico());
    }

    @Test
    @DisplayName("le stanze sono collegate come previsto (a 'T')")
    void collegamenti() {
        Mondo mondo = MondoFactory.creaMondoDiDefault();
        Stanza ingresso = mondo.getStanzaIniziale();

        Stanza corridoio = ingresso.getUscita(Direzione.EST).orElseThrow();
        assertEquals("Corridoio", corridoio.getNome());

        assertEquals("Armeria", corridoio.getUscita(Direzione.NORD).orElseThrow().getNome());
        assertEquals("Sala del Trono", corridoio.getUscita(Direzione.EST).orElseThrow().getNome());
    }

    @Test
    @DisplayName("i collegamenti sono bidirezionali: dal Corridoio si torna all'Ingresso a ovest")
    void collegamentoDiRitorno() {
        Mondo mondo = MondoFactory.creaMondoDiDefault();
        Stanza corridoio = mondo.getStanza("Corridoio").orElseThrow();

        assertEquals("Ingresso", corridoio.getUscita(Direzione.OVEST).orElseThrow().getNome());
    }
}
