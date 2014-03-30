package ga.square.magic.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ga.square.magic.GeneticAlgorithm;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class MagicSquareGAOXCrossoverSwapMutationPanmixiaSelection
        implements GeneticAlgorithm<MagicSquare> {
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
    public List<ImmutablePair<MagicSquare, MagicSquare>> selectParents(
            final Multimap<Integer, MagicSquare> population) {
        checkArgument(population != null, "Illegal argument population: null");
        checkArgument(
                population.size() > 1,
                "Population should contain more than one individual");

        final List<MagicSquare> possibleParents =
                new ArrayList<>(population.values());
        Collections.shuffle(possibleParents);

        final Pair<Integer, Integer> indices =
                randomDifferentIndices(possibleParents.size());

        return Arrays.asList(
                new ImmutablePair<>(
                        possibleParents.get(indices.getLeft()),
                        possibleParents.get(indices.getRight())));
    }

    private Pair<Integer, Integer> randomDifferentIndices(final int max) {
        final int i = RandomUtils.nextInt(0, max);
        int j = RandomUtils.nextInt(0, max);
        while (j == i) {
            j = RandomUtils.nextInt(0, max);
        }
        return new ImmutablePair<>(i, j);
    }

    @Override
    public Multimap<Integer, MagicSquare> selectForRemoval(
            final int n,
            final Multimap<Integer, MagicSquare> population) {
        checkArgument(
                n <= population.size(),
                "Can't remove more individuals than population contains.");
        checkArgument(population != null, "Illegal argument population: null");
        checkArgument(
                population.size() > 1,
                "Population should contain more than one individual");

        final List<Integer> fitnessValues = new ArrayList<>(population.keySet());
        Collections.sort(fitnessValues);
        Collections.reverse(fitnessValues);
        final Multimap<Integer, MagicSquare> result = ArrayListMultimap.create();

        while (result.size() != n && !fitnessValues.isEmpty()) {
            final Stack<MagicSquare> candidates = new Stack<>();
            for (final MagicSquare ms : population.get(fitnessValues.get(0))) {
                candidates.push(ms);
            }

            while (!candidates.isEmpty() && result.size() != n) {
                result.put(fitnessValues.get(0), candidates.pop());
            }

            fitnessValues.remove(0);
        }

        return result;
    }

    /**
     * Simple swap mutation: swaps two random elements in the chromosome.
     */
    @Override
    public MagicSquare mutate(final MagicSquare individual) {
        checkArgument(individual != null, "Illegal argument individual: null");

        final List<Integer> chromosome =
                new ArrayList<>(individual.chromosome());

        final Pair<Integer, Integer> indices =
                randomDifferentIndices(chromosome.size());

        final int ithGene = chromosome.get(indices.getLeft());
        final int jthGene = chromosome.get(indices.getRight());

        chromosome.set(indices.getLeft(), jthGene);
        chromosome.set(indices.getRight(), ithGene);

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
