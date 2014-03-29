package ga.square.magic.impl;

import ga.square.magic.Solver;
import ga.square.magic.SolverConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MagicSquareSolver
        implements Solver<MagicSquare, MagicSquareGAOXCrossoverSwapMutation> {
    public MagicSquareSolver() {}

    @Override
    public MagicSquare solve(
            final MagicSquareGAOXCrossoverSwapMutation algorithm,
            final SolverConfiguration configuration,
            final Set<MagicSquare> initialPopulation) {
        // TODO
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<MagicSquare>(initialPopulation).get(0);
    }

    private Set<MagicSquare> generateInitialPopulation() {
        // TODO
        return new HashSet<>(Arrays.asList(new MagicSquare(3)));
    }

    private boolean isEvolutionFinished(
            final int generation, final Set<MagicSquare> population) {
        // TODO
        return false;
    }
}
