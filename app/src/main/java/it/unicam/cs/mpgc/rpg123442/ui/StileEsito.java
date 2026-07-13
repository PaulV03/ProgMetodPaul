package it.unicam.cs.mpgc.rpg123442.ui;

import javafx.scene.control.Label;

/**
 * Tinge un'etichetta del colore di un esito: verde se si e' vinto, rosso se si e'
 * perso.
 *
 * <p>Esiste perche' due schermate hanno lo stesso bisogno — la fine di uno scontro
 * e la fine della partita — e la regola "come si colora un esito" deve stare in un
 * punto solo. Se domani la vittoria si volesse anche in grassetto, si cambia qui.
 *
 * <p>Nota: qui si scelgono le <b>classi CSS</b>, non i colori. Quale verde e quale
 * rosso lo decide <code>stile.css</code>: la palette resta un fatto di aspetto e non
 * entra nel codice Java.
 */
final class StileEsito {

    private static final String VITTORIA = "esito-vittoria";
    private static final String SCONFITTA = "esito-sconfitta";

    /** Classe di sole utilita': non ha senso costruirne un'istanza. */
    private StileEsito() {
    }

    /**
     * Colora l'etichetta secondo l'esito, togliendo prima l'eventuale colore
     * precedente: un'etichetta non puo' essere insieme vittoriosa e sconfitta.
     *
     * @param etichetta l'etichetta da tingere (non null)
     * @param vittoria  vero se l'esito da mostrare e' una vittoria
     */
    static void applica(Label etichetta, boolean vittoria) {
        pulisci(etichetta);
        etichetta.getStyleClass().add(vittoria ? VITTORIA : SCONFITTA);
    }

    /**
     * Toglie all'etichetta ogni colore d'esito, riportandola neutra.
     *
     * <p>Serve dove l'esito non c'e' ancora: uno scontro appena cominciato non e'
     * ne' vinto ne' perso.
     */
    static void pulisci(Label etichetta) {
        etichetta.getStyleClass().removeAll(VITTORIA, SCONFITTA);
    }
}
