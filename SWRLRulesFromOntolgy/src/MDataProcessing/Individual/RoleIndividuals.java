/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing.Individual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * Class used to manage the role individuals.
 */
public class RoleIndividuals 
{
    private Set<RoleIndividual> individuals;
    private Set<RoleIndividual> invert_individuals;
    private ArrayList<String> domainIndividuals;
    private ArrayList<String> rangeIndividuals;
    
    /**
     * Initialize the role individuals (domain individuals, range individuals and total individuals).
     */
    public RoleIndividuals()
    {
        this.individuals = new LinkedHashSet<RoleIndividual>();
        this.invert_individuals = new LinkedHashSet<RoleIndividual>();
        this.domainIndividuals = new ArrayList<String>();
        this.rangeIndividuals = new ArrayList<String>();
    }
    
    /**
     * Initialize the role individuals (domain individuals, range individuals and total individuals).
     * @param roleIndividuals
     */
    public RoleIndividuals(RoleIndividuals roleIndividuals)
    {
        this.individuals = new LinkedHashSet<RoleIndividual>(roleIndividuals.getIndividuals());
        this.invert_individuals = new LinkedHashSet<RoleIndividual>(roleIndividuals.getInvertIndividuals());
        this.domainIndividuals = new ArrayList<String>(roleIndividuals.getDomainIndividuals());
        this.rangeIndividuals = new ArrayList<String>(roleIndividuals.getRangeIndividuals());
    }
    
    /**
     * 
     * @return the individuals of the role.
     */
    public Set<RoleIndividual> getIndividuals()
    {
        return this.individuals;
    }
    
    /**
     * 
     * @return the inverted individuals of the role (the domain is the range and the range is the domain).
     */
    public Set<RoleIndividual> getInvertIndividuals()
    {
        return this.invert_individuals;
    }
    
    /**
     * Get a list of domains from individuals.
     * @return the individuals of the domain class.
     */
    public ArrayList<String> getDomainIndividuals()
    {
        return this.domainIndividuals;
    }
    
    /**
     * Get a list of the domain's individuals of the role individual that have range equal to the string in input.
     * @param strRangeIndividual
     * @return the list of domain's individuals.
     */
    public Set<String> getDomainIndividuals(String strRangeIndividual)
    {   
        Set<String> listDomainIndividuals = new LinkedHashSet<String>();
        
        for(Iterator<RoleIndividual> it = this.getIndividuals().iterator(); it.hasNext();)
        {
            RoleIndividual roleIndividual = it.next();
            if (roleIndividual.getRange().equals(strRangeIndividual))
                listDomainIndividuals.add(roleIndividual.getDomain());
        }
        
        return listDomainIndividuals;        
    }
    
    /**
     * Get a list of ranges' individuals. 
     * @return the list of ranges' individuals.
     */
    public ArrayList<String> getRangeIndividuals()
    {
        return this.rangeIndividuals;
    }
    
    /**
     * Get a list of ranges' individuals of the role which have domain equal to the string in input.
     * @param strDomainIndividual
     * @return the list of ranges' individuals.
     */
    public Set<String> getRangeIndividuals(String strDomainIndividual)
    {   
        Set<String> listRangeIndividuals = new LinkedHashSet<String>();
        
        for(Iterator<RoleIndividual> it = this.getIndividuals().iterator(); it.hasNext();)
        {
            RoleIndividual roleIndividual = it.next();
            if (roleIndividual.getDomain().equals(strDomainIndividual))
                listRangeIndividuals.add(roleIndividual.getRange());
        }
        
        return listRangeIndividuals;        
    }
    
    /**
     * Add the role individual to the list of individuals. And then add his invertion (domain is the range and range is the domain) to the list of invert individuals.
     * @param roleIndividual
     */
    public void addIndividual(RoleIndividual roleIndividual)
    {
        this.individuals.add(roleIndividual);        
        
        RoleIndividual invertRoleIndividual = new RoleIndividual(roleIndividual.getRange(), roleIndividual.getDomain());
        this.invert_individuals.add(invertRoleIndividual);
        
        this.domainIndividuals.add(roleIndividual.getDomain());
        this.rangeIndividuals.add(roleIndividual.getRange());
    }
    
    /**
     * Check if the set of individuals contains the role in input.
     * @param roleIndividual
     * @return true if the set of individuals contains the role individual, false otherwise.
     */
    public boolean checkIndividual(RoleIndividual roleIndividual)
    {
        return individuals.contains(roleIndividual);
    }
    
    /**
     * 
     * @return the size of the set of individuals.
     */
    public int size()
    {
        return individuals.size();
    }
}
