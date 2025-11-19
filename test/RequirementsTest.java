/**
 * Тесты для проверки соответствия техническому заданию
 */
public class RequirementsTest {

    public static void main(String[] args) {
        System.out.println("=== Тесты соответствия ТЗ ===");
        testToroidalEnvironment();
        testOneAgentPerCell();
        testSensorAreas();
        testActions();
        testEnergyAndMetabolism();
        testReproductionCondition();
        testNeuralNetworkStructure();
        testWinnerTakesAll();
        System.out.println("Все тесты ТЗ пройдены!\n");
    }

    private static void testToroidalEnvironment() {
        System.out.print("Тест тороидальной среды (агент переходит через края)... ");

        Environment env = new Environment(10, 10);
        Herbivore h = new Herbivore(9, 9, Direction.EAST);
        env.addAgent(h);

        // Движение через восточную границу
        env.moveAgent(h, 10, 9);
        assert h.getX() == 0 : "Агент должен появиться на западной границе";

        // Движение через северную границу
        h.setPosition(5, 0);
        env.moveAgent(h, 5, -1);
        assert h.getY() == 9 : "Агент должен появиться на южной границе";

        System.out.println("✓");
    }

    private static void testOneAgentPerCell() {
        System.out.print("Тест одного агента на клетку... ");

        Environment env = new Environment(10, 10);
        Herbivore h1 = new Herbivore(5, 5, Direction.NORTH);
        Herbivore h2 = new Herbivore(6, 6, Direction.SOUTH);
        env.addAgent(h1);
        env.addAgent(h2);

        // Попытка переместить второго агента на клетку первого
        boolean moved = env.moveAgent(h2, 5, 5);
        assert !moved : "Не должно быть возможности переместить агента на занятую клетку";
        assert h2.getX() == 6 && h2.getY() == 6 : "Агент должен остаться на исходной позиции";

        System.out.println("✓");
    }

    private static void testSensorAreas() {
        System.out.print("Тест четырех областей сенсоров (nearness, front, left, right)... ");

        Environment env = new Environment(20, 20);
        Herbivore h = new Herbivore(10, 10, Direction.NORTH);
        env.addAgent(h);

        // Nearness area (1 клетка впереди)
        env.addPlant(10, 9);

        // Front area (2 клетки впереди)
        env.addAgent(new Herbivore(10, 8, Direction.SOUTH));

        // Left area (слева)
        env.addAgent(new Carnivore(8, 10, Direction.EAST));

        // Right area (справа)
        env.addAgent(new Carnivore(12, 10, Direction.WEST));

        double[] sensors = env.getSensorInput(h);

        // Проверяем структуру входов
        assert sensors.length == 14 : "Должно быть 14 входов";

        // Nearness [0-2]
        assert sensors[0] > 0 : "Nearness должен обнаружить растение";

        // Front [3-5]
        assert sensors[4] > 0 : "Front должен обнаружить травоядное";

        // Left [6-8]
        assert sensors[8] > 0 : "Left должен обнаружить хищника";

        // Right [9-11]
        assert sensors[11] > 0 : "Right должен обнаружить хищника";

        // Energy [12]
        assert sensors[12] >= 0 && sensors[12] <= 1 : "Энергия должна быть нормализована";

        // Bias [13]
        assert sensors[13] == 1.0 : "Bias должен быть 1.0";

        System.out.println("✓");
    }

    private static void testActions() {
        System.out.print("Тест четырех действий (move, turn left, turn right, eat)... ");

        Environment env = new Environment(10, 10);
        Herbivore h = new Herbivore(5, 5, Direction.NORTH);
        env.addAgent(h);

        // ACTION_MOVE (0)
        int oldY = h.getY();
        h.executeAction(0, env);
        assert h.getY() < oldY : "ACTION_MOVE должен перемещать агента вперед";

        // ACTION_TURN_LEFT (1)
        Direction oldDir = h.getDirection();
        h.executeAction(1, env);
        assert h.getDirection() == oldDir.turnLeft() : "ACTION_TURN_LEFT должен поворачивать налево";

        // ACTION_TURN_RIGHT (2)
        oldDir = h.getDirection();
        h.executeAction(2, env);
        assert h.getDirection() == oldDir.turnRight() : "ACTION_TURN_RIGHT должен поворачивать направо";

        // ACTION_EAT (3)
        env.addPlant(h.getX(), h.getY() - 1);
        h.direction = Direction.NORTH;
        int oldEnergy = h.getEnergy();
        h.executeAction(3, env);
        assert h.getEnergy() > oldEnergy : "ACTION_EAT должен увеличивать энергию";

        System.out.println("✓");
    }

