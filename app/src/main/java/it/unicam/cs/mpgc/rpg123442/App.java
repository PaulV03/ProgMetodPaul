package it.unicam.cs.mpgc.rpg123442;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import it.unicam.cs.mpgc.rpg123442.service.MondoFactory;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione: la finestra JavaFX del gioco.
 *
 * <p>Per ora mostra un semplice <b>menu iniziale</b>. Il pulsante "Nuova Partita"
 * assembla una partita ({@link MondoFactory} per il mondo, un eroe di default) e
 * mostra la stanza di partenza: e' la prova che tutta la catena
 * factory &rarr; {@link GameEngine} &rarr; interfaccia grafica funziona.
 *
 * <p>Gli schermi veri di esplorazione e combattimento verranno aggiunti nei passi
 * successivi (con FXML), senza dover riscrivere questa impalcatura.
 */
public class App extends Application {

    private static final String TITOLO = "RPG a turni";

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
     * Avvia una nuova partita e mostra, per ora, solo la stanza iniziale.
     * E' un segnaposto: al prossimo passo diventera' lo schermo di esplorazione
     * con i pulsanti di movimento collegati a {@link GameEngine#muovi}.
     */
    private Scene schermoPartita() {
        Eroe eroe = new Eroe("Eroe", new Statistiche(30, 10, 4));
        GameEngine partita = new GameEngine(eroe, MondoFactory.creaMondoDiDefault());

        Label dove = new Label("Sei in: " + partita.getStanzaCorrente().getNome());
        dove.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label descrizione = new Label(partita.getStanzaCorrente().getDescrizione());
        descrizione.setWrapText(true);

        VBox radice = new VBox(15, dove, descrizione);
        radice.setAlignment(Pos.CENTER);
        radice.setPadding(new Insets(40));
        return new Scene(radice, 480, 360);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
