package ga.square.magic.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ga.square.magic.GeneticAlgorithm;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class MagicSquareGA
        implements GeneticAlgorithm<MagicSquare> {
    private final int T;
    private final double constrainedSelectionPart;

    /**
     * @param T size of tournament
     * @param constrainedSelectionPart percentage of individuals chosen to the
     *                                 parent pool using constrained selection
     */
    public MagicSquareGA(
            final int T,
            final double constrainedSelectionPart) {
        this.T = T;
        this.constrainedSelectionPart = constrainedSelectionPart;
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

    /**
     * Combination of elite and roulette-wheel selection methods.
     */
    @Override
    public List<ImmutablePair<MagicSquare, MagicSquare>> selectParents(
            final long n,
            final Multimap<Integer, MagicSquare> population) {
        checkArgument(population != null, "Illegal argument population: null");
        checkArgument(
                population.size() > 1,
                "Population should contain more than one individual");

        // TODO should be configurable
        final double elitePercent = 0.1;
        final int eliteQuantity = new Double(elitePercent * n).intValue();
        final List<MagicSquare> parentPool = eliteSelection(eliteQuantity, population);

        parentPool.addAll(rouletteWheelSelection(n - eliteQuantity, population));

        final List<ImmutablePair<MagicSquare, MagicSquare>> result =
                new ArrayList<>();

        while (parentPool.size() > 1) {
            final Pair<Integer, Integer> indices =
                    randomDifferentIndices(parentPool.size());
            final int first = indices.getLeft();
            final int second = indices.getRight();
            result.add(new ImmutablePair<>(
                    parentPool.get(first), parentPool.get(second)));

            if (first < second) {
                parentPool.remove(first);
                parentPool.remove(second - 1);
            } else {
                parentPool.remove(second);
                parentPool.remove(first - 1);
            }
        }

        return result;
    }

    private List<MagicSquare> eliteSelection(
            final long n,
            final Multimap<Integer, MagicSquare> population) {
        assert population != null;

        final List<MagicSquare> parentPool = new ArrayList<>();

        final List<Integer> fitnessValues = new ArrayList<>(population.keySet());
        Collections.sort(fitnessValues);

        while (parentPool.size() < n && fitnessValues.size() > 0) {
            final int fitness = fitnessValues.remove(0);

            for (final MagicSquare ms : population.get(fitness)) {
                if (parentPool.size() < n) {
                    parentPool.add(ms);
                }
            }
        }

        return parentPool;
    }

    private List<MagicSquare> rouletteWheelSelection(
            final long n,
            final Multimap<Integer, MagicSquare> population) {
        assert population != null;

        final List<MagicSquare> parentPool = new ArrayList<>();
        final Random rnd = new Random();

        double totalReverseFitness = 0;
        final List<ImmutablePair<MagicSquare, Double>> populationWithReverseFitness =
                new ArrayList<>();
        for (final Map.Entry<Integer, MagicSquare> e : population.entries()) {
            final double reverseFitness = 1.0 / e.getKey();
            totalReverseFitness += reverseFitness;
            populationWithReverseFitness.add(
                    new ImmutablePair<>(e.getValue(), reverseFitness));
        }
        final List<ImmutablePair<MagicSquare, Double>> populationWithProbability =
                new ArrayList<>();
        for (final ImmutablePair<MagicSquare, Double> e : populationWithReverseFitness) {
            populationWithProbability.add(
                    new ImmutablePair<>(e.getLeft(), e.getRight() / totalReverseFitness));
        }

        while (parentPool.size() < n) {
            parentPool.add(rouletteWheelSelectOne(rnd, populationWithProbability));
        }

        return parentPool;
    }

    private MagicSquare rouletteWheelSelectOne(
            final Random rnd,
            final List<ImmutablePair<MagicSquare, Double>> populationWithProb) {
        assert populationWithProb.size() > 0;

        MagicSquare selected = populationWithProb.get(0).getLeft();
        double total = populationWithProb.get(0).getRight();

        for(int i = 1; i < populationWithProb.size(); i++) {
            final double normalizedFitness = populationWithProb.get(i).getRight();
            total += normalizedFitness;
            if (rnd.nextDouble() <= (normalizedFitness / total)) {
                selected = populationWithProb.get(i).getLeft();
            }
        }

        return selected;
    }

    /**
     * Children is used as a next generation.
     */
    @Override
    public Multimap<Integer, MagicSquare> nextGenerationFrom(
            final Multimap<Integer, MagicSquare> population,
            final Multimap<Integer, MagicSquare> children) {
        checkArgument(population != null, "Illegal argument population: null");
        checkArgument(children != null, "Illegal argument population: null");
        checkArgument(
                population.size() > 1,
                "Population should contain more than one individual");

        return children;
    }

    /**
     * Swap mutation: swaps either two random elements in the chromosome, two
     * rows of the encoded square, or two columns of the encoded square.
     */
    @Override
    public MagicSquare mutate(final MagicSquare individual) {
        checkArgument(individual != null, "Illegal argument individual: null");

        final double rand = RandomUtils.nextDouble(0, 1.0);
        if (rand < 0.3) {
            return swapColumns(individual);
        } else if (rand < 0.6) {
            return swapRows(individual);
        } else {
            return swapGenes(individual);
        }
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

    private Pair<Integer, Integer> randomDifferentIndices(final int max) {
        final int i = RandomUtils.nextInt(0, max);
        int j = RandomUtils.nextInt(0, max);
        while (j == i) {
            j = RandomUtils.nextInt(0, max);
        }
        return new ImmutablePair<>(i, j);
    }

    private MagicSquare swapGenes(final MagicSquare individual) {
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

    private MagicSquare swapRows(final MagicSquare individual) {
        final Pair<Integer, Integer> indices =
                randomDifferentIndices(individual.getSquareSize());

        final List<Integer> chromosome =
                new ArrayList<>(individual.chromosome());

        final List<Integer> ithRow = new ArrayList<>();
        final List<Integer> jthRow = new ArrayList<>();
        for (int i = 0; i < individual.getSquareSize(); i++) {
            ithRow.add(individual.getCellValue(i, indices.getLeft()));
            jthRow.add(individual.getCellValue(i, indices.getRight()));
        }

        for (int i = 0; i < individual.getSquareSize(); i++) {
            chromosome.set(
                    indices.getLeft() * individual.getSquareSize() + i,
                    jthRow.get(i));
            chromosome.set(
                    indices.getRight() * individual.getSquareSize() + i,
                    ithRow.get(i));
        }

        return new MagicSquare(chromosome);
    }

    private MagicSquare swapColumns(final MagicSquare individual) {
        final Pair<Integer, Integer> indices =
                randomDifferentIndices(individual.getSquareSize());

        final List<Integer> chromosome =
                new ArrayList<>(individual.chromosome());

        final List<Integer> ithColumn = new ArrayList<>();
        final List<Integer> jthColumn = new ArrayList<>();
        for (int i = 0; i < individual.getSquareSize(); i++) {
            ithColumn.add(individual.getCellValue(indices.getLeft(), i));
            jthColumn.add(individual.getCellValue(indices.getRight(), i));
        }

        for (int i = 0; i < individual.getSquareSize(); i++) {
            chromosome.set(
                    i * individual.getSquareSize() + indices.getLeft(),
                    jthColumn.get(i));
            chromosome.set(
                    i * individual.getSquareSize() + indices.getRight(),
                    ithColumn.get(i));
        }

        return new MagicSquare(chromosome);
    }
}
