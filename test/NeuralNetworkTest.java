/**
 * Тесты для класса NeuralNetwork
 */
public class NeuralNetworkTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование NeuralNetwork ===");
        testInitialization();
        testForward();
        testSelectAction();
        testInheritance();
        System.out.println("Все тесты NeuralNetwork пройдены!\n");
    }

    private static void testInitialization() {
        System.out.print("Тест инициализации... ");

        NeuralNetwork nn = new NeuralNetwork(14, 4);
        assert nn.getInputSize() == 14 : "Размер входов должен быть 14";
        assert nn.getOutputSize() == 4 : "Размер выходов должен быть 4";

        System.out.println("✓");
    }

    private static void testForward() {
        System.out.print("Тест прямого прохода... ");

        NeuralNetwork nn = new NeuralNetwork(14, 4);
        double[] inputs = new double[14];
        for (int i = 0; i < 14; i++) {
            inputs[i] = 0.5;
        }

        double[] outputs = nn.forward(inputs);
        assert outputs.length == 4 : "Должно быть 4 выхода";

        // Проверка, что выходы не NaN
        for (int i = 0; i < outputs.length; i++) {
            assert !Double.isNaN(outputs[i]) : "Выход не должен быть NaN";
        }

        // Тест с неправильным размером входов
        try {
            nn.forward(new double[10]);
            assert false : "Должно выброситься исключение при неправильном размере входов";
        } catch (IllegalArgumentException e) {
            // Ожидаемое исключение
        }

        System.out.println("✓");
    }

    private static void testSelectAction() {
        System.out.print("Тест выбора действия... ");

        NeuralNetwork nn = new NeuralNetwork(14, 4);
        double[] inputs = new double[14];
        for (int i = 0; i < 14; i++) {
            inputs[i] = Math.random();
        }

        int action = nn.selectAction(inputs);
        assert action >= 0 && action < 4 : "Действие должно быть от 0 до 3";

        System.out.println("✓");
    }

    private static void testInheritance() {
        System.out.print("Тест наследования (мутации)... ");

        NeuralNetwork parent = new NeuralNetwork(14, 4);
        NeuralNetwork child = new NeuralNetwork(parent, 0.1);

        assert child.getInputSize() == parent.getInputSize() : "Размер входов должен совпадать";
        assert child.getOutputSize() == parent.getOutputSize() : "Размер выходов должен совпадать";

        // Проверка, что потомок может работать
        double[] inputs = new double[14];
        for (int i = 0; i < 14; i++) {
            inputs[i] = 0.5;
        }

        int parentAction = parent.selectAction(inputs);
        int childAction = child.selectAction(inputs);
        // Из-за мутации действия могут отличаться, но должны быть валидными
        assert parentAction >= 0 && parentAction < 4 : "Действие родителя должно быть валидным";
        assert childAction >= 0 && childAction < 4 : "Действие потомка должно быть валидным";

        System.out.println("✓");
    }
}

