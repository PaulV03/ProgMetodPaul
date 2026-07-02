package it.unicam.cs.mpgc.rpg123442.model.combat;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalcolatoreDannoStandard")
class CalcolatoreDannoStandardTest {

    private final CalcolatoreDanno calcolatore = new CalcolatoreDannoStandard();

    @Test
    @DisplayName("danno = attacco dell'attaccante - difesa del difensore")
    void dannoEAttaccoMenoDifesa() {
        Eroe attaccante = new Eroe("Aragorn", new Statistiche(30, 10, 4));
        Nemico difensore = new Nemico("Goblin", new Statistiche(20, 7, 2), 15);
        assertEquals(8, calcolatore.calcola(attaccante, difensore)); // 10 - 2
    }

    @Test
    @DisplayName("il danno e' sempre almeno 1, anche con difesa altissima")
    void ilDannoEAlmenoUno() {
        Eroe attaccante = new Eroe("Debole", new Statistiche(30, 3, 0));
        Nemico difensore = new Nemico("Corazzato", new Statistiche(20, 5, 10), 5);
        assertEquals(1, calcolatore.calcola(attaccante, difensore)); // 3 - 10 -> minimo 1
    }
}
