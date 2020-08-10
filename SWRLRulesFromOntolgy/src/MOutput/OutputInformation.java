/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MOutput;

import MAtom.Atom;
import MAtom.ConceptAtom;
import MAtom.RoleAtom;
import MCommon.Global;
import MDataProcessing.Assertion.ConceptAssertion;
import MDataProcessing.Assertion.RoleAssertion;
import MDataProcessing.Individual.ConceptIndividuals;
import MDataProcessing.Individual.RoleIndividual;
import MDataProcessing.Individual.RoleIndividuals;
import MGeneticAlgorithm.Population;
import MPattern.Pattern;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

/**
 *
 * Class used to manage the output of the program.
 */
public class OutputInformation 
{
    /**
     * Initialize the output file for the generated rules.
     */
    public static File outputFile = null;
    
    /**
     * Initialize the print stream.
     */
    public static PrintStream print_file = null;
 
    
    /**
     * Create an output file with the string in input and create a new print stream with it.
     * @param strOutputFileName
     */
    public OutputInformation(String strOutputFileName)
    {
        try
        {
            OutputInformation.outputFile = new File(strOutputFileName);
            OutputInformation.print_file = new PrintStream(OutputInformation.outputFile);
        }
        catch (IOException e)
        {
            e.getStackTrace();
        }
    }
    
  
    /**
     * Show in the output file the text in input, without going to the line.
     * @param strText is the text to show.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showText(String strText, boolean type)
    {
        OutputInformation.outputType(type).print(strText);
    }

    /**
     * Show in the output file the text in input.
     * @param strText is the text to show
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showTextln(String strText, boolean type)
    {
        OutputInformation.outputType(type).println(strText);
    }
    
    /**
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     * @return true if the text must be printed also in the Eclipse console, false otherwise.
     */
    public static PrintStream outputType(boolean type)
    {
        if (type)
            return System.out;
        else
            return OutputInformation.print_file;
    }
	
    /**
     * Close the output file.
     */
    public void closeFile()
    {
        if (OutputInformation.print_file != null)        
            OutputInformation.print_file.close();
    }
    
    
    //////////////////////////////////////////////////////////////////////////
    //BEGIN: The below methods are used for showing information about the KB//
    //////////////////////////////////////////////////////////////////////////
    
