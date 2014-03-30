package ga.square.magic.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MagicSquareGATest {
    private MagicSquareGA algorithm;

    @Before
    public void setup() {
        algorithm = new MagicSquareGA(1, 0.1);
    }

    @Test
    public void fitnessOf3x3OrderedSquareShouldBe180() {
        final MagicSquare ms = new MagicSquare(3);
        assertEquals(180, algorithm.fitnessOf(ms));
    }

    @Test
    public void fitnessOfMagicSquareShouldBeZero() {
        final MagicSquare ms = new MagicSquare(
                Arrays.asList(2, 7, 6, 9, 5, 1, 4, 3, 8));
        assertEquals(0, algorithm.fitnessOf(ms));
    }
}
