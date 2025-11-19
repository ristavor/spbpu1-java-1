/**
 * Абстрактный класс агента
 */
public abstract class Agent {
    // Действия агента (выходы нейронной сети)
    protected static final int ACTION_MOVE = 0;
    protected static final int ACTION_TURN_LEFT = 1;
    protected static final int ACTION_TURN_RIGHT = 2;
    protected static final int ACTION_EAT = 3;
    protected int x, y;
    protected Direction direction;
    protected int energy;
    protected int maxEnergy;
    protected NeuralNetwork brain;
    protected boolean alive;

    public Agent(int x, int y, Direction direction, int maxEnergy) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy / 2;
        this.alive = true;
        this.brain = new NeuralNetwork(14, 4); // 14 входов, 4 действия
    }

    /**
     * Конструктор для создания потомка
     */
    public Agent(Agent parent, int x, int y) {
        this.x = x;
        this.y = y;
        this.direction = Direction.values()[(int) (Math.random() * 4)];
        this.maxEnergy = parent.maxEnergy;
        this.energy = parent.energy / 2; // Потомок получает половину энергии родителя
        this.alive = true;
        this.brain = new NeuralNetwork(parent.brain, 0.1); // 10% мутация
    }

    /**
     * Выполнение действия агента
     */
    public void act(Environment env) {
        if (!alive) return;

        // Получить сенсорный ввод
        double[] sensorInput = env.getSensorInput(this);

        // Выбрать действие с помощью нейронной сети
        int action = brain.selectAction(sensorInput);

        // Проверить размножение ДО уменьшения энергии
        if (energy >= maxEnergy * 0.9) {
            reproduce(env);
        }

        // Выполнить действие
        executeAction(action, env);

        // Уменьшить энергию
        energy--;

        // Проверить, жив ли агент
        if (energy <= 0) {
            kill();
            env.removeAgent(this);
        }
    }

    protected void executeAction(int action, Environment env) {
        switch (action) {
            case ACTION_MOVE:
                move(env);
                break;
            case ACTION_TURN_LEFT:
                direction = direction.turnLeft();
                break;
            case ACTION_TURN_RIGHT:
                direction = direction.turnRight();
                break;
            case ACTION_EAT:
                eat(env);
                break;
        }
    }

    protected void move(Environment env) {
        int newX = x;
        int newY = y;

        switch (direction) {
            case NORTH:
                newY--;
                break;
            case SOUTH:
                newY++;
                break;
            case EAST:
                newX++;
                break;
            case WEST:
                newX--;
                break;
        }

        env.moveAgent(this, newX, newY);
    }

    /**
     * Абстрактный метод питания (реализуется в подклассах)
     */
    protected abstract void eat(Environment env);

    /**
     * Абстрактный метод размножения (реализуется в подклассах)
     */
    protected abstract void reproduce(Environment env);

    /**
     * Убить агента (установить alive = false)
     * Используется для корректного изменения состояния
     */
    protected void kill() {
        this.alive = false;
    }

    // Геттеры и сеттеры
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

