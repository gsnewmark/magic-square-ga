package ga.square.magic;

public class SolverConfiguration {
    private final int maxGenerations;
    private final int populationSize;
    private final double crossoverProbability;
    private final double mutationProbability;

    public SolverConfiguration(
            final int maxGenerations,
            final int populationSize,
            final double crossoverProbability,
            final double mutationProbability) {
        this.maxGenerations = maxGenerations;
        this.populationSize = populationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
    }

    public int maxGenerations() {
        return maxGenerations;
    }

    public int populationSize() {
        return populationSize;
    }

    public double crossoverProbability() {
        return crossoverProbability;
    }

    public double mutationProbability() {
        return mutationProbability;
    }
}
