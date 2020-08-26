import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import MCommon.Global;
import MCommon.MyRandom;
import MDataProcessing.ConceptProcessing;
import MDataProcessing.RoleProcessing;
import MGeneticAlgorithm.GeneticAlgorithm;
import MGeneticAlgorithm.Population;
import MOutput.OutputInformation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Duc Minh Tran and Valeria Bellusci
 */
public class MainClass 
{   
    /**
	 * This method is used to populate the data structure containing all the main elements of the data-sets.
	 * All these elements are taken from the sparql endpoint, through query sparql.
     * @throws Exception 
	 */
    public MainClass() throws Exception
    {   
        Global.myRandom = new MyRandom(); 
        
        //Fills the array from a file which contains all the necessary graphs.
		ArrayList<String> graph = new ArrayList<String>();
		File file = new File("C:\\Users\\valer\\eclipse-workspace\\GeneticAltog2\\src\\graph.txt"); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String iri; 
		while ((iri = br.readLine()) != null) {
			graph.add(iri); 
		}
		br.close();
		
        // SPARQL Query     
        //Populate the data structures about class
        try {
        	//Output of the queries
         	OutputInformation output = new OutputInformation("RESULTSQueryClass.txt");
         	
         	//Do the query for each graph in the array. 
         	//If the use of a graph is not necessary, remove the for loop and the "FROM" clause from the query.
        	for(String i : graph) {
        		
        		//The endpoint 
        		String sparqlEndpoint = "http://covidontheweb.inria.fr/sparql";
        		ConceptProcessing q = new ConceptProcessing();
        		
        		//Query which takes all the classes which have individuals
        		String classQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
        				+ "SELECT DISTINCT ?class "
        				+ "FROM <" + i + "> "
        				+ "WHERE { ?ind a ?class } "; //or better: { ?class a rdfs:Class }
 
        		q.queryClass(classQuery, sparqlEndpoint);
        		Thread.sleep(5000);
        		
        	}
            
            //Write in the output file
            OutputInformation.showTextln("Result of query concept individuals (iri of the class): ", false);
    		OutputInformation.showFrequentConceptsStratified(false);
    		OutputInformation.showTextln(" ", false);
    		
    		OutputInformation.showTextln("Result of query concept subsumed by concepts: ", false);
    		OutputInformation.showFrequentConceptsStratifiedAndSuperConcepts(false);
    		OutputInformation.showTextln(" ", false);
    		
    		OutputInformation.showTextln("Result of query concept which subsumes concepts: ", false);
    		OutputInformation.showFrequentConceptsStratifiedAndSubConcepts(false);
    		OutputInformation.showTextln(" ", false);
    		
    		output.closeFile(); 
        }
        catch (HttpException ex) {
            Thread.sleep(10000);
        } 
        
      //Populate the data structures about property
        try {
        
            String sparqlEndpoint = "http://covidontheweb.inria.fr/sparql";
            OutputInformation outputProp = new OutputInformation("RESULTSQueryProperty.txt");
            
          //Do the query for each graph in the array. 
         	//If the use of a graph is not necessary, remove the for loop and the "FROM" clause from the query.
            for(String i: graph) {
            	RoleProcessing r = new RoleProcessing();
            	
//            	if(i.equals("https://github.com/fhircat/CORD-19-on-FHIR/pubtator"))
//            	{
//            		String propertyIndividuals = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
//            				+ "SELECT DISTINCT  ?prop "
//            				+ "FROM <" + i + ">"
//            				+ "WHERE {?sub ?prop ?obj. FILTER ( ?prop != rdf:type ) } LIMIT 50";
//
//            		r.queryPropertyIndividuals(propertyIndividuals, sparqlEndpoint);
//            		Thread.sleep(5000);
//            	} else {
            	String propertyIndividuals = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            			+ "SELECT DISTINCT  ?prop "
            			+ "FROM <" + i + ">"
            			+ "WHERE {?sub ?prop ?obj. FILTER ( ?prop != rdf:type ) }";

            		r.queryPropertyIndividuals(propertyIndividuals, sparqlEndpoint);
            		Thread.sleep(5000);
            	}
          //  }
            
            ////Write in the output file the results about the properties
            OutputInformation.showTextln("Result of query role individuals(iri of the role): ", false);
     		OutputInformation.showFrequentRolesStratified(false);
     		OutputInformation.showTextln(" ", false);
     		
     		OutputInformation.showTextln("Result of query role subsumed by roles: ", false);
     		OutputInformation.showFrequentRolesStratifiedAndSuperRoles(false);
     		OutputInformation.showTextln(" ", false);
     		
     		OutputInformation.showTextln("Result of query role which subsumes roles: ", false);
     		OutputInformation.showFrequentRolesStratifiedAndSubRoles(false);
     		OutputInformation.showTextln(" ", false);
     		
     		OutputInformation.showTextln("Result of query domain classes: ", false);
     		OutputInformation.showFrequentRolesStratifiedAndDomainsOfRoles(false);
     		OutputInformation.showTextln(" ", false);
     		
     		OutputInformation.showTextln("Result of query range classes: ", false);
     		OutputInformation.showFrequentRolesStratifiedAndRangesOfRoles(false);
     		OutputInformation.showTextln(" ", false);
     		
        	outputProp.closeFile();
        	
        	//Write in the output file the results about the individuals of all the classes and properties
        	OutputInformation outputInd = new OutputInformation("RESULTSQueryInd.txt");
        	OutputInformation.showTextln("Result of query all individuals: ", false);
    		OutputInformation.showIndividualsStratified(false);
    		OutputInformation.showTextln(" ", false);
    		outputInd.closeFile();
        }
        catch (HttpException ex) {
        	Thread.sleep(10000);
        }
    }
    
