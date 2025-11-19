import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Главный класс симуляции искусственной жизни
 */
public class Simulation {
    private Environment environment;
    private SimulationPanel panel;
    private JFrame frame;
    private JLabel statsLabel;
    private Timer timer;
    private int generation = 0;
    private boolean running = false;
    private int speed = 100; // мс между обновлениями

    public Simulation(int width, int height) {
        environment = new Environment(width, height);
        initializeEnvironment();
        setupGUI();
    }

    private void initializeEnvironment() {
        Random random = new Random();

        // Добавляем растения
        for (int i = 0; i < 200; i++) {
            int x = random.nextInt(environment.getWidth());
            int y = random.nextInt(environment.getHeight());
            environment.addPlant(x, y);
        }

        // Добавляем травоядных
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(environment.getWidth());
            int y = random.nextInt(environment.getHeight());
            Direction dir = Direction.values()[random.nextInt(4)];
            environment.addAgent(new Herbivore(x, y, dir));
        }

        // Добавляем хищников
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(environment.getWidth());
            int y = random.nextInt(environment.getHeight());
            Direction dir = Direction.values()[random.nextInt(4)];
            environment.addAgent(new Carnivore(x, y, dir));
        }
    }

    private void setupGUI() {
        frame = new JFrame("Artificial Life Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Панель симуляции
        panel = new SimulationPanel(environment);
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton startButton = new JButton("Старт");
        JButton stopButton = new JButton("Стоп");
        JButton resetButton = new JButton("Сброс");
        JButton stepButton = new JButton("Шаг");

        JSlider speedSlider = new JSlider(1, 500, speed);
        speedSlider.addChangeListener(e -> {
            speed = speedSlider.getValue();
            if (running) {
                timer.setDelay(speed);
            }
        });

        startButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        resetButton.addActionListener(e -> reset());
        stepButton.addActionListener(e -> step());

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(stepButton);
        controlPanel.add(resetButton);
        controlPanel.add(new JLabel("Скорость:"));
        controlPanel.add(speedSlider);

        frame.add(controlPanel, BorderLayout.NORTH);

        // Панель статистики
        statsLabel = new JLabel();
        updateStats();
        frame.add(statsLabel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Таймер для обновления симуляции
        timer = new Timer(speed, e -> step());
    }

    private void start() {
        if (!running) {
            running = true;
            timer.start();
        }
    }

    private void stop() {
        if (running) {
            running = false;
            timer.stop();
        }
    }

    private void step() {
        environment.update();
        generation++;
        updateStats();
        panel.repaint();
    }

    private void reset() {
        stop();
        generation = 0;
        environment = new Environment(environment.getWidth(), environment.getHeight());
        initializeEnvironment();
        panel = new SimulationPanel(environment);
        updateStats();

        // Обновляем фрейм
        frame.getContentPane().removeAll();
        setupGUI();
    }

    private void updateStats() {
        int herbivores = 0;
        int carnivores = 0;
        int plants = 0;

        for (Agent agent : environment.getAgents()) {
            if (agent instanceof Herbivore) {
                herbivores++;
            } else if (agent instanceof Carnivore) {
                carnivores++;
            }
        }

        for (int y = 0; y < environment.getHeight(); y++) {
            for (int x = 0; x < environment.getWidth(); x++) {
                if (environment.getCell(x, y).hasPlant()) {
                    plants++;
                }
            }
        }

        statsLabel.setText(String.format(
                "  Поколение: %d  |  Травоядные: %d  |  Хищники: %d  |  Растения: %d  ",
                generation, herbivores, carnivores, plants
        ));
    }
}

