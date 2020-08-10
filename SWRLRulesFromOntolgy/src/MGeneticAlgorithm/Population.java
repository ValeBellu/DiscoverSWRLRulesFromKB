/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MGeneticAlgorithm;

import MCommon.Global;
import MPattern.Computation.PatternComparator;
import MPattern.Pattern;
import java.util.ArrayList;

/**
 *
 * @author tdminh
 */
public class Population 
{
    private ArrayList<Pattern> listIndividuals;
    
    private double sumFitness;
    private double dblAverageFitness;
    
    private double sumFitnessBiggerThan0;
    private double dblAverageFitnessBiggerThan0;
    
    private int countPatternFitnessBiggerThan0;
    

    /**
     * Initialize the population.
     */
    public Population()
    {
        this.listIndividuals = new ArrayList<Pattern>();
    }
    
    /**
     * Create a population with the patterns in input.
     * @param listPatterns
     */
    public Population(ArrayList<Pattern> listPatterns)
    {
        this.listIndividuals = new ArrayList<Pattern>(listPatterns);
    }
    
    /**
     * This method initialize the first population, adding new patterns in the list of individuals.
     * The new patterns are generated using the atoms containing in the list of frequent atoms in the knowledge base.
     *
     */
    public void autoCreatePopulation()
    {        
        int count = 0;
        
        while (count < Global.MAX_SIZE_POPULATION)
        {
            Pattern pattern = new Pattern();      
            
            if (pattern.autoCreatePattern())
                if (!isPatternExist(pattern))
                {
                	//compute the fitness value for the pattern
                    pattern.compute();
                    this.listIndividuals.add(pattern);
                    count++;
                }
        }   
        
        sort();
    }
    
    /**
     * Sort the list of individuals by decreasing fitness values.
     */
    public void sort()
    {
        this.listIndividuals.sort(new PatternComparator());
    }
    
    /**
     * This method is used to check if a rule exists in the Population.
     * @param pattern which has been generated.
     * @return true if the pattern already exists, false otherwise.
     */
    public boolean isPatternExist(Pattern pattern)
    {
        for(int i = 0; i < this.listIndividuals.size(); i++)
        {
            Pattern patternInList = this.listIndividuals.get(i);
            if (patternInList.equals(pattern))
                return true;
        }
        return false;            
    }

    /**
     * Compute the average between the fitness values of the patterns in the population.
     */
    public void compute()
    {
    	//There is at least one pattern in the population
        if (this.listIndividuals.size() > 0)
        {
            this.sumFitness = 0.0;
            this.dblAverageFitness = 0.0;    
            this.sumFitnessBiggerThan0 = 0.0;
            this.dblAverageFitnessBiggerThan0 = 0.0;    
            this.countPatternFitnessBiggerThan0 = 0;
            
            //sum the fitness value of each pattern of the population
            for(int i = 0; i < this.listIndividuals.size(); i++)
            {
                sumFitness += this.listIndividuals.get(i).getPatternComputation().getFitnessValue();
                
                
                if (this.listIndividuals.get(i).getPatternComputation().getFitnessValue() > 0)
                {
                    this.sumFitnessBiggerThan0 += this.listIndividuals.get(i).getPatternComputation().getFitnessValue();
                    this.countPatternFitnessBiggerThan0++;
                }
            }
            //do the average between the fitness values of the population
            this.dblAverageFitness = this.sumFitness / Global.MAX_SIZE_POPULATION;
            this.dblAverageFitnessBiggerThan0 = this.sumFitnessBiggerThan0 / this.countPatternFitnessBiggerThan0;
        }
    }
    
    /**
     * Get the list of patterns (individuals) in a population.
     * @return the list.
     */
    public ArrayList<Pattern> getListIndividuals()
    {
        return this.listIndividuals;
    }
    
    /**
     * Get the pattern in position of the index.
     * @param index position of the pattern.
     * @return the pattern.
     */
    public Pattern getIndividual(int index)
    {
        return this.listIndividuals.get(index);
    }
    
    /**
     * Get the average fitness value between all the fitness values.
     * @return the average fitness value.
     */
    public double getAverageFitness()
    {
        return this.dblAverageFitness;
    }
    
    /**
     * 
     * @return the average fitness value between the fitness values bigger than zero.
     */
    public double getAverageFitnessBiggerThan0()
    {
        return this.dblAverageFitnessBiggerThan0;
    }
    
    /**
     * @return the number of pattern with a fitness value bigger than zero.
     */
    public int getCountPatternFitnessBiggerThan0()
    {
        return this.countPatternFitnessBiggerThan0;
    }
}
