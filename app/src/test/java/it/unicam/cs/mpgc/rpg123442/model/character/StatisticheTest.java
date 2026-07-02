package it.unicam.cs.mpgc.rpg123442.model.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Statistiche")
class StatisticheTest {

    @Test
    @DisplayName("crea statistiche valide e restituisce i valori")
    void creaStatisticheValide() {
        Statistiche s = new Statistiche(30, 10, 4);
        assertEquals(30, s.getVitaMassima());
        assertEquals(10, s.getAttacco());
        assertEquals(4, s.getDifesa());
    }

    @Test
    @DisplayName("vita massima non positiva -> eccezione")
    void vitaMassimaNonPositivaLanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> new Statistiche(0, 10, 4));
        assertThrows(IllegalArgumentException.class, () -> new Statistiche(-5, 10, 4));
    }

    @Test
    @DisplayName("attacco o difesa negativi -> eccezione")
    void attaccoODifesaNegativiLancianoEccezione() {
        assertThrows(IllegalArgumentException.class, () -> new Statistiche(30, -1, 4));
        assertThrows(IllegalArgumentException.class, () -> new Statistiche(30, 10, -1));
    }
}
