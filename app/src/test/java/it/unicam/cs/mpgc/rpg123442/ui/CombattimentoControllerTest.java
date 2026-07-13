package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.item.PozioneCura;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
 * Verifica il cablaggio fra lo schermo di combattimento e il motore di gioco:
 * che i pulsanti chiamino le azioni giuste, che restino accesi solo quando quelle
 * azioni sono possibili e che a scontro finito si possa tornare nel mondo.
 *
 * <p>Come per l'esplorazione, l'FXML viene caricato davvero e i pulsanti premuti
 * davvero: un <code>fx:id</code> sbagliato non e' un errore di compilazione, si
 * scopre solo qui.
 */
@DisplayName("CombattimentoController")
class CombattimentoControllerTest {

    private static final String FXML = "/it/unicam/cs/mpgc/rpg123442/ui/combattimento.fxml";

    @BeforeAll
    static void avviaToolkitGrafico() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException giaAvviato) {
            // Il toolkit sopravvive fra una classe di test e l'altra: va bene cosi'.
        }
        Platform.setImplicitExit(false);
    }

    /** Annota soltanto se le e' stato chiesto di tornare al mondo. */
    private static final class NavigazioneFinta implements Navigazione {
        private boolean esplorazioneChiesta;

        @Override
        public void mostraEsplorazione() {
            esplorazioneChiesta = true;
        }

        @Override
        public void mostraCombattimento() {
            // non serve a questi test
        }

        @Override
        public void mostraMenuIniziale() {
            // non serve a questi test
        }
    }

    private static <T> T sulThreadGrafico(Callable<T> compito) throws Exception {
        FutureTask<T> attivita = new FutureTask<>(compito);
        Platform.runLater(attivita);
        return attivita.get(10, TimeUnit.SECONDS);
    }

    /**
     * Prepara una partita che comincia gia' faccia a faccia con il nemico indicato,
     * e ne apre lo schermo di combattimento.
     *
     * @param nemico   l'avversario che sorveglia la stanza iniziale
     * @param pozioni  quante pozioni ha in tasca l'eroe
     */
    private static Parent caricaSchermo(Nemico nemico, int pozioni, Navigazione navigazione)
            throws Exception {
        Eroe eroe = new Eroe("Eroe", new Statistiche(30, 10, 4));
        for (int i = 0; i < pozioni; i++) {
            eroe.getInventario().aggiungi(new PozioneCura(12));
        }
        Stanza tana = new Stanza("Tana", "Un antro buio.", nemico);
        GameEngine partita = new GameEngine(eroe, new Mondo(tana));

        FXMLLoader caricatore = new FXMLLoader(CombattimentoControllerTest.class.getResource(FXML));
        caricatore.setController(
                new CombattimentoController(partita.iniziaCombattimento(), navigazione));
        return caricatore.load();
    }

    /** Un avversario che regge parecchi colpi: lo scontro non finisce subito. */
    private static Nemico goblin() {
        return new Nemico("Goblin", new Statistiche(20, 7, 2), 15);
    }

    /** Un avversario che cade al primo colpo dell'eroe (8 danni su 5 punti vita). */
    private static Nemico moribondo() {
        return new Nemico("Goblin ferito", new Statistiche(5, 7, 2), 15);
    }

    private static String testoDi(Parent radice, String fxId) {
        return ((Label) radice.lookup("#" + fxId)).getText();
    }

    private static Button bottone(Parent radice, String fxId) {
        return (Button) radice.lookup("#" + fxId);
    }

    private static String diario(Parent radice) {
        return ((TextArea) radice.lookup("#diario")).getText();
    }

    @Test
    @DisplayName("all'apertura mostra i due contendenti e non si puo' ancora proseguire")
    void allAperturaMostraIContendenti() throws Exception {
        Parent radice = sulThreadGrafico(
                () -> caricaSchermo(goblin(), 1, new NavigazioneFinta()));

        assertAll(
                () -> assertTrue(testoDi(radice, "statoNemico").contains("Goblin")),
                () -> assertTrue(testoDi(radice, "statoEroe").contains("Eroe")),
                () -> assertTrue(diario(radice).contains("Goblin")),
                () -> assertFalse(bottone(radice, "bottoneAttacca").isDisabled()),
                () -> assertFalse(bottone(radice, "bottonePozione").isDisabled()),
                () -> assertTrue(bottone(radice, "bottoneContinua").isDisabled(),
                        "si prosegue solo a scontro finito"));
    }

    @Test
    @DisplayName("premere Attacca colpisce il nemico e lo racconta nel diario")
    void premereAttaccaColpisceIlNemico() throws Exception {
        Parent radice = sulThreadGrafico(
                () -> caricaSchermo(goblin(), 1, new NavigazioneFinta()));

        sulThreadGrafico(() -> {
            bottone(radice, "bottoneAttacca").fire();
            return null;
        });

        assertAll(
                () -> assertTrue(testoDi(radice, "statoNemico").contains("12/20"), "20 - 8 danni"),
                () -> assertTrue(testoDi(radice, "statoEroe").contains("27/30"), "30 - 3 danni"),
                () -> assertTrue(diario(radice).contains("colpisce")));
    }

    @Test
    @DisplayName("senza pozioni il pulsante Usa pozione e' spento")
    void senzaPozioniIlPulsanteESpento() throws Exception {
        Parent radice = sulThreadGrafico(
                () -> caricaSchermo(goblin(), 0, new NavigazioneFinta()));

        assertTrue(bottone(radice, "bottonePozione").isDisabled());
    }

    @Test
    @DisplayName("usare l'ultima pozione la esaurisce e spegne il pulsante")
    void usareLUltimaPozioneSpegneIlPulsante() throws Exception {
        Parent radice = sulThreadGrafico(
                () -> caricaSchermo(goblin(), 1, new NavigazioneFinta()));

        sulThreadGrafico(() -> {
            bottone(radice, "bottoneAttacca").fire();  // l'eroe incassa 3 danni: 27/30
            bottone(radice, "bottonePozione").fire();  // +12 di cura (max 30), poi altri 3 danni
            return null;
        });

        assertAll(
                () -> assertTrue(testoDi(radice, "statoEroe").contains("27/30")),
                () -> assertTrue(testoDi(radice, "statoEroe").contains("pozioni: 0")),
                () -> assertTrue(bottone(radice, "bottonePozione").isDisabled(),
                        "niente pozioni, niente pulsante"));
    }

    @Test
    @DisplayName("vinto lo scontro si puo' solo proseguire, e si torna nel mondo")
    void vintoLoScontroSiTornaNelMondo() throws Exception {
        NavigazioneFinta navigazione = new NavigazioneFinta();
        Parent radice = sulThreadGrafico(() -> caricaSchermo(moribondo(), 1, navigazione));

        sulThreadGrafico(() -> {
            bottone(radice, "bottoneAttacca").fire(); // un colpo solo e il nemico cade
            return null;
        });

        assertAll(
                () -> assertTrue(testoDi(radice, "esito").contains("vinto")),
                () -> assertTrue(radice.lookup("#esito").getStyleClass().contains("esito-vittoria"),
                        "l'esito deve tingersi del colore della vittoria"),
                () -> assertTrue(bottone(radice, "bottoneAttacca").isDisabled()),
                () -> assertTrue(bottone(radice, "bottonePozione").isDisabled()),
                () -> assertFalse(bottone(radice, "bottoneContinua").isDisabled()));

        sulThreadGrafico(() -> {
            bottone(radice, "bottoneContinua").fire();
            return null;
        });

        assertTrue(navigazione.esplorazioneChiesta);
    }
}
