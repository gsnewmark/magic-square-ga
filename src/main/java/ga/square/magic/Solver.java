package ga.square.magic;

import com.google.common.collect.Multimap;

public interface Solver<I extends Individual, GA extends GeneticAlgorithm<I>> {
    public static class SolverResult<I> {
        private final I result;
        private final int fitness;
        private final long generation;
        private final Multimap<Integer, I> population;

        public SolverResult(
                final I result, final int fitness, final long generation, final Multimap<Integer, I> population) {
            this.result = result;
            this.fitness = fitness;
            this.generation = generation;
            this.population = population;
        }

        public I getResult() {
            return result;
        }

        public long getGeneration() {
            return generation;
        }

        public int getFitness() {
            return fitness;
        }

        public Multimap<Integer, I> getPopulation() {
            return population;
        }

        @Override
        public String toString() {
            return "SolverResult{" +
                    "generation=" + generation +
                    ", fitness=" + fitness +
                    ", result=" + result +
                    '}';
        }
    }

    SolverResult<I> solve();
}
