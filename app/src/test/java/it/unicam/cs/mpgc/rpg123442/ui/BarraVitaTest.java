package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * La barra della vita deve raccontare a colpo d'occhio come sta un combattente:
 * lunghezza proporzionale ai punti vita e colore che peggiora quando ne restano
 * pochi. Il colore vero e' scelto dal CSS, qui si verifica che venga chiesta la
 * classe giusta.
 */
@DisplayName("BarraVita")
class BarraVitaTest {

    @BeforeAll
    static void avviaToolkitGrafico() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException giaAvviato) {
            // Il toolkit sopravvive fra una classe di test e l'altra: va bene cosi'.
        }
        Platform.setImplicitExit(false);
    }

    /** Un eroe da 100 punti vita: cosi' i danni si leggono come percentuali. */
    private Eroe eroeCon(int danniSubiti) {
        Eroe eroe = new Eroe("Eroe", new Statistiche(100, 10, 0));
        eroe.subisciDanno(danniSubiti);
        return eroe;
    }

    @Test
    @DisplayName("in salute: barra piena, nessuna classe di allarme")
    void inSaluteNessunAllarme() {
        ProgressBar barra = new ProgressBar();
        BarraVita.aggiorna(barra, eroeCon(0));

        assertAll(
                () -> assertEquals(1.0, barra.getProgress()),
                () -> assertFalse(barra.getStyleClass().contains("barra-vita-media")),
                () -> assertFalse(barra.getStyleClass().contains("barra-vita-bassa")));
    }

    @Test
    @DisplayName("a meta' vita la barra passa al colore intermedio")
    void aMetaVitaColoreIntermedio() {
        ProgressBar barra = new ProgressBar();
        BarraVita.aggiorna(barra, eroeCon(60)); // 40 PV su 100

        assertAll(
                () -> assertEquals(0.4, barra.getProgress()),
                () -> assertTrue(barra.getStyleClass().contains("barra-vita-media")),
                () -> assertFalse(barra.getStyleClass().contains("barra-vita-bassa")));
    }

    @Test
    @DisplayName("agli sgoccioli la barra passa al colore critico")
    void agliSgoccioliColoreCritico() {
        ProgressBar barra = new ProgressBar();
        BarraVita.aggiorna(barra, eroeCon(85)); // 15 PV su 100

        assertTrue(barra.getStyleClass().contains("barra-vita-bassa"));
    }

    @Test
    @DisplayName("una barra che risale torna al colore pieno")
    void unaBarraCheRisaleTornaVerde() {
        ProgressBar barra = new ProgressBar();
        Eroe eroe = eroeCon(85);
        BarraVita.aggiorna(barra, eroe); // rossa

        eroe.curati(85); // beve una pozione e torna in forze
        BarraVita.aggiorna(barra, eroe);

        assertAll(
                () -> assertEquals(1.0, barra.getProgress()),
                () -> assertFalse(barra.getStyleClass().contains("barra-vita-bassa"),
                        "il vecchio colore non deve restare appiccicato"),
                () -> assertFalse(barra.getStyleClass().contains("barra-vita-media")));
    }
}
