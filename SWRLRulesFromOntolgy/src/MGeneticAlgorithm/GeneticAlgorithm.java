/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MGeneticAlgorithm;

import MAtom.Atom;
import MAtom.ConceptAtom;
import MAtom.RoleAtom;
import MCommon.Global;
import MPattern.Computation.PatternComparator;
import MPattern.Pattern;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * A class to manage the evolution of the population for the creation of SWRL rules.
 */
public class GeneticAlgorithm 
{

    
    /**
     * This method generates a new generation applying the crossover and then the mutation operators.
     * @param oldPopulation the population that needs to evolve.
     * @return the new population.
     */
    public Population generateNewGenerationByCrossover(Population oldPopulation)
    {
        ArrayList<Pattern> oldListPatterns = oldPopulation.getListIndividuals();
        
        ArrayList<Pattern> newListPatterns = new ArrayList<Pattern>();
        newListPatterns.addAll(oldListPatterns);
        
        for(int i=0; i<Global.CROSSOVER_SIZE; i++)
        { 
        	//a population has maximum 5000 individuals, so in this case we take the best
        	//the crossover_size is 1000, so we mate patterns in position from 0 to 2000
        	//and then we mate patterns from 0 to 3000
            ArrayList<Pattern> patternCrossOver1 = crossOver(oldListPatterns.get(i), oldListPatterns.get(i+Global.CROSSOVER_SIZE));
            ArrayList<Pattern> patternCrossOver2 = crossOver(oldListPatterns.get(i), oldListPatterns.get(i+2*Global.CROSSOVER_SIZE));
          
            //it loops only 2 times, because there are only 2 offsprings
            for(int j=0; j<patternCrossOver1.size(); j++)
            {
                boolean acceptAdd = true;
                
                for(int k = 0; k < newListPatterns.size(); k++)
                {
                	//check if the new patterns already exist in the population
                    Pattern patternInNewList = newListPatterns.get(k);
                    if (patternInNewList.equals(patternCrossOver1.get(j)))                
                    {
                        acceptAdd = false;
                        break;
                    }
                }
                //the generated patterns don't exist in the population
                if (acceptAdd) 
                {
                    patternCrossOver1.get(j).compute();
                    
                    int numberRandom = Global.myRandom.generateInt(1, 100);
                    //perturb the first pattern generated (offsprings) with a probability
                    if (numberRandom <= 5)
                    {
                        Pattern patternMutation = mutation(patternCrossOver1.get(j));
                        
                        for(int k = 0; k < newListPatterns.size(); k++)
                        {
                            Pattern patternInNewList = newListPatterns.get(k);
                            if (patternInNewList.equals(patternMutation))                
                            {
                                acceptAdd = false;
                                break;
                            }
                        }
                        
                        if (acceptAdd) 
                        {
                            patternMutation.compute();
                            newListPatterns.add(patternMutation);
                        }
                    }
                    else
                        newListPatterns.add(patternCrossOver1.get(j));
                }
            }
            
            for(int j=0; j<patternCrossOver2.size(); j++)
            {
                boolean acceptAdd = true;
                
                for(int k = 0; k < newListPatterns.size(); k++)
                {
                    Pattern patternInNewList = newListPatterns.get(k);
                    if (patternInNewList.equals(patternCrossOver2.get(j)))                
                    {
                        acceptAdd = false;
                        break;
                    }
                }
                
                if (acceptAdd)
                {	
                    patternCrossOver2.get(j).compute();
                    
                    int numberRandom = Global.myRandom.generateInt(1, 100);

                    if (numberRandom <= 5)
                    {   //disturbs the second pattern generated
                        Pattern patternMutation = mutation(patternCrossOver2.get(j));
                        
                        for(int k = 0; k < newListPatterns.size(); k++)
                        {
                            Pattern patternInNewList = newListPatterns.get(k);
                            if (patternInNewList.equals(patternMutation))                
                            {
                                acceptAdd = false;
                                break;
                            }
                        }
                        
                        if (acceptAdd) 
                        {
                            patternMutation.compute();
                            newListPatterns.add(patternMutation);
                        }
                    }
                    else
                        newListPatterns.add(patternCrossOver2.get(j));
                }
            }
        }
        newListPatterns.sort(new PatternComparator());
        
        while(newListPatterns.size() > Global.MAX_SIZE_POPULATION)
            newListPatterns.remove(newListPatterns.size() - 1);
        
        return new Population(newListPatterns);
    }

