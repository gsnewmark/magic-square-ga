package ga.square.magic.impl;

import ga.square.magic.GeneticAlgorithm;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class MagicSquareGAOXCrossoverSwapMutation
        implements GeneticAlgorithm<MagicSquare> {
    /**
     * Sum of squared differences of magic sum of square and each column, row,
     * diagonal.
     *
     * So, bigger fitness means worse individual.
     */
    @Override
    public int fitnessOf(final MagicSquare individual) {
        checkArgument(individual != null, "Illegal argument individual: null");

        final int size = individual.getSquareSize();
        final int magicSum = magicSum(size);

        int fitness = 0;
        for (int row = 0; row < size; row++) {
            fitness += squaredDiff(magicSum, rowSum(individual, row));
        }
        for (int column = 0; column < size; column++) {
            fitness += squaredDiff(magicSum, columnSum(individual, column));
        }
        fitness += squaredDiff(magicSum, leftDiagonalSum(individual));
        fitness += squaredDiff(magicSum, rightDiagonalSum(individual));

        return fitness;
    }

    private int magicSum(final int n) {
        return n * (n * n + 1) / 2;
    }

    private int rowSum(final MagicSquare individual, final int row) {
        int sum = 0;
        for (int x = 0; x < individual.getSquareSize(); x++) {
            sum += individual.getCellValue(x, row);
        }
        return sum;
    }

    private int columnSum(final MagicSquare individual, final int column) {
        int sum = 0;
        for (int y = 0; y < individual.getSquareSize(); y++) {
            sum += individual.getCellValue(column, y);
        }
        return sum;
    }

    private int leftDiagonalSum(final MagicSquare individual) {
        int sum = 0;
        for (int i = 0; i < individual.getSquareSize(); i++) {
            sum += individual.getCellValue(i, i);
        }
        return sum;
    }

    private int rightDiagonalSum(final MagicSquare individual) {
        final int size = individual.getSquareSize();
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += individual.getCellValue(size - 1 - i, i);
        }
        return sum;
    }

    private int squaredDiff(final int x, final int y) {
        return (x - y) * (x - y);
    }

    @Override
    public MagicSquare randomIndividual(final int sideSize) {
        final ArrayList<Integer> chromosome =
                new ArrayList<>(sideSize * sideSize);
        for (int i = 1; i <= sideSize * sideSize; i++) {
            chromosome.add(i);
        }
        Collections.shuffle(chromosome);

        return new MagicSquare(chromosome);
    }

    /**
     * Simple swap mutation: swaps two random elements in the chromosome.
     */
    @Override
    public MagicSquare mutate(final MagicSquare individual) {
        checkArgument(individual != null, "Illegal argument individual: null");

        final List<Integer> chromosome =
                new ArrayList<>(individual.chromosome());

        final int i = RandomUtils.nextInt(0, chromosome.size());
        int j = RandomUtils.nextInt(0, chromosome.size());
        while (j == i) {
            j = RandomUtils.nextInt(0, chromosome.size());
        }

        final int ithGene = chromosome.get(i);
        final int jthGene = chromosome.get(j);

        chromosome.set(i, jthGene);
        chromosome.set(j, ithGene);

        return new MagicSquare(chromosome);
    }

    /**
     * OX-crossover: choose a crossing point, take genes of first parent up to
     * this point directly, then add missing genes from the second parent
     * preserving the order they appear in it.
     */
    @Override
    public MagicSquare crossover(
            final MagicSquare father, final MagicSquare mother) {
        checkArgument(father != null, "Illegal argument father: null");
        checkArgument(mother != null, "Illegal argument mother: null");
        checkArgument(
                father.getSquareSize() == mother.getSquareSize(),
                "Parents have unaligned sizes");

        final List<Integer> fatherChromosome = father.chromosome();
        final List<Integer> motherChromosome = mother.chromosome();
        final List<Integer> childChromosome =
                new ArrayList<>(fatherChromosome.size());

        final Set<Integer> unusedGenes = new HashSet<>(fatherChromosome);
        final int crossPoint =
                RandomUtils.nextInt(1, fatherChromosome.size() - 1);

        for (int i = 0; i < crossPoint; i++) {
            childChromosome.add(fatherChromosome.get(i));
            unusedGenes.remove(fatherChromosome.get(i));
        }

        for (final Integer gene : motherChromosome) {
            if (unusedGenes.isEmpty()) {
                break;
            } else if (unusedGenes.contains(gene)) {
                childChromosome.add(gene);
                unusedGenes.remove(gene);
            }
        }

        return new MagicSquare(childChromosome);
    }
}
