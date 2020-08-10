/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing;

import MCommon.Global;
import MDataProcessing.Assertion.ConceptAssertion;
import MDataProcessing.Individual.ConceptIndividuals;
import MKnowledge.KnowledgeBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 *
 * Class which manages the concepts.
 */
public class ConceptProcessing 
{	
	/**
    * Get all the concepts which has assertions greater or equal to a specific number. 
    * The concept A is subsumed by B if and only if the two concepts are equals, or the set of the IRIs of the concepts that subsume A contains the IRI of the concept B
    * @param knowledgeBase to analyze to discover the concepts.
    * @param iNumMinAssertions the minimum number of assertions that the concept must have.
    */
    public static void createFrequentConceptsStratified(KnowledgeBase knowledgeBase, int iNumMinAssertions)
    {
    	//Reasoner PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            Global.allFrequentConceptsStratified = new ArrayList<ConceptAssertion>();
            Global.allIRIFrequentConceptsStratified = new ArrayList<IRI>();

            Set<OWLClass> classes = knowledgeBase.getOntology().getClassesInSignature(); 
            classes.remove(knowledgeBase.getDataFactory().getOWLThing());

            //for each class in the KB take the instances of it.
            for (OWLClass owlClass : classes)
            {
                Set<OWLNamedIndividual> setIndividuals = knowledgeBase.getPelletReasoner().getInstances(owlClass, Global.FILTER_WITH_INDIVIDUAL).getFlattened();

                //create a ConceptAssertion with the IRI of the class and the individuals of that.
                if (setIndividuals.size() >= iNumMinAssertions)
                {
                    ConceptIndividuals conceptIndividuals = new ConceptIndividuals();                
                    for (OWLNamedIndividual individual : setIndividuals)
                        conceptIndividuals.addIndividual(Global.cutNameOfIRI(individual.getIRI()));

                    ConceptAssertion conceptAssertion = new ConceptAssertion(owlClass.getIRI(), conceptIndividuals);
                    Global.allFrequentConceptsStratified.add(conceptAssertion);
                    Global.allIRIFrequentConceptsStratified.add(owlClass.getIRI());
                }
            }
        }
        
        //The same thing with the reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.allFrequentConceptsStratified = new ArrayList<ConceptAssertion>();
            Global.allIRIFrequentConceptsStratified = new ArrayList<IRI>();

            Set<OWLClass> classes = knowledgeBase.getOntology().getClassesInSignature(); 
            classes.remove(knowledgeBase.getDataFactory().getOWLThing());

