/**
 * Тесты для класса Environment
 */
public class EnvironmentTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование Environment ===");
        testInitialization();
        testToroidalTopology();
        testPlantOperations();
        testAgentOperations();
        testAgentMovement();
        System.out.println("Все тесты Environment пройдены!\n");
    }

    private static void testInitialization() {
        System.out.print("Тест инициализации... ");

        Environment env = new Environment(10, 8);
        assert env.getWidth() == 10 : "Ширина должна быть 10";
        assert env.getHeight() == 8 : "Высота должна быть 8";
        assert env.getAgents().isEmpty() : "Список агентов должен быть пустым";

        System.out.println("✓");
    }

    private static void testToroidalTopology() {
        System.out.print("Тест тороидальной топологии... ");

        Environment env = new Environment(10, 8);

        // Тест normalizeX
        assert env.normalizeX(0) == 0 : "normalizeX(0) должен быть 0";
        assert env.normalizeX(9) == 9 : "normalizeX(9) должен быть 9";
        assert env.normalizeX(10) == 0 : "normalizeX(10) должен быть 0";
        assert env.normalizeX(-1) == 9 : "normalizeX(-1) должен быть 9";
        assert env.normalizeX(15) == 5 : "normalizeX(15) должен быть 5";

        // Тест normalizeY
        assert env.normalizeY(0) == 0 : "normalizeY(0) должен быть 0";
        assert env.normalizeY(7) == 7 : "normalizeY(7) должен быть 7";
        assert env.normalizeY(8) == 0 : "normalizeY(8) должен быть 0";
        assert env.normalizeY(-1) == 7 : "normalizeY(-1) должен быть 7";
        assert env.normalizeY(10) == 2 : "normalizeY(10) должен быть 2";

        System.out.println("✓");
    }

    private static void testPlantOperations() {
        System.out.print("Тест операций с растениями... ");

        Environment env = new Environment(10, 8);

        env.addPlant(5, 5);
        assert env.getCell(5, 5).hasPlant() : "Клетка (5,5) должна содержать растение";

        // Попытка добавить растение в занятую клетку
        env.addPlant(5, 5);
        assert env.getCell(5, 5).hasPlant() : "Клетка (5,5) все еще должна содержать растение";

        System.out.println("✓");
    }

    private static void testAgentOperations() {
        System.out.print("Тест операций с агентами... ");

        Environment env = new Environment(10, 8);
        Herbivore herbivore = new Herbivore(3, 4, Direction.NORTH);

        env.addAgent(herbivore);
        assert env.getAgents().size() == 1 : "Должен быть 1 агент";
        assert env.getCell(3, 4).hasAgent() : "Клетка (3,4) должна содержать агента";
        assert env.getCell(3, 4).getAgent() == herbivore : "Агент должен быть тем же";

        env.removeAgent(herbivore);
        assert env.getAgents().isEmpty() : "Список агентов должен быть пустым";
        assert !env.getCell(3, 4).hasAgent() : "Клетка (3,4) не должна содержать агента";

        System.out.println("✓");
    }

    private static void testAgentMovement() {
        System.out.print("Тест движения агентов... ");

        Environment env = new Environment(10, 8);
        Herbivore herbivore = new Herbivore(5, 5, Direction.NORTH);
        env.addAgent(herbivore);

        // Тест успешного движения
        boolean moved = env.moveAgent(herbivore, 6, 5);
        assert moved : "Движение должно быть успешным";
        assert herbivore.getX() == 6 : "X координата должна быть 6";
        assert herbivore.getY() == 5 : "Y координата должна быть 5";
        assert !env.getCell(5, 5).hasAgent() : "Старая клетка должна быть пустой";
        assert env.getCell(6, 5).hasAgent() : "Новая клетка должна содержать агента";

        // Тест тороидального движения
        Carnivore carnivore = new Carnivore(9, 7, Direction.EAST);
        env.addAgent(carnivore);
        moved = env.moveAgent(carnivore, 10, 8); // Должно нормализоваться к (0, 0)
        assert moved : "Движение через границу должно быть успешным";
        assert carnivore.getX() == 0 : "X координата должна быть 0";
        assert carnivore.getY() == 0 : "Y координата должна быть 0";

        System.out.println("✓");
    }
}

