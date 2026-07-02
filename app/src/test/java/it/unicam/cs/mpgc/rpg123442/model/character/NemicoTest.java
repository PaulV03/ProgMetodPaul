package it.unicam.cs.mpgc.rpg123442.model.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Nemico")
class NemicoTest {

    @Test
    @DisplayName("restituisce l'esperienza rilasciata")
    void restituisceLEsperienzaRilasciata() {
        Nemico n = new Nemico("Goblin", new Statistiche(20, 7, 2), 15);
        assertEquals(15, n.getEsperienzaRilasciata());
    }

    @Test
    @DisplayName("esperienza rilasciata negativa -> eccezione")
    void esperienzaNegativaLanciaEccezione() {
        Statistiche s = new Statistiche(20, 7, 2);
        assertThrows(IllegalArgumentException.class, () -> new Nemico("Goblin", s, -1));
    }
}
