package it.unicam.cs.mpgc.rpg123442.model.character;

/**
 * Raggruppa le statistiche base di combattimento di un {@link Combattente}.
 *
 * <p>È un oggetto <b>immutabile</b>: i suoi valori non cambiano dopo la
 * costruzione. La sua unica responsabilita' (principio SRP) e' custodire e
 * validare i valori base; non sa nulla di come si combatte o di chi la usa.
 */
public final class Statistiche {

    private final int vitaMassima;
    private final int attacco;
    private final int difesa;

    /**
     * Crea un set di statistiche, validando i valori.
     *
     * @param vitaMassima punti vita massimi (deve essere > 0)
     * @param attacco     valore d'attacco (non negativo)
     * @param difesa      valore di difesa (non negativo)
     * @throws IllegalArgumentException se i valori non sono validi
     */
    public Statistiche(int vitaMassima, int attacco, int difesa) {
        if (vitaMassima <= 0) {
            throw new IllegalArgumentException("La vita massima deve essere positiva");
        }
        if (attacco < 0 || difesa < 0) {
            throw new IllegalArgumentException("Attacco e difesa non possono essere negativi");
        }
        this.vitaMassima = vitaMassima;
        this.attacco = attacco;
        this.difesa = difesa;
    }

    public int getVitaMassima() {
        return vitaMassima;
    }

    public int getAttacco() {
        return attacco;
    }

    public int getDifesa() {
        return difesa;
    }
}
