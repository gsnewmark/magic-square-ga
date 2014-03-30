package ga.square.magic;

public interface Solver<I extends Individual, GA extends GeneticAlgorithm<I>> {
    I solve(GA algo, int squareSize, SolverConfiguration configuration);
}
