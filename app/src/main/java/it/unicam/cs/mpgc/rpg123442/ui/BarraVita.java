package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;
import javafx.scene.control.ProgressBar;

/**
 * Disegna i punti vita di un combattente come barra colorata.
 *
 * <p>Serve a entrambe le schermate (l'eroe nell'esplorazione, i due contendenti
 * nello scontro): la regola "verde quando sta bene, oro a meta', sangue agli
 * sgoccioli" vive percio' qui, in un punto solo, invece di essere ricopiata in
 * ogni controller che mostra una barra.
 *
 * <p>Il colore vero e proprio non e' scritto qui: questa classe si limita a dire
 * <i>in che stato</i> sia la barra applicandole una classe CSS; che aspetto abbia
 * quello stato lo decide <code>stile.css</code>.
 */
final class BarraVita {

    /** Sotto queste frazioni di vita la barra cambia colore. */
    private static final double SOGLIA_META = 0.5;
    private static final double SOGLIA_CRITICA = 0.25;

    private static final String CLASSE_MEDIA = "barra-vita-media";
    private static final String CLASSE_BASSA = "barra-vita-bassa";

    private BarraVita() {
        // classe di utilita': non deve essere istanziata
    }

    /**
     * Porta la barra allo stato attuale del combattente: lunghezza proporzionale
     * ai punti vita rimasti, colore scelto in base a quanto siano pochi.
     *
     * @param barra        la barra da aggiornare
     * @param combattente  il personaggio di cui mostrare la vita
     */
    static void aggiorna(ProgressBar barra, Combattente combattente) {
        double frazione = (double) combattente.getVitaCorrente()
                / combattente.getStatistiche().getVitaMassima();
        barra.setProgress(frazione);

        // Si riparte sempre dal colore pieno, cosi' una barra che risale (una pozione)
        // torna verde invece di restare rossa.
        barra.getStyleClass().removeAll(CLASSE_MEDIA, CLASSE_BASSA);
        if (frazione <= SOGLIA_CRITICA) {
            barra.getStyleClass().add(CLASSE_BASSA);
        } else if (frazione <= SOGLIA_META) {
            barra.getStyleClass().add(CLASSE_MEDIA);
        }
    }
}
