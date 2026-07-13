package it.unicam.cs.mpgc.rpg123442.model.combat;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;
import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Combattimento")
class CombattimentoTest {

    private Eroe eroe() {
        return new Eroe("Aragorn", new Statistiche(30, 10, 4));
    }

    private Nemico goblin() {
        return new Nemico("Goblin", new Statistiche(20, 7, 2), 15);
    }

    @Test
    @DisplayName("all'inizio non e' finito e non c'e' vincitore")
    void allInizioNonEFinito() {
        Combattimento c = new Combattimento(eroe(), goblin());
        assertFalse(c.isFinito());
        assertTrue(c.getVincitore().isEmpty());
    }

    @Test
    @DisplayName("il primo turno infligge danno al secondo sfidante")
    void primoTurnoInfliggeDanno() {
        Eroe e = eroe();
        Nemico g = goblin();
        Combattimento c = new Combattimento(e, g);

        RisultatoTurno r = c.eseguiTurno();

        assertEquals(e, r.attaccante());
        assertEquals(g, r.difensore());
        assertEquals(8, r.danno());            // 10 - 2
        assertEquals(12, g.getVitaCorrente()); // 20 - 8
    }

    @Test
    @DisplayName("i turni si alternano fra i due sfidanti")
    void iTurniSiAlternano() {
        Eroe e = eroe();
        Nemico g = goblin();
        Combattimento c = new Combattimento(e, g);

        c.eseguiTurno();                       // tocca a e
        RisultatoTurno secondo = c.eseguiTurno();

        assertEquals(g, secondo.attaccante());
        assertEquals(e, secondo.difensore());
    }

    @Test
    @DisplayName("alla fine il vincitore e' il combattente ancora vivo")
    void allaFineVinceIlVivo() {
        Eroe e = eroe();
        Nemico g = goblin();
        Combattimento c = new Combattimento(e, g);

        while (!c.isFinito()) {
            c.eseguiTurno();
        }

        assertTrue(c.getVincitore().isPresent());
        Combattente vincitore = c.getVincitore().get();
        assertTrue(vincitore.isVivo());
        assertEquals(e, vincitore); // con queste statistiche vince l'eroe
    }

    @Test
    @DisplayName("eseguire un turno dopo la fine -> eccezione")
    void turnoDopoLaFineLanciaEccezione() {
        Combattimento c = new Combattimento(eroe(), goblin());
        while (!c.isFinito()) {
            c.eseguiTurno();
        }
        assertThrows(IllegalStateException.class, c::eseguiTurno);
    }

    @Test
    @DisplayName("passare il turno lo cede all'avversario senza fare danno")
    void passaTurnoCedeIlTurnoSenzaDanno() {
        Eroe e = eroe();
        Nemico g = goblin();
        Combattimento c = new Combattimento(e, g);

        c.passaTurno(); // l'eroe rinuncia al colpo (ha fatto altro)

        assertAll(
                () -> assertEquals(g, c.getTurnoDi(), "ora tocca al goblin"),
                () -> assertEquals(20, g.getVitaCorrente(), "il goblin non deve aver subito danno"),
                () -> assertEquals(30, e.getVitaCorrente(), "nemmeno l'eroe deve subire danno")
        );
    }

    @Test
    @DisplayName("dopo un turno passato tocca all'avversario attaccare")
    void dopoPassaTurnoAttaccaLAvversario() {
        Eroe e = eroe();
        Nemico g = goblin();
        Combattimento c = new Combattimento(e, g);

        c.passaTurno();
        RisultatoTurno r = c.eseguiTurno();

        assertEquals(g, r.attaccante());
        assertEquals(e, r.difensore());
    }

    @Test
    @DisplayName("passare il turno dopo la fine -> eccezione")
    void passaTurnoDopoLaFineLanciaEccezione() {
        Combattimento c = new Combattimento(eroe(), goblin());
        while (!c.isFinito()) {
            c.eseguiTurno();
        }
        assertThrows(IllegalStateException.class, c::passaTurno);
    }

    @Test
    @DisplayName("non si puo' combattere contro se stessi")
    void nonSiCombatteControSeStessi() {
        Eroe e = eroe();
        assertThrows(IllegalArgumentException.class, () -> new Combattimento(e, e));
    }

    @Test
    @DisplayName("combattenti nulli -> eccezione")
    void combattentiNulliLancianoEccezione() {
        assertThrows(IllegalArgumentException.class, () -> new Combattimento(eroe(), null));
        assertThrows(IllegalArgumentException.class, () -> new Combattimento(null, goblin()));
    }
}
