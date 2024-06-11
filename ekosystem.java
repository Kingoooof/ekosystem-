import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ekosystem extends JPanel implements ActionListener {
    private final int szerokosc;
    private final int wysokosc;
    private Organizm[][] plansza;
    private List<roslina> rosliny;
    private List<Zwierze> zwierzeta;
    private static Random random;
    private Timer timer;
    private JTextField dataField;
    private Calendar calendar;
    private int dniSymulacji = 0;

    private JLabel lisLabel;
    private JLabel wilkLabel;
    private JLabel sarnaLabel;
    private JLabel zajacLabel;
    private JLabel roslinaLabel;
    private JLabel dataLabel;

    public ekosystem(int szerokosc, int wysokosc, Calendar startDate) {
        this.szerokosc = szerokosc;
        this.wysokosc = wysokosc;
        this.plansza = new Organizm[wysokosc][szerokosc];
        this.rosliny = new ArrayList<roslina>();
        this.zwierzeta = new ArrayList<>();
        random = new Random();
        this.timer = new Timer(700, this);
        this.timer.start();
        this.calendar = startDate;

        dataLabel = new JLabel("Data:");
        lisLabel = new JLabel("Lisy: 0");
        wilkLabel = new JLabel("Wilki: 0");
        sarnaLabel = new JLabel("Sarny: 0");
        zajacLabel = new JLabel("Zające: 0");
        roslinaLabel = new JLabel("Rośliny: 0");

        dataField = new JTextField(10);
        dataField.setEditable(false);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(7, 1));
        sidePanel.add(lisLabel);
        sidePanel.add(wilkLabel);
        sidePanel.add(sarnaLabel);
        sidePanel.add(zajacLabel);
        sidePanel.add(roslinaLabel);
        sidePanel.add(dataLabel);
        sidePanel.add(dataField);

        setLayout(new BorderLayout());
        add(sidePanel, BorderLayout.SOUTH);
    }

    public int getSzerokosc() {
        return szerokosc;
    }

    public int getWysokosc() {
        return wysokosc;
    }

    public Organizm[][] getPlansza() {
        return plansza;
    }

    public void dodajRoslineNaWolneMiejsce(roslina roslina) {
        int x, y;
        do {
            x = random.nextInt(szerokosc);
            y = random.nextInt(wysokosc);
        } while (plansza[y][x] != null);

        plansza[y][x] = roslina;
        rosliny.add(roslina);
        updateLabels();
    }

    public void dodajZwierze(Zwierze zwierze) {
        int x, y;
        do {
            x = random.nextInt(szerokosc);
            y = random.nextInt(wysokosc);
        } while (plansza[y][x] != null);

        plansza[y][x] = zwierze;
        zwierzeta.add(zwierze);
        updateLabels();
    }

    public void updateLabels() {
        int lisCount = 0;
        int wilkCount = 0;
        int sarnaCount = 0;
        int zajacCount = 0;
        int roslinaCount = rosliny.size();

        for (Zwierze zwierze : zwierzeta) {
            if (zwierze instanceof Lis) lisCount++;
            else if (zwierze instanceof Wilk) wilkCount++;
            else if (zwierze instanceof Sarna) sarnaCount++;
            else if (zwierze instanceof Zajac) zajacCount++;
        }

        lisLabel.setText("Lisy: " + lisCount);
        wilkLabel.setText("Wilki: " + wilkCount);
        sarnaLabel.setText("Sarny: " + sarnaCount);
        zajacLabel.setText("Zające: " + zajacCount);
        roslinaLabel.setText("Rośliny: " + roslinaCount);
    }

    public void wyswietlPlansze(Graphics g) {
        for (int i = 0; i < wysokosc; i++) {
            for (int j = 0; j < szerokosc; j++) {
                if (plansza[i][j] != null) {
                    g.drawString(String.valueOf(plansza[i][j].symbol), j * 20, i * 20 + 15);
                } else {
                    g.drawString(".", j * 20, i * 20 + 15);
                }
            }
        }
    }

    public void symulujKolejnaEpoche() {
        ArrayList<Zwierze> doUsuniecia = new ArrayList<>();
        ArrayList<Zwierze> doDodania = new ArrayList<>();
        przesunZwierzeta();

        for (Zwierze zwierze : zwierzeta) {
            int x = -1, y = -1;
            outerloop:
            for (int i = 0; i < wysokosc; i++) {
                for (int j = 0; j < szerokosc; j++) {
                    if (plansza[i][j] == zwierze) {
                        x = j;
                        y = i;
                        break outerloop;
                    }
                }
            }

            if (x != -1 && y != -1) {
                zwierze.incrementDaysSinceLastMeal();
                if (zwierze.death()) {
                    doUsuniecia.add(zwierze);
                    plansza[y][x] = null;
                }
                if (zwierze instanceof Zajac || zwierze instanceof Sarna) {
                    for (int i = Math.max(0, y - 1); i <= Math.min(wysokosc - 1, y + 1); i++) {
                        for (int j = Math.max(0, x - 1); j <= Math.min(szerokosc - 1, x + 1); j++) {
                            if (plansza[i][j] instanceof roslina) {
                                rosliny.remove(plansza[i][j]);
                                plansza[i][j] = null;
                                updateLabels();
                                if (zwierze instanceof Sarna || zwierze instanceof Zajac) {
                                    zwierze.updateLastMeal();
                                }
                            }
                        }
                    }
                }
                if (zwierze.getIloscPozywienia() > 0) {
                    for (Zwierze other : zwierzeta) {
                        if (zwierze.getClass() == other.getClass() && other != zwierze && other.getIloscPozywienia() > 0) {
                            switch (zwierze) {
                                case Sarna sarna -> doDodania.add(new Sarna("Sarna", 'S'));
                                case Zajac zajac -> doDodania.add(new Zajac("Zając", 'Z'));
                                case Wilk wilk -> doDodania.add(new Wilk("Wilk", 'W', 1));
                                case Lis lis -> doDodania.add(new Lis("Lis", 'L', 1));
                                default -> throw new NoSuchElementException("Unknown animal");
                            }

                            zwierze.setIloscPozywienia(zwierze.getIloscPozywienia() - 1);
                            other.setIloscPozywienia(other.getIloscPozywienia() - 1);
                            break;
                        }
                    }
                }
                for (int i = Math.max(0, y - 1); i <= Math.min(wysokosc - 1, y + 1); i++) {
                    for (int j = Math.max(0, x - 1); j <= Math.min(szerokosc - 1, x + 1); j++) {
                        if (plansza[i][j] instanceof Zwierze && plansza[i][j] != zwierze) {
                            if (zwierze instanceof Wilk && plansza[i][j] instanceof Sarna) {
                                zwierze.updateLastMeal();
                                doUsuniecia.add((Zwierze) plansza[i][j]);
                                plansza[i][j] = null;
                                updateLabels();
                            } else if (zwierze instanceof Lis && plansza[i][j] instanceof Zajac) {
                                zwierze.updateLastMeal();
                                doUsuniecia.add((Zwierze) plansza[i][j]);
                                plansza[i][j] = null;
                                updateLabels();
                            }
                        }
                    }
                }
            }
        }

        zwierzeta.removeAll(doUsuniecia);
        for(Zwierze zwierze : doDodania){
            dodajZwierze(zwierze);
        }
        doDodania.clear();
        updateLabels();

        // Sprawdzanie, czy nie ma więcej zwierząt
        if (zwierzeta.isEmpty()) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Symulacja zakończona po " + dniSymulacji + " dniach.");
            System.exit(0);
        }
    }

    private void przesunZwierzeta() {
        for (Zwierze zwierze : zwierzeta) {
            int x = -1, y = -1;
            outerloop:
            for (int i = 0; i < wysokosc; i++) {
                for (int j = 0; j < szerokosc; j++) {
                    if (plansza[i][j] == zwierze) {
                        x = j;
                        y = i;
                        break outerloop;
                    }
                }
            }
            if (x != -1) {
                zwierze.poruszanieSie(this, x, y, random);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        wyswietlPlansze(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dniSymulacji++;
        symulujKolejnaEpoche();
        repaint();
        updateLabels();
        updateDate();
        dodajRoslinyNaPodstawieDeszczu();
    }

    public void updateDate() {
        this.calendar.add(Calendar.DAY_OF_MONTH, 1); // Zwiększenie daty o 1 dzień
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(this.calendar.getTime());
        dataField.setText(dateStr); // Ustawienie nowej daty w polu tekstowym
    }

    private void dodajRoslinyNaPodstawieDeszczu() {
        int miesiac = calendar.get(Calendar.MONTH);
        int iloscDeszczu;

        // Określanie ilości deszczu na podstawie pory roku
        if (miesiac >= Calendar.MARCH && miesiac <= Calendar.MAY) { // Wiosna
            iloscDeszczu = random.nextInt(3);
        } else if (miesiac >= Calendar.JUNE && miesiac <= Calendar.AUGUST) { // Lato
            iloscDeszczu = random.nextInt(4);
        } else if (miesiac >= Calendar.SEPTEMBER && miesiac <= Calendar.NOVEMBER) { // Jesień
            iloscDeszczu = random.nextInt(3);
        } else { // Zima
            iloscDeszczu = random.nextInt(2);
        }

        // Dodawanie roślin w zależności od ilości deszczu
        for (int i = 0; i < iloscDeszczu; i++) {
            if (random.nextInt(100) < 40) {
                roslina roslina = new roslina("Roślina", 'R');
                dodajRoslineNaWolneMiejsce(roslina);
            }
        }
    }

    public static void main(String[] args) {
        int szerokosc = 20; // Nowa szerokość
        int wysokosc = 20;  // Nowa wysokość

        // Pytanie o liczbę roślin
        int iloscRoslin = 0;
        do {
            try {
                iloscRoslin = Integer.parseInt(JOptionPane.showInputDialog("Podaj liczbę roślin: "));
                if (iloscRoslin < 0) {
                    JOptionPane.showMessageDialog(null, "Liczba roślin nie może być ujemna. Podaj wartość jeszcze raz.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy format liczby. Podaj wartość jeszcze raz.");
            }
        } while (iloscRoslin < 0);

        // Pytanie o liczbę lisów
        int iloscLisow = 0;
        do {
            try {
                iloscLisow = Integer.parseInt(JOptionPane.showInputDialog("Podaj liczbę lisów: "));
                if (iloscLisow < 0) {
                    JOptionPane.showMessageDialog(null, "Liczba lisów nie może być ujemna. Podaj wartość jeszcze raz.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy format liczby. Podaj wartość jeszcze raz.");
            }
        } while (iloscLisow < 0);

        // Pytanie o liczbę wilków (reszta drapieżników)
        int iloscWilkow = 0;
        do {
            try {
                iloscWilkow = Integer.parseInt(JOptionPane.showInputDialog("Podaj liczbę wilków: "));
                if (iloscWilkow < 0) {
                    JOptionPane.showMessageDialog(null, "Liczba wilków nie może być ujemna. Podaj wartość jeszcze raz.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy format liczby. Podaj wartość jeszcze raz.");
            }
        } while (iloscWilkow < 0);

        // Pytanie o liczbę saren
        int iloscSaren = 0;
        do {
            try {
                iloscSaren = Integer.parseInt(JOptionPane.showInputDialog("Podaj liczbę saren: "));
                if (iloscSaren < 0) {
                    JOptionPane.showMessageDialog(null, "Liczba saren nie może być ujemna. Podaj wartość jeszcze raz.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy format liczby. Podaj wartość jeszcze raz.");
            }
        } while (iloscSaren < 0);

        // Pytanie o liczbę zajęcy (reszta roślinożerców)
        int iloscZajecy = 0;
        do {
            try {
                iloscZajecy = Integer.parseInt(JOptionPane.showInputDialog("Podaj liczbę zajęcy: "));
                if (iloscZajecy < 0) {
                    JOptionPane.showMessageDialog(null, "Liczba zajęcy nie może być ujemna. Podaj wartość jeszcze raz.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy format liczby. Podaj wartość jeszcze raz.");
            }
        } while (iloscZajecy < 0);

        // Inicjalizacja kalendarza
        Calendar startDate = Calendar.getInstance();
        do {
            String startDateStr = JOptionPane.showInputDialog("Podaj datę rozpoczęcia programu w formacie yyyy-MM-dd: ");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                startDate.setTime(sdf.parse(startDateStr));
                break;
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy format daty. Podaj wartość jeszcze raz.");
            }
        } while (true);


        ekosystem ekosystem = new ekosystem(szerokosc, wysokosc, startDate);

        // Dodanie roślin
        for (int i = 0; i < iloscRoslin; i++) {
            roslina roslina = new roslina("Roślina " + (i + 1), 'R');
            ekosystem.dodajRoslineNaWolneMiejsce(roslina);
        }

        // Dodanie lisów
        for (int i = 0; i < iloscLisow; i++) {
            Zwierze lis = new Lis("Lis", 'L', random.nextInt(5) + 1);
            ekosystem.dodajZwierze(lis);
        }

        // Dodanie wilków
        for (int i = 0; i < iloscWilkow; i++) {
            Zwierze wilk = new Wilk("Wilk", 'W', random.nextInt(5) + 1);
            ekosystem.dodajZwierze(wilk);
        }

        // Dodanie saren
        for (int i = 0; i < iloscSaren; i++) {
            Zwierze sarna = new Sarna("Sarna", 'S');
            ekosystem.dodajZwierze(sarna);
        }

        // Dodanie zajęcy
        for (int i = 0; i < iloscZajecy; i++) {
            Zwierze zajac = new Zajac("Zając", 'Z');
            ekosystem.dodajZwierze(zajac);
        }

        JFrame frame = new JFrame("Ekosystem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(ekosystem, BorderLayout.CENTER);
        frame.setSize(700, 700);
        frame.setLocationRelativeTo(null); // Ustawienie okna na środku ekranu
        frame.setVisible(true);
    }
}
