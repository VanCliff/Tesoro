package pe.gobierno.tesoro.model;

/***
 * Representation of the ground (i.e. a plot)
 */
public class Ground {
    private GroundType type;
    private boolean isAdventurerPresent;

    public Ground(GroundType type, boolean isAdventurerPresent) {
        this.type = type;
        this.isAdventurerPresent = isAdventurerPresent;
    }

    public Ground(GroundType type) {
        this(type, false);
    }

}
