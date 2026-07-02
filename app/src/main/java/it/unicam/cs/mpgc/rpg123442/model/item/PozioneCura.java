package it.unicam.cs.mpgc.rpg123442.model.item;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;

/**
 * Un consumabile che ripristina una quantita' fissa di punti vita.
 */
public class PozioneCura implements Consumabile {

    private final int quantitaCura;

    public PozioneCura(int quantitaCura) {
        if (quantitaCura <= 0) {
            throw new IllegalArgumentException("La quantita' di cura deve essere positiva");
        }
        this.quantitaCura = quantitaCura;
    }

    @Override
    public String getNome() {
        return "Pozione di cura";
    }

    @Override
    public String getDescrizione() {
        return "Ripristina " + quantitaCura + " punti vita.";
    }

    @Override
    public void usaSu(Combattente bersaglio) {
        if (bersaglio == null) {
            throw new IllegalArgumentException("Il bersaglio non puo' essere null");
        }
        bersaglio.curati(quantitaCura);
    }

    public int getQuantitaCura() {
        return quantitaCura;
    }
}