    private static void testEnergyAndMetabolism() {
        System.out.print("Тест энергии и метаболизма... ");

        Environment env = new Environment(10, 10);
        Herbivore h = new Herbivore(5, 5, Direction.NORTH);
        Carnivore c = new Carnivore(3, 3, Direction.SOUTH);
        env.addAgent(h);
        env.addAgent(c);

        // Травоядное получает 1 единицу энергии от растения
        env.addPlant(5, 4);
        int energyBefore = h.getEnergy();
        h.eat(env);
        assert h.getEnergy() == energyBefore + 1 : "Травоядное должно получить 1 единицу энергии";

        // Хищник получает 2 единицы энергии от травоядного
        // Хищник находится в (3,3) и смотрит на SOUTH, значит добыча должна быть в (3,4)
        Herbivore prey = new Herbivore(3, 4, Direction.EAST);
        env.addAgent(prey);
        energyBefore = c.getEnergy();
        c.eat(env);
        assert c.getEnergy() == energyBefore + 2 : "Хищник должен получить 2 единицы энергии";

        // Агент тратит энергию при действии
        energyBefore = h.getEnergy();
        h.act(env);
        assert h.getEnergy() < energyBefore : "Агент должен тратить энергию";

        // Агент умирает при энергии = 0
        h.energy = 1;
        h.act(env);
        assert !h.isAlive() : "Агент должен умереть при энергии 0";

        System.out.println("✓");
    }

    private static void testReproductionCondition() {
        System.out.print("Тест размножения при 90% энергии... ");

        Environment env = new Environment(10, 10);
        Herbivore h = new Herbivore(5, 5, Direction.NORTH);
        env.addAgent(h);

        // Не размножается при 80% энергии
        h.energy = (int) (h.getMaxEnergy() * 0.8);
        int agentsBefore = env.getAgents().size();
        h.act(env);
        assert env.getAgents().size() == agentsBefore : "Не должно быть размножения при 80% энергии";

        // Размножается при 90% энергии
        h.energy = (int) (h.getMaxEnergy() * 0.91);
        agentsBefore = env.getAgents().size();
        h.act(env);
        assert env.getAgents().size() > agentsBefore : "Должно быть размножение при 90%+ энергии";

        System.out.println("✓");
    }

    private static void testNeuralNetworkStructure() {
        System.out.print("Тест структуры нейронной сети (14 входов, 4 выхода)... ");

        NeuralNetwork nn = new NeuralNetwork(14, 4);

        assert nn.getInputSize() == 14 : "Должно быть 14 входов";
        assert nn.getOutputSize() == 4 : "Должно быть 4 выхода";

        double[] inputs = new double[14];
        for (int i = 0; i < 14; i++) {
            inputs[i] = Math.random();
        }

        double[] outputs = nn.forward(inputs);
        assert outputs.length == 4 : "Выход должен содержать 4 значения";

        System.out.println("✓");
    }

    private static void testWinnerTakesAll() {
        System.out.print("Тест принципа 'победитель забирает все'... ");

        NeuralNetwork nn = new NeuralNetwork(14, 4);
        double[] inputs = new double[14];
        for (int i = 0; i < 14; i++) {
            inputs[i] = Math.random();
        }

        // Получаем выходы
        double[] outputs = nn.forward(inputs);

        // Находим максимальный выход
        int maxIndex = 0;
        double maxValue = outputs[0];
        for (int i = 1; i < outputs.length; i++) {
            if (outputs[i] > maxValue) {
                maxValue = outputs[i];
                maxIndex = i;
            }
        }

        // Проверяем, что selectAction выбирает тот же индекс
        int selectedAction = nn.selectAction(inputs);
        assert selectedAction == maxIndex : "selectAction должен выбрать действие с максимальным выходом";

        System.out.println("✓");
    }
}

