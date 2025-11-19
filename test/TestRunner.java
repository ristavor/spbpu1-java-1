/**
 * Главный класс для запуска всех тестов
 */
public class TestRunner {

    static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ЗАПУСК ТЕСТОВ СИМУЛЯЦИИ ИСКУССТВЕННОЙ ЖИЗНИ       ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        try {
            DirectionTest.main(args);
            CellTest.main(args);
            EnvironmentTest.main(args);
            NeuralNetworkTest.main(args);
            AgentTest.main(args);
            IntegrationTest.main(args);
            RequirementsTest.main(args);

            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║   ✓ ВСЕ ТЕСТЫ УСПЕШНО ПРОЙДЕНЫ!                     ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
        } catch (AssertionError e) {
            System.err.println("\n╔══════════════════════════════════════════════════════╗");
            System.err.println("║   ✗ ТЕСТ НЕ ПРОЙДЕН!                                ║");
            System.err.println("╚══════════════════════════════════════════════════════╝");
            System.err.println("\nОшибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n╔══════════════════════════════════════════════════════╗");
            System.err.println("║   ✗ КРИТИЧЕСКАЯ ОШИБКА!                             ║");
            System.err.println("╚══════════════════════════════════════════════════════╝");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

