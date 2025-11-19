/**
 * Тесты для классов Agent, Herbivore и Carnivore
 */
public class AgentTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование Agent ===");
        testHerbivoreInitialization();
        testCarnivoreInitialization();
        testHerbivoreEating();
        testCarnivoreEating();
        testEnergyConsumption();
        testReproduction();
        testMovement();
        System.out.println("Все тесты Agent пройдены!\n");
    }

    private static void testHerbivoreInitialization() {
        System.out.print("Тест инициализации травоядного... ");

        Herbivore herbivore = new Herbivore(5, 5, Direction.NORTH);
        assert herbivore.getX() == 5 : "X должен быть 5";
        assert herbivore.getY() == 5 : "Y должен быть 5";
        assert herbivore.getDirection() == Direction.NORTH : "Направление должно быть NORTH";
        assert herbivore.isAlive() : "Агент должен быть живым";
        assert herbivore.getMaxEnergy() == 20 : "Максимальная энергия травоядного должна быть 20";
        assert herbivore.getEnergy() == 10 : "Начальная энергия должна быть половина максимальной";

        System.out.println("✓");
    }

    private static void testCarnivoreInitialization() {
        System.out.print("Тест инициализации хищника... ");

        Carnivore carnivore = new Carnivore(3, 3, Direction.SOUTH);
        assert carnivore.getX() == 3 : "X должен быть 3";
        assert carnivore.getY() == 3 : "Y должен быть 3";
        assert carnivore.getDirection() == Direction.SOUTH : "Направление должно быть SOUTH";
        assert carnivore.isAlive() : "Агент должен быть живым";
        assert carnivore.getMaxEnergy() == 30 : "Максимальная энергия хищника должна быть 30";
        assert carnivore.getEnergy() == 15 : "Начальная энергия должна быть половина максимальной";

        System.out.println("✓");
    }

    private static void testHerbivoreEating() {
        System.out.print("Тест питания травоядного... ");

        Environment env = new Environment(10, 10);
        Herbivore herbivore = new Herbivore(5, 5, Direction.NORTH);
        env.addAgent(herbivore);

        // Добавляем растение впереди агента
        env.addPlant(5, 4); // NORTH от (5,5)

        int energyBefore = herbivore.getEnergy();
        herbivore.eat(env);
        int energyAfter = herbivore.getEnergy();

        assert energyAfter == energyBefore + 1 : "Энергия должна увеличиться на 1";
        assert !env.getCell(5, 4).hasPlant() : "Растение должно быть съедено";

        System.out.println("✓");
    }

    private static void testCarnivoreEating() {
        System.out.print("Тест питания хищника... ");

        Environment env = new Environment(10, 10);
        Carnivore carnivore = new Carnivore(5, 5, Direction.NORTH);
        Herbivore herbivore = new Herbivore(5, 4, Direction.SOUTH);
        env.addAgent(carnivore);
        env.addAgent(herbivore);

        int energyBefore = carnivore.getEnergy();
        carnivore.eat(env);
        int energyAfter = carnivore.getEnergy();

        assert energyAfter == energyBefore + 2 : "Энергия хищника должна увеличиться на 2";
        assert !herbivore.isAlive() : "Травоядное должно быть мертво";

        System.out.println("✓");
    }

    private static void testEnergyConsumption() {
        System.out.print("Тест потребления энергии... ");

        Environment env = new Environment(10, 10);
        Herbivore herbivore = new Herbivore(5, 5, Direction.NORTH);
        env.addAgent(herbivore);

        int energyBefore = herbivore.getEnergy();
        herbivore.act(env);
        int energyAfter = herbivore.getEnergy();

        assert energyAfter < energyBefore : "Энергия должна уменьшиться после действия";

        // Тест смерти при нулевой энергии
        while (herbivore.getEnergy() > 0) {
            herbivore.act(env);
        }
        assert !herbivore.isAlive() : "Агент должен умереть при энергии 0";

        System.out.println("✓");
    }

    private static void testReproduction() {
        System.out.print("Тест размножения... ");

        Environment env = new Environment(10, 10);
        Herbivore herbivore = new Herbivore(5, 5, Direction.NORTH);
        herbivore.energy = (int) (herbivore.getMaxEnergy() * 0.95); // 95% энергии
        env.addAgent(herbivore);

        int agentsBefore = env.getAgents().size();
        herbivore.act(env);
        int agentsAfter = env.getAgents().size();

        assert agentsAfter > agentsBefore : "Должен появиться новый агент после размножения";

        System.out.println("✓");
    }

    private static void testMovement() {
        System.out.print("Тест движения... ");

        Environment env = new Environment(10, 10);
        Herbivore herbivore = new Herbivore(5, 5, Direction.NORTH);
        env.addAgent(herbivore);

        herbivore.move(env);
        assert herbivore.getY() == 4 : "При движении на NORTH Y должен уменьшиться";

        // Тест поворотов
        Direction oldDir = herbivore.getDirection();
        herbivore.executeAction(Agent.ACTION_TURN_LEFT, env);
        assert herbivore.getDirection() == oldDir.turnLeft() : "Направление должно повернуться налево";

        oldDir = herbivore.getDirection();
        herbivore.executeAction(Agent.ACTION_TURN_RIGHT, env);
        assert herbivore.getDirection() == oldDir.turnRight() : "Направление должно повернуться направо";

        System.out.println("✓");
    }
}

