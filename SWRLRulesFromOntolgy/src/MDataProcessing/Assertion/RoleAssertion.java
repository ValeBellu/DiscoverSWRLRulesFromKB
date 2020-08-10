/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing.Assertion;

import MCommon.Global;
import MDataProcessing.Individual.RoleIndividuals;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * Class used to manage the role assertions.
 */
public class RoleAssertion 
{
    private IRI iriRole;
    private RoleIndividuals individuals;
    
    /**
     * Initialize a role assertion with the IRI and the individuals in input.
     * @param iriRole
     * @param individuals
     */
    public RoleAssertion(IRI iriRole, RoleIndividuals individuals)
    {
        this.iriRole = iriRole;
        this.individuals = new RoleIndividuals(individuals);
    }
    
    /**
     * 
     * @return the IRI of the role.
     */
    public IRI getIRIRole()
    {
        return this.iriRole;
    }
    
    /**
     * Get the name of the role, using the cut IRI.
     * @return the name of the role.
     */
    public String getRoleName()
    {
        return Global.cutNameOfIRI(this.iriRole);        
    }
    
    /**
     * 
     * @return the individuals of the role.
     */
    public RoleIndividuals getIndividuals()
    {
        return this.individuals;
    }
}
