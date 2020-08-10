/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing.Individual;

/**
 *
 * Class used to manage the role individual, and so its range and its domain.
 */
public class RoleIndividual 
{
    private String strDomain, strRange;        

    /**
     * Initialize a role with the domain and the range in input.
     * @param strDomain
     * @param strRange
     */
    public RoleIndividual(String strDomain, String strRange)
    {
        this.strDomain = strDomain;
        this.strRange = strRange;                
    }
    
    /**
     * 
     * @return the domain of the role.
     */
    public String getDomain()
    {
        return this.strDomain;
    }

    /**
     * 
     * @return the range of the role.
     */
    public String getRange()
    {
        return this.strRange;
    }
    
    /**
     * The id of a role is the domain and the range.
     * @return the domain and the range of the role.
     */
    public String getId() 
    {
        return this.strDomain + this.strRange;
    }
    
    @Override
    public int hashCode() 
    {
        return this.getId().hashCode();
    }
    
    /**
     * Check if two roles are equals and return true if they are, false otherwise.
     */
    public boolean equals(Object obj)
    {
        return ((obj instanceof RoleIndividual) && (((RoleIndividual) obj).strDomain.equals(this.strDomain)) &&
                                                   (((RoleIndividual) obj).strRange.equals(this.strRange)) );
    }
}
