public class Sarna extends Zwierze {

    private static final int szybkosc = 1;

    public Sarna(String nazwa, char symbol) {
        super(nazwa, symbol, szybkosc, false); // Roślinożerca
    }

    @Override
    public boolean death() {
        return getDaysSinceLastMeal() > 30;
    }
}
