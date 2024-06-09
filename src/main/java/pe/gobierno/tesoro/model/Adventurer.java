package pe.gobierno.tesoro.model;

/***
 * Representation of the adventurer
 */
public class Adventurer {
    private final long id;
    private final String name;
    private long posX;
    private long posY;
    private char orientation;
    private long treasure;
    private String movementSequence;

    public Adventurer(long id, String name, long posX, long posY, String movementSequence, char orientation) {
        this.id = id;
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.movementSequence = movementSequence;
        this.orientation = orientation;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPosX() {
        return posX;
    }

    public void setPosX(long posX) {
        this.posX = posX;
    }

    public long getPosY() {
        return posY;
    }

    public void setPosY(long posY) {
        this.posY = posY;
    }

    public char getOrientation() {
        return orientation;
    }

    public void setOrientation(char orientation) {
        this.orientation = orientation;
    }

    public long getTreasure() {
        return treasure;
    }

    public void setTreasure(long treasure) {
        this.treasure = treasure;
    }

    public String getMovementSequence() {
        return movementSequence;
    }

    public void setMovementSequence(String movementSequence) {
        this.movementSequence = movementSequence;
    }

    @Override
    public String toString() {
        return "Adventurer {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", orientation=" + orientation +
                ", treasure=" + treasure +
                ", movementSequence='" + movementSequence + '\'' +
                '}';
    }
}
