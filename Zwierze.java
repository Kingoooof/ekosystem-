import java.util.Random;

public abstract class Zwierze extends Organizm {
    private int szybkosc;
    private boolean czyDrapiezny;
    private int x;
    private int y;
    private int daysSinceLastMeal;
    private int iloscPozywienia;

    public Zwierze(String nazwa, char symbol, int szybkosc, boolean czyDrapiezny) {
        super(nazwa, symbol);
        this.szybkosc = szybkosc;
        this.czyDrapiezny = czyDrapiezny;
        this.iloscPozywienia = 0;
    }
    private void zjedz() {
        iloscPozywienia++;
    }
    public void updateLastMeal(){
        daysSinceLastMeal = 0;
        zjedz();
    }
    public abstract boolean death();
    public void incrementDaysSinceLastMeal(){
        daysSinceLastMeal++;
    }
    public Zwierze(String nazwa, char symbol) {
        super(nazwa, symbol);
    }

    public int getSzybkosc() {
        return szybkosc;
    }

    public boolean czyDrapiezny() {
        return czyDrapiezny;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void poruszanieSie(ekosystem ekosystem, int x, int y, Random random) {
        int newX, newY;
        do {
            newX = x + random.nextInt(3) - 1;
            newY = y + random.nextInt(3) - 1;
            // Sprawdź, czy nowe współrzędne nie wychodzą poza granice planszy
            newX = Math.max(0, Math.min(ekosystem.getSzerokosc() - 1, newX));
            newY = Math.max(0, Math.min(ekosystem.getWysokosc() - 1, newY));
        } while (newX < 0 || newX >= ekosystem.getSzerokosc() || newY < 0 || newY >= ekosystem.getWysokosc() || ekosystem.getPlansza()[newY][newX] != null);

        // Sprawdź, czy nowe współrzędne nie są zajęte przez inne zwierzę
        if (ekosystem.getPlansza()[newY][newX] == null) {
            ekosystem.getPlansza()[y][x] = null;
            ekosystem.getPlansza()[newY][newX] = this;
        }
    }

    public int getDaysSinceLastMeal() {
        return daysSinceLastMeal;
    }

    public void setDaysSinceLastMeal(int daysSinceLastMeal) {
        this.daysSinceLastMeal = daysSinceLastMeal;
    }

    public int getIloscPozywienia() {
        return iloscPozywienia;
    }

    public void setIloscPozywienia(int iloscPozywienia) {
        this.iloscPozywienia = iloscPozywienia;
    }
}