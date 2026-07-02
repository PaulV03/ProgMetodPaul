package it.unicam.cs.mpgc.rpg123442.model.item;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PozioneCura")
class PozioneCuraTest {

    @Test
    @DisplayName("usata su un combattente ne ripristina la vita")
    void usaSuCuraIlBersaglio() {
        Eroe e = new Eroe("Aragorn", new Statistiche(30, 10, 4));
        e.subisciDanno(20); // vita 30 -> 10
        PozioneCura pozione = new PozioneCura(8);

        pozione.usaSu(e);

        assertEquals(18, e.getVitaCorrente());
    }

    @Test
    @DisplayName("quantita' di cura non positiva -> eccezione")
    void quantitaNonPositivaLanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> new PozioneCura(0));
        assertThrows(IllegalArgumentException.class, () -> new PozioneCura(-5));
    }

    @Test
    @DisplayName("usata su un bersaglio null -> eccezione")
    void bersaglioNullLanciaEccezione() {
        PozioneCura pozione = new PozioneCura(8);
        assertThrows(IllegalArgumentException.class, () -> pozione.usaSu(null));
    }
}
