package it.unicam.cs.mpgc.rpg123442;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import it.unicam.cs.mpgc.rpg123442.service.MondoFactory;
import it.unicam.cs.mpgc.rpg123442.ui.EsplorazioneController;
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
 * <p>Mostra un <b>menu iniziale</b>; il pulsante "Nuova Partita" assembla una
 * partita ({@link MondoFactory} per il mondo, un eroe di default) e apre lo
 * schermo di esplorazione descritto in <code>esplorazione.fxml</code>.
 *
 * <p>La sua responsabilita' e' quella di <b>montatore</b>: costruisce gli oggetti
 * del dominio e li consegna alla schermata giusta. Non conosce le regole del
 * gioco (stanno nel motore) ne' come e' disegnata una schermata (sta nell'FXML).
 */
public class App extends Application {

    private static final String TITOLO = "RPG a turni";

    /** Percorso dello schermo di esplorazione, dentro le risorse del progetto. */
    private static final String FXML_ESPLORAZIONE =
            "/it/unicam/cs/mpgc/rpg123442/ui/esplorazione.fxml";

    @Override
    public void start(Stage stage) {
        stage.setTitle(TITOLO);
        stage.setScene(menuIniziale(stage));
        stage.show();
    }

    /**
     * Costruisce la scena del menu iniziale: titolo del gioco piu' i pulsanti per
     * iniziare o uscire.
     */
    private Scene menuIniziale(Stage stage) {
        Label titolo = new Label(TITOLO);
        titolo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Button nuovaPartita = new Button("Nuova Partita");
        nuovaPartita.setOnAction(e -> stage.setScene(schermoPartita()));

        Button esci = new Button("Esci");
        esci.setOnAction(e -> stage.close());

        VBox radice = new VBox(15, titolo, nuovaPartita, esci);
        radice.setAlignment(Pos.CENTER);
        radice.setPadding(new Insets(40));
        return new Scene(radice, 480, 360);
    }

    /**
     * Avvia una nuova partita e ne apre lo schermo di esplorazione.
     */
    private Scene schermoPartita() {
        Eroe eroe = new Eroe("Eroe", new Statistiche(30, 10, 4));
        GameEngine partita = new GameEngine(eroe, MondoFactory.creaMondoDiDefault());
        return new Scene(caricaEsplorazione(partita));
    }

    /**
     * Carica lo schermo di esplorazione collegandolo alla partita indicata.
     *
     * <p>Il controller viene creato qui e passato al caricatore gia' pronto:
     * per questo l'FXML non dichiara <code>fx:controller</code>. In cambio il
     * controller riceve la partita nel costruttore e non puo' trovarsi senza.
     *
     * @throws UncheckedIOException se il file FXML manca o e' malformato: sarebbe
     *         un errore di programmazione, non una situazione che l'utente possa
     *         correggere, quindi non ha senso obbligare chi chiama a gestirlo
     */
    private Parent caricaEsplorazione(GameEngine partita) {
        URL risorsa = App.class.getResource(FXML_ESPLORAZIONE);
        if (risorsa == null) {
            throw new IllegalStateException("Schermata non trovata: " + FXML_ESPLORAZIONE);
        }
        FXMLLoader caricatore = new FXMLLoader(risorsa);
        caricatore.setController(new EsplorazioneController(partita));
        try {
            return caricatore.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile caricare " + FXML_ESPLORAZIONE, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