    /**
     * Print in the file the classes of the KB stratified.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentConceptsStratified(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        for(int i = 0; i < Global.allFrequentConceptsStratified.size(); i++)
        {
            ConceptAssertion conceptAssertions = Global.allFrequentConceptsStratified.get(i);
            output.println(String.valueOf(i + 1) + ". " + conceptAssertions.getConceptName());
        }
    }
    
    /**
     * Print in the file the classes of the KB stratified and their individuals.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentConceptsStratifiedAndAssertions(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        for(int i = 0; i < Global.allFrequentConceptsStratified.size(); i++)
        {
            ConceptAssertion conceptAssertions = Global.allFrequentConceptsStratified.get(i);
            output.println(String.valueOf(i + 1) + ". " + conceptAssertions.getConceptName());
            
            ConceptIndividuals conceptIndividuals = conceptAssertions.getIndividuals();
            Set<String> setIndividualsName = conceptIndividuals.getIndividualsName();
            
            if (!setIndividualsName.isEmpty())
            {
                int j = 1;
                for(String strIndividualName : setIndividualsName)
                    output.println("    " + String.valueOf(j++) + ". " + strIndividualName);
            }
        }
    }        
    
    /**
     * Print in the file the classes and their sub classes.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentConceptsStratifiedAndSubConcepts(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.conceptSubsumsConcepts.isEmpty())
        {       
            Set<IRI> setSuperConcepts = Global.conceptSubsumsConcepts.keySet();
            
            for(int i = 0; i < Global.allFrequentConceptsStratified.size(); i++)
            {
                ConceptAssertion conceptAssertions = Global.allFrequentConceptsStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + conceptAssertions.getConceptName());
                
                if (setSuperConcepts.contains(conceptAssertions.getIRIConcept()))
                {
                    Set<IRI> setSubConcepts = Global.conceptSubsumsConcepts.get(conceptAssertions.getIRIConcept());
                    
                    if (!setSubConcepts.isEmpty())
                    {
                        int j = 1;
                        for(IRI subConcept : setSubConcepts)
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(subConcept));
                    }    
                }
            }
        }
      
    }
    
    /**
     * Print in the output file the classes and their super classes.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentConceptsStratifiedAndSuperConcepts(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.conceptIsSubsumedByConcepts.isEmpty())
        {       
            Set<IRI> setSubConcepts = Global.conceptIsSubsumedByConcepts.keySet();
            
            for(int i = 0; i < Global.allFrequentConceptsStratified.size(); i++)
            {
                ConceptAssertion conceptAssertions = Global.allFrequentConceptsStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + conceptAssertions.getConceptName());
                
                if (setSubConcepts.contains(conceptAssertions.getIRIConcept()))
                {
                    Set<IRI> setSuperConcepts = Global.conceptIsSubsumedByConcepts.get(conceptAssertions.getIRIConcept());
                    
                    if (!setSuperConcepts.isEmpty())
                    {
                        int j = 1;
                        for(IRI superConcept : setSuperConcepts)
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(superConcept));
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratified(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
        {
            RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
            output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their individuals.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndAssertions(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
        {
            RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
            output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
            
            RoleIndividuals roleIndividuals = roleAssertions.getIndividuals();
            Set<RoleIndividual> setIndividualsName = roleIndividuals.getIndividuals();
            
            if (!setIndividualsName.isEmpty())
            {
                int j = 1;
                for(RoleIndividual roleIndividualName : setIndividualsName)
                    output.println("    " + String.valueOf(j++) + ". " + roleIndividualName.getDomain() + " - " + roleIndividualName.getRange());
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their sub properties.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndSubRoles(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.roleIsSuperPropertyOfRoles.isEmpty())
        {        
            Set<IRI> setSuperRoles = Global.roleIsSuperPropertyOfRoles.keySet();
            
            for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
            {
                RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
                
                if (setSuperRoles.contains(roleAssertions.getIRIRole()))
                {
                    Set<IRI> setSubRoles = Global.roleIsSuperPropertyOfRoles.get(roleAssertions.getIRIRole());
                    
                    if (!setSubRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI subRole : setSubRoles)
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(subRole));
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their super properties.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndSuperRoles(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.roleIsSubPropertyOfRoles.isEmpty())
        {        
            Set<IRI> setSubRoles = Global.roleIsSubPropertyOfRoles.keySet();
            
            for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
            {
                RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
                
                if (setSubRoles.contains(roleAssertions.getIRIRole()))
                {
                    Set<IRI> setSuperRoles = Global.roleIsSubPropertyOfRoles.get(roleAssertions.getIRIRole());
                    
                    if (!setSuperRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI superRole : setSuperRoles)
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(superRole));
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their domain classes.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndDomainsOfRoles(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.conceptsDomainOfRole.isEmpty())
        {        
            Set<IRI> setOfRoles = Global.conceptsDomainOfRole.keySet(); 
            
            for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
            {
                RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
                
                if (setOfRoles.contains(roleAssertions.getIRIRole()))
                {
                    Set<IRI> setConceptsDomainOfRoles = Global.conceptsDomainOfRole.get(roleAssertions.getIRIRole());
                    
                    if (!setConceptsDomainOfRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI conceptDomain : setConceptsDomainOfRoles)
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(conceptDomain));
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their domain classes (with also the classes which subsume the domain class).
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndDomainsAndConceptsSubsumDomainOfRoles(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.conceptsDomainOfRole.isEmpty())
        {        
            Set<IRI> setOfRoles = Global.conceptsDomainOfRole.keySet(); 
            
            for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
            {
                RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
                
                if (setOfRoles.contains(roleAssertions.getIRIRole()))
                {
                    Set<IRI> setConceptsDomainOfRoles = Global.conceptsDomainOfRole.get(roleAssertions.getIRIRole());
                    
                    if (!setConceptsDomainOfRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI conceptDomain : setConceptsDomainOfRoles)
                        {
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(conceptDomain));
                            
                            Set<IRI> iriListConceptsSubsum = Global.conceptIsSubsumedByConcepts.get(conceptDomain);
                            
                            int k = 1;
                            for(IRI iriConceptSubsum : iriListConceptsSubsum)
                                output.println("        " + String.valueOf(k++) + ". " + Global.cutNameOfIRI(iriConceptSubsum));
                        }
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their range classes.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndRangesOfRoles(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.conceptsRangeOfRole.isEmpty())
        {        
            Set<IRI> setOfRoles = Global.conceptsRangeOfRole.keySet(); 
            
            for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
            {
                RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
                
                if (setOfRoles.contains(roleAssertions.getIRIRole()))
                {
                    Set<IRI> setConceptsRangeOfRoles = Global.conceptsRangeOfRole.get(roleAssertions.getIRIRole());
                    
                    if (!setConceptsRangeOfRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI conceptRange : setConceptsRangeOfRoles)
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(conceptRange));
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their range classes (with also the classes which subsume the range class).
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndRangesAndConceptsSubsumRangeOfRoles(boolean type)    
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.conceptsRangeOfRole.isEmpty())
        {        
            Set<IRI> setOfRoles = Global.conceptsRangeOfRole.keySet(); 
            
            for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
            {
                RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
                
                if (setOfRoles.contains(roleAssertions.getIRIRole()))
                {
                    Set<IRI> setConceptsRangeOfRoles = Global.conceptsRangeOfRole.get(roleAssertions.getIRIRole());
                    
                    if (!setConceptsRangeOfRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI conceptRange : setConceptsRangeOfRoles)
                        {
                            output.println("    " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(conceptRange));
                            
                            Set<IRI> iriListConceptsSubsum = Global.conceptIsSubsumedByConcepts.get(conceptRange);
                            
                            int k = 1;
                            for(IRI iriConceptSubsum : iriListConceptsSubsum)
                                output.println("        " + String.valueOf(k++) + ". " + Global.cutNameOfIRI(iriConceptSubsum));
                        }
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the properties of the KB stratified and their domain and range classes.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showFrequentRolesStratifiedAndDomainsRangesOfRoles(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        if (!Global.conceptsDomainOfRole.isEmpty() && !Global.conceptsRangeOfRole.isEmpty())
        {        
            Set<IRI> setOfRolesOfDomain = Global.conceptsDomainOfRole.keySet(); 
            Set<IRI> setOfRolesOfRange = Global.conceptsRangeOfRole.keySet(); 
            
            for(int i = 0; i < Global.allFrequentRolesStratified.size(); i++)
            {
                RoleAssertion roleAssertions = Global.allFrequentRolesStratified.get(i);
                output.println(String.valueOf(i + 1) + ". " + roleAssertions.getRoleName());
                
                if (setOfRolesOfDomain.contains(roleAssertions.getIRIRole()))
                {
                    output.println("    Domain:");
                    
                    Set<IRI> setConceptsDomainOfRoles = Global.conceptsDomainOfRole.get(roleAssertions.getIRIRole());
                    
                    if (!setConceptsDomainOfRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI conceptDomain : setConceptsDomainOfRoles)
                            output.println("        " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(conceptDomain));
                    }    
                }
                
                if (setOfRolesOfRange.contains(roleAssertions.getIRIRole()))
                {
                    output.println("    Range:");
                    
                    Set<IRI> setConceptsRangeOfRoles = Global.conceptsRangeOfRole.get(roleAssertions.getIRIRole());
                    
                    if (!setConceptsRangeOfRoles.isEmpty())
                    {
                        int j = 1;
                        for(IRI conceptRange : setConceptsRangeOfRoles)
                            output.println("        " + String.valueOf(j++) + ". " + Global.cutNameOfIRI(conceptRange));
                    }    
                }
            }
        }
    }
    
    /**
     * Print in the output file the individuals of the KB stratified.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showIndividualsStratified(boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        output.println("Size: " + Global.allIndividuals.size());
        
        if (!Global.allIndividuals.isEmpty())
        {
            int count = 0;
            
            for(String strIndividual : Global.allIndividuals)            
                output.println(++count + ": " + strIndividual);            
        }
    }
    
    /**
     * Print in the output file the pattern.
     * @param pattern to be shown.
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showPattern(Pattern pattern, boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        ArrayList<Atom> listAtoms = pattern.getListAtoms();
        
        for (int i = 0; i < listAtoms.size(); i++)
        {
            Atom atom = listAtoms.get(i);
            
            if (atom instanceof ConceptAtom)
                output.print(((ConceptAtom) atom).getName() + "(" + ((ConceptAtom) atom).getVariable() + ")");
            else if (atom instanceof RoleAtom)
                output.print(((RoleAtom) atom).getName() + "(" + ((RoleAtom) atom).getDomainVariable() + ", " + ((RoleAtom) atom).getRangeVariable() + ")");
            
            if (i == 0)
                output.print(" <= ");
            else
                output.print(" & ");
        }
        
        output.println("\t" + pattern.getPatternComputation().getFitnessValue());        
    }
    
    /**
     * Print in the output file all the pattern in the population.
     * @param population
     * @param type could be true (if the text must be printed also in the Eclipse console), false otherwise.
     */
    public static void showPopulation(Population population, boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        ArrayList<Pattern> listIndividuals = population.getListIndividuals();
        
        for(int i = 0; i < listIndividuals.size(); i++)
        {
            output.print(i + 1 + ": ");
            
            Pattern individual = listIndividuals.get(i);
            ArrayList<Atom> listAtoms = individual.getListAtoms();
            
            for (int j = 0; j < listAtoms.size(); j++)
            {
                Atom atom = listAtoms.get(j);

                if (atom instanceof ConceptAtom)
                    output.print(((ConceptAtom) atom).getName() + "(" + ((ConceptAtom) atom).getVariable() + ")");
                else if (atom instanceof RoleAtom)
                    output.print(((RoleAtom) atom).getName() + "(" + ((RoleAtom) atom).getDomainVariable() + ", " + ((RoleAtom) atom).getRangeVariable() + ")");

                if (j == 0)
                    output.print(" <= ");
                else
                    output.print(" & ");
            }
            
            output.print(individual.getPatternComputation().getFitnessValue() + " = " + individual.getPatternComputation().getHeadCoverage() + " + " + individual.getPatternComputation().getPCAConfidence() + " ; ");
            output.println();
        }
    }
}
