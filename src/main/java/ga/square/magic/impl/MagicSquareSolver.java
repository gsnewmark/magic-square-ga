package ga.square.magic.impl;

import ga.square.magic.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MagicSquareSolver implements Solver<MagicSquare, MagicSquareGA> {
    private final int size;
    private final MagicSquareGA algo;

    public MagicSquareSolver(final int size) {
        this.size = size;
        algo = createAlgorithm();
    }

    @Override
    public MagicSquareGA createAlgorithm() {
        return new MagicSquareGA();
    }

    @Override
    public Set<MagicSquare> generateInitialPopulation() {
        // TODO
        return new HashSet<MagicSquare>(Arrays.asList(new MagicSquare(size)));
    }

    @Override
    public boolean isEvolutionFinished(
            final int generation, final Set<MagicSquare> population) {
        // TODO
        return false;
    }

    @Override
    public MagicSquare solve(final Set<MagicSquare> initialPopulation) {
        // TODO
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<MagicSquare>(initialPopulation).get(0);
    }
}
