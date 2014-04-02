package ga.square.magic;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface GeneticAlgorithm<I extends Individual> {
    I randomIndividual(final int sideSize);
    int fitnessOf(I individual);
    List<ImmutablePair<I, I>> selectParents(long n, Multimap<Integer, I> population);
    Multimap<Integer, I> selectForRemoval(long n, Multimap<Integer, I> population);
    I mutate(I individual);
    I crossover(I father, I mother);
}
