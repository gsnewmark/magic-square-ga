package ga.square.magic;

import java.util.Set;

public interface GeneticAlgorithm<I extends Individual> {
    Set<I> produceNextGeneration(Set<I> population);
    double fitnessOf(I individual);
}
