public class Wilk extends Zwierze {
    public Wilk(String nazwa, char symbol, int szybkosc) {
        super(nazwa, symbol, szybkosc, true); // Drapieżnik
    }

    @Override
    public boolean death() {
        return getDaysSinceLastMeal() > 60;
    }
}