package ga.square.magic;

public interface GeneticAlgorithm<I extends Individual> {
    I randomIndividual(final int sideSize);
    int fitnessOf(I individual);
    I mutate(I individual);
    I crossover(I father, I mother);
}