            for (OWLClass owlClass : classes)
            {
                Set<OWLNamedIndividual> setIndividuals = knowledgeBase.getHermitReasoner().getInstances(owlClass, Global.FILTER_WITH_INDIVIDUAL).getFlattened();

                if (setIndividuals.size() >= iNumMinAssertions)
                {
                    ConceptIndividuals conceptIndividuals = new ConceptIndividuals();                
                    for (OWLNamedIndividual individual : setIndividuals)
                        conceptIndividuals.addIndividual(Global.cutNameOfIRI(individual.getIRI()));

                    ConceptAssertion conceptAssertion = new ConceptAssertion(owlClass.getIRI(), conceptIndividuals);
                    Global.allFrequentConceptsStratified.add(conceptAssertion);
                    Global.allIRIFrequentConceptsStratified.add(owlClass.getIRI());
                }
            }
        }            
    }
    
    
    /**
     * Get the couple (class, superclasses) where to each class corresponds a set of superclasses.
     * @param knowledgeBase to be analyze.
     */
    public static void createConceptIsSubsumedByConcepts(KnowledgeBase knowledgeBase)
    {
    	//Reasoner: PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            Global.conceptIsSubsumedByConcepts = new HashMap<IRI, Set<IRI>>();

            Set<OWLClass> classes = knowledgeBase.getOntology().getClassesInSignature(); 
            classes.remove(knowledgeBase.getDataFactory().getOWLThing());        

            //for each class, return the set of superclasses.
            for (OWLClass owlClass : classes)
            {
                Set<OWLClass> superClasses = knowledgeBase.getPelletReasoner().getSuperClasses(owlClass, false).getFlattened();
                superClasses.remove(knowledgeBase.getDataFactory().getOWLThing());

                Set<IRI> superClassesInMap = new HashSet<IRI>();

                if (!superClasses.isEmpty())               
                    for(OWLClass superClass : superClasses)
                        superClassesInMap.add(superClass.getIRI());

                Global.conceptIsSubsumedByConcepts.put(owlClass.getIRI(), superClassesInMap);                
            }
        }
        
        //The same with reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.conceptIsSubsumedByConcepts = new HashMap<IRI, Set<IRI>>();

            Set<OWLClass> classes = knowledgeBase.getOntology().getClassesInSignature(); 
            classes.remove(knowledgeBase.getDataFactory().getOWLThing());        

            for (OWLClass owlClass : classes)
            {
                Set<OWLClass> superClasses = knowledgeBase.getHermitReasoner().getSuperClasses(owlClass, false).getFlattened();
                superClasses.remove(knowledgeBase.getDataFactory().getOWLThing());

                Set<IRI> superClassesInMap = new HashSet<IRI>();

                if (!superClasses.isEmpty())               
                    for(OWLClass superClass : superClasses)
                        superClassesInMap.add(superClass.getIRI());

                Global.conceptIsSubsumedByConcepts.put(owlClass.getIRI(), superClassesInMap);                
            }
        }
    }
    
    
    /**
     * Get the couple (class, subclasses) where to each class corresponds a set of subclasses.
     * @param knowledgeBase to be analyze.
     */   
    public static void createConceptSubsumsConcepts(KnowledgeBase knowledgeBase)
    {
    	//Reasoner: PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            Global.conceptSubsumsConcepts = new HashMap<IRI, Set<IRI>>();

            Set<OWLClass> classes = knowledgeBase.getOntology().getClassesInSignature(); 
            classes.remove(knowledgeBase.getDataFactory().getOWLThing());        

            for (OWLClass owlClass : classes)
            {
                Set<OWLClass> subClasses = knowledgeBase.getPelletReasoner().getSubClasses(owlClass, false).getFlattened();
                subClasses.remove(knowledgeBase.getDataFactory().getOWLNothing());

                Set<IRI> subClassesInMap = new HashSet<IRI>();

                if (!subClasses.isEmpty())               
                    for(OWLClass subClass : subClasses)
                        subClassesInMap.add(subClass.getIRI());

                Global.conceptSubsumsConcepts.put(owlClass.getIRI(), subClassesInMap);                
            }
        }
        
        //The same with reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.conceptSubsumsConcepts = new HashMap<IRI, Set<IRI>>();

            Set<OWLClass> classes = knowledgeBase.getOntology().getClassesInSignature(); 
            classes.remove(knowledgeBase.getDataFactory().getOWLThing());        

            for (OWLClass owlClass : classes)
            {
                Set<OWLClass> subClasses = knowledgeBase.getHermitReasoner().getSubClasses(owlClass, false).getFlattened();
                subClasses.remove(knowledgeBase.getDataFactory().getOWLNothing());

                Set<IRI> subClassesInMap = new HashSet<IRI>();

                if (!subClasses.isEmpty())               
                    for(OWLClass subClass : subClasses)
                        subClassesInMap.add(subClass.getIRI());

                Global.conceptSubsumsConcepts.put(owlClass.getIRI(), subClassesInMap);                
            }
        }
    }
    
    /**
     * Get assertions of the concept with the IRI in input.
     * @param iriConcept
     * @return if exists, it returns a concept assertion from those already present with the IRI equal to the input one.
     */
    public static ConceptAssertion getConceptAssertionsFromFrequentConcepts(IRI iriConcept)
    {
        for(ConceptAssertion ca : Global.allFrequentConceptsStratified)        
            if (ca.getIRIConcept().equals(iriConcept))
                return ca;        
        return null;        
    }
    
    /**
     * Check whether the concept A is subsumed by the concept B. 
     * The concept A is subsumed by B if and only if the two concepts are equals, or the set of the IRIs of the concepts that subsume A contains the IRI of the concept B
     * @param conceptA
     * @param conceptB
     * @return true if A is subsumed by B and false otherwise.
     */
    public static boolean isConceptSubsumedBy(IRI conceptA, IRI conceptB)
    {
        if (conceptA.equals(conceptB))
            return true;
        else
        {	
        	if(Global.conceptIsSubsumedByConcepts.get(conceptA)==null)
        		return false; 
            Set<IRI> iriListConceptsSubsum = Global.conceptIsSubsumedByConcepts.get(conceptA);
            
            if (iriListConceptsSubsum.contains(conceptB))            			
                return true;
            else
                return false;
        }
    }
    
    
    /**
     * Check whether the concept A subsumes the concept B.
     * @param conceptA
     * @param conceptB
     * @return if the two concepts are equals and if the concept B is subsumed by the concept A.
     */
    public static boolean isConceptSubsumming(IRI conceptA, IRI conceptB)
    {
        if (conceptA.equals(conceptB))
                return true;
        else
        {	
            Set<IRI> iriListConceptsSubsumedBy = Global.conceptSubsumsConcepts.get(conceptA);

            if (iriListConceptsSubsumedBy.contains(conceptB))            			
                return true;
            else
                return false;
        }
    }

	

	
}
