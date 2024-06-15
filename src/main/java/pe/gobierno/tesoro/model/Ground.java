package pe.gobierno.tesoro.model;

/***
 * Representation of the ground (i.e. a plot)
 */
public class Ground {
    private final GroundType type;
    private boolean isAdventurerPresent;
    private long numberOfTreasure;

    public Ground(GroundType type, boolean isAdventurerPresent, long numberOfTreasure) {
        this.type = type;
        this.isAdventurerPresent = isAdventurerPresent;
        this.numberOfTreasure = numberOfTreasure;
    }

    public Ground(GroundType type, boolean isAdventurerPresent) {
        this(type, isAdventurerPresent, 0);
    }

    public boolean isAdventurerPresent() {
        return isAdventurerPresent;
    }

    public GroundType getType() {
        return type;
    }

    public long getNumberOfTreasure() {
        return numberOfTreasure;
    }

    public void setNumberOfTreasure(long numberOfTreasure) {
        this.numberOfTreasure = numberOfTreasure;
    }

    public void setAdventurerPresent(boolean adventurerPresent) {
        isAdventurerPresent = adventurerPresent;
    }
}
