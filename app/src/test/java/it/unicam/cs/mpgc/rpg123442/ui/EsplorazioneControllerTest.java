package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import it.unicam.cs.mpgc.rpg123442.service.MondoFactory;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il cablaggio fra lo schermo di esplorazione e il motore di gioco.
 *
 * <p>Il compilatore non puo' accorgersi se un <code>fx:id</code> non corrisponde
 * a nessun campo o se un <code>onAction</code> punta a un metodo inesistente:
 * sono errori che si manifestano solo caricando davvero il file FXML. Questi
 * test lo caricano e premono i pulsanti per davvero.
 *
 * <p>Le schermate JavaFX possono essere costruite solo dopo l'avvio del toolkit
 * grafico e vanno toccate sul suo thread: da qui l'avvio in {@link BeforeAll} e
 * l'esecuzione di ogni verifica dentro {@link #sulThreadGrafico}.
 */
@DisplayName("EsplorazioneController")
class EsplorazioneControllerTest {

    private static final String FXML = "/it/unicam/cs/mpgc/rpg123442/ui/esplorazione.fxml";

    @BeforeAll
    static void avviaToolkitGrafico() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException giaAvviato) {
            // Il toolkit sopravvive fra una classe di test e l'altra: va bene cosi'.
        }
        Platform.setImplicitExit(false);
    }

    /**
     * Esegue il compito sul thread grafico di JavaFX e ne attende il risultato,
     * rilanciando l'eventuale errore cosi' che il test fallisca davvero.
     */
    private static <T> T sulThreadGrafico(Callable<T> compito) throws Exception {
        FutureTask<T> attivita = new FutureTask<>(compito);
        Platform.runLater(attivita);
        return attivita.get(10, TimeUnit.SECONDS);
    }

    /** Carica lo schermo di esplorazione per una nuova partita nel mondo di default. */
    private static Parent caricaSchermo() throws Exception {
        GameEngine partita = new GameEngine(
                new Eroe("Eroe", new Statistiche(30, 10, 4)),
                MondoFactory.creaMondoDiDefault());
        FXMLLoader caricatore = new FXMLLoader(EsplorazioneControllerTest.class.getResource(FXML));
        caricatore.setController(new EsplorazioneController(partita));
        return caricatore.load();
    }

    private static String testoDi(Parent radice, String fxId) {
        return ((Label) radice.lookup("#" + fxId)).getText();
    }

    private static Button bottone(Parent radice, String fxId) {
        return (Button) radice.lookup("#" + fxId);
    }

    @Test
    @DisplayName("lo schermo si carica e mostra la stanza iniziale")
    void loSchermoSiCaricaEMostraLaStanzaIniziale() throws Exception {
        Parent radice = sulThreadGrafico(EsplorazioneControllerTest::caricaSchermo);

        assertAll(
                () -> assertTrue(testoDi(radice, "nomeStanza").contains("Ingresso")),
                () -> assertFalse(testoDi(radice, "descrizioneStanza").isBlank()),
                () -> assertTrue(testoDi(radice, "statoNemico").contains("tranquilla")),
                () -> assertTrue(testoDi(radice, "statoEroe").contains("Eroe")));
    }

    @Test
    @DisplayName("solo le direzioni con un'uscita sono attive")
    void soloLeDirezioniConUnUscitaSonoAttive() throws Exception {
        Parent radice = sulThreadGrafico(EsplorazioneControllerTest::caricaSchermo);

        // Dall'Ingresso del mondo di default si esce soltanto verso est.
        assertAll(
                () -> assertFalse(bottone(radice, "bottoneEst").isDisabled()),
                () -> assertTrue(bottone(radice, "bottoneNord").isDisabled()),
                () -> assertTrue(bottone(radice, "bottoneSud").isDisabled()),
                () -> assertTrue(bottone(radice, "bottoneOvest").isDisabled()));
    }

    @Test
    @DisplayName("premere una direzione sposta l'eroe e aggiorna la schermata")
    void premereUnaDirezioneSpostaLEroeEAggiornaLaSchermata() throws Exception {
        Parent radice = sulThreadGrafico(EsplorazioneControllerTest::caricaSchermo);

        sulThreadGrafico(() -> {
            bottone(radice, "bottoneEst").fire();
            return null;
        });

        assertAll(
                () -> assertTrue(testoDi(radice, "nomeStanza").contains("Corridoio")),
                () -> assertTrue(testoDi(radice, "statoNemico").contains("Goblin")),
                () -> assertTrue(testoDi(radice, "messaggio").contains("Est")),
                // Tornando indietro si rientra nell'Ingresso: il collegamento e' bidirezionale.
                () -> assertFalse(bottone(radice, "bottoneOvest").isDisabled()));
    }
}
