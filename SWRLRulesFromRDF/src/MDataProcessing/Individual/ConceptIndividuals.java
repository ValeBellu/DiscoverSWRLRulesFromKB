/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing.Individual;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Class used to manage the concept individuals.
 */
public class ConceptIndividuals 
{
    private Set<String> individualsName;
   
    /**
     * Initialize a concept individuals with the names of inputs individuals.
     */
    public ConceptIndividuals()
    {
        this.individualsName = new HashSet<String>();
    } 
    
    /**
     * Initialize a concept individuals with the names of inputs individuals.
     * @param conceptIndividuals
     */
    public ConceptIndividuals(ConceptIndividuals conceptIndividuals)
    {
        this.individualsName = new HashSet<String>(conceptIndividuals.getIndividualsName());
    }
    
    /**
     * 
     * @return the names of the individuals
     */
    public Set<String> getIndividualsName()
    {
        return this.individualsName;
    }
    
    /**
     * Add the name of the individual in input to the set of individuals.
     * @param strIndividual
     */
    public void addIndividual(String strIndividual)
    {        
        this.individualsName.add(strIndividual);
    }
    
    /**
     * Check if the set of individuals name contains the individual in input.
     * @param strIndividual
     * @return true if it is contained, false otherwise.
     */
    public boolean checkIndividual(String strIndividual)
    {
        return this.individualsName.contains(strIndividual);
    }
    
    /**
     * 
     * @return the number of individuals.
     */
    public int size()
    {
        return individualsName.size();
    }
}
