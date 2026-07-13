package it.unicam.cs.mpgc.rpg123442;

import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import it.unicam.cs.mpgc.rpg123442.service.PartitaFactory;
import it.unicam.cs.mpgc.rpg123442.ui.CombattimentoController;
import it.unicam.cs.mpgc.rpg123442.ui.EsplorazioneController;
import it.unicam.cs.mpgc.rpg123442.ui.Navigazione;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Punto di ingresso dell'applicazione: la finestra JavaFX del gioco.
 *
 * <p>Ha due responsabilita', entrambe di <b>montaggio</b>: assemblare la partita
 * (delegando a {@link PartitaFactory}) e mostrare la schermata giusta al momento
 * giusto. Non conosce le regole del gioco (stanno nel motore) ne' com'e' disegnata
 * una schermata (sta nei file FXML).
 *
 * <p>Implementa {@link Navigazione}: sono i controller a chiedere il cambio di
 * schermata, ma senza conoscere questa classe. Loro dicono <i>dove</i> andare,
 * qui si decide <i>come</i> arrivarci.
 */
public class App extends Application implements Navigazione {

    private static final String TITOLO = "RPG a turni";
    private static final String NOME_EROE_DEFAULT = "Eroe";

    /** Le schermate del gioco, dentro le risorse del progetto. */
    private static final String FXML_ESPLORAZIONE =
            "/it/unicam/cs/mpgc/rpg123442/ui/esplorazione.fxml";
    private static final String FXML_COMBATTIMENTO =
            "/it/unicam/cs/mpgc/rpg123442/ui/combattimento.fxml";

    /** La finestra su cui si avvicendano le schermate. */
    private Stage stage;

    /** La partita in corso: nasce con "Nuova Partita" e sopravvive ai cambi di schermata. */
    private GameEngine partita;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle(TITOLO);
        stage.setScene(menuIniziale());
        stage.show();
    }

    /**
     * Costruisce la scena del menu iniziale: titolo del gioco piu' i pulsanti per
     * iniziare o uscire.
     */
    private Scene menuIniziale() {
        Label titolo = new Label(TITOLO);
        titolo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Button nuovaPartita = new Button("Nuova Partita");
        nuovaPartita.setOnAction(e -> iniziaNuovaPartita());

        Button esci = new Button("Esci");
        esci.setOnAction(e -> stage.close());

        VBox radice = new VBox(15, titolo, nuovaPartita, esci);
        radice.setAlignment(Pos.CENTER);
        radice.setPadding(new Insets(40));
        return new Scene(radice, 480, 360);
    }

    /**
     * Crea una partita nuova di zecca e apre il mondo.
     */
    private void iniziaNuovaPartita() {
        this.partita = PartitaFactory.creaNuovaPartita(NOME_EROE_DEFAULT);
        mostraEsplorazione();
    }

    @Override
    public void mostraEsplorazione() {
        mostra(FXML_ESPLORAZIONE, new EsplorazioneController(partita, this));
    }

    @Override
    public void mostraCombattimento() {
        mostra(FXML_COMBATTIMENTO,
                new CombattimentoController(partita.iniziaCombattimento(), this));
    }

    /**
     * Carica una schermata dal suo file FXML, la collega al controller indicato e
     * la porta in scena.
     *
     * <p>Il controller viene creato qui e consegnato gia' pronto al caricatore: per
     * questo i file FXML non dichiarano <code>fx:controller</code>. In cambio ogni
     * controller riceve nel costruttore tutto cio' che gli serve, e non puo' trovarsi
     * a meta' strada.
     *
     * @throws UncheckedIOException se il file FXML manca o e' malformato: sarebbe un
     *         errore di programmazione, non una situazione che l'utente possa
     *         correggere, quindi non ha senso obbligare chi chiama a gestirlo
     */
    private void mostra(String percorsoFxml, Object controller) {
        URL risorsa = App.class.getResource(percorsoFxml);
        if (risorsa == null) {
            throw new IllegalStateException("Schermata non trovata: " + percorsoFxml);
        }
        FXMLLoader caricatore = new FXMLLoader(risorsa);
        caricatore.setController(controller);
        try {
            Parent radice = caricatore.load();
            stage.setScene(new Scene(radice));
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile caricare " + percorsoFxml, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
