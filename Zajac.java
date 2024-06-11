public class Zajac extends Zwierze {
    private static final int szybkosc = 1;

    public Zajac(String nazwa, char symbol) {
        super(nazwa, symbol, szybkosc, false); // Roślinożerca
    }

    @Override
    public boolean death() {
        return getDaysSinceLastMeal() > 30;
    }
}
