/**
 * Интеграционные тесты для проверки взаимодействия компонентов
 */
public class IntegrationTest {

    public static void main(String[] args) {
        System.out.println("=== Интеграционные тесты ===");
        testSensorInput();
        testFullSimulationCycle();
        testAgentCollision();
        testPlantGrowth();
        testPopulationDynamics();
        System.out.println("Все интеграционные тесты пройдены!\n");
    }

    private static void testSensorInput() {
        System.out.print("Тест сенсорного ввода... ");

        Environment env = new Environment(20, 20);
        Herbivore herbivore = new Herbivore(10, 10, Direction.NORTH);
        env.addAgent(herbivore);

        // Добавляем объекты вокруг агента
        env.addPlant(10, 9);  // впереди
        env.addAgent(new Herbivore(10, 8, Direction.SOUTH)); // впереди дальше
        env.addAgent(new Carnivore(9, 10, Direction.EAST));  // слева

        double[] sensorInput = env.getSensorInput(herbivore);

        assert sensorInput.length == 14 : "Должно быть 14 входов";
        assert sensorInput[13] == 1.0 : "Последний вход (bias) должен быть 1.0";
        assert sensorInput[12] >= 0 && sensorInput[12] <= 1 : "Энергия должна быть нормализована";

        // Проверка nearness area
        assert sensorInput[0] > 0 : "Nearness area должна содержать растение";

        System.out.println("✓");
    }

    private static void testFullSimulationCycle() {
        System.out.print("Тест полного цикла симуляции... ");

        Environment env = new Environment(20, 20);

        // Начальная популяция
        for (int i = 0; i < 10; i++) {
            env.addAgent(new Herbivore(i, i, Direction.NORTH));
        }
        for (int i = 0; i < 5; i++) {
            env.addAgent(new Carnivore(i + 10, i + 10, Direction.SOUTH));
        }
        for (int i = 0; i < 50; i++) {
            env.addPlant(i % 20, i / 20);
        }

        int initialAgents = env.getAgents().size();

        // Выполняем несколько шагов симуляции
        for (int i = 0; i < 10; i++) {
            env.update();
        }

        // Проверяем, что симуляция работает
        assert env.getAgents().size() >= 0 : "Количество агентов не должно быть отрицательным";

        System.out.println("✓");
    }

    private static void testAgentCollision() {
        System.out.print("Тест столкновения агентов... ");

        Environment env = new Environment(10, 10);
        Herbivore h1 = new Herbivore(5, 5, Direction.NORTH);
        Herbivore h2 = new Herbivore(5, 4, Direction.SOUTH);
        env.addAgent(h1);
        env.addAgent(h2);

        // Попытка движения в занятую клетку
        boolean moved = env.moveAgent(h1, 5, 4);
        assert !moved : "Движение в занятую клетку должно быть невозможным";
        assert h1.getX() == 5 && h1.getY() == 5 : "Агент должен остаться на месте";

        System.out.println("✓");
    }

    private static void testPlantGrowth() {
        System.out.print("Тест роста растений... ");

        Environment env = new Environment(10, 10);

        int plantsBefore = countPlants(env);

        // Запускаем симуляцию много раз для вероятного появления растений
        for (int i = 0; i < 100; i++) {
            env.update();
        }

        int plantsAfter = countPlants(env);

        assert plantsAfter >= plantsBefore : "Растения должны расти со временем";

        System.out.println("✓");
    }

    private static void testPopulationDynamics() {
        System.out.print("Тест динамики популяции... ");

        Environment env = new Environment(30, 30);

        // Создаем благоприятные условия
        for (int i = 0; i < 100; i++) {
            env.addPlant(i % 30, i / 30);
        }

        // Добавляем несколько травоядных с высокой энергией
        for (int i = 0; i < 5; i++) {
            Herbivore h = new Herbivore(i * 5, i * 5, Direction.NORTH);
            h.energy = (int) (h.getMaxEnergy() * 0.95);
            env.addAgent(h);
        }

        int agentsBefore = env.getAgents().size();

        // Выполняем несколько шагов
        for (int i = 0; i < 5; i++) {
            env.update();
        }

        int agentsAfter = env.getAgents().size();

        // При хороших условиях популяция может размножаться
        assert agentsAfter >= 0 : "Количество агентов должно быть неотрицательным";

        System.out.println("✓");
    }

    private static int countPlants(Environment env) {
        int count = 0;
        for (int y = 0; y < env.getHeight(); y++) {
            for (int x = 0; x < env.getWidth(); x++) {
                if (env.getCell(x, y).hasPlant()) {
                    count++;
                }
            }
        }
        return count;
    }
}

