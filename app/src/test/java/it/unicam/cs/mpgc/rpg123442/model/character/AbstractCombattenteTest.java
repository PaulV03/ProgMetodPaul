package it.unicam.cs.mpgc.rpg123442.model.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la logica comune definita in {@link AbstractCombattente}.
 * Essendo la classe astratta, la si prova tramite una sottoclasse concreta ({@link Eroe}).
 */
@DisplayName("AbstractCombattente (via Eroe)")
class AbstractCombattenteTest {

    private Eroe nuovoEroe() {
        return new Eroe("Aragorn", new Statistiche(30, 10, 4));
    }

    @Test
    @DisplayName("alla creazione la vita e' al massimo ed e' vivo")
    void allaCreazioneLaVitaEAlMassimo() {
        Eroe e = nuovoEroe();
        assertEquals(30, e.getVitaCorrente());
        assertTrue(e.isVivo());
    }

    @Test
    @DisplayName("subisciDanno riduce la vita corrente")
    void subisciDannoRiduceLaVita() {
        Eroe e = nuovoEroe();
        e.subisciDanno(10);
        assertEquals(20, e.getVitaCorrente());
        assertTrue(e.isVivo());
    }

    @Test
    @DisplayName("la vita non scende sotto zero e il combattente muore")
    void laVitaNonScendeSottoZero() {
        Eroe e = nuovoEroe();
        e.subisciDanno(1000);
        assertEquals(0, e.getVitaCorrente());
        assertFalse(e.isVivo());
    }

    @Test
    @DisplayName("danno negativo -> eccezione")
    void dannoNegativoLanciaEccezione() {
        Eroe e = nuovoEroe();
        assertThrows(IllegalArgumentException.class, () -> e.subisciDanno(-1));
    }

    @Test
    @DisplayName("nome nullo o vuoto -> eccezione")
    void nomeNulloOVuotoLanciaEccezione() {
        Statistiche s = new Statistiche(30, 10, 4);
        assertThrows(IllegalArgumentException.class, () -> new Eroe(null, s));
        assertThrows(IllegalArgumentException.class, () -> new Eroe("", s));
        assertThrows(IllegalArgumentException.class, () -> new Eroe("   ", s));
    }

    @Test
    @DisplayName("statistiche nulle -> eccezione")
    void statisticheNulleLancianoEccezione() {
        assertThrows(IllegalArgumentException.class, () -> new Eroe("Aragorn", null));
    }
}
