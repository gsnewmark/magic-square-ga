package ga.square.magic.impl;

import ga.square.magic.Individual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class MagicSquare implements Individual<Integer> {
    final ArrayList<Integer> chromosome;
    final int size;

    public MagicSquare(final int size) {
        checkArgument(size > 0, "Size of square should be positive");
        this.size = size;

        chromosome = new ArrayList<Integer>(this.size * this.size);
        for (int i = 1; i <= this.size * this.size; ++i) {
            chromosome.add(i);
        }
    }

    public MagicSquare(final List<Integer> chromosome) {
        checkArgument(chromosome != null, "Illegal argument chromosome: null");

        final int size = Double.valueOf(Math.sqrt(chromosome.size())).intValue();
        checkArgument(
                size * size == chromosome.size(),
                "Chromosome should encode a square");

        this.size = size;
        this.chromosome = new ArrayList<>(chromosome);
    }

    @Override
    public List<Integer> chromosome() {
        return Collections.unmodifiableList(chromosome);
    }

    public int getCellValue(final int x, final int y) {
        checkArgument(x >= 0, "Cell x index should be non-negative");
        checkArgument(x < size, "Cell x index should be smaller than size" );
        checkArgument(y >= 0, "Cell y index should be non-negative");
        checkArgument(y < size, "Cell y index should be smaller than size" );

        return chromosome.get(y * size + x);
    }
}
