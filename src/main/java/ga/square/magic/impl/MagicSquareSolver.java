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
import java.util.Map;

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

        final ArrayListMultimap<Integer, MagicSquare> population =
                generateInitialPopulation(
                        algorithm,
                        squareSize,
                        configuration.populationSize());
        int t = 1;

        while (!isEvolutionFinished(configuration.maxGenerations(), t, population)) {
            final List<ImmutablePair<MagicSquare, MagicSquare>> parents =
                    algorithm.selectParents(configuration.parentPoolSize(), population);
            final List<MagicSquare> children = new ArrayList<>();

            for (final ImmutablePair<MagicSquare, MagicSquare> p : parents) {
                if (RandomUtils.nextDouble(0, 1) < configuration.crossoverProbability()) {
                    final MagicSquare child = algorithm.crossover(p.getLeft(), p.getRight());
                    if (RandomUtils.nextDouble(0, 1) < configuration.mutationProbability()) {
                        children.add(algorithm.mutate(child));
                    } else {
                        children.add(child);
                    }
                }
            }

            for (final MagicSquare ms : children) {
                population.put(algorithm.fitnessOf(ms), ms);
            }

            final Multimap<Integer, MagicSquare> toRemove =
                    algorithm.selectForRemoval(children.size(), population);

            for (final Map.Entry<Integer, MagicSquare> e : toRemove.entries()) {
                population.remove(e.getKey(), e.getValue());
            }

            t += 1;
        }

        final List<Integer> fitness = new ArrayList<>(population.keySet());
        Collections.sort(fitness);

        return population.get(fitness.get(0)).get(0);
    }

    private ArrayListMultimap<Integer, MagicSquare> generateInitialPopulation(
            final GeneticAlgorithm<MagicSquare> algorithm,
            final int squareSize,
            final int populationSize) {
        final ArrayListMultimap<Integer, MagicSquare> initial =
                ArrayListMultimap.create();

        for (int i = 0; i < populationSize; i++) {
            final MagicSquare square = algorithm.randomIndividual(squareSize);
            initial.put(algorithm.fitnessOf(square), square);
        }

        return initial;
    }

    private boolean isEvolutionFinished(
            final int maxGeneration,
            final int currentGeneration,
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
        final MagicSquare r = s.solve(a, 4, sc);
        System.out.println(a.fitnessOf(r));
        System.out.println(r);
    }
}
