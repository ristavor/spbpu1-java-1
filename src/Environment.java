import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Класс окружающей среды, представленный в виде тороидальной сетки
 */
public class Environment {
    // Константы для управления популяцией
    private static final int MAX_AGENTS = 500; // Максимальное количество агентов
    private static final int MIN_PLANTS = 50;  // Минимальное количество растений
    private static final double PLANT_GROWTH_RATE = 0.1; // Базовая вероятность роста растений
    private final int width;
    private final int height;
    private final Cell[][] grid;
    private final List<Agent> agents;
    private final Random random;

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[height][width];
        this.agents = new ArrayList<>();
        this.random = new Random();

        // Инициализация сетки
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Cell();
            }
        }
    }

    /**
     * Добавляет растение в случайную свободную клетку
     */
    public void addPlant(int x, int y) {
        if (isValidPosition(x, y) && !grid[y][x].hasPlant()) {
            grid[y][x].setPlant(true);
        }
    }

    /**
     * Добавляет агента в окружение
     */
    public void addAgent(Agent agent) {
        // Проверка на переполнение популяции
        if (agents.size() >= MAX_AGENTS) {
            return; // Не добавляем агента, если популяция слишком велика
        }

        if (isValidPosition(agent.getX(), agent.getY())) {
            agents.add(agent);
            grid[agent.getY()][agent.getX()].setAgent(agent);
        }
    }

    /**
     * Удаляет агента из окружения
     */
    public void removeAgent(Agent agent) {
        agents.remove(agent);
        if (isValidPosition(agent.getX(), agent.getY())) {
            grid[agent.getY()][agent.getX()].setAgent(null);
        }
    }

    /**
     * Перемещает агента на новую позицию
     */
    public boolean moveAgent(Agent agent, int newX, int newY) {
        newX = normalizeX(newX);
        newY = normalizeY(newY);

        if (grid[newY][newX].hasAgent()) {
            return false; // Клетка занята
        }

        grid[agent.getY()][agent.getX()].setAgent(null);
        agent.setPosition(newX, newY);
        grid[newY][newX].setAgent(agent);
        return true;
    }

    /**
     * Нормализация координат для тороидальной топологии
     */
    public int normalizeX(int x) {
        return ((x % width) + width) % width;
    }

    public int normalizeY(int y) {
        return ((y % height) + height) % height;
    }

    /**
     * Проверка валидности позиции
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Получить клетку по координатам
     */
    public Cell getCell(int x, int y) {
        x = normalizeX(x);
        y = normalizeY(y);
        return grid[y][x];
    }

    /**
     * Обновление состояния окружения (один шаг симуляции)
     */
    public void update() {
        // Копируем список агентов для безопасной итерации
        List<Agent> agentsCopy = new ArrayList<>(agents);

        // Обновляем каждого агента
        for (Agent agent : agentsCopy) {
            if (agent.isAlive()) {
                agent.act(this);
            }
        }

        // Удаляем мертвых агентов
        agents.removeIf(agent -> !agent.isAlive());

        // Управление ростом растений
        int currentPlants = countTotalPlants();
        double growthRate = PLANT_GROWTH_RATE;

        // Увеличиваем вероятность роста, если растений мало
        if (currentPlants < MIN_PLANTS) {
            growthRate = 0.5; // 50% шанс при дефиците растений
        }

        // Добавляем новые растения
        if (random.nextDouble() < growthRate) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            addPlant(x, y);
        }
    }

    /**
     * Подсчитывает общее количество растений в окружении
     */
    private int countTotalPlants() {
        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x].hasPlant()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Получить входные данные для нейронной сети агента
     * <p>
     * Структура входов (14 значений):
     * [0-2]   Nearness area (впереди на 1 клетку): растения, травоядные, хищники
     * [3-5]   Front area (впереди на 2 клетки): растения, травоядные, хищники
     * [6-8]   Left area (слева на 2 клетки): растения, травоядные, хищники
     * [9-11]  Right area (справа на 2 клетки): растения, травоядные, хищники
     * [12]    Уровень энергии агента (нормализованный)
     * [13]    Bias (всегда 1.0)
     */
    public double[] getSensorInput(Agent agent) {
        int x = agent.getX();
        int y = agent.getY();
        Direction dir = agent.getDirection();

        // 14 входов: nearness (3) + front (3) + left (3) + right (3) + energy (1) + bias (1)
        double[] inputs = new double[14];

        // Nearness area (впереди агента)
        int[] nearPos = getPositionInDirection(x, y, dir, 1);
        inputs[0] = countPlantsInArea(nearPos[0], nearPos[1], 1);
        inputs[1] = countHerbivoresInArea(nearPos[0], nearPos[1], 1);
        inputs[2] = countCarnivoresInArea(nearPos[0], nearPos[1], 1);

        // Front area (дальше впереди)
        int[] frontPos = getPositionInDirection(x, y, dir, 2);
        inputs[3] = countPlantsInArea(frontPos[0], frontPos[1], 2);
        inputs[4] = countHerbivoresInArea(frontPos[0], frontPos[1], 2);
        inputs[5] = countCarnivoresInArea(frontPos[0], frontPos[1], 2);

        // Left area
        Direction leftDir = dir.turnLeft();
        int[] leftPos = getPositionInDirection(x, y, leftDir, 2);
        inputs[6] = countPlantsInArea(leftPos[0], leftPos[1], 2);
        inputs[7] = countHerbivoresInArea(leftPos[0], leftPos[1], 2);
        inputs[8] = countCarnivoresInArea(leftPos[0], leftPos[1], 2);

        // Right area
        Direction rightDir = dir.turnRight();
        int[] rightPos = getPositionInDirection(x, y, rightDir, 2);
        inputs[9] = countPlantsInArea(rightPos[0], rightPos[1], 2);
        inputs[10] = countHerbivoresInArea(rightPos[0], rightPos[1], 2);
        inputs[11] = countCarnivoresInArea(rightPos[0], rightPos[1], 2);

        // Energy level
        inputs[12] = agent.getEnergy() / (double) agent.getMaxEnergy();

        // Bias
        inputs[13] = 1.0;

        return inputs;
    }

    private int[] getPositionInDirection(int x, int y, Direction dir, int distance) {
        int newX = x;
        int newY = y;

        switch (dir) {
            case NORTH:
                newY -= distance;
                break;
            case SOUTH:
                newY += distance;
                break;
            case EAST:
                newX += distance;
                break;
            case WEST:
                newX -= distance;
                break;
        }

        return new int[]{normalizeX(newX), normalizeY(newY)};
    }

    private double countPlantsInArea(int centerX, int centerY, int radius) {
        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (getCell(centerX + dx, centerY + dy).hasPlant()) {
                    count++;
                }
            }
        }
        return Math.min(count / 10.0, 1.0); // Нормализация с ограничением до 1.0
    }

    private double countHerbivoresInArea(int centerX, int centerY, int radius) {
        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                Agent agent = getCell(centerX + dx, centerY + dy).getAgent();
                if (agent != null && agent instanceof Herbivore) {
                    count++;
                }
            }
        }
        return Math.min(count / 10.0, 1.0); // Нормализация с ограничением до 1.0
    }

    private double countCarnivoresInArea(int centerX, int centerY, int radius) {
        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                Agent agent = getCell(centerX + dx, centerY + dy).getAgent();
                if (agent != null && agent instanceof Carnivore) {
                    count++;
                }
            }
        }
        return Math.min(count / 10.0, 1.0); // Нормализация с ограничением до 1.0
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Agent> getAgents() {
        return new ArrayList<>(agents);
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public int getMaxAgents() {
        return MAX_AGENTS;
    }

    public int getMinPlants() {
        return MIN_PLANTS;
    }
}