    /**
     * @param strFileName the file in which the rules will be written.
     * @return false if everything is okay.
     */
    public boolean myRun(String strFileName)
    {
        OutputInformation output = new OutputInformation(strFileName);
        
        double sumAverageFitness = 0;
        double dblAverageFitness = 0;
    
        double sumFitnessBiggerThan0 = 0;
        double dblAverageFitnessBiggerThan0 = 0;        
                
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        System.out.println("Initialization of the initial population (creation of patterns using the atoms in the list of frequent concepts and frequent roles)");
        Population population = new Population();
        System.out.println("Frequent concepts size: " + Global.allIRIFrequentConceptsStratified.size());
        System.out.println("Frequent roles size: " + Global.allIRIFrequentRolesStratified.size());
        population.autoCreatePopulation();  
       // OutputInformation.showPopulation(population, true);
        
        boolean blRunAgain = false;
        System.out.println("BEGIN=======================================================");
        for (int i = 1; i <= Global.MAX_SIZE_GENERATION; i++)
        {            
            try
            {
                System.out.println("Generation: " + i);
                population = geneticAlgorithm.generateNewGenerationByCrossover(population);
                //OutputInformation.showPopulation(population, true);
                population.compute();
                OutputInformation.showTextln("Generation:\t" + i + "\t" + population.getAverageFitness() + "\t" + 
                population.getAverageFitnessBiggerThan0() + "\t" + population.getCountPatternFitnessBiggerThan0(), false);
                sumAverageFitness += population.getAverageFitness();
                sumFitnessBiggerThan0 += population.getAverageFitnessBiggerThan0();
            }
            
            catch(java.lang.OutOfMemoryError e)
            {
            	System.out.println(e);
                blRunAgain = true;
                break;
            }
        }
        
        if (!blRunAgain)
        {        
            dblAverageFitness = sumAverageFitness / Global.MAX_SIZE_GENERATION;
            dblAverageFitnessBiggerThan0 = sumFitnessBiggerThan0 / Global.MAX_SIZE_GENERATION;

            OutputInformation.showTextln("", false);
            OutputInformation.showTextln("Average of sum Average Fitness of Stratified Ontology: " + dblAverageFitness, false);
            OutputInformation.showTextln("Average of sum of Average Fitness > 0 of Stratified Ontology: " + dblAverageFitnessBiggerThan0, false);
            OutputInformation.showTextln("", false);

            System.out.println("END=======================================================");        

            int count = 0;

            System.out.println("Size of final population: " + population.getListIndividuals().size());
            int count2 = 0;
            for(int i = 0; i < population.getListIndividuals().size(); i++)
            {   
                if (population.getListIndividuals().get(i).getPatternComputation().getFitnessValue() > 0)                        
                    //if (kbStratified.addPatternHorn(population.getListIndividuals().get(i)))
                    {
                        ++count;

                        OutputInformation.showText(count + ". ", true);
                        OutputInformation.showPattern(population.getListIndividuals().get(i), true);
                        OutputInformation.showText(count + ". ", false);
                        OutputInformation.showPattern(population.getListIndividuals().get(i), false);
                    }
                else {
                	count2++;
                }

            }
            System.out.println("Survived " + count + " patterns.");
            System.out.println("Lost " + count2 + " patterns, because the fintess values were less than zero.");
        }
        
        output.closeFile();    
        
        return blRunAgain;
    }
    
