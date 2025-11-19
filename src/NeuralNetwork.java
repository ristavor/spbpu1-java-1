import java.util.Random;

/**
 * Нейронная сеть для управления агентом
 */
public class NeuralNetwork {
    private final double[][] weights; // weights[i][j] - вес от входа j к выходу i
    private final double[] biases;    // biases[i] - смещение для выхода i
    private final int inputSize;
    private final int outputSize;
    private final Random random;

    public NeuralNetwork(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.weights = new double[outputSize][inputSize];
        this.biases = new double[outputSize];
        this.random = new Random();

        // Инициализация случайными значениями
        initializeRandom();
    }

    /**
     * Конструктор для создания копии (с мутацией)
     */
    public NeuralNetwork(NeuralNetwork parent, double mutationRate) {
        this.inputSize = parent.inputSize;
        this.outputSize = parent.outputSize;
        this.weights = new double[outputSize][inputSize];
        this.biases = new double[outputSize];
        this.random = new Random();

        // Копируем веса и смещения с мутацией
        for (int i = 0; i < outputSize; i++) {
            biases[i] = parent.biases[i];
            if (random.nextDouble() < mutationRate) {
                biases[i] += random.nextGaussian() * 0.2;
            }

            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = parent.weights[i][j];
                if (random.nextDouble() < mutationRate) {
                    weights[i][j] += random.nextGaussian() * 0.2;
                }
            }
        }
    }

    private void initializeRandom() {
        for (int i = 0; i < outputSize; i++) {
            biases[i] = random.nextGaussian() * 0.5;
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = random.nextGaussian() * 0.5;
            }
        }
    }

    /**
     * Вычисление выхода нейронной сети
     * Out(i) = bias(i) + Σ w(i,j) * u(j)
     */
    public double[] forward(double[] inputs) {
        if (inputs.length != inputSize) {
            throw new IllegalArgumentException("Invalid input size");
        }

        double[] outputs = new double[outputSize];

        for (int i = 0; i < outputSize; i++) {
            outputs[i] = biases[i];
            for (int j = 0; j < inputSize; j++) {
                outputs[i] += weights[i][j] * inputs[j];
            }
        }

        return outputs;
    }

    /**
     * Выбор действия по принципу "победитель забирает все"
     * <p>
     * Выходы нейронной сети (4 действия):
     * 0 - Двигаться вперед в текущем направлении
     * 1 - Повернуть налево
     * 2 - Повернуть направо
     * 3 - Съесть объект в области nearness
     *
     * @return индекс выбранного действия (0-3)
     */
    public int selectAction(double[] inputs) {
        double[] outputs = forward(inputs);

        int maxIndex = 0;
        double maxValue = outputs[0];

        for (int i = 1; i < outputs.length; i++) {
            if (outputs[i] > maxValue) {
                maxValue = outputs[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public int getInputSize() {
        return inputSize;
    }

    public int getOutputSize() {
        return outputSize;
    }
}

