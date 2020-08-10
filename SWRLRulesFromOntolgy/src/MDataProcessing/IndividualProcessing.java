/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing;

import MCommon.Global;
import MKnowledge.KnowledgeBase;
import java.util.LinkedHashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 *
 * Class used to manage the individuals of the knowledge base.
 */
public class IndividualProcessing 
{
    /**
     * Get all of individuals of the knowledge base.
     * @param knowledgeBase to be analyze.
     */
    public static void getAllOfIndividuals(KnowledgeBase knowledgeBase)
    {
        Global.allIndividuals = new LinkedHashSet<String>();
        
        //Reasoner: PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            for (OWLClass owlClass : knowledgeBase.getOntology().getClassesInSignature()) 
            {    
                Set<OWLNamedIndividual> setInstances = knowledgeBase.getPelletReasoner().getInstances(owlClass, true).getFlattened();        
                for (OWLNamedIndividual instance : setInstances)
                    Global.allIndividuals.add(Global.cutNameOfIRI(instance.getIRI()));            
            }
        }
        
        //Reasoner: HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            for (OWLClass owlClass : knowledgeBase.getOntology().getClassesInSignature()) 
            {    
                Set<OWLNamedIndividual> setInstances = knowledgeBase.getHermitReasoner().getInstances(owlClass, true).getFlattened();        
                for (OWLNamedIndividual instance : setInstances)
                    Global.allIndividuals.add(Global.cutNameOfIRI(instance.getIRI()));            
            }
        }
    }
}