    /**
	 * The entry point of the Genetic Algorithm application.
     * @param args 
     * @throws Exception 
	 */
    public static void main(String args[]) throws Exception
    {
        System.out.println("Start !!!");
        
        try {
        	//Populate the data structure
        	MainClass mainClass = new MainClass();

        	int i = 1;

        	while (i <= 2)        
        	{
        		System.out.println();
        		System.out.println("=======================================================");
        		System.out.println("BEGIN THE SAMPLE " + i);            
        		if (!mainClass.myRun(Global.OUTPUT_PATTERNS_IN_FILE + "_" + String.valueOf(i) + ".txt")) 
        		{
        			System.out.println("END THE SAMPLE " + i);
        			System.out.println("=======================================================");
        	    	System.out.println("BEGIN THE CHECK " + i );
        	    	int survived=0;
        	    	FileWriter fileWriter = new FileWriter("C:/Users/valer/Desktop/Internship/CovidRules_Consistent.txt",true);
        			BufferedWriter writer = new BufferedWriter(fileWriter);
        	    	try
        	    	{
        	    	//	OutputInformation output = new OutputInformation("C:/Users/valer/Desktop/Internship/CovidRules_Consistent.txt");
        	    		FileReader inputFile = new FileReader("C:\\\\Users\\\\valer\\\\eclipse-workspace\\\\GeneticAltog2\\Covid19_"+i+".txt");
        	    		BufferedReader brInputFile = new BufferedReader(inputFile);

        	    		String line = "";            
        	    		while ((line = brInputFile.readLine()) != null)
        	    		{
        	    			String strPattern = line.substring(line.indexOf(".") + 1, line.lastIndexOf("&") + 1).trim();
        	    			System.out.println(strPattern);
        	    			survived = survived + createRuleByText(strPattern);

        	    		}
        	    		writer.append("\n");
        	    		writer.append("Rules survived: " + survived);
        	    		

        	    		brInputFile.close();  
        	    		writer.close();
        	    	//	output.closeFile();
        	    	}
        	    	catch (IOException e)
        	    	{
        	    		e.getStackTrace();
        	    		System.out.println(e);
        	    	}  
        	    	//CheckPattern checkPattern = new CheckPattern();
        	    	System.out.println("=======================================================");
        	    	System.out.println();
        			i++;
        		}            

        		System.out.println("=======================================================");
        		System.out.println();
        }
        
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        System.out.println("Finish !!!");
    }
    

        /**
	 * Read the rules from the file generated with the Genetic Algorithm and 
	 * for each generated rule went to query the dataset with a new query to understand if the 
	 * rule is true.
	 * If the number of elements returned by the query is greater than 3, the rule is considered false.
	 * Otherwise, the rule is considered true.
	 * @param strRule is the rule to check
	 * @return zero if there rule is not correct and one in the other case.
	 * @throws IOException
	 */
    public static  int createRuleByText(String strRule) throws IOException
	{
		FileWriter fileWriter = new FileWriter("C:/Users/valer/Desktop/Internship/CovidRules_Consistent.txt",true);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		int count=0;
		//HEAD: create the string for the IRI, and the variables.
		String strHead = strRule.substring(0, strRule.indexOf("<=")).trim();
		String head, strVariable1, strVariable2 = null;
		if (strHead.contains(", "))
		{
			head = strHead.substring(0, strHead.indexOf("(")).trim();
			strVariable1 = strHead.substring(strHead.indexOf("(") + 1, strHead.indexOf(",")).trim();
			strVariable2 = strHead.substring(strHead.indexOf(",") + 1, strHead.indexOf(")")).trim();
		} else {
			head = strHead.substring(0, strHead.indexOf("(")).trim();
			strVariable1 = strHead.substring(strHead.indexOf("(") + 1, strHead.indexOf(")")).trim();   
		}

		//BODY: create an arraylist containing all the IRI of the body and one containing for each position
		//the variables of the corresponding IRI
		String strBody = strRule.substring(strRule.indexOf("<=") + 2).trim();
		String[] body = strBody.split("&");
		ArrayList<String> variables = new ArrayList<String>();
		ArrayList<String> patternsBody = new ArrayList<String>();


		for(int i=0; i<body.length; i++) {
			String strName = body[i].substring(0, body[i].indexOf("(")).trim();
			patternsBody.add(strName);
			/*if (body[i].contains(",")) {
				String var1= body[i].substring(body[i].indexOf("(") + 1, body[i].indexOf(",")).trim();
				String var2 = body[i].substring(body[i].indexOf(",") + 1, body[i].indexOf(")")).trim();
				String var=var1.concat(",").concat(var2);
				variables.add(var);
			} else {*/
				String var = body[i].substring(body[i].indexOf("(") + 1, body[i].indexOf(")")).trim();
				variables.add(var);
			//}
		}


		String where = "WHERE { "; 
		for(int i=0; i<patternsBody.size(); i++) {
			System.out.println(variables.get(i));
			if(variables.get(i).length()!=3 && variables.get(i).length()!=2) {
				where =where.concat((variables.get(i).substring(0, variables.get(i).indexOf(",")))).concat(" ");
				where = where.concat("<").concat(patternsBody.get(i)).concat("> ");
				where =where.concat((variables.get(i).substring(variables.get(i).indexOf(",")+1))).concat(". ");
			}
			else {
				where =where.concat(variables.get(i)).concat(" a ");
				where = where.concat("<").concat(patternsBody.get(i)).concat("> .");
			}
		}

		//SELECT: only the variables in the head
		String select = "SELECT DISTINCT ";
		if(strVariable2!=null){
			select =select.concat(strVariable1).concat(" ");
			select =select.concat(strVariable2).concat(" ");
		} else
			select =select.concat(strVariable1).concat(" ");


		//FILTER
		//FILTER (NOT EXISTS {?x  <http://purl.org/spar/fabio/hasPubMedId> ?y} )}
		String filter = " FILTER (NOT EXISTS {";
		if(strVariable2!=null){
			filter =filter.concat(strVariable1).concat(" ");
			filter = filter.concat("<").concat(head).concat("> " );
			filter =filter.concat(strVariable2).concat("})}");
		} else 
			filter =filter.concat(strVariable1).concat(" a ").concat("<").concat(head).concat("> })}");

		// OutputInformation output = new OutputInformation("Covid_Rules_consistent.txt");
		String queryPattern = select.concat(where).concat(filter);
		System.out.println(queryPattern);


		Query query = QueryFactory.create(queryPattern);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://covidontheweb.inria.fr/sparql", query);

		try {
			ResultSet results = qexec.execSelect();
			ResultSetFormatter.consume(results);
			if(results.getRowNumber()<3) {
				writer.append(strRule);
				writer.append("\n");
				count++;
			} 
		}

		finally {
			qexec.close();
		}
	
		writer.close();
		return count;
	}
}
