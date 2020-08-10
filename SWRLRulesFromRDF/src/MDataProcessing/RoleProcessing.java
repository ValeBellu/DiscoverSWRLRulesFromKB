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
 * Class used to manage the roles of the knowledge base.
 */
public class RoleProcessing 
{
	/**
	 * Query the sparql endpoint to have all that is needed for the properties. 
	 * First, make a query which returns the properties of the data-sets (the query in input). 
	 * Then, for each result, there are other queries which returns  
	 * the individuals of the result property, 
	 * the sub properties of the property, 
	 * the super properties of the property,
	 * the domain class of the property,
	 * the range class of the property.
	 * @param propertyIndividuals is the first query made.
	 * @param sparqlEndpoint the sparql enpoint to be queried.
	 * @throws InterruptedException 
	 * @throws Exception
	 */
	public void queryPropertyIndividuals(String propertyIndividuals, String sparqlEndpoint) throws Exception {
		
		// Creation and execution of the query in input which returns all the properties
		Query query = QueryFactory.create(propertyIndividuals);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
		ResultSet rs = qexec.execSelect();
		String resultProp = null;
		String resultSub = null;
		String resultObj = null;

		//Get the result
		while (rs.hasNext()) {
			RoleIndividuals roleIndividuals = new RoleIndividuals();
			QuerySolution qs = rs.next();
			Iterator<String> itVars = qs.varNames();

			//Display the results
			while (itVars.hasNext()) {
				String szVar = itVars.next().toString();
				if (szVar.equals("prop"))
				{
					resultProp = qs.get(szVar).toString();
					//    					System.out.println("prop: "+ resultProp);
				}	
			}

			IRI iriProp = IRI.create(resultProp);
			System.out.println(iriProp);

			//Add the property to the set of IRI Properties
			if(!(Global.allIRIFrequentRolesStratified.contains(iriProp))) {
				Global.allIRIFrequentRolesStratified.add(iriProp);


				//INDIVIDUALS:
				//Creation and execution of the query which returns the individuals of the property
				Query query2 = QueryFactory.create("SELECT DISTINCT ?sub ?obj "
						+ "WHERE {?sub <" + iriProp +"> ?obj } LIMIT 300");

				QueryExecution qexec2 = QueryExecutionFactory.sparqlService(sparqlEndpoint, query2);

				ResultSet rs2 = qexec2.execSelect();
				while (rs2.hasNext()) {
					QuerySolution qs2 = rs2.next();	
					Iterator<String> itVars2 = qs2.varNames();

					while (itVars2.hasNext()) {
						String szVar2 = itVars2.next().toString();

						if (szVar2.equals("sub")) {
							resultSub = qs2.get(szVar2).toString();		
						}
						else {
							if (szVar2.equals("obj")) {
								resultObj = qs2.get(szVar2).toString();

							}	
						}

					}
					
					RoleIndividual roleInd = new RoleIndividual(resultSub, resultObj);
					roleIndividuals.addIndividual(roleInd);
				}
				qexec2.close();
				
				RoleAssertion y = new RoleAssertion(iriProp, roleIndividuals );
				if(roleIndividuals.getIndividuals().size()!=0)
					Global.allFrequentRolesStratified.add(y);	
				Global.allIndividuals.add(resultSub);
				Global.allIndividuals.add(resultObj);

				//SUBPROPERTY:
				//Creation and execution of the query which returns the sub properties of the property
				Query query3 = QueryFactory.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" 
						+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
						+ "SELECT DISTINCT ?y "
						+ "WHERE { <" + iriProp + "> rdfs:subPropertyOf ?y. "
						+ "FILTER(?y != owl:TopObjectProperty)}");

				QueryExecution qexec3 = QueryExecutionFactory.sparqlService(sparqlEndpoint, query3);
				ResultSet rs3 = qexec3.execSelect();
				Set<IRI> superPropertiesMap = new HashSet<IRI>();
				String resultSuperProperty = null;

				while (rs3.hasNext()) {
					QuerySolution qs3 = rs3.next();
					Iterator<String> itVars3 = qs3.varNames();

					while (itVars3.hasNext()) {			
						String szVar3 = itVars3.next().toString();
						if (szVar3.equals("y"))
						{
							resultSuperProperty = qs3.get(szVar3).toString();
						}

					}

					IRI iriSuperProperty = IRI.create(resultSuperProperty);
					superPropertiesMap.add(iriSuperProperty);

				}
				
				if(!(superPropertiesMap.isEmpty()))
					Global.roleIsSubPropertyOfRoles.put(iriProp, superPropertiesMap);
				qexec3.close();

				//SUPERPROPERTY:
				//Creation and execution of the query which returns the super properties of the property
				Query query4 = QueryFactory.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
						+ "SELECT DISTINCT ?y "
						+ "WHERE { ?y rdfs:subPropertyOf <" + iriProp + ">. "
						+ "FILTER(?y != owl:BottomObjectProperty)}");

				QueryExecution qexec4 = QueryExecutionFactory.sparqlService(sparqlEndpoint, query4);
				ResultSet rs4 = qexec4.execSelect();
				Set<IRI> subPropertiesMap = new HashSet<IRI>();
				String resultSubProperty = null;

				while (rs4.hasNext()) {
					QuerySolution qs4 = rs4.next();
					Iterator<String> itVars4 = qs4.varNames();

					while (itVars4.hasNext()) {			
						String szVar4 = itVars4.next().toString();
						if (szVar4.equals("y"))
						{
							resultSubProperty = qs4.get(szVar4).toString();
						}

					}

					IRI iriSubProperty = IRI.create(resultSubProperty);
					subPropertiesMap.add(iriSubProperty);

				}
				
				if(!(subPropertiesMap.isEmpty()))
					Global.roleIsSuperPropertyOfRoles.put(iriProp, subPropertiesMap);
				qexec4.close();

				//DOMAIN:
				//Creation and execution of the query which returns the domain class of the property
				Query query5 = QueryFactory.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX owl: <http://www.w3.org/2002/07/owl#>"
						+ "SELECT DISTINCT ?domain "
						+ "WHERE { <" + iriProp + "> rdfs:domain ?domain. "
						+ "FILTER(?domain != owl:Thing)}");

