/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing.Individual;

import java.util.ArrayList;

/**
 *
 * Class used to manage the variable of the individuals.
 */
public class VariableIndividuals 
{
    private String strNameVariable;
    private ArrayList<String> individuals;
    
    /**
     * The string in input is the name of the variable.
     * @param strNameVariable
     */
    public VariableIndividuals(String strNameVariable)
    {
        this.strNameVariable = strNameVariable;
        individuals = new ArrayList<String>();
    }
    

    /**
     * Save the variable of the individual and create an array list with all the individuals with this variable.
     * @param variableIndividual
     */
    public VariableIndividuals(VariableIndividuals variableIndividual)
    {
        this.strNameVariable = variableIndividual.getNameVariable();
        individuals = new ArrayList<String>(variableIndividual.getIndividuals());
    }
    
    /**
     * @param strNameVariable
     * @param newIndividuals
     */
    public VariableIndividuals(String strNameVariable, ArrayList<String> newIndividuals)
    {
        this.strNameVariable = strNameVariable;
        individuals = new ArrayList<String>(newIndividuals);
    }
    
    /**
     * Add the new individual in input to the set of individuals.
     * @param newIndividual
     */
    public void addIndividual(String newIndividual)
    {
        individuals.add(newIndividual);       
    }
    
    /**
     * Add all the individuals in input to the set of individuals.
     * @param newIndividuals
     */
    public void addIndividuals(ArrayList<String> newIndividuals)
    {
        individuals.addAll(newIndividuals);     
    }
    
    /**
     * 
     * @return the set of individuals of the variable.
     */
    public ArrayList<String> getIndividuals()
    {
        return this.individuals;
    }
    
    /**
     * 
     * @param i : the position of the individual.
     * @return the individual in position i.
     */
    public String getIndividual(int i)
    {
        return this.individuals.get(i);
    }
    
    /**
     * Remove the individual in position i.
     * @param i : the position of the individual to remove.
     */
    public void removeIndividual(int i)
    {
    	individuals.remove(i);
    }
    
    /**
     * Set the name of the variable with the string in input.
     * @param strNameVariable
     */
    public void setNameVariable(String strNameVariable)
    {
        this.strNameVariable = strNameVariable;        
    }
    
    /**
     * @return the name of the variable.
     */
    public String getNameVariable()
    {
        return this.strNameVariable;        
    }
    
    /**
     * @param strNameVariable
     * @param newIndividuals
     */
    public void change(String strNameVariable, ArrayList<String> newIndividuals)
    {
        this.strNameVariable = strNameVariable;
        individuals = new ArrayList<String>(newIndividuals);
    }
}
