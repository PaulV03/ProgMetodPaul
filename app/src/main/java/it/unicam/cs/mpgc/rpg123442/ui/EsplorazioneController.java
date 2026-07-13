package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.EnumMap;
import java.util.Map;

/**
 * Controller dello schermo di esplorazione (<code>esplorazione.fxml</code>).
 *
 * <p>Fa da <b>ponte</b> fra l'interfaccia grafica e il dominio: traduce i click
 * dell'utente in chiamate al {@link GameEngine} e riporta a schermo lo stato
 * risultante. Non contiene regole di gioco: decidere se un movimento e' lecito
 * spetta al motore, non a questa classe.
 *
 * <p>La partita arriva dal <b>costruttore</b> e non da un setter: cosi' il
 * controller non puo' mai esistere senza la partita che deve mostrare. Per
 * questo il file FXML non dichiara <code>fx:controller</code> ed e' chi carica
 * la schermata a fornire l'istanza gia' pronta.
 */
public class EsplorazioneController {

    private final GameEngine partita;
    private final Navigazione navigazione;
    private final GestioneSalvataggi salvataggi;

    @FXML private Label nomeStanza;
    @FXML private Label descrizioneStanza;
    @FXML private Label statoNemico;
    @FXML private Label statoEroe;
    @FXML private Label messaggio;

    @FXML private ProgressBar barraEroe;

    @FXML private Button bottoneNord;
    @FXML private Button bottoneSud;
    @FXML private Button bottoneEst;
    @FXML private Button bottoneOvest;
    @FXML private Button bottoneCombatti;

    /** Associa ogni direzione al pulsante che la comanda: evita quattro casi ripetuti. */
    private Map<Direzione, Button> bottoniPerDirezione;

    /**
     * @param partita     la partita da mostrare e comandare (non null)
     * @param navigazione a chi chiedere l'apertura di un'altra schermata (non null)
     * @param salvataggi  a chi chiedere di salvare o riprendere una partita (non null)
     */
    public EsplorazioneController(GameEngine partita,
                                  Navigazione navigazione,
                                  GestioneSalvataggi salvataggi) {
        if (partita == null || navigazione == null || salvataggi == null) {
            throw new IllegalArgumentException(
                    "Partita, navigazione e salvataggi non possono essere null");
        }
        this.partita = partita;
        this.navigazione = navigazione;
        this.salvataggi = salvataggi;
    }

    /**
     * Chiamato automaticamente da JavaFX dopo aver iniettato i campi annotati
     * con {@link FXML}. Prima di questo momento i pulsanti e le etichette sono
     * ancora null: e' qui, e non nel costruttore, che si disegna la prima volta.
     */
    @FXML
    private void initialize() {
        bottoniPerDirezione = new EnumMap<>(Direzione.class);
        bottoniPerDirezione.put(Direzione.NORD, bottoneNord);
        bottoniPerDirezione.put(Direzione.SUD, bottoneSud);
        bottoniPerDirezione.put(Direzione.EST, bottoneEst);
        bottoniPerDirezione.put(Direzione.OVEST, bottoneOvest);

        messaggio.setText("L'avventura ha inizio.");
        aggiornaVista();
    }

    @FXML private void vaiNord() { muoviVerso(Direzione.NORD); }
    @FXML private void vaiSud() { muoviVerso(Direzione.SUD); }
    @FXML private void vaiEst() { muoviVerso(Direzione.EST); }
    @FXML private void vaiOvest() { muoviVerso(Direzione.OVEST); }

    /**
     * Affronta il nemico della stanza corrente, aprendo lo schermo di combattimento.
     *
     * <p>Il pulsante e' abilitato solo dove c'e' un nemico, quindi non serve un
     * controllo qui: e' {@link #aggiornaVista()} a garantire che questa azione sia
     * proponibile solo quando ha senso.
     */
    @FXML
    private void combatti() {
        navigazione.mostraCombattimento();
    }

    /**
     * Mette da parte la partita.
     *
     * <p>Questo controller non sa dove finisca il file ne' in che formato: apre un
     * dialogo, scrive il JSON e riferisce l'eventuale errore chi implementa
     * {@link GestioneSalvataggi}. Qui c'e' solo la richiesta.
     */
    @FXML
    private void salva() {
        salvataggi.salvaPartita();
    }

    /**
     * Riprende una partita salvata, abbandonando quella in corso.
     *
     * <p>La partita caricata e' un'altra: non basta ridisegnare questa schermata,
     * va riaperta su quella nuova. Se ne occupa chi implementa
     * {@link GestioneSalvataggi}, che la partita corrente ce l'ha in mano.
     */
    @FXML
    private void carica() {
        salvataggi.caricaPartita();
    }

    /**
     * Tenta lo spostamento e aggiorna la schermata di conseguenza.
     *
     * <p>I quattro gestori dei pulsanti si limitano a indicare la direzione:
     * la logica sta tutta qui, in un punto solo.
     */
    private void muoviVerso(Direzione direzione) {
        boolean spostato = partita.muovi(direzione);
        messaggio.setText(spostato
                ? "Ti muovi verso " + nomeLeggibile(direzione) + "."
                : "Verso " + nomeLeggibile(direzione) + " c'e' solo un muro.");
        aggiornaVista();
    }

    /**
     * Riporta a schermo lo stato corrente della partita.
     *
     * <p>Un unico metodo ridisegna <b>tutto</b> cio' che dipende dallo stato:
     * ogni azione deve solo ricordarsi di chiamarlo, senza sapere quali
     * etichette abbia toccato. Meno occasioni di lasciare la vista incoerente.
     */
    private void aggiornaVista() {
        Stanza stanza = partita.getStanzaCorrente();
        nomeStanza.setText("Sei in: " + stanza.getNome());
        descrizioneStanza.setText(stanza.getDescrizione());
        statoNemico.setText(descriviNemico(stanza));
        statoEroe.setText(descriviEroe(partita.getEroe()));
        BarraVita.aggiorna(barraEroe, partita.getEroe());

        // Un pulsante e' attivo solo se dalla stanza corrente si esce da quella parte.
        bottoniPerDirezione.forEach(
                (direzione, bottone) -> bottone.setDisable(!stanza.haUscita(direzione)));

        // Si combatte solo dove c'e' qualcuno da combattere.
        bottoneCombatti.setDisable(!stanza.haNemico());
    }

    private String descriviNemico(Stanza stanza) {
        return stanza.getNemico()
                .map(this::descriviNemico)
                .orElse("La stanza e' tranquilla.");
    }

    private String descriviNemico(Nemico nemico) {
        return "Ti sbarra la strada un " + nemico.getNome()
                + " (" + nemico.getVitaCorrente() + " PV).";
    }

    private String descriviEroe(Eroe eroe) {
        return eroe.getNome()
                + " - livello " + eroe.getLivello()
                + " - PV " + eroe.getVitaCorrente() + "/" + eroe.getStatistiche().getVitaMassima();
    }

    /** Il nome della direzione in forma leggibile, per i messaggi a schermo. */
    private String nomeLeggibile(Direzione direzione) {
        String nome = direzione.name();
        return nome.charAt(0) + nome.substring(1).toLowerCase();
    }
}
