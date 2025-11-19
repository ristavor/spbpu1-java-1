import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Artificial Life Simulation - single-file Java 25 implementation
 * Toroidal grid, herbivores & carnivores with small neural networks.
 */
public class Main extends JFrame {

    // ==== DEFAULT SIMULATION CONSTANTS (по умолчанию) ====
    private static final int DEFAULT_GRID_SIZE = 20;
    private static final int DEFAULT_INITIAL_HERBIVORES = 22;
    private static final int DEFAULT_INITIAL_CARNIVORES = 5;
    private static final int DEFAULT_INITIAL_PLANTS = 90;
    private static final double DEFAULT_PLANT_GROW_CHANCE = 0.045;

    private static final int ENERGY_UNIT = 10;   // "one unit" from ТЗ == 10 points
    private static final int DEFAULT_BASE_MAX_ENERGY_HERB = 100;
    private static final int DEFAULT_BASE_MAX_ENERGY_CARN = 130;
    private static final int DEFAULT_INITIAL_ENERGY_HERB = 60;
    private static final int DEFAULT_INITIAL_ENERGY_CARN = 90;

    private static final double REPRODUCTION_THRESHOLD = 0.9; // 90% of max energy

    // NN model
    private static final int NN_INPUTS = 14;
    private static final int NN_OUTPUTS = 4;

    // UI
    private static final int CELL_SIZE = 30;
    private static final int MIN_DELAY = 20;     // fastest speed (ms)
    private static final int MAX_DELAY = 500;    // slowest speed (ms)
    private static final int DEFAULT_DELAY = 120;

    // Конфиг, который можно менять через UI
    private static class SimConfig {
        int gridSize;
        int initialHerbivores;
        int initialCarnivores;
        int initialPlants;
        double plantGrowChance;
        int baseMaxEnergyHerb;
        int baseMaxEnergyCarn;
        int initialEnergyHerb;
        int initialEnergyCarn;
    }

    private final SimConfig config;
    private Environment environment;
    private final SimulationPanel panel;
    private final JLabel infoLabel;
    private final JSlider speedSlider;
    private final JButton startPauseButton;

    // UI-поля для конфигурации
    private final JSpinner gridSizeSpinner;
    private final JSpinner initialPlantsSpinner;
    private final JSpinner initialHerbivoresSpinner;
    private final JSpinner initialCarnivoresSpinner;
    private final JSpinner plantChanceSpinner;
    private final JSpinner initEnergyHerbSpinner;
    private final JSpinner maxEnergyHerbSpinner;
    private final JSpinner initEnergyCarnSpinner;
    private final JSpinner maxEnergyCarnSpinner;

    private javax.swing.Timer timer;
    private boolean running = false;
    private long generation = 0;

