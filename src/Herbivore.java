import java.util.Random;

/**
 * Травоядное животное - ест растения и избегает хищников
 */
public class Herbivore extends Agent {
    private static final Random random = new Random();

    public Herbivore(int x, int y, Direction direction) {
        super(x, y, direction, 20);
    }

    public Herbivore(Herbivore parent, int x, int y) {
        super(parent, x, y);
    }

    @Override
    protected void eat(Environment env) {
        // Получить позицию впереди (nearness area)
        int targetX = x;
        int targetY = y;

        switch (direction) {
            case NORTH:
                targetY--;
                break;
            case SOUTH:
                targetY++;
                break;
            case EAST:
                targetX++;
                break;
            case WEST:
                targetX--;
                break;
        }

        targetX = env.normalizeX(targetX);
        targetY = env.normalizeY(targetY);

        Cell targetCell = env.getCell(targetX, targetY);

        // Съесть растение, если оно есть
        if (targetCell.hasPlant()) {
            targetCell.setPlant(false);
            energy += 1; // Получить 1 единицу энергии
            if (energy > maxEnergy) {
                energy = maxEnergy;
            }
        }
    }

    @Override
    protected void reproduce(Environment env) {
        // Найти свободную клетку рядом
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newX = env.normalizeX(x + dx[i]);
            int newY = env.normalizeY(y + dy[i]);

            Cell cell = env.getCell(newX, newY);
            if (!cell.hasAgent()) {
                Herbivore child = new Herbivore(this, newX, newY);
                env.addAgent(child);
                energy /= 2; // Родитель теряет половину энергии
                return;
            }
        }
    }
}

