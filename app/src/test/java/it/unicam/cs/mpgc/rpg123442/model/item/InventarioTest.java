package it.unicam.cs.mpgc.rpg123442.model.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventario")
class InventarioTest {

    @Test
    @DisplayName("un inventario nuovo e' vuoto")
    void inventarioNuovoEVuoto() {
        Inventario inv = new Inventario();
        assertTrue(inv.isVuoto());
        assertEquals(0, inv.dimensione());
    }

    @Test
    @DisplayName("aggiungere e rimuovere oggetti")
    void aggiungiERimuovi() {
        Inventario inv = new Inventario();
        PozioneCura pozione = new PozioneCura(10);

        inv.aggiungi(pozione);
        assertTrue(inv.contiene(pozione));
        assertEquals(1, inv.dimensione());
        assertFalse(inv.isVuoto());

        assertTrue(inv.rimuovi(pozione));
        assertFalse(inv.contiene(pozione));
        assertTrue(inv.isVuoto());
    }

    @Test
    @DisplayName("rimuovere un oggetto non presente restituisce false")
    void rimuovereOggettoAssente() {
        Inventario inv = new Inventario();
        assertFalse(inv.rimuovi(new PozioneCura(10)));
    }

    @Test
    @DisplayName("aggiungere null -> eccezione")
    void aggiungereNullLanciaEccezione() {
        Inventario inv = new Inventario();
        assertThrows(IllegalArgumentException.class, () -> inv.aggiungi(null));
    }

    @Test
    @DisplayName("la lista restituita e' una copia non modificabile")
    void listaRestituitaNonModificabile() {
        Inventario inv = new Inventario();
        inv.aggiungi(new PozioneCura(10));
        assertThrows(UnsupportedOperationException.class,
                () -> inv.getOggetti().add(new PozioneCura(5)));
    }
}