    /**
     * AThe crossover operator produces two offsprings from two parent patterns. 
     * The atoms of the offspring are selected from the atoms of the parents.
     * These offsprings are then perturbed by the mutation operator with a given probability. 
     * Finally, the offsprings are added to the population.
     * @param pattern1 the parent pattern
     * @param pattern2 the parent pattern
     * @return the two output patterns created by taking random atoms from the list of atoms of the parents and adjusting the variables of the atoms.
     */
    public ArrayList<Pattern> crossOver(Pattern pattern1, Pattern pattern2)
    {
        ArrayList<Atom> listAtomPattern1 = new ArrayList<Atom>(pattern1.getListAtoms());
        ArrayList<Atom> listAtomPattern2 = new ArrayList<Atom>(pattern2.getListAtoms());
        
        //initialize the sets containing the atoms of the parents
        Set<Atom> setAtomsPatternOut1 = new LinkedHashSet<Atom>();
        Set<Atom> setAtomsPatternOut2 = new LinkedHashSet<Atom>();        
        //these sets contain only the concepts or roles names of the parents patterns
        for(int i = 0; i < listAtomPattern1.size(); i++)
        {
            setAtomsPatternOut1.add(listAtomPattern1.get(i));
            setAtomsPatternOut2.add(listAtomPattern1.get(i));
        }
        
        for(int i = 0; i < listAtomPattern2.size(); i++)
        {
            setAtomsPatternOut1.add(listAtomPattern2.get(i));
            setAtomsPatternOut2.add(listAtomPattern2.get(i));
        }
        
        ArrayList<Atom> listAtoms1 = new ArrayList<Atom>(setAtomsPatternOut1);
        ArrayList<Atom> listAtoms2 = new ArrayList<Atom>(setAtomsPatternOut2);
        
        //initialize randomly the maximum length of the two offsprings
        int maxLengthPatternOut1 = Global.myRandom.generateInt(2, Global.MAX_LENGTH_PATTERN);
        int maxLengthPatternOut2 = Global.myRandom.generateInt(2, Global.MAX_LENGTH_PATTERN);        
        
        ArrayList<Pattern> patternsReturn = new ArrayList<Pattern>();
        Pattern patternOut1 = null;
        Pattern patternOut2 = null;
        
        int countAdjustPattern1 = 0; 
        
        do
        {        
            patternOut1 = new Pattern();
            int countAtomPatternOut1 = 0;

            while (countAtomPatternOut1 < maxLengthPatternOut1)
            {
                int count = 0;
                while (count < Global.MAX_LOOP_FOR_CHECK)
                {
                	//take a random atom from the parent 1 and add it to the new pattern
                    int patternLocation = Global.myRandom.generateInt(1, listAtoms1.size());
                    Atom atom = listAtoms1.get(patternLocation - 1); 
                    if (atom instanceof ConceptAtom)
                    {
                        if (patternOut1.addAtom(new ConceptAtom(atom.getIRI())))
                        {
                            countAtomPatternOut1++;
                            break;
                        }
                        else count++;
                            
                    }
                    else if (atom instanceof RoleAtom)
                    {
                        if (patternOut1.addAtom(new RoleAtom(atom.getIRI())))
                        {
                            countAtomPatternOut1++;
                            break;
                        }
                        else count++;
                    }
                }
                if (count >= Global.MAX_LOOP_FOR_CHECK) break;
            }
            //the new pattern is created
            
            countAdjustPattern1++;
        }
        while (!patternOut1.adjustPattern() && (countAdjustPattern1 < Global.MAX_LOOP_FOR_CHECK));               
        
        //add the new pattern to the list of patterns of the offspring
        if ((patternOut1.getListAtoms().size() >= 2) && (countAdjustPattern1 < Global.MAX_LOOP_FOR_CHECK))
            patternsReturn.add(patternOut1);        
        
        int countEqual = 0;           
        do
        {        
            int countAdjustPattern2 = 0;
            do
            {   
                patternOut2 = new Pattern();
                int countAtomPatternOut2 = 0;

                while (countAtomPatternOut2 < maxLengthPatternOut2)
                {
                    int count = 0;

                    while (count < Global.MAX_LOOP_FOR_CHECK)
                    {	//take a random atom from the parent 2 and add it to the new pattern
                        int patternLocation = Global.myRandom.generateInt(1, listAtoms2.size());
                        Atom atom = listAtoms2.get(patternLocation - 1);                        
                        if (atom instanceof ConceptAtom)
                        {
                            if (patternOut2.addAtom(new ConceptAtom(atom.getIRI())))
                            {
                                countAtomPatternOut2++;
                                break;
                            }
                            else count++;

                        }
                        else if (atom instanceof RoleAtom)
                        {
                            if (patternOut2.addAtom(new RoleAtom(atom.getIRI())))
                            {
                                countAtomPatternOut2++;
                                break;
                            }
                            else count++;
                        }
                    }
                    if (count >= Global.MAX_LOOP_FOR_CHECK) break;
                }
                //the new pattern is created
                
                countAdjustPattern2++;
            }
            while (!patternOut2.adjustPattern() && (countAdjustPattern2 < Global.MAX_LOOP_FOR_CHECK));
            
            countEqual++;
        }
        while(patternOut2.equals(patternOut1) && (countEqual < Global.MAX_LOOP_FOR_CHECK));
        //add the second offspring to the set of patterns
        if ((patternOut2.getListAtoms().size() >= 2) && (countEqual < Global.MAX_LOOP_FOR_CHECK))
            patternsReturn.add(patternOut2);
        
        return patternsReturn;
    }
    