    public Main() {
        super("Искусственная жизнь (нейросети, хищники / травоядные)");

        // Инициализируем конфиг значениями по умолчанию
        config = new SimConfig();
        config.gridSize = DEFAULT_GRID_SIZE;
        config.initialHerbivores = DEFAULT_INITIAL_HERBIVORES;
        config.initialCarnivores = DEFAULT_INITIAL_CARNIVORES;
        config.initialPlants = DEFAULT_INITIAL_PLANTS;
        config.plantGrowChance = DEFAULT_PLANT_GROW_CHANCE;
        config.baseMaxEnergyHerb = DEFAULT_BASE_MAX_ENERGY_HERB;
        config.baseMaxEnergyCarn = DEFAULT_BASE_MAX_ENERGY_CARN;
        config.initialEnergyHerb = DEFAULT_INITIAL_ENERGY_HERB;
        config.initialEnergyCarn = DEFAULT_INITIAL_ENERGY_CARN;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Создаём мир с текущим конфигом
        environment = new Environment(config.gridSize, config);
        environment.initializeDefaultPopulation();

        // Панель симуляции
        panel = new SimulationPanel();

        // ==== ПАНЕЛЬ НАСТРОЕК (СВЕРХУ) ====
        gridSizeSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_GRID_SIZE, 5, 80, 1
        ));
        initialPlantsSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_INITIAL_PLANTS, 0, 10000, 10
        ));
        initialHerbivoresSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_INITIAL_HERBIVORES, 0, 5000, 1
        ));
        initialCarnivoresSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_INITIAL_CARNIVORES, 0, 5000, 1
        ));
        plantChanceSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_PLANT_GROW_CHANCE, 0.0, 0.5, 0.005
        ));
        initEnergyHerbSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_INITIAL_ENERGY_HERB, 1, 1000, 5
        ));
        maxEnergyHerbSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_BASE_MAX_ENERGY_HERB, 10, 2000, 10
        ));
        initEnergyCarnSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_INITIAL_ENERGY_CARN, 1, 1000, 5
        ));
        maxEnergyCarnSpinner = new JSpinner(new SpinnerNumberModel(
                DEFAULT_BASE_MAX_ENERGY_CARN, 10, 2000, 10
        ));

        JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridLayout(2, 1));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Размер:"));
        row1.add(gridSizeSpinner);
        row1.add(new JLabel("Растений:"));
        row1.add(initialPlantsSpinner);
        row1.add(new JLabel("Травоядных:"));
        row1.add(initialHerbivoresSpinner);
        row1.add(new JLabel("Хищников:"));
        row1.add(initialCarnivoresSpinner);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Рост растений:"));
        row2.add(plantChanceSpinner);
        row2.add(new JLabel("E нач трав:"));
        row2.add(initEnergyHerbSpinner);
        row2.add(new JLabel("E макс трав:"));
        row2.add(maxEnergyHerbSpinner);
        row2.add(new JLabel("E нач хищн:"));
        row2.add(initEnergyCarnSpinner);
        row2.add(new JLabel("E макс хищн:"));
        row2.add(maxEnergyCarnSpinner);

        configPanel.add(row1);
        configPanel.add(row2);

        // ==== НИЖНЯЯ ПАНЕЛЬ УПРАВЛЕНИЯ ====
        infoLabel = new JLabel();
        updateInfoLabel();

        startPauseButton = new JButton("Старт");
        startPauseButton.addActionListener(_ -> toggleRunning());

        JButton stepButton = new JButton("Шаг");
        stepButton.addActionListener(_ -> doOneStep());

        JButton resetButton = new JButton("Сброс");
        resetButton.addActionListener(_ -> resetSimulation());

        speedSlider = new JSlider(0, 100, 50);
        speedSlider.addChangeListener(_ -> {
            int delay = sliderToDelay(speedSlider.getValue());
            if (timer != null) {
                timer.setDelay(delay);
            }
        });

        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel leftControls = new JPanel();

        leftControls.add(startPauseButton);
        leftControls.add(stepButton);
        leftControls.add(resetButton);
        leftControls.add(new JLabel("Скорость:"));
        leftControls.add(speedSlider);

        controlPanel.add(leftControls, BorderLayout.WEST);
        controlPanel.add(infoLabel, BorderLayout.CENTER);

        add(configPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        timer = new javax.swing.Timer(DEFAULT_DELAY, _ -> {
            environment.step();
            generation++;
            updateInfoLabel();
            panel.repaint();
        });

        pack();
        setLocationRelativeTo(null);
    }

    static void main() {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }

    private int sliderToDelay(int sliderValue) {
        double t = sliderValue / 100.0;
        return (int) (MIN_DELAY + t * (MAX_DELAY - MIN_DELAY));
    }

    private void toggleRunning() {
        if (running) {
            timer.stop();
            startPauseButton.setText("Старт");
        } else {
            timer.start();
            startPauseButton.setText("Пауза");
        }
        running = !running;
    }

    private void doOneStep() {
        if (running) {
            toggleRunning();
        }
        environment.step();
        generation++;
        updateInfoLabel();
        panel.repaint();
    }

    private void resetSimulation() {
        if (running) {
            timer.stop();
            running = false;
        }
        startPauseButton.setText("Старт");

        // Забираем значения из UI
        config.gridSize = (Integer) gridSizeSpinner.getValue();
        config.initialPlants = (Integer) initialPlantsSpinner.getValue();
        config.initialHerbivores = (Integer) initialHerbivoresSpinner.getValue();
        config.initialCarnivores = (Integer) initialCarnivoresSpinner.getValue();
        config.plantGrowChance = ((Number) plantChanceSpinner.getValue()).doubleValue();
        config.baseMaxEnergyHerb = (Integer) maxEnergyHerbSpinner.getValue();
        config.baseMaxEnergyCarn = (Integer) maxEnergyCarnSpinner.getValue();
        config.initialEnergyHerb = (Integer) initEnergyHerbSpinner.getValue();
        config.initialEnergyCarn = (Integer) initEnergyCarnSpinner.getValue();

        // Корректируем энергии: начальная не должна быть больше максимальной
        if (config.initialEnergyHerb > config.baseMaxEnergyHerb) {
            config.initialEnergyHerb = config.baseMaxEnergyHerb;
            initEnergyHerbSpinner.setValue(config.initialEnergyHerb);
        }
        if (config.initialEnergyCarn > config.baseMaxEnergyCarn) {
            config.initialEnergyCarn = config.baseMaxEnergyCarn;
            initEnergyCarnSpinner.setValue(config.initialEnergyCarn);
        }

        // Ограничиваем стартовые количества размером поля
        int maxCells = config.gridSize * config.gridSize;
        if (config.initialPlants > maxCells) {
            config.initialPlants = maxCells;
            initialPlantsSpinner.setValue(config.initialPlants);
        }
        if (config.initialHerbivores > maxCells) {
            config.initialHerbivores = maxCells;
            initialHerbivoresSpinner.setValue(config.initialHerbivores);
        }
        if (config.initialCarnivores > maxCells) {
            config.initialCarnivores = maxCells;
            initialCarnivoresSpinner.setValue(config.initialCarnivores);
        }

        // Пересоздаём мир с новым размером и конфигом
        environment = new Environment(config.gridSize, config);
        environment.initializeDefaultPopulation();
        generation = 0;

        panel.refreshSize();
        updateInfoLabel();
        panel.repaint();
        pack();
    }

    private void updateInfoLabel() {
        int herb = environment.countAgentsOfType(Species.HERBIVORE);
        int carn = environment.countAgentsOfType(Species.CARNIVORE);
        int plants = environment.countPlants();

        infoLabel.setText(String.format(
                "Поколение: %d | Травоядных: %d | Хищников: %d | Растений: %d",
                generation, herb, carn, plants
        ));
    }

    // ==== ENUMS & SIMPLE TYPES ====

    private enum Species {
        HERBIVORE, CARNIVORE
    }

    private enum Direction {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        Direction turnLeft() {
            return switch (this) {
                case NORTH -> WEST;
                case WEST -> SOUTH;
                case SOUTH -> EAST;
                case EAST -> NORTH;
            };
        }

        Direction turnRight() {
            return switch (this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
            };
        }
    }

    private enum ActionType {
        MOVE_FORWARD,
        TURN_LEFT,
        TURN_RIGHT,
        EAT
    }

    // ==== NEURAL NETWORK ====

    private static class NeuralNetwork {
        private static final double MUTATION_STD = 0.05;

        private final double[][] weights; // [output][input]
        private final double[] biases;    // [output]
        private final Random rnd;

        NeuralNetwork(Random rnd) {
            this.rnd = rnd;
            this.weights = new double[NN_OUTPUTS][NN_INPUTS];
            this.biases = new double[NN_OUTPUTS];
            randomize();
        }

        private NeuralNetwork(Random rnd, double[][] w, double[] b) {
            this.rnd = rnd;
            this.weights = w;
            this.biases = b;
        }

        void randomize() {
            for (int i = 0; i < NN_OUTPUTS; i++) {
                biases[i] = rnd.nextGaussian() * 0.1;
                for (int j = 0; j < NN_INPUTS; j++) {
                    weights[i][j] = rnd.nextGaussian() * 0.3;
                }
            }
        }

        ActionType decide(double[] inputs) {
            double best = Double.NEGATIVE_INFINITY;
            int bestIndex = 0;
            for (int i = 0; i < NN_OUTPUTS; i++) {
                double sum = biases[i];
                double[] row = weights[i];
                for (int j = 0; j < NN_INPUTS; j++) {
                    sum += row[j] * inputs[j];
                }
                if (sum > best) {
                    best = sum;
                    bestIndex = i;
                }
            }
            return ActionType.values()[bestIndex];
        }

        NeuralNetwork copyWithMutation() {
            double[][] newW = new double[NN_OUTPUTS][NN_INPUTS];
            double[] newB = new double[NN_OUTPUTS];
            for (int i = 0; i < NN_OUTPUTS; i++) {
                newB[i] = biases[i] + rnd.nextGaussian() * MUTATION_STD;
                for (int j = 0; j < NN_INPUTS; j++) {
                    newW[i][j] = weights[i][j] + rnd.nextGaussian() * MUTATION_STD;
                }
            }
            return new NeuralNetwork(rnd, newW, newB);
        }
    }

    // ==== AGENT ====

    private static class Agent {
        Species species;
        int x, y;
        Direction dir;
        int energy;
        int maxEnergy;
        NeuralNetwork brain;
        boolean alive = true;

        Agent(Species species, int x, int y, Direction dir, int energy, int maxEnergy, NeuralNetwork brain) {
            this.species = species;
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.energy = energy;
            this.maxEnergy = maxEnergy;
            this.brain = brain;
        }

        double energyNormalized() {
            return Math.max(0.0, Math.min(1.0, energy / (double) maxEnergy));
        }
    }

    // ==== CELL & ENVIRONMENT ====

    private static class Cell {
        boolean plant;
        Agent agent;
    }

    private static class Environment {
        private final int size;
        private final Cell[][] grid;
        private final List<Agent> agents;
        private final Random rnd;
        private final SimConfig config;

        Environment(int size, SimConfig config) {
            this.size = size;
            this.config = config;
            this.grid = new Cell[size][size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    grid[y][x] = new Cell();
                }
            }
            this.agents = new ArrayList<>();
            this.rnd = new Random();
        }

        int getSize() {
            return size;
        }

        void initializeDefaultPopulation() {
            agents.clear();
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    grid[y][x].agent = null;
                    grid[y][x].plant = false;
                }
            }

            // Initial plants
            for (int i = 0; i < config.initialPlants; i++) {
                int x = rnd.nextInt(size);
                int y = rnd.nextInt(size);
                grid[y][x].plant = true;
            }

            // Herbivores
            for (int i = 0; i < config.initialHerbivores; i++) {
                placeRandomAgent(
                        Species.HERBIVORE,
                        config.initialEnergyHerb,
                        config.baseMaxEnergyHerb
                );
            }

            // Carnivores
            for (int i = 0; i < config.initialCarnivores; i++) {
                placeRandomAgent(
                        Species.CARNIVORE,
                        config.initialEnergyCarn,
                        config.baseMaxEnergyCarn
                );
            }
        }

        private void placeRandomAgent(Species species, int energy, int maxEnergy) {
            for (int tries = 0; tries < 100; tries++) {
                int x = rnd.nextInt(size);
                int y = rnd.nextInt(size);
                if (grid[y][x].agent == null) {
                    Direction dir = Direction.values()[rnd.nextInt(Direction.values().length)];
                    NeuralNetwork brain = new NeuralNetwork(rnd);
                    Agent a = new Agent(species, x, y, dir, energy, maxEnergy, brain);
                    grid[y][x].agent = a;
                    agents.add(a);
                    return;
                }
            }
        }

        int wrap(int v) {
            if (v < 0) return v + size;
            if (v >= size) return v - size;
            return v;
        }

        void step() {
            Collections.shuffle(agents, rnd);

            List<Agent> newAgents = new ArrayList<>();
            List<Agent> deadAgents = new ArrayList<>();

            for (Agent a : agents) {
                if (!a.alive) continue;

                // Base energy cost per tick
                a.energy--;
                if (a.energy <= 0) {
                    killAgent(a, deadAgents);
                    continue;
                }

                // Sense environment and choose action
                double[] inputs = buildInputs(a);
                ActionType act = a.brain.decide(inputs);

                switch (act) {
                    case MOVE_FORWARD -> performMove(a, deadAgents);
                    case TURN_LEFT -> a.dir = a.dir.turnLeft();
                    case TURN_RIGHT -> a.dir = a.dir.turnRight();
                    case EAT -> performEat(a, deadAgents);
                }

                if (!a.alive) continue;

                // Reproduction
                if (a.energy >= a.maxEnergy * REPRODUCTION_THRESHOLD) {
                    maybeReproduce(a, newAgents);
                }

                // Death if energy exhausted after actions
                if (a.energy <= 0) {
                    killAgent(a, deadAgents);
                }
            }

            agents.removeAll(deadAgents);
            agents.addAll(newAgents);
            growPlants();
        }

        private void killAgent(Agent a, List<Agent> dead) {
            a.alive = false;
            Cell c = grid[a.y][a.x];
            if (c.agent == a) {
                c.agent = null;
            }
            dead.add(a);
        }

        private void performMove(Agent a, List<Agent> deadAgents) {
            int nx = wrap(a.x + a.dir.dx);
            int ny = wrap(a.y + a.dir.dy);
            Cell from = grid[a.y][a.x];
            Cell to = grid[ny][nx];

            if (to.agent != null) {
                if (a.species == Species.CARNIVORE && to.agent.species == Species.HERBIVORE) {
                    Agent victim = to.agent;
                    victim.alive = false;
                    to.agent = null;
                    deadAgents.add(victim);

                    from.agent = null;
                    a.x = nx;
                    a.y = ny;
                    to.agent = a;

                    a.energy = Math.min(a.maxEnergy, a.energy + 2 * ENERGY_UNIT);
                }
                return;
            }

            from.agent = null;
            a.x = nx;
            a.y = ny;
            to.agent = a;

            if (a.species == Species.HERBIVORE && to.plant) {
                to.plant = false;
                a.energy = Math.min(a.maxEnergy, a.energy + ENERGY_UNIT);
            }
        }

        private void performEat(Agent a, List<Agent> deadAgents) {
            int nx = wrap(a.x + a.dir.dx);
            int ny = wrap(a.y + a.dir.dy);
            Cell target = grid[ny][nx];

            if (a.species == Species.HERBIVORE) {
                if (target.plant && target.agent == null) {
                    target.plant = false;
                    a.energy = Math.min(a.maxEnergy, a.energy + ENERGY_UNIT);
                }
            } else {
                if (target.agent != null && target.agent.species == Species.HERBIVORE) {
                    Agent victim = target.agent;
                    victim.alive = false;
                    target.agent = null;
                    deadAgents.add(victim);

                    Cell from = grid[a.y][a.x];
                    from.agent = null;
                    a.x = nx;
                    a.y = ny;
                    target.agent = a;

                    a.energy = Math.min(a.maxEnergy, a.energy + 2 * ENERGY_UNIT);
                }
            }
        }

        private void maybeReproduce(Agent parent, List<Agent> newborns) {
            List<int[]> free = new ArrayList<>();
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = wrap(parent.x + dx);
                    int ny = wrap(parent.y + dy);
                    if (grid[ny][nx].agent == null) {
                        free.add(new int[]{nx, ny});
                    }
                }
            }
            if (free.isEmpty()) return;

            int[] pos = free.get(rnd.nextInt(free.size()));

            int childEnergy = parent.energy / 2;
            parent.energy = parent.energy - childEnergy;

            if (childEnergy <= 0) return;

            NeuralNetwork childBrain = parent.brain.copyWithMutation();
            Agent child = new Agent(
                    parent.species,
                    pos[0],
                    pos[1],
                    Direction.values()[rnd.nextInt(Direction.values().length)],
                    childEnergy,
                    parent.maxEnergy,
                    childBrain
            );
            grid[pos[1]][pos[0]].agent = child;
            newborns.add(child);
        }

        private void growPlants() {
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    Cell c = grid[y][x];
                    if (!c.plant && c.agent == null) {
                        if (rnd.nextDouble() < config.plantGrowChance) {
                            c.plant = true;
                        }
                    }
                }
            }
        }

        private double[] buildInputs(Agent a) {
            double[] in = new double[NN_INPUTS];

            int nx1 = wrap(a.x + a.dir.dx);
            int ny1 = wrap(a.y + a.dir.dy);
            int nx2 = wrap(nx1 + a.dir.dx);
            int ny2 = wrap(ny1 + a.dir.dy);

            Direction leftDir = a.dir.turnLeft();
            Direction rightDir = a.dir.turnRight();
            int lx = wrap(a.x + leftDir.dx);
            int ly = wrap(a.y + leftDir.dy);
            int rx = wrap(a.x + rightDir.dx);
            int ry = wrap(a.y + rightDir.dy);

            int[] nearness = senseCell(nx1, ny1);
            int[] front = senseCell(nx2, ny2);
            int[] left = senseCell(lx, ly);
            int[] right = senseCell(rx, ry);

            in[0] = nearness[0];
            in[1] = nearness[1];
            in[2] = nearness[2];

            in[3] = front[0];
            in[4] = front[1];
            in[5] = front[2];

            in[6] = left[0];
            in[7] = left[1];
            in[8] = left[2];

            in[9] = right[0];
            in[10] = right[1];
            in[11] = right[2];

            in[12] = a.energyNormalized();
            in[13] = 1.0; // bias input

            return in;
        }

        private int[] senseCell(int x, int y) {
            Cell c = grid[y][x];
            int plants = c.plant ? 1 : 0;
            int herb = 0;
            int carn = 0;
            if (c.agent != null) {
                if (c.agent.species == Species.HERBIVORE) herb = 1;
                else carn = 1;
            }
            return new int[]{plants, herb, carn};
        }

        int countAgentsOfType(Species s) {
            int count = 0;
            for (Agent a : agents) {
                if (a.alive && a.species == s) count++;
            }
            return count;
        }

        int countPlants() {
            int count = 0;
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (grid[y][x].plant) count++;
                }
            }
            return count;
        }
    }

    // ==== RENDERING PANEL ====

    private class SimulationPanel extends JPanel {
        SimulationPanel() {
            refreshSize();
            setBackground(Color.BLACK);
        }

        void refreshSize() {
            int s = environment.getSize();
            setPreferredSize(new Dimension(s * CELL_SIZE, s * CELL_SIZE));
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = environment.getSize();

            // Draw grid
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    int px = x * CELL_SIZE;
                    int py = y * CELL_SIZE;

                    g2.setColor(new Color(230, 230, 230));
                    g2.fillRect(px, py, CELL_SIZE, CELL_SIZE);

                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRect(px, py, CELL_SIZE, CELL_SIZE);
                }
            }

            // Draw plants and agents
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    Cell c = environment.grid[y][x];
                    int px = x * CELL_SIZE;
                    int py = y * CELL_SIZE;

                    if (c.plant) {
                        g2.setColor(new Color(0, 170, 0));
                        int margin = CELL_SIZE / 6;
                        g2.fillOval(px + margin, py + margin, CELL_SIZE - 2 * margin, CELL_SIZE - 2 * margin);
                    }

                    if (c.agent != null && c.agent.alive) {
                        Agent a = c.agent;
                        if (a.species == Species.HERBIVORE) {
                            g2.setColor(new Color(30, 144, 255));
                        } else {
                            g2.setColor(new Color(200, 50, 50));
                        }
                        int margin = CELL_SIZE / 8;
                        g2.fillOval(px + margin, py + margin, CELL_SIZE - 2 * margin, CELL_SIZE - 2 * margin);

                        g2.setColor(Color.WHITE);
                        int cx = px + CELL_SIZE / 2;
                        int cy = py + CELL_SIZE / 2;
                        int r = CELL_SIZE / 3;

                        int dx = a.dir.dx;
                        int dy = a.dir.dy;
                        int tipX = cx + dx * r;
                        int tipY = cy + dy * r;
                        int ortX = -dy;
                        int base1X = cx + ortX * (r / 2);
                        int base1Y = cy + dx * (r / 2);
                        int base2X = cx - ortX * (r / 2);
                        int base2Y = cy - dx * (r / 2);

                        Polygon p = new Polygon();
                        p.addPoint(tipX, tipY);
                        p.addPoint(base1X, base1Y);
                        p.addPoint(base2X, base2Y);
                        g2.fillPolygon(p);

                        g2.setColor(Color.BLACK);
                        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 10f));
                        String es = String.valueOf(a.energy);
                        g2.drawString(es, px + 3, py + CELL_SIZE - 4);
                    }
                }
            }
        }
    }
}
