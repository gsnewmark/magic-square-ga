package ga.square.magic.impl;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MagicSquareTest {
    @Test
    public void shouldCreateSquareOfGivenSize() {
        final int size = 3;

        final MagicSquare ms = new MagicSquare(size);

        assertEquals(size * size, ms.chromosome().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldntCreateSquareWithNegativeSize() {
        new MagicSquare(-1);
    }

    @Test
    public void shouldCreateSquareFromList() {
        final List<Integer> nums = Arrays.asList(1, 2, 3, 4);
        final MagicSquare ms = new MagicSquare(nums);

        assertEquals(nums.size(), ms.chromosome().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldntCreateSquareFromNullList() {
        new MagicSquare(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldntCreateSquareFromMalformedList() {
        new MagicSquare(Arrays.asList(1, 2, 3));
    }

    @Test
    public void shouldBeAbleToRetrieveCellValuesFromSquare() {
        final List<Integer> nums = Arrays.asList(1, 2, 3, 4);
        final MagicSquare ms = new MagicSquare(nums);

        for (int i = 0; i < nums.size() / 2; ++i) {
            for (int j = 0; j < nums.size() / 2; ++j) {
                assertEquals(
                        nums.get(i + j * nums.size() / 2).intValue(),
                        ms.getCellValue(i, j));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cellXArgumentShouldBePositive() {
        final MagicSquare ms = new MagicSquare(2);

        ms.getCellValue(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cellYArgumentShouldBePositive() {
        final MagicSquare ms = new MagicSquare(2);

        ms.getCellValue(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cellXArgumentShouldBeSmallerThanSquareSide() {
        final MagicSquare ms = new MagicSquare(2);

        ms.getCellValue(2, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cellYArgumentShouldBeSmallerThanSquareSide() {
        final MagicSquare ms = new MagicSquare(2);

        ms.getCellValue(0, 2);
    }
}
