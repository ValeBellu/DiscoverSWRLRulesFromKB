/*
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing;

import MCommon.Global;
import MDataProcessing.Assertion.RoleAssertion;
import MDataProcessing.Individual.RoleIndividual;
import MDataProcessing.Individual.RoleIndividuals;
import MKnowledge.KnowledgeBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

/**
 *
 * @author tdminh
 */
public class RoleProcessing 
{
    /**
     * Get all the properties which have the number of assertions greater or equal to a specific number.
     * @param knowledgeBase to be analyze.
     * @param iNumMinAssertions the minimum number of assertions.
     */
    public static void createFrequentRolesStratified(KnowledgeBase knowledgeBase, int iNumMinAssertions)
    {
    	//Reasoner: PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {
            Global.allFrequentRolesStratified = new ArrayList<RoleAssertion>();
            Global.allIRIFrequentRolesStratified = new ArrayList<IRI>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {   
                //Get all the classes of the domain of the property   
                Set<OWLClass> setDomainClass =  knowledgeBase.getPelletReasoner().getObjectPropertyDomains(owlObjectProperty, true).getFlattened();

                int countAssertion = 0;
                boolean acceptAddToFrequentRole = false;            

                if (iNumMinAssertions > 0)
                {
                    //Check if the number of assertions is greater than or equals to the minimum
                    for(OWLClass domainClass : setDomainClass)
                    {
                        //Get all the individuals of each domain class
                        Set<OWLNamedIndividual> setDomainIndividuals = knowledgeBase.getPelletReasoner().getInstances(domainClass, Global.FILTER_WITH_INDIVIDUAL).getFlattened();

                        for(OWLNamedIndividual domainIndividual : setDomainIndividuals)
                        {                
                        	//Get all the individuals of each range class                   
                            Set<OWLNamedIndividual> setRangeIndividuals = knowledgeBase.getPelletReasoner().getObjectPropertyValues(domainIndividual, owlObjectProperty).getFlattened();
                            countAssertion += setRangeIndividuals.size();

                            if (countAssertion >= iNumMinAssertions)
                            {
                                acceptAddToFrequentRole = true;
                                break;
                            }
                        }

                        if (acceptAddToFrequentRole) break;
                    }
                }
                else
                    acceptAddToFrequentRole = true;

                //If the number of assertions is greater or equal to the minimum            
                if (acceptAddToFrequentRole)
                {
                    RoleIndividuals roleIndividuals = new RoleIndividuals();

                    for(OWLClass domainClass : setDomainClass)
                    {
                        Set<OWLNamedIndividual> setDomainIndividuals = knowledgeBase.getPelletReasoner().getInstances(domainClass, Global.FILTER_WITH_INDIVIDUAL).getFlattened();

                        for(OWLNamedIndividual domainIndividual : setDomainIndividuals)
                        {
                            Set<OWLNamedIndividual> setRangeIndividuals = knowledgeBase.getPelletReasoner().getObjectPropertyValues(domainIndividual, owlObjectProperty).getFlattened();

                            if (setRangeIndividuals.size() > 0)
                            {
                                for(OWLNamedIndividual rangeIndividual : setRangeIndividuals)
                                {                            
                                    String strDomainIndividual = Global.cutNameOfIRI(domainIndividual.getIRI());
                                    String strRangeIndividual = Global.cutNameOfIRI(rangeIndividual.getIRI());

                                    RoleIndividual roleIndividual = new RoleIndividual(strDomainIndividual, strRangeIndividual);
                                    roleIndividuals.addIndividual(roleIndividual);
                                }
                            }
                        }                    
                    }

                    RoleAssertion roleAssertion = new RoleAssertion(owlObjectProperty.getIRI(), roleIndividuals);
                    Global.allFrequentRolesStratified.add(roleAssertion);
                    Global.allIRIFrequentRolesStratified.add(owlObjectProperty.getIRI());
                }
            }
        }
        
        //The same thing with the reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.allFrequentRolesStratified = new ArrayList<RoleAssertion>();
            Global.allIRIFrequentRolesStratified = new ArrayList<IRI>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Map<OWLNamedIndividual,Set<OWLNamedIndividual>> mapIndividuals = knowledgeBase.getHermitReasoner().getObjectPropertyInstances(owlObjectProperty);
                Set<OWLNamedIndividual> individualsDomain = mapIndividuals.keySet();            	
                