				QueryExecution qexec5 = QueryExecutionFactory.sparqlService(sparqlEndpoint, query5);
				ResultSet rs5 = qexec5.execSelect();
				Set<IRI> classesDomainMap = new HashSet<IRI>();
				String resultClassDomain = null;
			
				while (rs5.hasNext()) {
					QuerySolution qs5 = rs5.next();
					Iterator<String> itVars5 = qs5.varNames();

					while (itVars5.hasNext()) {			
						String szVar5 = itVars5.next().toString();
						if (szVar5.equals("domain"))
						{
							resultClassDomain = qs5.get(szVar5).toString();
						}

					}

					IRI iriClassDomain = IRI.create(resultClassDomain);
					classesDomainMap.add(iriClassDomain);

				}
				
				if(!(classesDomainMap.isEmpty()))
					Global.conceptsDomainOfRole.put(iriProp, classesDomainMap);
				qexec5.close();

				//RANGE:
				//Creation and execution of the query which returns the range class of the property
				Query query6 = QueryFactory.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX owl: <http://www.w3.org/2002/07/owl#>"
						+ "SELECT DISTINCT ?range "
						+ "WHERE { <" + iriProp + "> rdfs:range ?range. FILTER(?range != owl:Thing)}");

				QueryExecution qexec6 = QueryExecutionFactory.sparqlService(sparqlEndpoint, query6);
				ResultSet rs6 = qexec6.execSelect();
				Set<IRI> classesRangeMap = new HashSet<IRI>();
				String resultClassRange = null;

				while (rs6.hasNext()) {
					QuerySolution qs6 = rs6.next();
					Iterator<String> itVars6 = qs6.varNames();

					while (itVars6.hasNext()) {			
						String szVar6 = itVars6.next().toString();
						if (szVar6.equals("range"))
						{
							resultClassRange = qs6.get(szVar6).toString();
						}

					}

					IRI iriClassRange = IRI.create(resultClassRange);
					classesRangeMap.add(iriClassRange);

				}
			
				if(!(classesRangeMap.isEmpty()))
					Global.conceptsRangeOfRole.put(iriProp, classesRangeMap);
				qexec6.close();
			}


		}
		System.out.println("END QUERY PROPERTIES");
		qexec.close();

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
