package ga.square.magic;

import static com.google.common.base.Preconditions.checkArgument;

public interface Solver<I extends Individual, GA extends GeneticAlgorithm<I>> {
    public static class SolverResult<I> {
        private I result;
        private long generation;

        public SolverResult(final I result, final long generation) {
            checkArgument(result != null, "Illegal argument result: null");
            this.result = result;
            this.generation = generation;
        }

        public I getResult() {
            return result;
        }

        public long getGeneration() {
            return generation;
        }

        @Override
        public String toString() {
            return "SolverResult{" +
                    "generation=" + generation +
                    ", result=" + result +
                    '}';
        }
    }

    SolverResult<I> solve(GA algo, int squareSize, SolverConfiguration configuration);
}
