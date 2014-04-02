package ga.square.magic.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ga.square.magic.CallableWithProgress;
import ga.square.magic.GeneticAlgorithm;
import ga.square.magic.Solver;
import ga.square.magic.SolverConfiguration;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;

public class MagicSquareSolver
        implements Solver<MagicSquare, GeneticAlgorithm<MagicSquare>>,
        CallableWithProgress<Solver.SolverResult<MagicSquare>> {
    private final GeneticAlgorithm<MagicSquare> algorithm;
    private final int squareSize;
    private final SolverConfiguration configuration;

    private SolverResult<MagicSquare> currentBestIndividual;

    public MagicSquareSolver(
            final GeneticAlgorithm<MagicSquare> algorithm,
            final int squareSize,
            final SolverConfiguration configuration) {
        checkArgument(algorithm != null, "Illegal argument algorithm: null");
        checkArgument(configuration != null, "Illegal argument configuration: null");

        this.algorithm = algorithm;
        this.squareSize = squareSize;
        this.configuration = configuration;
        currentBestIndividual = null;
    }

    @Override
    public SolverResult<MagicSquare> solve() {
        checkArgument(algorithm != null, "Illegal argument algorithm: null");
        checkArgument(configuration != null, "Illegal argument configuration: null");

        Multimap<Integer, MagicSquare> population =
                generateInitialPopulation(
                        algorithm,
                        squareSize,
                        configuration.populationSize());
        long t = 0;

        while (!isEvolutionFinished(configuration.maxGenerations(), t, population)) {
            if (t % 100 == 0) {
                currentBestIndividual = findBestIndividual(population, t);
            }

            t += 1;

            final List<ImmutablePair<MagicSquare, MagicSquare>> parents =
                    algorithm.selectParents(configuration.parentPoolSize(), population);
            final Multimap<Integer, MagicSquare> children =
                    ArrayListMultimap.create();

            for (final ImmutablePair<MagicSquare, MagicSquare> p : parents) {
                if (RandomUtils.nextDouble(0, 1) < configuration.crossoverProbability()) {
                    MagicSquare child = algorithm.crossover(p.getLeft(), p.getRight());
                    if (RandomUtils.nextDouble(0, 1) < configuration.mutationProbability()) {
                        child = algorithm.mutate(child);
                    }
                    children.put(algorithm.fitnessOf(child), child);
                }
            }

            population = algorithm.nextGenerationFrom(population, children);
        }

        return findBestIndividual(population, t);
    }

    private SolverResult<MagicSquare> findBestIndividual(
            final Multimap<Integer, MagicSquare> population,
            final long t) {
        final List<Integer> fitness = new ArrayList<>(population.keySet());
        Collections.sort(fitness);

        return new SolverResult<>(
                ArrayListMultimap.create(population).get(fitness.get(0)).get(0),
                t);
    }

    private ArrayListMultimap<Integer, MagicSquare> generateInitialPopulation(
            final GeneticAlgorithm<MagicSquare> algorithm,
            final int squareSize,
            final long populationSize) {
        final ArrayListMultimap<Integer, MagicSquare> initial =
                ArrayListMultimap.create();

        for (long i = 0; i < populationSize; i++) {
            final MagicSquare square = algorithm.randomIndividual(squareSize);
            initial.put(algorithm.fitnessOf(square), square);
        }

        return initial;
    }

    private boolean isEvolutionFinished(
            final long maxGeneration,
            final long currentGeneration,
            final Multimap<Integer, MagicSquare> population) {

        return population.containsKey(0) || currentGeneration >= maxGeneration;
    }

    @Override
    public SolverResult<MagicSquare> currentProgress() {
        if (currentBestIndividual == null) {
            return null;
        }

        return new SolverResult<>(
                currentBestIndividual.getResult(),
                currentBestIndividual.getGeneration());
    }

    @Override
    public SolverResult<MagicSquare> call() throws Exception {
        return solve();
    }

    public static void main(final String[] args) {
        final long maxGenerations = 1000;
        final SolverConfiguration sc = new SolverConfiguration.Builder()
                .maxGenerations(maxGenerations)
                .populationSize(1000)
                .parentPoolSize(250)
                .crossoverProbability(0.8)
                .mutationProbability(0.4)
                .build();
        final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(50, 0.3);
        final MagicSquareSolver s = new MagicSquareSolver(a, 5, sc);

        // Start algorithm in a separate thread in order to be able to receive
        // info about algorithm's progress
        final ExecutorService es = Executors.newSingleThreadExecutor();
        final Future<SolverResult<MagicSquare>> asyncResult = es.submit(s);

        // To stop execution of the algorithm before it finishes use
        // asyncResult.cancel(true);

        // Retrieving information about the current progress
        // HACK polling is bad
        while (!asyncResult.isDone()) {
            final SolverResult<MagicSquare> cp = s.currentProgress();
            if (cp != null) {
                System.out.println(cp.getGeneration() + "/" + maxGenerations + ":");
                System.out.println("Fitness: " + a.fitnessOf(cp.getResult()));
                System.out.println(cp.getResult());
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Extracting final result from the future
        SolverResult<MagicSquare> result = null;
        try {
            result = asyncResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (result != null) {
            System.out.println("Final result:");
            System.out.println("Fitness: " + a.fitnessOf(result.getResult()));
            System.out.println(result);
        }
    }
}
