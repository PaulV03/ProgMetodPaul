package it.unicam.cs.mpgc.rpg123442.model.world;

/**
 * Le direzioni di movimento fra una stanza e l'altra.
 *
 * <p>E' un <b>enum</b> perche' i valori possibili sono un insieme chiuso e noto
 * in anticipo: un movimento puo' essere solo verso nord, sud, est o ovest. Usare
 * un tipo dedicato (invece di una stringa come "nord") rende impossibile scrivere
 * una direzione sbagliata: l'errore verrebbe segnalato dal compilatore e non a
 * gioco avviato.
 *
 * <p>Ogni direzione conosce il proprio {@link #opposta() opposto}: cosi' quando
 * colleghiamo due stanze possiamo creare automaticamente anche il passaggio di
 * ritorno (se dalla stanza A vado a NORD nella stanza B, da B torno ad A a SUD).
 */
public enum Direzione {

    NORD,
    SUD,
    EST,
    OVEST;

    /**
     * @return la direzione opposta a questa (NORD&lt;-&gt;SUD, EST&lt;-&gt;OVEST)
     */
    public Direzione opposta() {
        return switch (this) {
            case NORD -> SUD;
            case SUD -> NORD;
            case EST -> OVEST;
            case OVEST -> EST;
        };
    }
}
