/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.genetics;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;

/**
 * Implementation of a genetic algorithm. All factors that govern the operation
 * of the algorithm can be configured for a specific problem.
 *
 * @since 2.0
 * @version $Revision:$ $Date:$
 */
public class GeneticAlgorithm {

    /**
     * Static random number generator shared by GA implementation classes.
     * Set the randomGenerator seed to get reproducible results.  
     * Use {@link #setRandomGenerator(RandomGenerator)} to supply an alternative
     * to the default JDK-provided PRNG.
     */
    private static RandomGenerator randomGenerator = new JDKRandomGenerator();
    
    /**
     * Set the (static) random generator.
     * 
     * @param random random generator
     */
    public synchronized static void setRandomGenerator(RandomGenerator random) {
        randomGenerator = random;
    }
    
    /**
     * Returns the (static) random generator.
     * 
     * @return the static random generator shared by GA implementation classes
     */
    public synchronized static RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }
      
    /** the crossover policy used by the algorithm. */
    protected final CrossoverPolicy crossoverPolicy;

    /** the rate of crossover for the algorithm. */
    protected final double crossoverRate;

    /** the mutation policy used by the algorithm. */
    protected final MutationPolicy mutationPolicy;

    /** the rate of mutation for the algorithm. */
    protected final double mutationRate;

    /** the selection policy used by the algorithm. */
    protected final SelectionPolicy selectionPolicy;
    
    /**
     * @param crossoverPolicy The {@link CrossoverPolicy}
     * @param crossoverRate The crossover rate as a percentage (0-1 inclusive)
     * @param mutationPolicy The {@link MutationPolicy}
     * @param mutationRate The mutation rate as a percentage (0-1 inclusive)
     * @param selectionPolicy The {@link selectionPolicy}
     */
    public GeneticAlgorithm(
            CrossoverPolicy crossoverPolicy, double crossoverRate,
            MutationPolicy mutationPolicy, double mutationRate,
            SelectionPolicy selectionPolicy) {
        if (crossoverRate < 0 || crossoverRate > 1) {
            throw new IllegalArgumentException("crossoverRate must be between 0 and 1");
        }
        if (mutationRate < 0 || mutationRate > 1) {
            throw new IllegalArgumentException("mutationRate must be between 0 and 1");
        }
        this.crossoverPolicy = crossoverPolicy;
        this.crossoverRate = crossoverRate;
        this.mutationPolicy = mutationPolicy;
        this.mutationRate = mutationRate;
        this.selectionPolicy = selectionPolicy;
    }
    
    /**
     * Evolve the given population. Evolution stops when the stopping condition
     * is satisfied.
     * 
     * @param initial the initial, seed population.
     * @param condition the stopping condition used to stop evolution.
     * @return the population that satisfies the stopping condition.
     */
    public Population evolve(Population initial, StoppingCondition condition) {
        Population current = initial;
        while (!condition.isSatisfied(current)) {
            current = nextGeneration(current);
        }
        return current;
    }

    /**
     * <p>Evolve the given population into the next generation.</p>
     * <p><ol>
     *    <li>Get nextGeneration population to fill from <code>current</code>
     *        generation, using its nextGeneration method</li>
     *    <li>Loop until new generation is filled:</li>
     *    <ul><li>Apply configured SelectionPolicy to select a pair of parents
     *            from <code>current</code></li>
     *        <li>With probability = {@link #getCrossoverRate()}, apply
     *            configured {@link CrossoverPolicy} to parents</li>
     *        <li>With probability = {@link #getMutationRate()}, apply
     *            configured {@link MutationPolicy} to each of the offspring</li>
     *        <li>Add offspring individually to nextGeneration,
     *            space permitting</li>
     *    </ul>
     *    <li>Return nextGeneration</li>
     *    </ol>
     * </p>
     * 
     * @param current the current population.
     * @return the population for the next generation.
     */
    public Population nextGeneration(Population current) {
        Population nextGeneration = current.nextGeneration();

        while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
            // select parent chromosomes
            ChromosomePair pair = getSelectionPolicy().select(current);

            // crossover?
            if (randomGenerator.nextDouble() < getCrossoverRate()) {
                // apply crossover policy to create two offspring
                pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond());
            }

            // mutation?
            if (randomGenerator.nextDouble() < getMutationRate()) {
                // apply mutation policy to the chromosomes
                pair = new ChromosomePair(
                    getMutationPolicy().mutate(pair.getFirst()),
                    getMutationPolicy().mutate(pair.getSecond()));
            }

            // add the first chromosome to the population
            nextGeneration.addChromosome(pair.getFirst());
            // is there still a place for the second chromosome?
            if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
                // add the second chromosome to the population
                nextGeneration.addChromosome(pair.getSecond());
            }
        }

        return nextGeneration;
    }    
    
    /**
     * Returns the crossover policy.
     * @return crossover policy
     */
    public CrossoverPolicy getCrossoverPolicy() {
        return crossoverPolicy;
    }

    /**
     * Returns the crossover rate.
     * @return crossover rate
     */
    public double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Returns the mutation policy.
     * @return mutation policy
     */
    public MutationPolicy getMutationPolicy() {
        return mutationPolicy;
    }

    /**
     * Returns the mutation rate.
     * @return mutation rate
     */
    public double getMutationRate() {
        return mutationRate;
    }

    /**
     * Returns the selection policy.
     * @return selection policy
     */
    public SelectionPolicy getSelectionPolicy() {
        return selectionPolicy;
    }
        
}
