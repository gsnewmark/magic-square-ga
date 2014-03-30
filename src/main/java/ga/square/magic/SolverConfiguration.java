package ga.square.magic;

import static com.google.common.base.Preconditions.checkArgument;

public class SolverConfiguration {
    public static class Builder {
        private int maxGenerations;
        private int populationSize;
        private int parentPoolSize;
        private double crossoverProbability;
        private double mutationProbability;

        public Builder() {}

        public Builder maxGenerations(final int maxGenerations) {
            this.maxGenerations = maxGenerations;
            return this;
        }

        public Builder populationSize(final int populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        public Builder parentPoolSize(final int parentPoolSize) {
            this.parentPoolSize = parentPoolSize;
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
                    parentPoolSize,
                    crossoverProbability,
                    mutationProbability);
        }
    }

    private final int maxGenerations;
    private final int populationSize;
    private final int parentPoolSize;
    private final double crossoverProbability;
    private final double mutationProbability;

    public SolverConfiguration(
            final int maxGenerations,
            final int populationSize,
            final int parentPoolSize,
            final double crossoverProbability,
            final double mutationProbability) {
        checkArgument(
                populationSize >= parentPoolSize,
                "Parent pool size couldn't be more than population size");
        this.maxGenerations = maxGenerations;
        this.populationSize = populationSize;
        this.parentPoolSize = parentPoolSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
    }

    public int maxGenerations() {
        return maxGenerations;
    }

    public int populationSize() {
        return populationSize;
    }

    public int parentPoolSize() {
        return parentPoolSize;
    }

    public double crossoverProbability() {
        return crossoverProbability;
    }

    public double mutationProbability() {
        return mutationProbability;
    }
}
