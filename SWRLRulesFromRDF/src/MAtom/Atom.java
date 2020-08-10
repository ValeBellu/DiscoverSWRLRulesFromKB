/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MAtom;

import org.semanticweb.owlapi.model.IRI;

/**
 * Class used to manage the atoms, in general.
 * It is the superclass of the classes ConceptAtom and RoleAtom.
 */
public class Atom 
{
	private IRI iri;

	/**
	 * Initialize an atom with the IRI in input.
	 * @param iri
	 */
    public Atom(IRI iri)
    {
        this.iri = iri;
    }

    /**
     * Get the IRI.
     * @return the IRI.
     */
    public IRI getIRI()
    {
        return this.iri;
    }
    
    /**
     * Get the name of the atom, by cutting the IRI.
     * @return the IRI as a String.
     */
    public String getName()
    {
        return iri.toString();        
    }
    
    @Override
    public int hashCode() 
    {
        return this.getName().hashCode();
    }

    /**
     * Returns true id the atoms are equals, false otherwise.
     */
    public boolean equals(Object obj)
    {
        return ((obj instanceof Atom) && (((Atom) obj).getIRI().equals(this.iri)) );
    }
}
