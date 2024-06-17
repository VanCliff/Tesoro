package pe.gobierno.tesoro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardinalPointsTypeTest {

    @Test
    void testTurnRight() {
        assertEquals(CardinalPointsType.E, CardinalPointsType.N.turnRight());
        assertEquals(CardinalPointsType.S, CardinalPointsType.E.turnRight());
        assertEquals(CardinalPointsType.O, CardinalPointsType.S.turnRight());
        assertEquals(CardinalPointsType.N, CardinalPointsType.O.turnRight());
    }

    @Test
    void testTurnLeft() {
        assertEquals(CardinalPointsType.O, CardinalPointsType.N.turnLeft());
        assertEquals(CardinalPointsType.N, CardinalPointsType.E.turnLeft());
        assertEquals(CardinalPointsType.E, CardinalPointsType.S.turnLeft());
        assertEquals(CardinalPointsType.S, CardinalPointsType.O.turnLeft());
    }
}