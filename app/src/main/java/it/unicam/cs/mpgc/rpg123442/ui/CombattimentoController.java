package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.combat.RisultatoTurno;
import it.unicam.cs.mpgc.rpg123442.model.item.Consumabile;
import it.unicam.cs.mpgc.rpg123442.service.SessioneCombattimento;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.Optional;

/**
 * Controller dello schermo di combattimento (<code>combattimento.fxml</code>).
 *
 * <p>Traduce i click del giocatore in azioni sulla {@link SessioneCombattimento} e
 * racconta a schermo i turni che ne risultano. Come il controller di esplorazione,
 * non contiene regole di gioco: quanto danno faccia un colpo, se il nemico
 * risponda, chi vinca lo decide il motore. Qui si decide solo <i>cosa mostrare</i>.
 *
 * <p>Lo scontro e la navigazione arrivano dal <b>costruttore</b>: il controller non
 * puo' esistere a meta'.
 */
public class CombattimentoController {

    private final SessioneCombattimento scontro;
    private final Navigazione navigazione;

    @FXML private Label titolo;
    @FXML private Label statoNemico;
    @FXML private Label statoEroe;
    @FXML private Label esito;
    @FXML private TextArea diario;

    @FXML private ProgressBar barraNemico;
    @FXML private ProgressBar barraEroe;

    @FXML private Button bottoneAttacca;
    @FXML private Button bottonePozione;
    @FXML private Button bottoneContinua;

    /**
     * @param scontro     lo scontro in corso da giocare (non null)
     * @param navigazione a chi chiedere il cambio di schermata a scontro finito (non null)
     */
    public CombattimentoController(SessioneCombattimento scontro, Navigazione navigazione) {
        if (scontro == null || navigazione == null) {
            throw new IllegalArgumentException("Scontro e navigazione non possono essere null");
        }
        this.scontro = scontro;
        this.navigazione = navigazione;
    }

    /**
     * Chiamato da JavaFX quando i campi annotati con {@link FXML} sono stati
     * iniettati: e' il momento in cui la schermata puo' essere disegnata.
     */
    @FXML
    private void initialize() {
        titolo.setText("Scontro!");
        diario.setText("Ti sbarra la strada un " + scontro.getNemico().getNome() + ".\n");
        aggiornaVista();
    }

    /**
     * L'eroe attacca; il nemico, se sopravvive, risponde.
     */
    @FXML
    private void attacca() {
        racconta(scontro.attacca());
        aggiornaVista();
    }

    /**
     * L'eroe beve una pozione: recupera vita ma cede il turno al nemico.
     *
     * <p>Il pulsante e' spento quando non ci sono consumabili, quindi qui la
     * pozione c'e'; l'{@link Optional} evita comunque di dare per scontato lo
     * stato dell'inventario.
     */
    @FXML
    private void usaPozione() {
        primoConsumabile().ifPresent(consumabile -> {
            diario.appendText(scontro.getEroe().getNome() + " usa: " + consumabile.getNome() + ".\n");
            racconta(scontro.usaConsumabile(consumabile));
        });
        aggiornaVista();
    }

    /**
     * Chiude la schermata dello scontro e riporta il giocatore nel mondo.
     */
    @FXML
    private void continua() {
        navigazione.mostraEsplorazione();
    }

    /**
     * Aggiunge al diario una riga per ogni turno giocato.
     *
     * <p>Il motore restituisce {@link RisultatoTurno}, cioe' i <i>fatti</i> (chi ha
     * colpito chi, per quanto); metterli in parole spetta a questa schermata.
     */
    private void racconta(List<RisultatoTurno> turni) {
        for (RisultatoTurno turno : turni) {
            diario.appendText(turno.attaccante().getNome()
                    + " colpisce " + turno.difensore().getNome()
                    + " per " + turno.danno() + " danni.\n");
            if (turno.difensoreSconfitto()) {
                diario.appendText(turno.difensore().getNome() + " cade a terra.\n");
            }
        }
    }

    /**
     * Riporta a schermo tutto cio' che dipende dallo stato dello scontro: le due
     * schede dei combattenti, l'esito e quali azioni siano ancora possibili.
     */
    private void aggiornaVista() {
        statoNemico.setText(descrivi(scontro.getNemico()));
        statoEroe.setText(descriviEroe(scontro.getEroe()));
        esito.setText(descriviEsito());

        BarraVita.aggiorna(barraNemico, scontro.getNemico());
        BarraVita.aggiorna(barraEroe, scontro.getEroe());

        boolean finito = scontro.isFinito();
        bottoneAttacca.setDisable(finito);
        bottonePozione.setDisable(finito || primoConsumabile().isEmpty());
        bottoneContinua.setDisable(!finito); // si prosegue solo a scontro concluso
    }

    private String descriviEsito() {
        if (!scontro.isFinito()) {
            return "";
        }
        return scontro.eroeVincitore()
                ? "Hai vinto! " + scontro.getNemico().getEsperienzaRilasciata() + " punti esperienza."
                : "Sei stato sconfitto...";
    }

    /**
     * @return il primo oggetto utilizzabile in combattimento fra quelli posseduti.
     *         Il filtro guarda l'astrazione {@link Consumabile}, non la pozione di
     *         cura: un consumabile nuovo comparirebbe qui senza modifiche.
     */
    private Optional<Consumabile> primoConsumabile() {
        return scontro.getEroe().getInventario().getOggetti().stream()
                .filter(Consumabile.class::isInstance)
                .map(Consumabile.class::cast)
                .findFirst();
    }

    private String descrivi(Nemico nemico) {
        return nemico.getNome()
                + " - PV " + nemico.getVitaCorrente() + "/" + nemico.getStatistiche().getVitaMassima();
    }

    private String descriviEroe(Eroe eroe) {
        long consumabili = eroe.getInventario().getOggetti().stream()
                .filter(Consumabile.class::isInstance)
                .count();
        return eroe.getNome()
                + " - livello " + eroe.getLivello()
                + " - PV " + eroe.getVitaCorrente() + "/" + eroe.getStatistiche().getVitaMassima()
                + " - pozioni: " + consumabili;
    }
}
