package ga.square.magic;

import java.util.Set;

public interface Solver<I extends Individual, GA extends GeneticAlgorithm<I>> {
    I solve(
            GA algo,
            SolverConfiguration configuration,
            Set<I> initialPopulation);
}
