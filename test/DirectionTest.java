/**
 * Тесты для класса Direction
 */
public class DirectionTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование Direction ===");
        testTurnLeft();
        testTurnRight();
        testFullCircle();
        System.out.println("Все тесты Direction пройдены!\n");
    }

    private static void testTurnLeft() {
        System.out.print("Тест поворота налево... ");

        assert Direction.NORTH.turnLeft() == Direction.WEST : "NORTH.turnLeft() должен быть WEST";
        assert Direction.WEST.turnLeft() == Direction.SOUTH : "WEST.turnLeft() должен быть SOUTH";
        assert Direction.SOUTH.turnLeft() == Direction.EAST : "SOUTH.turnLeft() должен быть EAST";
        assert Direction.EAST.turnLeft() == Direction.NORTH : "EAST.turnLeft() должен быть NORTH";

        System.out.println("✓");
    }

    private static void testTurnRight() {
        System.out.print("Тест поворота направо... ");

        assert Direction.NORTH.turnRight() == Direction.EAST : "NORTH.turnRight() должен быть EAST";
        assert Direction.EAST.turnRight() == Direction.SOUTH : "EAST.turnRight() должен быть SOUTH";
        assert Direction.SOUTH.turnRight() == Direction.WEST : "SOUTH.turnRight() должен быть WEST";
        assert Direction.WEST.turnRight() == Direction.NORTH : "WEST.turnRight() должен быть NORTH";

        System.out.println("✓");
    }

    private static void testFullCircle() {
        System.out.print("Тест полного круга... ");

        Direction dir = Direction.NORTH;
        dir = dir.turnRight().turnRight().turnRight().turnRight();
        assert dir == Direction.NORTH : "4 поворота направо должны вернуть к NORTH";

        dir = Direction.NORTH;
        dir = dir.turnLeft().turnLeft().turnLeft().turnLeft();
        assert dir == Direction.NORTH : "4 поворота налево должны вернуть к NORTH";

        System.out.println("✓");
    }
}

