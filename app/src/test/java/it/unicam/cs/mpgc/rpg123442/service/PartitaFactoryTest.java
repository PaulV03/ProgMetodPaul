package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.item.Consumabile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PartitaFactory")
class PartitaFactoryTest {

    @Test
    @DisplayName("la nuova partita parte dall'ingresso con un eroe di primo livello")
    void nuovaPartitaParteDallIngresso() {
        GameEngine partita = PartitaFactory.creaNuovaPartita("Aragorn");

        assertAll(
                () -> assertEquals("Aragorn", partita.getEroe().getNome()),
                () -> assertEquals(1, partita.getEroe().getLivello()),
                () -> assertTrue(partita.getEroe().isVivo()),
                () -> assertEquals(partita.getMondo().getStanzaIniziale(), partita.getStanzaCorrente()),
                () -> assertFalse(partita.getStanzaCorrente().haNemico(), "si comincia al sicuro"),
                () -> assertEquals(StatoPartita.IN_CORSO, partita.statoPartita()));
    }

    @Test
    @DisplayName("l'eroe parte con delle pozioni in tasca")
    void lEroeParteConDellePozioni() {
        GameEngine partita = PartitaFactory.creaNuovaPartita("Aragorn");

        long consumabili = partita.getEroe().getInventario().getOggetti().stream()
                .filter(Consumabile.class::isInstance)
                .count();

        assertTrue(consumabili > 0, "senza consumabili l'azione 'usa pozione' non sarebbe mai possibile");
    }
}
