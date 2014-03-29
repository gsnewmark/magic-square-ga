package ga.square.magic;

public interface SolverConfiguration {
    int maxGenerations();
    int populationSize();
    double crossoverProbability();
    double mutationProbability();
}
