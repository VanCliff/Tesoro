package pe.gobierno.tesoro.model;

import java.util.List;

/***
 * Representation of the adventurer
 */
public class Adventurer {
    private final String name;
    private long posX;
    private long posY;
    private CardinalPointsType orientation;
    private long treasure;
    private List<Character> movementSequence;

    public Adventurer(String name) {
        this.name = name;
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

    public CardinalPointsType getOrientation() {
        return orientation;
    }

    public void setOrientation(CardinalPointsType orientation) {
        this.orientation = orientation;
    }

    public long getTreasure() {
        return treasure;
    }

    public void setTreasure(long treasure) {
        this.treasure = treasure;
    }

    public List<Character> getMovementSequence() {
        return movementSequence;
    }

    public void setMovementSequence(List<Character> movementSequence) {
        this.movementSequence = movementSequence;
    }

}
