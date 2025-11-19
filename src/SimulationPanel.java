import javax.swing.*;
import java.awt.*;

/**
 * Панель для визуализации симуляции
 */
public class SimulationPanel extends JPanel {
    private final Environment environment;
    private int cellSize = 10;

    public SimulationPanel(Environment environment) {
        this.environment = environment;
        setPreferredSize(new Dimension(
                environment.getWidth() * cellSize,
                environment.getHeight() * cellSize
        ));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Cell[][] grid = environment.getGrid();

        // Рисуем каждую клетку
        for (int y = 0; y < environment.getHeight(); y++) {
            for (int x = 0; x < environment.getWidth(); x++) {
                Cell cell = grid[y][x];

                // Рисуем растения
                if (cell.hasPlant()) {
                    g.setColor(new Color(0, 200, 0));
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }

                // Рисуем агентов
                if (cell.hasAgent()) {
                    Agent agent = cell.getAgent();

                    if (agent instanceof Herbivore) {
                        // Синий для травоядных
                        g.setColor(new Color(100, 100, 255));
                    } else if (agent instanceof Carnivore) {
                        // Красный для хищников
                        g.setColor(new Color(255, 100, 100));
                    }

                    g.fillOval(x * cellSize, y * cellSize, cellSize, cellSize);

                    // Рисуем направление (маленькая линия)
                    g.setColor(Color.WHITE);
                    int centerX = x * cellSize + cellSize / 2;
                    int centerY = y * cellSize + cellSize / 2;
                    int dirX = centerX;
                    int dirY = centerY;

                    switch (agent.getDirection()) {
                        case NORTH:
                            dirY -= cellSize / 3;
                            break;
                        case SOUTH:
                            dirY += cellSize / 3;
                            break;
                        case EAST:
                            dirX += cellSize / 3;
                            break;
                        case WEST:
                            dirX -= cellSize / 3;
                            break;
                    }

                    g.drawLine(centerX, centerY, dirX, dirY);
                }
            }
        }

        // Рисуем сетку
        g.setColor(new Color(40, 40, 40));
        for (int x = 0; x <= environment.getWidth(); x++) {
            g.drawLine(x * cellSize, 0, x * cellSize, environment.getHeight() * cellSize);
        }
        for (int y = 0; y <= environment.getHeight(); y++) {
            g.drawLine(0, y * cellSize, environment.getWidth() * cellSize, y * cellSize);
        }
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        setPreferredSize(new Dimension(
                environment.getWidth() * cellSize,
                environment.getHeight() * cellSize
        ));
    }
}

