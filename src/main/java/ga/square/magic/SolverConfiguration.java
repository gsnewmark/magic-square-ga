package ga.square.magic;

import static com.google.common.base.Preconditions.checkArgument;

public class SolverConfiguration {
    public static class Builder {
        private long maxGenerations;
        private long populationSize;
        private double crossoverProbability;
        private double mutationProbability;

        public Builder() {}

        public Builder maxGenerations(final long maxGenerations) {
            this.maxGenerations = maxGenerations;
            return this;
        }

        public Builder populationSize(final long populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        public Builder crossoverProbability(final double crossoverProbability) {
            this.crossoverProbability = crossoverProbability;
            return this;
        }

        public Builder mutationProbability(final double mutationProbability) {
            this.mutationProbability = mutationProbability;
            return this;
        }

        public SolverConfiguration build() {
            return new SolverConfiguration(
                    maxGenerations,
                    populationSize,
                    crossoverProbability,
                    mutationProbability);
        }
    }

    private final long maxGenerations;
    private final long populationSize;
    private final double crossoverProbability;
    private final double mutationProbability;

    public SolverConfiguration(
            final long maxGenerations,
            final long populationSize,
            final double crossoverProbability,
            final double mutationProbability) {
        checkArgument(
                populationSize >= 0,
                "Populations should contain some individuals");
        this.maxGenerations = maxGenerations;
        this.populationSize = populationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
    }

    public long maxGenerations() {
        return maxGenerations;
    }

    public long populationSize() {
        return populationSize;
    }

    public double crossoverProbability() {
        return crossoverProbability;
    }

    public double mutationProbability() {
        return mutationProbability;
    }
}
