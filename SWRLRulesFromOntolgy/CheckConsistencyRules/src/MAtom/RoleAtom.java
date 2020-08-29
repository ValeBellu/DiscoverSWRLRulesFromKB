/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MAtom;

import org.semanticweb.owlapi.model.IRI;

/**
 * Class used to manage the role atoms.
 * It is a subclass of the class Atom.
 */
public class RoleAtom extends Atom
{
    private String strDomainVariable;    
    private String strRangeVariable;    

    /**
     * Initialize a role atom with the IRI in input.
     * @param iri
     */
    public RoleAtom(IRI iri)
    {
        super(iri);
        this.strDomainVariable = "";        
        this.strRangeVariable = "";
    }
	
    /**
     * Initialize the role atom with the IRI, the domain and the range in input.
     * @param iri
     * @param strVariableDomain
     * @param strVariableRange
     */
    public RoleAtom(IRI iri, String strVariableDomain, String strVariableRange)
    {
        super(iri);
        this.strDomainVariable = strVariableDomain;        
        this.strRangeVariable = strVariableRange;
    }
    
    /**
     * Set the name of the variable of the domain, with the string in input.
     * @param strDomainVariable
     */
    public void setDomainVariable(String strDomainVariable)
    {
        this.strDomainVariable = strDomainVariable;
    }
	
    /**
     * Set the name of the variable of the range, with the string in input.
     * @param strRangeVariable
     */
    public void setRangeVariable(String strRangeVariable)
    {
        this.strRangeVariable = strRangeVariable;
    }
	
    /**
     * 
     * @return the variable in the domain of the role atom.
     */
    public String getDomainVariable()
    {
        return this.strDomainVariable;
    }
	
    /**
     * @return the variable in the range of the role atom.
     */
    public String getRangeVariable()
    {
        return this.strRangeVariable;
    }

}
