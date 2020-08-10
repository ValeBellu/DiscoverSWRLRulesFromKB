/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MDataProcessing;

import MCommon.Global;
import MDataProcessing.Assertion.ConceptAssertion;
import MDataProcessing.Individual.ConceptIndividuals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * Class which manages the concepts.
 */
public class ConceptProcessing 
{	
	/**
	 * Query the sparql endpoint to have all that is needed for the classes. 
	 * First, make a query which returns the classes of the data-sets. 
	 * Then, for each result, there are other queries which returns  
	 * the individuals of the class, 
	 * the sub classes of the class, 
	 * the super classes of the class.
	 * @param szQuery the first query made.
	 * @param szEndpoint the sparql endpoint to be queried.
	 * @throws Exception
	 */
	public void queryClass(String szQuery, String szEndpoint) throws Exception
	{
		//Creation and execution of the query in input which returns all the classes
		Query query = QueryFactory.create(szQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(szEndpoint, query);
		ResultSet rs = qexec.execSelect();

		String resultClass = null;
		String resultInd = null;

		//Get the results
		while (rs.hasNext()) {
			ConceptIndividuals conceptIndividuals = new ConceptIndividuals();
			QuerySolution qs = rs.next();
			Iterator<String> itVars = qs.varNames();

			//Display the results
			while (itVars.hasNext()) {
				String szVar = itVars.next().toString();
				if (szVar.equals("class"))
				{
					resultClass = qs.get(szVar).toString();
					//System.out.println("Class: "+ resultClass);
				}
			}
			IRI iriClass = IRI.create(resultClass);
			System.out.println(iriClass);

			//Add the result class to the set of IRI classes.
			//If it is already there, then go with the other result
			//otherwise, do all the other queries with this result.
			if(!(Global.allIRIFrequentConceptsStratified.contains(iriClass))) {
				Global.allIRIFrequentConceptsStratified.add(iriClass);

				//INDIVIDUALS:
				//Creation and execution of the query which returns the individuals of the class.
				Query query2 = QueryFactory.create("SELECT DISTINCT ?ind "
								+ "WHERE {?ind a <" + iriClass +">. } "
								+ "LIMIT 300");
				
				QueryExecution qexec2 = QueryExecutionFactory.sparqlService(szEndpoint, query2);

				ResultSet rs2 = qexec2.execSelect();

				while (rs2.hasNext()) {
					QuerySolution qs2 = rs2.next();	
					Iterator<String> itVars2 = qs2.varNames();

					while (itVars2.hasNext()) {
						String szVar2 = itVars2.next().toString();

						if (szVar2.equals("ind")) {
							resultInd = qs2.get(szVar2).toString();
							//System.out.println("Ind: "+resultInd);		
						}


					}
					
					conceptIndividuals.addIndividual(resultInd);
					Global.allIndividuals.add(resultInd);
				}
				qexec2.close();
			

				ConceptAssertion y = new ConceptAssertion(iriClass, conceptIndividuals );
				if(!(conceptIndividuals.size()==0))
					Global.allFrequentConceptsStratified.add(y);

				//SUBCLASS:
				//Creation and execution of the query which returns the sub classes of the class
				Query query3 = QueryFactory.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX owl: <http://www.w3.org/2002/07/owl#>"
						+ "SELECT DISTINCT ?superClass "
						+ "WHERE { <" + iriClass + "> rdfs:subClassOf ?superClass. "
						+ "FILTER(?superClass != owl:Thing)}");

				QueryExecution qexec3 = QueryExecutionFactory.sparqlService(szEndpoint, query3);
				ResultSet rs3 = qexec3.execSelect();
				Set<IRI> superClassesMap = new HashSet<IRI>();
				String resultSuperClass = null;

				while (rs3.hasNext()) {
					QuerySolution qs3 = rs3.next();
					Iterator<String> itVars3 = qs3.varNames();

					while (itVars3.hasNext()) {			
						String szVar3 = itVars3.next().toString();
						if (szVar3.equals("superClass"))
						{
							resultSuperClass = qs3.get(szVar3).toString();
						}

					}

					IRI iriSuperClass = IRI.create(resultSuperClass);
					superClassesMap.add(iriSuperClass);

				}
				
				if(!(superClassesMap.isEmpty()))
					Global.conceptIsSubsumedByConcepts.put(iriClass, superClassesMap);
				qexec3.close();


				//SUPERCLASS:
				//Creation and execution of the query which returns the super classes of the class
				Query query4 = QueryFactory.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX owl: <http://www.w3.org/2002/07/owl#>"
						+ "SELECT DISTINCT ?subClass "
						+ "WHERE { ?subClass rdfs:subClassOf <" + iriClass + ">. FILTER(?subClass != owl:Thing)} ");

				QueryExecution qexec4 = QueryExecutionFactory.sparqlService(szEndpoint, query4);
				ResultSet rs4 = qexec4.execSelect();
				Set<IRI> subClassesMap = new HashSet<IRI>();
				String resultSubClass = null;

				while (rs4.hasNext()) {
					QuerySolution qs4 = rs4.next();
					Iterator<String> itVars4 = qs4.varNames();

					while (itVars4.hasNext()) {			
						String szVar4 = itVars4.next().toString();
						if (szVar4.equals("subClass"))
						{
							resultSubClass = qs4.get(szVar4).toString();
						}

					}

					IRI iriSubClass = IRI.create(resultSubClass);
					subClassesMap.add(iriSubClass);

				}
				
				if(!(subClassesMap.isEmpty()))
					Global.conceptSubsumsConcepts.put(iriClass, subClassesMap);
				qexec4.close();

			}

		}
		System.out.println("END QUERY CLASS");
		qexec.close();
	
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
