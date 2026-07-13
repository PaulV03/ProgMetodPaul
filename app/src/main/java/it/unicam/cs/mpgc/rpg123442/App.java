package it.unicam.cs.mpgc.rpg123442;

import it.unicam.cs.mpgc.rpg123442.repository.GameSaveRepository;
import it.unicam.cs.mpgc.rpg123442.repository.JsonGameSaveRepository;
import it.unicam.cs.mpgc.rpg123442.repository.PersistenzaException;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import it.unicam.cs.mpgc.rpg123442.service.PartitaFactory;
import it.unicam.cs.mpgc.rpg123442.service.StatoPartita;
import it.unicam.cs.mpgc.rpg123442.ui.CombattimentoController;
import it.unicam.cs.mpgc.rpg123442.ui.EsplorazioneController;
import it.unicam.cs.mpgc.rpg123442.ui.FineController;
import it.unicam.cs.mpgc.rpg123442.ui.GestioneSalvataggi;
import it.unicam.cs.mpgc.rpg123442.ui.Navigazione;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

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
public class App extends Application implements Navigazione, GestioneSalvataggi {

    private static final String TITOLO = "RPG a turni";
    private static final String NOME_EROE_DEFAULT = "Eroe";

    /** Come si chiama un salvataggio, se il giocatore non decide altrimenti. */
    private static final String SALVATAGGIO_PREDEFINITO = "partita.json";

    private static final int LARGHEZZA = 560;
    private static final int ALTEZZA = 460;

    /** Le schermate del gioco, dentro le risorse del progetto. */
    private static final String FXML_ESPLORAZIONE =
            "/it/unicam/cs/mpgc/rpg123442/ui/esplorazione.fxml";
    private static final String FXML_COMBATTIMENTO =
            "/it/unicam/cs/mpgc/rpg123442/ui/combattimento.fxml";
    private static final String FXML_FINE =
            "/it/unicam/cs/mpgc/rpg123442/ui/fine.fxml";

    /** L'aspetto di tutte le schermate: colori, caratteri, spaziature. */
    private static final String CSS = "/it/unicam/cs/mpgc/rpg123442/ui/stile.css";

    /** La finestra su cui si avvicendano le schermate. */
    private Stage stage;

    /** La partita in corso: nasce con "Nuova Partita" e sopravvive ai cambi di schermata. */
    private GameEngine partita;

    /**
     * Dove finiscono le partite salvate.
     *
     * <p>Il campo e' dichiarato del <b>tipo dell'interfaccia</b> e non della classe
     * concreta: qui si sa soltanto che una partita si puo' salvare e ricaricare, non
     * che lo si stia facendo in JSON. Passare a un altro formato — XML, un database —
     * significherebbe cambiare questa sola riga (DIP).
     */
    private final GameSaveRepository salvataggi = new JsonGameSaveRepository();

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
        titolo.getStyleClass().add("titolo-gioco");

        Label sottotitolo = new Label("Un dungeon, tre nemici, nessuna via di mezzo");
        sottotitolo.getStyleClass().add("sottotitolo");

        Button nuovaPartita = new Button("Nuova Partita");
        nuovaPartita.setOnAction(e -> iniziaNuovaPartita());
        nuovaPartita.setPrefWidth(200);

        // Si riprende un'avventura anche da qui, senza doverne prima cominciare una.
        Button caricaPartita = new Button("Carica Partita");
        caricaPartita.setOnAction(e -> caricaPartita());
        caricaPartita.setPrefWidth(200);

        Button esci = new Button("Esci");
        esci.setOnAction(e -> stage.close());
        esci.setPrefWidth(200);

