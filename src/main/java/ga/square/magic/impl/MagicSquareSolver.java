package ga.square.magic.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ga.square.magic.GeneticAlgorithm;
import ga.square.magic.Solver;
import ga.square.magic.SolverConfiguration;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class MagicSquareSolver
        extends SwingWorker<Solver.SolverResult<MagicSquare>, Solver.SolverResult<MagicSquare>>
        implements Solver<MagicSquare, GeneticAlgorithm<MagicSquare>> {
    private final GeneticAlgorithm<MagicSquare> algorithm;
    private final int squareSize;
    private final SolverConfiguration configuration;
    private final JTextArea resultText;
    private final JLabel totalTimeLabel;
    private long timeEllapsed;

    private SolverResult<MagicSquare> currentBestIndividual;

    public MagicSquareSolver(
            final GeneticAlgorithm<MagicSquare> algorithm,
            final int squareSize,
            final SolverConfiguration configuration,
            final JTextArea resultText,
            final JLabel totalTimeLabel) {
        checkArgument(algorithm != null, "Illegal argument algorithm: null");
        checkArgument(configuration != null, "Illegal argument configuration: null");

        this.algorithm = algorithm;
        this.squareSize = squareSize;
        this.configuration = configuration;
        this.resultText = resultText;
        this.totalTimeLabel = totalTimeLabel;
        this.timeEllapsed = 0;
        this.currentBestIndividual = null;
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
        final long startTime = System.currentTimeMillis();

        while (!isEvolutionFinished(configuration.maxGenerations(), t, population)) {
            if (t % 100 == 0) {
                final SolverResult<MagicSquare> newBestIndividual =
                        findBestIndividual(population, t);
                firePropertyChange(
                        "currentBestIndividual",
                        currentBestIndividual,
                        newBestIndividual);
                currentBestIndividual = newBestIndividual;
                setProgress(
                        new Double(100 * t / configuration.maxGenerations()).intValue());
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

        timeEllapsed = System.currentTimeMillis() - startTime;

        return findBestIndividual(population, t);
    }

    @Override
    protected void done() {
        try {
            setProgress(100);
            if (resultText != null) {
                resultText.setText(get().toString());
            }
            if (totalTimeLabel != null) {
                totalTimeLabel.setText(Double.toString(timeEllapsed / 1000));
            }
        } catch (Exception ignore) {

        }
        super.done();
    }

    private SolverResult<MagicSquare> findBestIndividual(
            final Multimap<Integer, MagicSquare> population,
            final long t) {
        final List<Integer> fitness = new ArrayList<>(population.keySet());
        Collections.sort(fitness);

        return new SolverResult<>(
                ArrayListMultimap.create(population).get(fitness.get(0)).get(0),
                fitness.get(0),
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
    protected SolverResult<MagicSquare> doInBackground() throws Exception {
        return solve();
    }

    public static void main(final String[] args) {
        final long maxGenerations = 1000;
        final SolverConfiguration sc = new SolverConfiguration.Builder()
                .maxGenerations(maxGenerations)
                .populationSize(1000)
                .parentPoolSize(250)
                .crossoverProbability(0.9)
                .mutationProbability(0.2)
                .build();
        final GeneticAlgorithm<MagicSquare> a = new MagicSquareGA(50, 0.6);
        final MagicSquareSolver s = new MagicSquareSolver(a, 5, sc, null, null);

        SolverResult<MagicSquare> result = s.solve();

        if (result != null) {
            System.out.println("Final result:");
            System.out.println("Fitness: " + a.fitnessOf(result.getResult()));
            System.out.println(result);
        }
    }
}
