package pe.gobierno.tesoro.tu.model;

/***
 * Representation of the different cardinal points
 * ⚠ ORDER OF THE ENUM IS IMPORTANT ⚠
 */
public enum CardinalPointsType {
    N,
    E,
    S,
    O;

    /**
     * This method return the current CardinalPointsType + 1 (turning right).
     * N  + turnRight() -> E
     * E  + turnRight() -> S
     * S  + turnRight() -> O
     * O  + turnRight() -> N
     *
     * @return The next CardinalPointsType
     */
    public CardinalPointsType turnRight() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * This method return the current CardinalPointsType - 1 (turning left).
     * N + turnLeft() -> O
     * O  + turnLeft() -> S
     * S + turnLeft() -> E
     * E  + turnLeft() -> N
     *
     * @return The previous CardinalPointsType
     */
    public CardinalPointsType turnLeft() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }
}
