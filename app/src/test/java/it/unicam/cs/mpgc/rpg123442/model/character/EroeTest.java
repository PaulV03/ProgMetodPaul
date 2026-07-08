package it.unicam.cs.mpgc.rpg123442.model.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Eroe (progressione livello/esperienza)")
class EroeTest {

    private Eroe nuovoEroe() {
        return new Eroe("Aragorn", new Statistiche(30, 10, 4));
    }

    @Test
    @DisplayName("un eroe nuovo parte a livello 1 con 0 esperienza")
    void eroeNuovoPartABaseLivello() {
        Eroe e = nuovoEroe();
        assertEquals(1, e.getLivello());
        assertEquals(0, e.getEsperienza());
        assertEquals(100, e.esperienzaProssimoLivello());
    }

    @Test
    @DisplayName("guadagnare esperienza sotto la soglia non fa salire di livello")
    void esperienzaSottoSogliaNonFaLivello() {
        Eroe e = nuovoEroe();
        e.guadagnaEsperienza(50);
        assertEquals(1, e.getLivello());
        assertEquals(50, e.getEsperienza());
    }

    @Test
    @DisplayName("raggiungere la soglia fa salire di livello e potenzia le statistiche")
    void raggiungereLaSogliaFaSalireDiLivello() {
        Eroe e = nuovoEroe();
        e.guadagnaEsperienza(100);

        assertEquals(2, e.getLivello());
        assertEquals(0, e.getEsperienza());
        // statistiche potenziate: 30->35, 10->12, 4->5
        assertEquals(35, e.getStatistiche().getVitaMassima());
        assertEquals(12, e.getStatistiche().getAttacco());
        assertEquals(5, e.getStatistiche().getDifesa());
        // salendo di livello viene curato a vita piena
        assertEquals(35, e.getVitaCorrente());
    }

    @Test
    @DisplayName("l'esperienza in eccesso resta dopo il passaggio di livello")
    void esperienzaInEccessoResta() {
        Eroe e = nuovoEroe();
        e.guadagnaEsperienza(130); // 100 per il livello, avanzano 30
        assertEquals(2, e.getLivello());
        assertEquals(30, e.getEsperienza());
    }

    @Test
    @DisplayName("molta esperienza fa salire piu' livelli in una volta")
    void moltaEsperienzaFaPiuLivelli() {
        Eroe e = nuovoEroe();
        // L1->L2 costa 100 (restano 200); L2->L3 costa 200 (restano 0)
        e.guadagnaEsperienza(300);
        assertEquals(3, e.getLivello());
        assertEquals(0, e.getEsperienza());
    }

    @Test
    @DisplayName("esperienza negativa -> eccezione")
    void esperienzaNegativaLanciaEccezione() {
        Eroe e = nuovoEroe();
        assertThrows(IllegalArgumentException.class, () -> e.guadagnaEsperienza(-1));
    }

    @Test
    @DisplayName("il costruttore con progressione ricrea un eroe gia' avviato")
    void costruttoreConProgressione() {
        Eroe e = new Eroe("Aragorn", new Statistiche(40, 14, 6), 3, 75);

        assertAll(
                () -> assertEquals(3, e.getLivello()),
                () -> assertEquals(75, e.getEsperienza()),
                () -> assertEquals(40, e.getVitaCorrente()),
                () -> assertTrue(e.getInventario().isVuoto())
        );
    }

    @Test
    @DisplayName("livello sotto 1 o esperienza negativa nel costruttore -> eccezione")
    void costruttoreConProgressioneInvalida() {
        Statistiche s = new Statistiche(30, 10, 4);
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Eroe("X", s, 0, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Eroe("X", s, 1, -1))
        );
    }
}
