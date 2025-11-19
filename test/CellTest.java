/**
 * Тесты для класса Cell
 */
public class CellTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование Cell ===");
        testInitialization();
        testPlantOperations();
        testAgentOperations();
        System.out.println("Все тесты Cell пройдены!\n");
    }

    private static void testInitialization() {
        System.out.print("Тест инициализации... ");

        Cell cell = new Cell();
        assert !cell.hasPlant() : "Новая клетка не должна содержать растение";
        assert !cell.hasAgent() : "Новая клетка не должна содержать агента";
        assert cell.getAgent() == null : "getAgent() должен вернуть null";

        System.out.println("✓");
    }

    private static void testPlantOperations() {
        System.out.print("Тест операций с растениями... ");

        Cell cell = new Cell();
        cell.setPlant(true);
        assert cell.hasPlant() : "Клетка должна содержать растение после setPlant(true)";

        cell.setPlant(false);
        assert !cell.hasPlant() : "Клетка не должна содержать растение после setPlant(false)";

        System.out.println("✓");
    }

    private static void testAgentOperations() {
        System.out.print("Тест операций с агентами... ");

        Cell cell = new Cell();
        Herbivore herbivore = new Herbivore(0, 0, Direction.NORTH);

        cell.setAgent(herbivore);
        assert cell.hasAgent() : "Клетка должна содержать агента после setAgent()";
        assert cell.getAgent() == herbivore : "getAgent() должен вернуть того же агента";

        cell.setAgent(null);
        assert !cell.hasAgent() : "Клетка не должна содержать агента после setAgent(null)";
        assert cell.getAgent() == null : "getAgent() должен вернуть null";

        System.out.println("✓");
    }
}

