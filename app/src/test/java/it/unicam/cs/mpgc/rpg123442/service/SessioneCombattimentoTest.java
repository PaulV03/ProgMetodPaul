package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.combat.RisultatoTurno;
import it.unicam.cs.mpgc.rpg123442.model.item.PozioneCura;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica lo scontro giocato un'azione alla volta.
 *
 * <p>Con la formula di danno standard (attacco - difesa, minimo 1) i numeri sono
 * prevedibili: eroe 10 att. contro goblin 2 dif. = 8 danni; goblin 7 att. contro
 * eroe 4 dif. = 3 danni. Sono questi i valori attesi qui sotto.
 */
@DisplayName("SessioneCombattimento")
class SessioneCombattimentoTest {

    private Eroe eroe() {
        return new Eroe("Aragorn", new Statistiche(30, 10, 4));
    }

    private Nemico goblin() {
        return new Nemico("Goblin", new Statistiche(20, 7, 2), 15);
    }

    /** Una stanza sorvegliata dal nemico indicato. */
    private Stanza stanzaCon(Nemico nemico) {
        return new Stanza("Corridoio", "Uno stretto corridoio.", nemico);
    }

    @Nested
    @DisplayName("attaccando")
    class Attaccando {

        @Test
        @DisplayName("l'eroe colpisce e il nemico sopravvissuto risponde")
        void eroeColpisceEIlNemicoRisponde() {
            Eroe e = eroe();
            Nemico g = goblin();
            SessioneCombattimento scontro = new SessioneCombattimento(e, stanzaCon(g));

            List<RisultatoTurno> turni = scontro.attacca();

            assertAll(
                    () -> assertEquals(2, turni.size(), "il colpo dell'eroe e la risposta del nemico"),
                    () -> assertEquals(e, turni.get(0).attaccante()),
                    () -> assertEquals(g, turni.get(1).attaccante()),
                    () -> assertEquals(12, g.getVitaCorrente()), // 20 - 8
                    () -> assertEquals(27, e.getVitaCorrente()), // 30 - 3
                    () -> assertFalse(scontro.isFinito())
            );
        }

        @Test
        @DisplayName("un nemico abbattuto non risponde piu'")
        void nemicoAbbattutoNonRisponde() {
            Eroe e = eroe();
            Nemico morente = new Nemico("Goblin ferito", new Statistiche(5, 7, 2), 15);
            SessioneCombattimento scontro = new SessioneCombattimento(e, stanzaCon(morente));

            List<RisultatoTurno> turni = scontro.attacca(); // 8 danni su 5 punti vita

            assertAll(
                    () -> assertEquals(1, turni.size(), "solo il colpo dell'eroe"),
                    () -> assertTrue(turni.get(0).difensoreSconfitto()),
                    () -> assertEquals(30, e.getVitaCorrente(), "l'eroe non ha subito nulla"),
                    () -> assertTrue(scontro.eroeVincitore())
            );
        }
    }

    @Nested
    @DisplayName("alla vittoria")
    class AllaVittoria {

        @Test
        @DisplayName("l'eroe incassa l'esperienza e la stanza resta libera")
        void vittoriaDaEsperienzaELiberaLaStanza() {
            Eroe e = eroe();
            Stanza stanza = stanzaCon(goblin());
            SessioneCombattimento scontro = new SessioneCombattimento(e, stanza);

            while (!scontro.isFinito()) {
                scontro.attacca();
            }

            assertAll(
                    () -> assertTrue(scontro.eroeVincitore()),
                    () -> assertEquals(15, e.getEsperienza(), "l'esperienza rilasciata dal goblin"),
                    () -> assertFalse(stanza.haNemico(), "la stanza e' stata liberata")
            );
        }

        @Test
        @DisplayName("agire su uno scontro finito -> eccezione")
        void azioneDopoLaFineLanciaEccezione() {
            SessioneCombattimento scontro = new SessioneCombattimento(eroe(), stanzaCon(goblin()));
            while (!scontro.isFinito()) {
                scontro.attacca();
            }
            assertThrows(IllegalStateException.class, scontro::attacca);
        }
    }

    @Nested
    @DisplayName("alla sconfitta")
    class AllaSconfitta {

        @Test
        @DisplayName("il nemico resta al suo posto e l'eroe non guadagna nulla")
        void sconfittaLasciaIlNemicoNellaStanza() {
            Eroe debole = new Eroe("Novellino", new Statistiche(5, 1, 0));
            Nemico drago = new Nemico("Drago", new Statistiche(100, 50, 0), 200);
            Stanza stanza = stanzaCon(drago);
            SessioneCombattimento scontro = new SessioneCombattimento(debole, stanza);

            scontro.attacca(); // colpisce per 1, incassa 50 e cade

            assertAll(
                    () -> assertTrue(scontro.isFinito()),
                    () -> assertFalse(scontro.eroeVincitore()),
                    () -> assertFalse(debole.isVivo()),
                    () -> assertEquals(0, debole.getEsperienza(), "nessuna ricompensa per chi perde"),
                    () -> assertTrue(stanza.haNemico(), "il nemico e' ancora li'")
            );
        }
    }

    @Nested
    @DisplayName("usando un consumabile")
    class UsandoUnConsumabile {

        @Test
        @DisplayName("la pozione cura, si esaurisce e costa il turno")
        void pozioneCuraECostaIlTurno() {
            Eroe e = eroe();
            e.subisciDanno(10); // 20/30 punti vita
            PozioneCura pozione = new PozioneCura(8);
            e.getInventario().aggiungi(pozione);
            Nemico g = goblin();
            SessioneCombattimento scontro = new SessioneCombattimento(e, stanzaCon(g));

            List<RisultatoTurno> turni = scontro.usaConsumabile(pozione);

            assertAll(
                    () -> assertEquals(1, turni.size(), "solo la risposta del nemico"),
                    () -> assertEquals(g, turni.get(0).attaccante()),
                    () -> assertEquals(25, e.getVitaCorrente(), "20 + 8 di cura - 3 di danno"),
                    () -> assertEquals(20, g.getVitaCorrente(), "il nemico non e' stato colpito"),
                    () -> assertTrue(e.getInventario().isVuoto(), "la pozione si e' consumata")
            );
        }

        @Test
        @DisplayName("non si puo' usare un oggetto che non si possiede")
        void oggettoNonPossedutoLanciaEccezione() {
            SessioneCombattimento scontro = new SessioneCombattimento(eroe(), stanzaCon(goblin()));
            assertThrows(IllegalArgumentException.class,
                    () -> scontro.usaConsumabile(new PozioneCura(8)));
        }
    }

    @Test
    @DisplayName("non si apre uno scontro in una stanza tranquilla")
    void stanzaSenzaNemicoLanciaEccezione() {
        Stanza tranquilla = new Stanza("Ingresso", "Non c'e' nessuno.");
        assertThrows(IllegalStateException.class,
                () -> new SessioneCombattimento(eroe(), tranquilla));
    }
}