                int objectPropertySize = 0;                
            	for(OWLNamedIndividual individualDomain : individualsDomain)
            	{	
                    Set<OWLNamedIndividual> individualsRange = mapIndividuals.get(individualDomain);            		
                    for(OWLNamedIndividual individualRange : individualsRange) 
                        objectPropertySize++;
            	}
                
                if (objectPropertySize >= iNumMinAssertions)
                {
                    RoleIndividuals roleIndividuals = new RoleIndividuals();

                    for(OWLNamedIndividual individualDomain : individualsDomain)
                    {	
                        Set<OWLNamedIndividual> individualsRange = mapIndividuals.get(individualDomain);
                        
                        for(OWLNamedIndividual individualRange : individualsRange)
                        {	
                            String strDomainIndividual = Global.cutNameOfIRI(individualDomain.getIRI());
                            String strRangeIndividual = Global.cutNameOfIRI(individualRange.getIRI());

                            RoleIndividual roleIndividual = new RoleIndividual(strDomainIndividual, strRangeIndividual);
                            roleIndividuals.addIndividual(roleIndividual);
                        }	
                    }

                    RoleAssertion roleAssertion = new RoleAssertion(owlObjectProperty.getIRI(), roleIndividuals);
                    Global.allFrequentRolesStratified.add(roleAssertion);
                    Global.allIRIFrequentRolesStratified.add(owlObjectProperty.getIRI());
                }
            }
        }
    }
    
    /**
     * Get assertions of the property identified by the IRI in input, if the IRI is already present in the list.
     * @param iriRole is the IRI of the property.
     * @return the IRI if it is already present is the list of IRIs properties.
     */
    public static RoleAssertion getRoleAssertionsFromFrequentRolesStratified(IRI iriRole)
    {
        for(RoleAssertion ra : Global.allFrequentRolesStratified)        
            if (ra.getIRIRole().equals(iriRole))
                return ra;        
        return null;        
    }
    
    /**
     * Get the couple (property, superproperties) where to each property (key) corresponds a set of superproperties (value).
     * @param knowledgeBase to be analyze.
     */
    public static void createRoleIsSuperPropertyRoles(KnowledgeBase knowledgeBase)
    {
    	//Reasoner: PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            Global.roleIsSuperPropertyOfRoles = new HashMap<IRI, Set<IRI>>();        

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLObjectPropertyExpression> subObjectProperties = knowledgeBase.getPelletReasoner().getSubObjectProperties(owlObjectProperty, false).getFlattened();
                subObjectProperties.remove(knowledgeBase.getDataFactory().getOWLBottomObjectProperty());

                Set<IRI> subObjectPropertiesInMap = new HashSet<IRI>();

                if (!subObjectProperties.isEmpty())               
                    for(OWLObjectPropertyExpression subObjectPropertyInMap : subObjectProperties)
                        subObjectPropertiesInMap.add(subObjectPropertyInMap.asOWLObjectProperty().getIRI());

                Global.roleIsSuperPropertyOfRoles.put(owlObjectProperty.getIRI(), subObjectPropertiesInMap); 
            }
        }
        //same thing eith reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.roleIsSuperPropertyOfRoles = new HashMap<IRI, Set<IRI>>();        

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLObjectPropertyExpression> subObjectProperties = knowledgeBase.getHermitReasoner().getSubObjectProperties(owlObjectProperty, false).getFlattened();
                subObjectProperties.remove(knowledgeBase.getDataFactory().getOWLBottomObjectProperty());

                Set<IRI> subObjectPropertiesInMap = new HashSet<IRI>();

                if (!subObjectProperties.isEmpty())               
                    for(OWLObjectPropertyExpression subObjectPropertyInMap : subObjectProperties)   
                        if (subObjectPropertyInMap instanceof OWLObjectPropertyImpl)
                            subObjectPropertiesInMap.add(subObjectPropertyInMap.asOWLObjectProperty().getIRI());

                Global.roleIsSuperPropertyOfRoles.put(owlObjectProperty.getIRI(), subObjectPropertiesInMap); 
            }
        }        
    }
    

    
    /**
     * /**
     * Get the couple (property, sub properties) where to each property (key) corresponds a set of sub properties (value).
     * @param knowledgeBase to be analyze.
     */
    public static void createRoleIsSubPropertyRoles(KnowledgeBase knowledgeBase)
    {
    	//Reasoner: PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            Global.roleIsSubPropertyOfRoles = new HashMap<IRI, Set<IRI>>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLObjectPropertyExpression> superObjectProperties = knowledgeBase.getPelletReasoner().getSuperObjectProperties(owlObjectProperty, false).getFlattened();
                superObjectProperties.remove(knowledgeBase.getDataFactory().getOWLTopObjectProperty());

                Set<IRI> superObjectPropertiesInMap = new HashSet<IRI>();

                if (!superObjectProperties.isEmpty())               
                    for(OWLObjectPropertyExpression superObjectPropertyInMap : superObjectProperties)
                        superObjectPropertiesInMap.add(superObjectPropertyInMap.asOWLObjectProperty().getIRI());

                Global.roleIsSubPropertyOfRoles.put(owlObjectProperty.getIRI(), superObjectPropertiesInMap); 
            }
        }
        
        //The same thing with reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.roleIsSubPropertyOfRoles = new HashMap<IRI, Set<IRI>>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLObjectPropertyExpression> superObjectProperties = knowledgeBase.getHermitReasoner().getSuperObjectProperties(owlObjectProperty, false).getFlattened();
                superObjectProperties.remove(knowledgeBase.getDataFactory().getOWLTopObjectProperty());

                Set<IRI> superObjectPropertiesInMap = new HashSet<IRI>();

                if (!superObjectProperties.isEmpty())               
                    for(OWLObjectPropertyExpression superObjectPropertyInMap : superObjectProperties)
                        if (superObjectPropertyInMap instanceof OWLObjectPropertyImpl)
                            superObjectPropertiesInMap.add(superObjectPropertyInMap.asOWLObjectProperty().getIRI());

                Global.roleIsSubPropertyOfRoles.put(owlObjectProperty.getIRI(), superObjectPropertiesInMap); 
            }
        }        
    }
    
    
    /**
     * Get the couple (property, domain classes) where to each property (key) corresponds a set of domain classes (value).
     * @param knowledgeBase to be analyze.
     */
    public static void createConceptsDomainOfRole(KnowledgeBase knowledgeBase)
    {
    	//Reasoner: PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            Global.conceptsDomainOfRole = new HashMap<IRI, Set<IRI>>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLClass> classesDomainOfRole = knowledgeBase.getPelletReasoner().getObjectPropertyDomains(owlObjectProperty, true).getFlattened();                
                classesDomainOfRole.remove(knowledgeBase.getDataFactory().getOWLThing());
                
                Set<IRI> classesDomainInMap = new HashSet<IRI>();

                if (!classesDomainOfRole.isEmpty())               
                    for(OWLClass classDomainInMap : classesDomainOfRole)
                        classesDomainInMap.add(classDomainInMap.getIRI());

                Global.conceptsDomainOfRole.put(owlObjectProperty.getIRI(), classesDomainInMap); 
            }
        }
        
        //Reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.conceptsDomainOfRole = new HashMap<IRI, Set<IRI>>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();            

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLClass> classesDomainOfRole = knowledgeBase.getHermitReasoner().getObjectPropertyDomains(owlObjectProperty, true).getFlattened();
                classesDomainOfRole.remove(knowledgeBase.getDataFactory().getOWLThing());
                
                Set<IRI> classesDomainInMap = new HashSet<IRI>();

                if (!classesDomainOfRole.isEmpty())               
                    for(OWLClass classDomainInMap : classesDomainOfRole)
                        classesDomainInMap.add(classDomainInMap.getIRI());

                Global.conceptsDomainOfRole.put(owlObjectProperty.getIRI(), classesDomainInMap); 
            }
        }
    }
    
    
    /**
     * Get the couple (property, range classes) where to each property (key) corresponds a set of range classes (value).
     * @param knowledgeBase to be analyze.
     */
    public static void createConceptsRangeOfRole(KnowledgeBase knowledgeBase)
    {
    	//Reasoner PELLET
        if (Global.TYPE_OF_REASONER.equals("Pellet"))
        {        
            Global.conceptsRangeOfRole = new HashMap<IRI, Set<IRI>>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLClass> classesRangeOfRole = knowledgeBase.getPelletReasoner().getObjectPropertyRanges(owlObjectProperty, true).getFlattened();                
                classesRangeOfRole.remove(knowledgeBase.getDataFactory().getOWLThing());
                
                Set<IRI> classesRangeInMap = new HashSet<IRI>();

                if (!classesRangeOfRole.isEmpty())               
                    for(OWLClass classRangeInMap : classesRangeOfRole)
                        classesRangeInMap.add(classRangeInMap.getIRI());

                Global.conceptsRangeOfRole.put(owlObjectProperty.getIRI(), classesRangeInMap); 
            }
        }
        //Reasoner HERMIT
        else if (Global.TYPE_OF_REASONER.equals("Hermit"))
        {
            Global.conceptsRangeOfRole = new HashMap<IRI, Set<IRI>>();

            Set<OWLObjectProperty> objectProperties = knowledgeBase.getOntology().getObjectPropertiesInSignature();

            for (OWLObjectProperty owlObjectProperty : objectProperties)
            {
                Set<OWLClass> classesRangeOfRole = knowledgeBase.getHermitReasoner().getObjectPropertyRanges(owlObjectProperty, true).getFlattened();                
                classesRangeOfRole.remove(knowledgeBase.getDataFactory().getOWLThing());
                
                Set<IRI> classesRangeInMap = new HashSet<IRI>();

                if (!classesRangeOfRole.isEmpty())               
                    for(OWLClass classRangeInMap : classesRangeOfRole)
                        classesRangeInMap.add(classRangeInMap.getIRI());

                Global.conceptsRangeOfRole.put(owlObjectProperty.getIRI(), classesRangeInMap); 
            }
        }
    }
    
    /**
     * Get assertions of a role that is represented by the iriRole.
     * @param iriRole is the IRI of the role of which we want the assertions.
     * @return the role assertions with the IRI equal to the IRI in input, if they exist.
     */
    public static RoleAssertion getRoleAssertionsFromFrequentRoles(IRI iriRole)
    {
        for(RoleAssertion ra : Global.allFrequentRolesStratified)        
            if (ra.getIRIRole().equals(iriRole))
                return ra;        
        return null;        
    }
    
    
    /**
     * It defines if the role A is a super property of the role B.
     * @param roleA
     * @param roleB
     * @return true if the role A is equal to the role B or if it is a super property of the role B; false otherwise.
     */
    public static boolean isRoleSuperProperty(IRI roleA, IRI roleB)
    {
        if (roleA.equals(roleB))
                return true;
        else
        {	
        	if(Global.roleIsSuperPropertyOfRoles.get(roleA)==null)
        		return false;
            Set<IRI> iriListRolesSubproperty = Global.roleIsSuperPropertyOfRoles.get(roleA);

            if (iriListRolesSubproperty.contains(roleB))            			
                return true;
            else
                return false;
        }
    }
    
    /**
     * It defines if the role A is a sub property of the role B.
     * @param roleA
     * @param roleB
     * @return true if the role A is equal to the role B or if it is a sub property of the role B; false otherwise.
     */
    public static boolean isRoleSubProperty(IRI roleA, IRI roleB)
    {
        if (roleA.equals(roleB))
                return true;
        else
        {	
        	if(Global.roleIsSubPropertyOfRoles.get(roleA) == null)
        		return false;
            Set<IRI> iriListRolesSuperproperty = Global.roleIsSubPropertyOfRoles.get(roleA);

            if (iriListRolesSuperproperty.contains(roleB))            			
                return true;
            else
                return false;
        }
    }
    
    /**
     * It defines if the role contains in his domain concepts which are subsumed by the concept.
     * @param role
     * @param concept
     * @return true if exists a concept, in the domain of the role, that is subsumed by the concept in input
     */
    public static boolean isDomainOfRoleSubsumedByConcept(IRI role, IRI concept)
    {   
    	if(Global.conceptsDomainOfRole.get(role) == null)
    		return false;
        Set<IRI> iriListConceptsDomain = Global.conceptsDomainOfRole.get(role);
        
        for(IRI iriConceptDomain : iriListConceptsDomain)        
            if(ConceptProcessing.isConceptSubsumedBy(iriConceptDomain, concept))
                return true;
        return false;
    }
    
    
    /**
     * It defines if the role contains in his range concepts which are subsumed by the concept.
     * @param role
     * @param concept
     * @return true if exists a concept, in the range of the role, that is subsumed by the concept in input
     */
    public static boolean isRangeOfRoleSubsumedByConcept(IRI role, IRI concept)
    {
    	if (Global.conceptsRangeOfRole.get(role)==null)
    		return false;
        Set<IRI> iriListConceptsRange = Global.conceptsRangeOfRole.get(role);
      
        for(IRI iriConceptRange : iriListConceptsRange)  { 
            if(ConceptProcessing.isConceptSubsumedBy(iriConceptRange, concept))
                return true;
        }
        return false;
        
    }
}
