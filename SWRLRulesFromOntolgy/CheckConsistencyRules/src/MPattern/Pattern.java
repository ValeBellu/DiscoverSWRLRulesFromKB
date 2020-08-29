/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MPattern;

import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import MAtom.ConceptAtom;
import MAtom.RoleAtom;
import MKnowledge.KnowledgeBase;

/**
 *
 * @author tdminh
 */
public class Pattern extends ArrayList
{
    private double dblMetric;
    
    public Pattern() 
    {
        super();  
        this.dblMetric = -1.0;        
    }
    
    public Pattern(KnowledgeBase kb, String strPatternText)
    {
        super();
        this.dblMetric = -1.0;        
        
        createRuleByText(kb, strPatternText);
    }
    
    public Pattern(Pattern pattern)
    {
        super(pattern);
        this.dblMetric = -1.0;
    }
    
    public void setMetric(double dblMetric)
    {
        this.dblMetric = dblMetric;
    }
    
    public double getMetric()
    {
        return this.dblMetric;
    }
    
    /**
  	 * Read the rules from the file generated with the Genetic Algorithm and 
  	 * for each generated rule rebuilds the head and body.
  	 * @param kb is the knowledge base
  	 * @param strRule is the rule to check
  	 */
    public void createRuleByText(KnowledgeBase kb, String strRule)
    {
        String strHead = strRule.substring(0, strRule.indexOf("<=")).trim();
        String strBody = strRule.substring(strRule.indexOf("<=") + 2).trim();
        
        if (strHead.contains(","))
        {
            String strName = strHead.substring(0, strHead.indexOf("(")).trim();
            String strVariable1 = strHead.substring(strHead.indexOf("(") + 1, strHead.indexOf(",")).trim();
            String strVariable2 = strHead.substring(strHead.indexOf(",") + 1, strHead.indexOf(")")).trim();

            OWLObjectProperty owlObjectProperty = kb.getDataFactory().getOWLObjectProperty(strName, kb.getPrefix());
            addAtomDirect(owlObjectProperty.getIRI(), strVariable1, strVariable2, false);
        }
        else
        {
            String strName = strHead.substring(0, strHead.indexOf("(")).trim();
            String strVariable1 = strHead.substring(strHead.indexOf("(") + 1, strHead.indexOf(")")).trim();

            OWLClass owlClass = kb.getDataFactory().getOWLClass(strName, kb.getPrefix());            
            addAtomDirect(owlClass.getIRI(), strVariable1, "", true);            
        }
        
        while (!strBody.isEmpty())
        {
            String strAtom = strBody.substring(0, strBody.indexOf("&")).trim();
            strBody = strBody.substring(strBody.indexOf("&") + 1).trim();
            
            if (strAtom.contains(","))
            {
                String strName = strAtom.substring(0, strAtom.indexOf("(")).trim();
                String strVariable1 = strAtom.substring(strAtom.indexOf("(") + 1, strAtom.indexOf(",")).trim();
                String strVariable2 = strAtom.substring(strAtom.indexOf(",") + 1, strAtom.indexOf(")")).trim();
                
                OWLObjectProperty owlObjectProperty = kb.getDataFactory().getOWLObjectProperty(strName, kb.getPrefix());
                addAtomDirect(owlObjectProperty.getIRI(), strVariable1, strVariable2, false);
            }
            else
            {
                String strName = strAtom.substring(0, strAtom.indexOf("(")).trim();
                String strVariable1 = strAtom.substring(strAtom.indexOf("(") + 1, strAtom.indexOf(")")).trim();
                
                OWLClass owlClass = kb.getDataFactory().getOWLClass(strName, kb.getPrefix());            
                addAtomDirect(owlClass.getIRI(), strVariable1, "", true);
            }
        }
    }
    
    public void addAtomDirect(IRI iriAtom, String variable1, String variable2, boolean isConceptAtom )
    {
        if (isConceptAtom)
            super.add(new ConceptAtom(iriAtom, variable1));
        else 
            super.add(new RoleAtom(iriAtom, variable1, variable2));
    }
}
