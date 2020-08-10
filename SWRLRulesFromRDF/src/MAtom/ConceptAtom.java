/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MAtom;

import org.semanticweb.owlapi.model.IRI;

/**
 *
 * Class used to manage the concept atoms.
 * It is a subclass of the class Atom.
 */
public class ConceptAtom extends Atom
{
    private String strVariable;
    
    /**
     * Create a concept atom with the IRI in input.
     * @param iri of the new concept atom.
     */
    public ConceptAtom(IRI iri)
    {
        super(iri);
        this.strVariable = "";       
    }
	
    /**
     * 
     * @param iri of the concept atom to be create.
     * @param strVariable is the string containing the variable that the concept has to contain.
     */
    public ConceptAtom(IRI iri, String strVariable)
    {
        super(iri);
        this.strVariable = strVariable;
    }
    
    /**
     * Set the name of the variable
     * @param strVariable
     */
    public void setVariable(String strVariable)
    {
        this.strVariable = strVariable;
    }
	
    /**
     * Get the name of the variable.
     * @return the string variable.
     */
    public String getVariable()
    {
        return this.strVariable;
    }
}
