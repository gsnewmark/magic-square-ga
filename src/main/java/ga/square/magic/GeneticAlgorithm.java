package ga.square.magic;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface GeneticAlgorithm<I extends Individual> {
    I randomIndividual(final int sideSize);
    int fitnessOf(I individual);
    List<ImmutablePair<I, I>> selectParents(Multimap<Integer, I> population);
    Multimap<Integer, I> nextGenerationFrom(
            Multimap<Integer, I> population,
            Multimap<Integer, I> children);
    I mutate(I individual);
    I crossover(I father, I mother);
}