    /**
     * The mutation operator disturbs the patterns from the offsprings of crossover with a given probability.
     * It uses two operators based on the idea of specialization (adding atoms) and generalization (removing atoms).
     * @param pattern the pattern to be disturbed.
     * @return the modified pattern.
     */
    public Pattern mutation(Pattern pattern)
    {
        ArrayList<IRI> listIRIFrequentConcepts = new ArrayList<IRI>(Global.allIRIFrequentConceptsStratified);
        ArrayList<IRI> listIRIFrequentRoles = new ArrayList<IRI>(Global.allIRIFrequentRolesStratified);
        
        Pattern patternTemp = new Pattern(pattern);
        
        //the head coverage value of the pattern is above a given threshold (0,2)
        if (pattern.getPatternComputation().getHeadCoverage() > Global.MUTATION_THR)
        {
            Pattern patternOut = null;
            //the size of the pattern is smaller than the maximum length (10)
            if (pattern.getListAtoms().size() < Global.MAX_LENGTH_PATTERN)
            {   
                int countAdjust = 0;
                //Specialization operator
                while(countAdjust < Global.MAX_LENGTH_PATTERN)
                {
                    patternOut = new Pattern(patternTemp);                    
                    int newLengthPatternOut = Global.myRandom.generateInt(pattern.getListAtoms().size() + 1, Global.MAX_LENGTH_PATTERN);
                    
                    boolean breakDoWhile = false;

                    for (int i = 1; i <= newLengthPatternOut - pattern.getListAtoms().size(); i++)
                    {
                        int count = 0;
                        
                        do
                        {
                            breakDoWhile = false;
                            
                          //take a random atom from the frequent atoms of the knowledge base and add it to the pattern
                            switch(Global.myRandom.generateInt(1, 2))
                            {  
                                case 1:      
                                    int iConceptLocation = Global.myRandom.generateInt(1, listIRIFrequentConcepts.size());
                                    ConceptAtom conceptAtom = new ConceptAtom(listIRIFrequentConcepts.get(iConceptLocation - 1));

                                    if (patternOut.addAtom(conceptAtom))                                
                                        breakDoWhile = true;                                
                                    else
                                        count++;

                                    break;

                                case 2: 
                                    int iRoleLocation = Global.myRandom.generateInt(1, listIRIFrequentRoles.size());
                                    RoleAtom roleAtom = new RoleAtom(listIRIFrequentRoles.get(iRoleLocation - 1));

                                    if (patternOut.addAtom(roleAtom))                                
                                        breakDoWhile = true;                                
                                    else
                                        count++;

                                    break;
                            }
                        }
                        while (!breakDoWhile && (count < Global.MAX_LENGTH_PATTERN * 2));

                        if (!breakDoWhile && (count >= Global.MAX_LOOP_FOR_CHECK * 2))                        
                           break;
                    }
                    
                    if (breakDoWhile)
                        if (!patternOut.adjustPattern() && (countAdjust < Global.MAX_LENGTH_PATTERN)) countAdjust++;
                        else break;
                    else
                        if (countAdjust >= Global.MAX_LENGTH_PATTERN) break;
                        else countAdjust++;                            
                }
                
                if (countAdjust >= Global.MAX_LENGTH_PATTERN)
                    return new Pattern();
                else
                    return patternOut;
            }
            else
                return mutationSupport(pattern);
        }
        //the head coverage is greater than the threshold
        else // <= Global.MUTATION_THR
        {
            if (patternTemp.getListAtoms().size() > 2)
            {//Generalization operator

                ArrayList<Integer> numberOfRandom = new ArrayList<Integer>();
                
                for(int i = 1; i <= patternTemp.getListAtoms().size() - 2; i++)
                    numberOfRandom.add(i);
                
                Pattern patternOut = null;
                
                while(!numberOfRandom.isEmpty())
                {   
                    patternOut = new Pattern(patternTemp); 
                    
                    int iLocation = Global.myRandom.generateInt(1, numberOfRandom.size());
                    int iQuantityOfRemoving = numberOfRandom.get(iLocation - 1);
                    
                    if (!patternOut.removeAtomContainsLastFreshVariable(iQuantityOfRemoving))
                        numberOfRandom.remove(iLocation - 1);
                    else break;
                }
                
                if (numberOfRandom.isEmpty())
                    return mutationSupport(pattern);
                else
                    return patternOut;
            }
            else
                return mutationSupport(pattern);
        }
    }
    
