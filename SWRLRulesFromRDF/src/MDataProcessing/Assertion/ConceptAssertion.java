/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing.Assertion;


import MDataProcessing.Individual.ConceptIndividuals;
import org.semanticweb.owlapi.model.*;
/**
 *
 * Class used to manage the concept assertions.
 */
public class ConceptAssertion 
{
    private IRI iriConcept;
    private ConceptIndividuals individuals;
   
    /**
     * Create an assertion with the IRI of the class and the individuals of this class.
     * @param iriConcept
     * @param individuals
     */
    public ConceptAssertion(IRI iriConcept, ConceptIndividuals individuals)
    {
        this.iriConcept = iriConcept;
        this.individuals = new ConceptIndividuals(individuals);
    }
    
    /**
     * Get the IRI of the concept.
     * @return the IRI.
     */
    public IRI getIRIConcept()
    {
        return this.iriConcept;
    }
    
    /**
     * Set the name of the concept, using the cut IRI.
     * @return the name of the concept.
     */
    public String getConceptName()
    {
        return this.iriConcept.toString();        
    }
    
    /**
     * Get the individuals.
     * @return the individuals of the class or property which has called the method.
     */
    public ConceptIndividuals getIndividuals()
    {
        return this.individuals;
    }

    
}

