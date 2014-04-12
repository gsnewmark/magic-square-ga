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
    private final double k;

    /**
     * @param T size of tournament
     * @param constrainedSelectionPart percentage of individuals chosen to the
     *                                 parent pool using constrained selection
     */
    public MagicSquareGA(
            final int T,
            final double constrainedSelectionPart,
            final double k) {
        this.T = T;
        this.constrainedSelectionPart = constrainedSelectionPart;
        this.k = k;
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

        fitness += asymmetricPenaltyOf(individual);

        return fitness;
    }

    private int asymmetricPenaltyOf(final MagicSquare individual) {
        final int size = individual.getSquareSize();
        final int border = size / 2;

        int diff = 0;
        for (int y = 0; y < border; ++y) {
            for (int x = 0; x < border; ++x) {
                final int leftDiff =
                        squaredDiff(
                                individual.getCellValue(x, y),
                                individual.getCellValue(x, size - 1 - y));
                final int rightDiff =
                        squaredDiff(
                                individual.getCellValue(size - 1 - x, y),
                                individual.getCellValue(size - 1 - x, size - 1 - y));
                diff += squaredDiff(leftDiff, rightDiff);
            }
        }

        return new Double(diff * k).intValue();
    }

    /**
     * Part of individuals are selected randomly from population, other part is
     * formed from individuals fitness of which are better than the average
     * fitness of the population.
     *
     * Distribution between parts are controlled by the
     * {@code constrainedSelectionPart} parameter.
     */
    @Override
    public List<ImmutablePair<MagicSquare, MagicSquare>> selectParents(
            final long n,
            final Multimap<Integer, MagicSquare> population) {
        checkArgument(population != null, "Illegal argument population: null");
        checkArgument(
                population.size() > 1,
                "Population should contain more than one individual");

        double averageFitness = 0;
        for (Map.Entry<Integer, MagicSquare> e : population.entries()) {
            averageFitness += e.getKey();
        }
        averageFitness /= population.values().size();

        final List<MagicSquare> possibleConstrainedParents = new ArrayList<>();
        for (Map.Entry<Integer, MagicSquare> e : population.entries()) {
            if (e.getKey() <= averageFitness) {
                possibleConstrainedParents.add(e.getValue());
            }
        }
        // HACK
        if (possibleConstrainedParents.size() < 2) {
            possibleConstrainedParents.add(possibleConstrainedParents.get(0));
        }

        final List<ImmutablePair<MagicSquare, MagicSquare>> result =
                new ArrayList<>();

        final long constrainedSelectionN =
                new Double(n  * constrainedSelectionPart).longValue();
        for (long i = 0; i < constrainedSelectionN; ++i) {
            final Pair<Integer, Integer> indices =
                    randomDifferentIndices(possibleConstrainedParents.size());
            result.add(new ImmutablePair<>(
                    possibleConstrainedParents.get(indices.getLeft()),
                    possibleConstrainedParents.get(indices.getRight())));
        }

        final List<MagicSquare> possibleParents = new ArrayList<>(population.values());
        for (long i = 0; i < n - constrainedSelectionN; ++i) {
            final Pair<Integer, Integer> indices =
                    randomDifferentIndices(possibleParents.size());
            result.add(new ImmutablePair<>(
                    possibleParents.get(indices.getLeft()),
                    possibleParents.get(indices.getRight())));
        }

        return result;
    }

    /**
     * Children is added to population and the tournament selection is used.
     * Size of population is not changed.
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

        final Multimap<Integer, MagicSquare> tournament =
                ArrayListMultimap.create(population);
        for (final Map.Entry<Integer, MagicSquare> e : children.entries()) {
            tournament.put(e.getKey(), e.getValue());
        }

        final List<Map.Entry<Integer, MagicSquare>> entries =
                new ArrayList<>(tournament.entries());
        final Multimap<Integer, Map.Entry<Integer, MagicSquare>> tournamentRes =
                ArrayListMultimap.create();
        for (Map.Entry<Integer, MagicSquare> e : entries) {
            int games = 0;
            int wins = 0;
            while (games < T) {
                final int rand = RandomUtils.nextInt(0, entries.size());
                final Map.Entry<Integer, MagicSquare> entry = entries.get(rand);
                if (entry.getValue() != e.getValue()) {
                    games += 1;
                    if (entry.getKey() > e.getKey()) {
                        wins += 1;
                    }
                }
            }

            tournamentRes.put(wins, e);
        }

        final List<Integer> winValues = new ArrayList<>(tournamentRes.keySet());
        Collections.sort(winValues);

        while (tournament.size() != population.size() && !winValues.isEmpty()) {
            final Stack<Map.Entry<Integer, MagicSquare>> candidates = new Stack<>();
            for (final Map.Entry<Integer, MagicSquare> ms :
                    tournamentRes.get(winValues.get(0))) {
                candidates.push(ms);
            }

            while (!candidates.isEmpty() && tournament.size() != population.size()) {
                final Map.Entry<Integer, MagicSquare> e = candidates.pop();
                tournament.remove(e.getKey(), e.getValue());
            }

            winValues.remove(0);
        }

        return tournament;
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
