package ga.square.magic;

import java.util.Set;

public interface Solver<I extends Individual, GA extends GeneticAlgorithm<I>> {
    GA createAlgorithm();

    Set<I> generateInitialPopulation();
    boolean isEvolutionFinished(int generation, Set<I> population);
    I solve(Set<I> initialPopulation);
}
