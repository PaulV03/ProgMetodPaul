package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica lo schermo di fine partita: che racconti l'esito giusto, che se ne
 * vesta del colore giusto e che riporti al menu.
 *
 * <p>Anche qui l'FXML viene caricato davvero e il pulsante premuto davvero: un
 * <code>fx:id</code> sbagliato o un <code>onAction</code> che punta a un metodo
 * inesistente non sono errori di compilazione, si scoprono solo cosi'.
 */
@DisplayName("FineController")
class FineControllerTest {

    private static final String FXML = "/it/unicam/cs/mpgc/rpg123442/ui/fine.fxml";

    @BeforeAll
    static void avviaToolkitGrafico() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException giaAvviato) {
            // Il toolkit sopravvive fra una classe di test e l'altra: va bene cosi'.
        }
        Platform.setImplicitExit(false);
    }

    /** Annota soltanto se le e' stato chiesto di tornare al menu. */
    private static final class NavigazioneFinta implements Navigazione {
        private boolean menuChiesto;

        @Override
        public void mostraEsplorazione() {
            // non serve a questi test
        }

        @Override
        public void mostraCombattimento() {
            // non serve a questi test
        }

        @Override
        public void mostraMenuIniziale() {
            menuChiesto = true;
        }
    }

    private static <T> T sulThreadGrafico(Callable<T> compito) throws Exception {
        FutureTask<T> attivita = new FutureTask<>(compito);
        Platform.runLater(attivita);
        return attivita.get(10, TimeUnit.SECONDS);
    }

    private static Eroe eroe() {
        return new Eroe("Eroe", new Statistiche(30, 10, 4));
    }

    /** Una partita vinta: il dungeon e' una stanza sola, e non la sorveglia nessuno. */
    private static GameEngine partitaVinta() {
        return new GameEngine(eroe(), new Mondo(new Stanza("Sala", "Vuota e silenziosa.")));
    }

    /** Una partita persa: resta un nemico in piedi, ma l'eroe non lo e' piu'. */
    private static GameEngine partitaPersa() {
        Eroe caduto = eroe();
        caduto.subisciDanno(caduto.getStatistiche().getVitaMassima());
        Stanza tana = new Stanza("Tana", "Un antro buio.",
                new Nemico("Goblin", new Statistiche(20, 7, 2), 15));
        return new GameEngine(caduto, new Mondo(tana));
    }

    /** Una partita ancora aperta: c'e' un nemico da battere e l'eroe sta benissimo. */
    private static GameEngine partitaInCorso() {
        Stanza tana = new Stanza("Tana", "Un antro buio.",
                new Nemico("Goblin", new Statistiche(20, 7, 2), 15));
        return new GameEngine(eroe(), new Mondo(tana));
    }

    private static Parent caricaSchermo(GameEngine partita, Navigazione navigazione)
            throws Exception {
        FXMLLoader caricatore = new FXMLLoader(FineControllerTest.class.getResource(FXML));
        caricatore.setController(new FineController(partita, navigazione));
        return caricatore.load();
    }

    private static String testoDi(Parent radice, String fxId) {
        return ((Label) radice.lookup("#" + fxId)).getText();
    }

    @Test
    @DisplayName("dopo aver ripulito il dungeon annuncia la vittoria, in verde")
    void ripulitoIlDungeonAnnunciaLaVittoria() throws Exception {
        Parent radice = sulThreadGrafico(
                () -> caricaSchermo(partitaVinta(), new NavigazioneFinta()));

        assertAll(
                () -> assertEquals("Vittoria", testoDi(radice, "titoloFine")),
                () -> assertTrue(radice.lookup("#esito").getStyleClass().contains("esito-vittoria")),
                () -> assertTrue(testoDi(radice, "riepilogo").contains("Eroe"),
                        "il riepilogo racconta fin dove e' arrivato l'eroe"));
    }

    @Test
    @DisplayName("caduto l'eroe annuncia la sconfitta, in rosso")
    void cadutoLEroeAnnunciaLaSconfitta() throws Exception {
        Parent radice = sulThreadGrafico(
                () -> caricaSchermo(partitaPersa(), new NavigazioneFinta()));

        assertAll(
                () -> assertEquals("Sconfitta", testoDi(radice, "titoloFine")),
                () -> assertTrue(radice.lookup("#esito").getStyleClass().contains("esito-sconfitta")));
    }

    @Test
    @DisplayName("il pulsante riporta al menu iniziale")
    void ilPulsanteRiportaAlMenu() throws Exception {
        NavigazioneFinta navigazione = new NavigazioneFinta();
        Parent radice = sulThreadGrafico(() -> caricaSchermo(partitaVinta(), navigazione));

        sulThreadGrafico(() -> {
            ((Button) radice.lookup("#bottoneMenu")).fire();
            return null;
        });

        assertTrue(navigazione.menuChiesto);
    }

    @Test
    @DisplayName("non si puo' aprire la fine di una partita che non e' finita")
    void nonSiApreSuUnaPartitaInCorso() {
        assertThrows(IllegalArgumentException.class,
                () -> new FineController(partitaInCorso(), new NavigazioneFinta()));
    }
}