        VBox radice = new VBox(15, titolo, sottotitolo, nuovaPartita, caricaPartita, esci);
        radice.getStyleClass().add("schermo");
        radice.setAlignment(Pos.CENTER);
        radice.setPadding(new Insets(40));
        return scenaCon(radice);
    }

    /**
     * Mette la schermata indicata dentro una scena, vestita con il foglio di stile
     * del gioco.
     *
     * <p>Il CSS viene applicato qui, in un punto solo: nessun controller deve
     * ricordarsi di caricarlo, e nessuna schermata puo' finire in scena "svestita".
     */
    private Scene scenaCon(Parent radice) {
        Scene scena = new Scene(radice, LARGHEZZA, ALTEZZA);
        URL foglioDiStile = App.class.getResource(CSS);
        if (foglioDiStile == null) {
            throw new IllegalStateException("Foglio di stile non trovato: " + CSS);
        }
        scena.getStylesheets().add(foglioDiStile.toExternalForm());
        return scena;
    }

    /**
     * Crea una partita nuova di zecca e apre il mondo.
     */
    private void iniziaNuovaPartita() {
        this.partita = PartitaFactory.creaNuovaPartita(NOME_EROE_DEFAULT);
        mostraEsplorazione();
    }

    /**
     * Riporta il giocatore nel mondo — <b>se</b> c'e' ancora una partita da giocare.
     *
     * <p>Qui sta l'unico controllo di fine partita del gioco. Chiunque voglia
     * tornare a esplorare passa di qua (il menu quando comincia, lo scontro quando
     * finisce), quindi e' il punto in cui la domanda "e' finita?" va fatta una volta
     * sola: nessun controller deve ricordarsi di porsela, e nessuno puo'
     * dimenticarsene. I controller chiedono di andare nel mondo; se il mondo non ha
     * piu' niente da offrire — l'eroe e' caduto, oppure il dungeon e' ripulito — e'
     * l'applicazione a dirottarli sulla schermata di fine.
     */
    @Override
    public void mostraEsplorazione() {
        if (partita.statoPartita() != StatoPartita.IN_CORSO) {
            mostra(FXML_FINE, new FineController(partita, this));
            return;
        }
        mostra(FXML_ESPLORAZIONE, new EsplorazioneController(partita, this, this));
    }

    @Override
    public void mostraCombattimento() {
        mostra(FXML_COMBATTIMENTO,
                new CombattimentoController(partita.iniziaCombattimento(), this));
    }

    /**
     * Torna al menu iniziale e lascia andare la partita: quella finita e' storia,
     * la prossima nascera' nuova da {@link PartitaFactory}.
     */
    @Override
    public void mostraMenuIniziale() {
        this.partita = null;
        stage.setScene(menuIniziale());
    }

    /**
     * Scrive la partita in corso nel file scelto dal giocatore.
     *
     * <p>Se il dialogo viene annullato non succede niente: e' una scelta legittima,
     * non un errore. Se invece la scrittura fallisce (permessi, disco pieno, percorso
     * inesistente) il repository solleva {@link PersistenzaException} e la si mostra
     * come un avviso: un salvataggio non riuscito non e' un buon motivo per far
     * chiudere il gioco e perdere la partita.
     */
    @Override
    public void salvaPartita() {
        scegliFile("Salva la partita", true).ifPresent(percorso -> {
            try {
                salvataggi.salva(partita, percorso);
                avviso(Alert.AlertType.INFORMATION, "Partita salvata in:\n" + percorso);
            } catch (PersistenzaException fallita) {
                avviso(Alert.AlertType.ERROR, "Non e' stato possibile salvare.\n"
                        + fallita.getMessage());
            }
        });
    }

    /**
     * Riprende una partita da un salvataggio, sostituendo quella eventualmente in
     * corso, e riapre il mondo dove il giocatore l'aveva lasciato.
     *
     * <p>Se il file non e' un salvataggio valido, la partita corrente resta intatta:
     * si assegna il campo <b>solo dopo</b> che il caricamento e' riuscito.
     */
    @Override
    public void caricaPartita() {
        scegliFile("Carica una partita", false).ifPresent(percorso -> {
            try {
                this.partita = salvataggi.carica(percorso);
                mostraEsplorazione();
            } catch (PersistenzaException fallita) {
                avviso(Alert.AlertType.ERROR, "Non e' stato possibile caricare.\n"
                        + fallita.getMessage());
            }
        });
    }

    /**
     * Apre il dialogo dei file e restituisce il percorso scelto.
     *
     * @param titolo      l'intestazione della finestra
     * @param inScrittura vero per scegliere dove scrivere, falso per scegliere cosa leggere
     * @return il percorso scelto, oppure {@link Optional#empty()} se il giocatore
     *         ha annullato: annullare e' una risposta, non un guasto
     */
    private Optional<Path> scegliFile(String titolo, boolean inScrittura) {
        FileChooser dialogo = new FileChooser();
        dialogo.setTitle(titolo);
        dialogo.setInitialFileName(SALVATAGGIO_PREDEFINITO);
        dialogo.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Partite salvate (*.json)", "*.json"));

        File scelto = inScrittura
                ? dialogo.showSaveDialog(stage)
                : dialogo.showOpenDialog(stage);
        return Optional.ofNullable(scelto).map(File::toPath);
    }

    /** Mostra un messaggio al giocatore e attende che lo abbia letto. */
    private void avviso(Alert.AlertType tipo, String messaggio) {
        Alert finestra = new Alert(tipo, messaggio);
        finestra.initOwner(stage);
        finestra.setHeaderText(null);
        finestra.showAndWait();
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
            stage.setScene(scenaCon(caricatore.load()));
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile caricare " + percorsoFxml, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
