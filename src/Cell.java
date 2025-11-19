/**
 * Класс клетки сетки
 */
public class Cell {
    private boolean hasPlant;
    private Agent agent;

    public Cell() {
        this.hasPlant = false;
        this.agent = null;
    }

    public boolean hasPlant() {
        return hasPlant;
    }

    public void setPlant(boolean hasPlant) {
        this.hasPlant = hasPlant;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public boolean hasAgent() {
        return agent != null;
    }
}

