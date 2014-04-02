package ga.square.magic.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ga.square.magic.GeneticAlgorithm;
import ga.square.magic.Solver;
import ga.square.magic.SolverConfiguration;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class MagicSquareSolver
        implements Solver<MagicSquare, GeneticAlgorithm<MagicSquare>> {
    public MagicSquareSolver() {}

    @Override
    public MagicSquare solve(
            final GeneticAlgorithm<MagicSquare> algorithm,
            final int squareSize,
            final SolverConfiguration configuration) {
        checkArgument(algorithm != null, "Illegal argument algorithm: null");
        checkArgument(configuration != null, "Illegal argument configuration: null");

        Multimap<Integer, MagicSquare> population =
                generateInitialPopulation(
                        algorithm,
                        squareSize,
                        configuration.populationSize());
        int t = 1;

        while (!isEvolutionFinished(configuration.maxGenerations(), t, population)) {
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

            t += 1;
        }

        final List<Integer> fitness = new ArrayList<>(population.keySet());
        Collections.sort(fitness);

        return ArrayListMultimap.create(population).get(fitness.get(0)).get(0);
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

        return population.containsKey(0) || currentGeneration > maxGeneration;
    }

    public static void main(final String[] args) {
        final SolverConfiguration sc = new SolverConfiguration.Builder()
                .maxGenerations(1000)
                .populationSize(1000)
                .parentPoolSize(250)
                .crossoverProbability(0.8)
                .mutationProbability(0.4)
                .build();
        final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(50, 0.3);
        final MagicSquareSolver s = new MagicSquareSolver();
        final MagicSquare r = s.solve(a, 5, sc);
        System.out.println(a.fitnessOf(r));
        System.out.println(r);
    }
}
