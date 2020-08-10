
import MCommon.Global;
import MCommon.MyRandom;
import MDataProcessing.ConceptProcessing;
import MDataProcessing.IndividualProcessing;
import MDataProcessing.RoleProcessing;
import MGeneticAlgorithm.GeneticAlgorithm;
import MGeneticAlgorithm.Population;
import MKnowledge.KnowledgeBase;
import MOutput.OutputInformation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Minh and Valeria Bellusci
 */
public class MainClass 
{   
	private KnowledgeBase kbStratified;
	
    /**
	 * This method is used to populate the data structure containing all the main elements of the data-sets.
	 * All these elements are taken from the owl file, using a reasoner.
	 */
    public MainClass()
    {   
        Global.myRandom = new MyRandom();        
        this.kbStratified = new KnowledgeBase(Global.IRI_INPUT_STRATIFIED); 
        ConceptProcessing.createFrequentConceptsStratified(this.kbStratified, Global.FR_THR);
        ConceptProcessing.createConceptIsSubsumedByConcepts(kbStratified);
        ConceptProcessing.createConceptSubsumsConcepts(kbStratified);
        RoleProcessing.createFrequentRolesStratified(this.kbStratified, Global.FR_THR);
        RoleProcessing.createRoleIsSuperPropertyRoles(this.kbStratified);
        RoleProcessing.createRoleIsSubPropertyRoles(this.kbStratified);
        RoleProcessing.createConceptsDomainOfRole(this.kbStratified);
        RoleProcessing.createConceptsRangeOfRole(this.kbStratified);
        IndividualProcessing.getAllOfIndividuals(this.kbStratified);
    }
    
    
    /**
     * @param strFileName the file in which the rules will be written.
     * @return true if everything is okay.
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
		    i++;
		}            
		
		System.out.println("=======================================================");
		System.out.println();
      }  
        System.out.println("Finish !!!");
    }
}
