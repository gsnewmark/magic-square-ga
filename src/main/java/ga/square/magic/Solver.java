package ga.square.magic;

import static com.google.common.base.Preconditions.checkArgument;

public interface Solver<I extends Individual, GA extends GeneticAlgorithm<I>> {
    public static class SolverResult<I> {
        private I result;
        private int fitness;
        private long generation;

        public SolverResult(final I result, final int fitness, final long generation) {
            this.result = result;
            this.fitness = fitness;
            this.generation = generation;
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