    /**
     * Add atoms to the pattern created with the variables and the IRI of the head atom of the input pattern, ensuring the language bias.
     * @param pattern It is the pattern to which take the head atom.
     * @return the filled pattern.
     */
    private Pattern mutationSupport(Pattern pattern)
    {
        ArrayList<IRI> listIRIFrequentConcepts = new ArrayList<IRI>(Global.allIRIFrequentConceptsStratified);
        ArrayList<IRI> listIRIFrequentRoles = new ArrayList<IRI>(Global.allIRIFrequentRolesStratified);
        
        Pattern patternOut = null;
        int countAdjust = 0;
        
        while(countAdjust < Global.MAX_LENGTH_PATTERN)
        {
            patternOut = new Pattern(pattern.getHeadAtom());
            int patternOutLength = Global.myRandom.generateInt(2, Global.MAX_LENGTH_PATTERN);
        
            boolean breakDoWhile = false;
            
            for (int i = 2; i <= patternOutLength; i++)
            {
                int count = 0;
                        
                do
                {
                    breakDoWhile = false;

                    switch(Global.myRandom.generateInt(1, 2))
                    {
                        case 1:      
                            int iConceptLocation = Global.myRandom.generateInt(1, listIRIFrequentConcepts.size());
                            ConceptAtom conceptAtom = new ConceptAtom(listIRIFrequentConcepts.get(iConceptLocation - 1));

                            if (patternOut.addAtom(conceptAtom))                                
                                breakDoWhile = true;                                
                            else
                                count++;

                            break;

                        case 2: 
                            int iRoleLocation = Global.myRandom.generateInt(1, listIRIFrequentRoles.size());
                            RoleAtom roleAtom = new RoleAtom(listIRIFrequentRoles.get(iRoleLocation - 1));

                            if (patternOut.addAtom(roleAtom))                                
                                breakDoWhile = true;                                
                            else
                                count++;

                            break;
                    }
                }
                while (!breakDoWhile && (count < Global.MAX_LENGTH_PATTERN * 2));
                
                if (!breakDoWhile && (count >= Global.MAX_LOOP_FOR_CHECK * 2))                        
                    break;
            }
            
            if (breakDoWhile)
                if (!patternOut.adjustPattern() && (countAdjust < Global.MAX_LENGTH_PATTERN)) countAdjust++;
                else break;
            else
                if (countAdjust >= Global.MAX_LENGTH_PATTERN) break;
                else countAdjust++;  
        }
        
        if (countAdjust >= Global.MAX_LENGTH_PATTERN)
            return new Pattern();
        else
            return patternOut;
    }
}
