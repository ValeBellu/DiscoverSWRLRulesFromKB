/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MPattern.Computation;

import MAtom.Atom;
import MAtom.ConceptAtom;
import MAtom.RoleAtom;
import MCommon.Global;
import MDataProcessing.Assertion.ConceptAssertion;
import MDataProcessing.Assertion.RoleAssertion;
import MDataProcessing.ConceptProcessing;
import MDataProcessing.Individual.ConceptIndividuals;
import MDataProcessing.Individual.RoleIndividual;
import MDataProcessing.Individual.RoleIndividuals;
import MDataProcessing.Individual.VariableIndividuals;
import MDataProcessing.RoleProcessing;
import MPattern.Pattern;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 *
 * Class used to manage the metrics of a pattern.
 */
public class PatternComputation 
{
    private Pattern pattern;
    
    private ArrayList<VariableIndividuals> computationSupport;
    private Set<String> supportExtensionConcept;
    private Set<RoleIndividual> supportExtensionRole;
    
    private int iSupport;
    private double dblHeadCoverage;
    private double dblConfidence;
    private double dblPCAConfidence;
    
    
    private ArrayList<VariableIndividuals> computationSupportNew;
    private Set<String> supportExtensionConceptNew;
    private Set<RoleIndividual> supportExtensionRoleNew;
    
    private int iSupportNew;
    private double dblHeadCoverageNew;
    private double dblConfidenceNew;
    private double dblPCAConfidenceNew;
    
    /**
     * Definition of the metrics of the pattern.
     * @param pattern
     */
    public PatternComputation(Pattern pattern)
    {
        this.pattern = pattern;
        this.computationSupport = new ArrayList<VariableIndividuals>();
        this.supportExtensionConcept = new LinkedHashSet<String>();
        this.supportExtensionRole = new LinkedHashSet<RoleIndividual>();
        
        this.iSupport = 0;
        this.dblHeadCoverage = 0.0;
        this.dblConfidence = 0.0;
        this.dblPCAConfidence = 0.0;
        
        this.computationSupportNew = new ArrayList<VariableIndividuals>();
        this.supportExtensionConceptNew = new LinkedHashSet<String>();
        this.supportExtensionRoleNew = new LinkedHashSet<RoleIndividual>();
        
        this.iSupportNew = 0;
        this.dblHeadCoverageNew = 0.0;
        this.dblConfidenceNew = 0.0;
        this.dblPCAConfidenceNew = 0.0;
        
        run();
    }
    
    /**
     * 
     * @return the support extension concept.
     */
    public Set<String> getSupportExtensionConcept()
    {
        return this.supportExtensionConcept;
    }
    
    /**
     * 
     * @return the support extension role.
     */
    public Set<RoleIndividual> getSupportExtensionRole()
    {
        return this.supportExtensionRole;
    }
    
    /**
     * 
     * @return the head coverage.
     */
    public double getHeadCoverage()
    {
        return this.dblHeadCoverage;
    }
    
    /**
     * 
     * @return the confidence.
     */
    public double getConfidence()
    {
        return this.dblConfidence;
    }
    
    /**
     * 
     * @return the PCA confidence.
     */
    public double getPCAConfidence()
    {
        return this.dblPCAConfidence;
    }
    
    /**
     * Get the fitness value of the pattern.
     * @return the sum between the value of the head coverage and the value of the PCA confidence.
     */
    public double getFitnessValue()
    {
        return this.dblHeadCoverage + this.dblPCAConfidence;
    }
    
    
    public Set<String> getSupportExtensionConceptNew()
    {
        return this.supportExtensionConceptNew;
    }
    
    public Set<RoleIndividual> getSupportExtensionRoleNew()
    {
        return this.supportExtensionRoleNew;
    }
    
    public double getHeadCoverageNew()
    {
        return this.dblHeadCoverageNew;
    }
    
    public double getConfidenceNew()
    {
        return this.dblConfidenceNew;
    }
    
    public double getPCAConfidenceNew()
    {
        return this.dblPCAConfidenceNew;
    }
    
    public double getFitnessValueNew()
    {
        return this.dblHeadCoverageNew + this.dblPCAConfidenceNew;
    }
    
    /**
     * Computation of the computation support, support extension, support, head coverage and Confidence and PCA Confidence.
     */
    private void run()
    {
        computeComputationSupport();
        computeSupportExtension();
        computeSupport();
        computeHeadCoverage();
        computeConfidenceAndPCAConfidence();
        
        computationSupport = new ArrayList<VariableIndividuals>();
        computationSupportNew = new ArrayList<VariableIndividuals>();
    }
    
    /**
     * Compute the computation support of the pattern: it contains the variable individuals.
     */
    private void computeComputationSupport()
    {
    	//list of atoms presented in the pattern
        ArrayList<Atom> listAtoms = this.pattern.getListAtoms();
        //for each atom add a variable individuals 
        for(int i = 0; i < listAtoms.size(); i++)
        {
            Atom atom = listAtoms.get(i);
            
            if (atom instanceof ConceptAtom)
            {
                ConceptAssertion conceptAssertion = ConceptProcessing.getConceptAssertionsFromFrequentConcepts(atom.getIRI()); 
                this.computationSupport = addVariableIndividuals(this.computationSupport, conceptAssertion.getIndividuals(), ((ConceptAtom) atom).getVariable());
            }
            else if (atom instanceof RoleAtom)
            {
                RoleAssertion roleAssertion = RoleProcessing.getRoleAssertionsFromFrequentRoles(atom.getIRI());
                this.computationSupport = addVariableIndividuals(this.computationSupport, roleAssertion.getIndividuals(), ((RoleAtom) atom).getDomainVariable(), ((RoleAtom) atom).getRangeVariable());
            }
        }
    }
    
    /**
     * Compute the support extension.
     */
    private void computeSupportExtension()
    {
        Atom headAtom = this.pattern.getHeadAtom();
        
        if (headAtom instanceof ConceptAtom)        
            this.supportExtensionConcept = new LinkedHashSet<String>(getIndividualsFromComputationSupport(this.computationSupport, ((ConceptAtom) headAtom).getVariable()));
        else if (headAtom instanceof RoleAtom)
        {
            ArrayList<String> domainIndividuals = getIndividualsFromComputationSupport(this.computationSupport, ((RoleAtom) headAtom).getDomainVariable());
            ArrayList<String> rangeIndividuals = getIndividualsFromComputationSupport(this.computationSupport, ((RoleAtom) headAtom).getRangeVariable());
            
            for(int i = 0; i < domainIndividuals.size(); i++)
                this.supportExtensionRole.add(new RoleIndividual(domainIndividuals.get(i), rangeIndividuals.get(i)));
        }
    }
    
    /**
     * Compute the support: the number of distinct bindings of the variables in the head atom.
     */
    private void computeSupport()
    {
    
        if (this.pattern.getHeadAtom() instanceof ConceptAtom)        
            this.iSupport = this.supportExtensionConcept.size();
        else if (this.pattern.getHeadAtom() instanceof RoleAtom)
            this.iSupport = this.supportExtensionRole.size();
    }
    
    /**
     * Compute the head coverage: it is the ratio between the support and the distinct variable bindings from the head of the rule.
     */
    private void computeHeadCoverage()
    {
        Atom headAtom = this.pattern.getHeadAtom();
        
        int sizeOfHeadAtom = -1;
        
        if (headAtom instanceof ConceptAtom)
            sizeOfHeadAtom = ConceptProcessing.getConceptAssertionsFromFrequentConcepts(headAtom.getIRI()).getIndividuals().size();
        else if (headAtom instanceof RoleAtom)
            sizeOfHeadAtom = RoleProcessing.getRoleAssertionsFromFrequentRoles(headAtom.getIRI()).getIndividuals().getIndividuals().size();
        
        if (sizeOfHeadAtom == 0)
            this.dblHeadCoverage = 0;
        else
            this.dblHeadCoverage = (double) this.iSupport / sizeOfHeadAtom;   

    }
    
    /**
     * Compute the confidence: it is the ratio between the support of the rule and the number of bindings in the rule body.
     * Compute also the PCA Confidence: if the head is a concept atom, then compute the confidence; if it is a role atom do the ratio between the support and the the set of distinct bindings of the variables occurring in the head 
     * of the role, provided that the body and the domain variable in the head along with a new variable are satisfied.
     */
    private void computeConfidenceAndPCAConfidence()
    {
        ArrayList<VariableIndividuals> conficenceComputationSupport = new ArrayList<VariableIndividuals>();        
        ArrayList<Atom> listBodyAtoms = this.pattern.getBodyAtoms();
        
        for(int i = 0; i < listBodyAtoms.size(); i++)
        {
            Atom atom = listBodyAtoms.get(i);
            
            if (atom instanceof ConceptAtom)
            {
                ConceptAssertion conceptAssertion = ConceptProcessing.getConceptAssertionsFromFrequentConcepts(atom.getIRI());                
                conficenceComputationSupport = addVariableIndividuals(conficenceComputationSupport, conceptAssertion.getIndividuals(), ((ConceptAtom) atom).getVariable());
            }
            else if (atom instanceof RoleAtom)
            {
                RoleAssertion roleAssertion = RoleProcessing.getRoleAssertionsFromFrequentRoles(atom.getIRI());
                conficenceComputationSupport = addVariableIndividuals(conficenceComputationSupport, roleAssertion.getIndividuals(), ((RoleAtom) atom).getDomainVariable(), ((RoleAtom) atom).getRangeVariable());
            }
        }
        
        Set<String> supportConfidenceExtensionConcept = new LinkedHashSet<String>();
        Set<RoleIndividual> supportConfidenceExtensionRole = new LinkedHashSet<RoleIndividual>();
        Set<RoleIndividual> supportPCAConfidenceExtensionRole = new LinkedHashSet<RoleIndividual>();
        
        Atom headAtom = this.pattern.getHeadAtom();
        
        if (headAtom instanceof ConceptAtom)
            supportConfidenceExtensionConcept = new LinkedHashSet<String>(getIndividualsFromComputationSupport(conficenceComputationSupport, ((ConceptAtom) headAtom).getVariable()));
        else if (headAtom instanceof RoleAtom)
        {
            ArrayList<String> domainIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getDomainVariable());
            ArrayList<String> rangeIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getRangeVariable());
            
            for(int i = 0; i < domainIndividuals.size(); i++)
                supportConfidenceExtensionRole.add(new RoleIndividual(domainIndividuals.get(i), rangeIndividuals.get(i)));
            
            RoleAssertion roleAssertion = RoleProcessing.getRoleAssertionsFromFrequentRoles(headAtom.getIRI());
            conficenceComputationSupport = addVariableIndividuals(conficenceComputationSupport, roleAssertion.getIndividuals(), ((RoleAtom) headAtom).getDomainVariable(), Global.variableForPCA);           
            
            ArrayList<String> domainPCAIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getDomainVariable());
            ArrayList<String> rangePCAIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getRangeVariable());
            
            for(int i = 0; i < domainPCAIndividuals.size(); i++)
                supportPCAConfidenceExtensionRole.add(new RoleIndividual(domainPCAIndividuals.get(i), rangePCAIndividuals.get(i)));
        }
        
        int iDenominatorOfConfidence = -1;
        
        if (this.pattern.getHeadAtom() instanceof ConceptAtom)        
            iDenominatorOfConfidence = supportConfidenceExtensionConcept.size();
        else if (this.pattern.getHeadAtom() instanceof RoleAtom)
            iDenominatorOfConfidence = supportConfidenceExtensionRole.size();
        
        if (iDenominatorOfConfidence == 0)
            this.dblConfidence = 0.0;
        else
            this.dblConfidence = (double) this.iSupport / iDenominatorOfConfidence;
        
        int iDenominatorOfPCAConfidence = -1;
        
        if (this.pattern.getHeadAtom() instanceof ConceptAtom)        
            iDenominatorOfPCAConfidence = supportConfidenceExtensionConcept.size(); //Global.allIndividuals.size();
        else if (this.pattern.getHeadAtom() instanceof RoleAtom)
            iDenominatorOfPCAConfidence = supportPCAConfidenceExtensionRole.size();
        
        if (iDenominatorOfPCAConfidence == 0)
            this.dblPCAConfidence = 0.0;
        else        
            this.dblPCAConfidence = (double) this.iSupport / iDenominatorOfPCAConfidence;
    }
    
    /**
     * This method returns the list of individuals of the inputComputationSupport, if it is equal to the name of the variable in input. Otherwise it returns a new array list.
     * @param inputComputationSupport
     * @param strVariable
     */
    private ArrayList<String> getIndividualsFromComputationSupport(ArrayList<VariableIndividuals> inputComputationSupport, String strVariable)
    {
        for(int i = 0; i < inputComputationSupport.size(); i++)
            if (inputComputationSupport.get(i).getNameVariable().equals(strVariable))
                return inputComputationSupport.get(i).getIndividuals();
        
        return new ArrayList<String>();
    }
    
    /**
     * This method is called if the atom is a concept atom.
     * @param oldListVariablesIndividuals the list of the variables of the individuals
     * @param conceptIndividuals 
     * @param strVariable the variable of the atom
     * @return the list of variable individuals
     */
    private ArrayList<VariableIndividuals> addVariableIndividuals(ArrayList<VariableIndividuals> oldListVariablesIndividuals,
                                                                  ConceptIndividuals conceptIndividuals, 
                                                                  String strVariable)
    {
        ArrayList<VariableIndividuals> listVariablesIndividuals = new ArrayList<VariableIndividuals>();
        
        //the list of variable is empty
        if (oldListVariablesIndividuals.isEmpty())
        {
        	//Creation of a new variable for the individuals in input, using the name in input
        	//and add it to the new list of variables
            VariableIndividuals newVariableIndividuals = new VariableIndividuals(strVariable, new ArrayList<String>(conceptIndividuals.getIndividualsName()));
            listVariablesIndividuals.add(newVariableIndividuals);
            return listVariablesIndividuals;
        }
        else //the list of variables is not empty
        {
            for (int i = 0; i < oldListVariablesIndividuals.size(); i++)
            {
            	//for each variable, create a new variable with the same name and add it to the new list
                VariableIndividuals newVariableIndividuals = new VariableIndividuals(oldListVariablesIndividuals.get(i).getNameVariable());
                listVariablesIndividuals.add(newVariableIndividuals);
            }
            
            int iLocationVariableConcept = checkVariableInListVariableIndividuals(oldListVariablesIndividuals, strVariable);
            
            //the variable was already present in the list of variables
            if (iLocationVariableConcept >= 0)
            {
            	//get all the individuals with this variable
                ArrayList<String> individuals = oldListVariablesIndividuals.get(iLocationVariableConcept).getIndividuals();
                
                for(String newIndividual : conceptIndividuals.getIndividualsName())
                    for(int i = 0; i < individuals.size(); i++)
                        if (individuals.get(i).equals(newIndividual))                        
                            for (int j = 0; j < listVariablesIndividuals.size(); j++)                            
                                listVariablesIndividuals.get(j).addIndividual(oldListVariablesIndividuals.get(j).getIndividual(i));
                
            }
            else
            {//the variable wasn't present in the list of variables
                VariableIndividuals newVariableIndividuals = new VariableIndividuals(strVariable);
                listVariablesIndividuals.add(newVariableIndividuals);
                
                ArrayList<String> individuals = oldListVariablesIndividuals.get(0).getIndividuals();
                
                for(String newIndividual : conceptIndividuals.getIndividualsName())                
                    for(int i = 0; i < individuals.size(); i++)
                    {                    
                        for(int j = 0; j < oldListVariablesIndividuals.size(); j++)                        
                            listVariablesIndividuals.get(j).addIndividual(oldListVariablesIndividuals.get(j).getIndividual(i));
                        listVariablesIndividuals.get(listVariablesIndividuals.size() - 1).addIndividual(newIndividual);
                    }
            }
            
            return listVariablesIndividuals;
        }
    }
    
    /**
     * This method is created if the atom is a role atom.
     * @param oldListVariablesIndividuals
     * @param roleIndividuals
     * @param strDomainVariable
     * @param strRangeVariable
     * @return the list of variable individuals.
     */
    private ArrayList<VariableIndividuals> addVariableIndividuals(ArrayList<VariableIndividuals> oldListVariablesIndividuals,
                                                                  RoleIndividuals roleIndividuals, 
                                                                  String strDomainVariable, String strRangeVariable)
    {
        ArrayList<VariableIndividuals> listVariablesIndividuals = new ArrayList<VariableIndividuals>();
        if (oldListVariablesIndividuals.isEmpty())
        {
        	
            VariableIndividuals newDomainVariableIndividuals = new VariableIndividuals(strDomainVariable, roleIndividuals.getDomainIndividuals());
            VariableIndividuals newRangeVariableIndividuals = new VariableIndividuals(strRangeVariable, roleIndividuals.getRangeIndividuals());
            listVariablesIndividuals.add(newDomainVariableIndividuals);
            listVariablesIndividuals.add(newRangeVariableIndividuals);
        }
        else
        {
            for (int i = 0; i < oldListVariablesIndividuals.size(); i++)
            {
                VariableIndividuals newVariableIndividuals = new VariableIndividuals(oldListVariablesIndividuals.get(i).getNameVariable());
                listVariablesIndividuals.add(newVariableIndividuals);
            }
            
            int iLocationDomainVariableConcept = checkVariableInListVariableIndividuals(oldListVariablesIndividuals, strDomainVariable);
            int iLocationRangeVariableConcept = checkVariableInListVariableIndividuals(oldListVariablesIndividuals, strRangeVariable);
            
            if ((iLocationDomainVariableConcept < 0) && (iLocationRangeVariableConcept < 0))
            {
                VariableIndividuals newDomainVariableIndividuals = new VariableIndividuals(strDomainVariable);
                VariableIndividuals newRangeVariableIndividuals = new VariableIndividuals(strRangeVariable);
                listVariablesIndividuals.add(newDomainVariableIndividuals);
                listVariablesIndividuals.add(newRangeVariableIndividuals);
                
                ArrayList<String> individuals = oldListVariablesIndividuals.get(0).getIndividuals();
                
                for(int i = 0; i < roleIndividuals.getDomainIndividuals().size(); i++)
                {
                    String newDomainIndividual = roleIndividuals.getDomainIndividuals().get(i);
                    String newRangeIndividual = roleIndividuals.getRangeIndividuals().get(i);
                    
                    for(int j = 0; j < individuals.size(); j++)
                    {
                        for(int k = 0; k < oldListVariablesIndividuals.size(); k++)                        
                            listVariablesIndividuals.get(k).addIndividual(oldListVariablesIndividuals.get(k).getIndividual(j));
                        listVariablesIndividuals.get(listVariablesIndividuals.size() - 2).addIndividual(newDomainIndividual);
                        listVariablesIndividuals.get(listVariablesIndividuals.size() - 1).addIndividual(newRangeIndividual);
                    }
                    
                }
            }
            else if ((iLocationDomainVariableConcept >= 0) && (iLocationRangeVariableConcept < 0))
            {
                VariableIndividuals newRangeVariableIndividuals = new VariableIndividuals(strRangeVariable);
                listVariablesIndividuals.add(newRangeVariableIndividuals);                
            
                ArrayList<String> domainIndividuals = oldListVariablesIndividuals.get(iLocationDomainVariableConcept).getIndividuals();
                
                for(int i = 0; i < roleIndividuals.getDomainIndividuals().size(); i++)
                {
                    String newDomainIndividual = roleIndividuals.getDomainIndividuals().get(i);
                    String newRangeIndividual = roleIndividuals.getRangeIndividuals().get(i);
                    
                    for(int j = 0; j < domainIndividuals.size(); j++)                    
                        if (domainIndividuals.get(j).equals(newDomainIndividual))
                        {
                            for(int k = 0; k < oldListVariablesIndividuals.size(); k++)                            
                                listVariablesIndividuals.get(k).addIndividual(oldListVariablesIndividuals.get(k).getIndividual(j));                        
                            listVariablesIndividuals.get(listVariablesIndividuals.size() - 1).addIndividual(newRangeIndividual);
                        }
                }
            }
            else if ((iLocationDomainVariableConcept < 0) && (iLocationRangeVariableConcept >= 0))
            {
                VariableIndividuals newDomainVariableIndividuals = new VariableIndividuals(strDomainVariable);
                listVariablesIndividuals.add(newDomainVariableIndividuals);                
            
                ArrayList<String> rangeIndividuals = oldListVariablesIndividuals.get(iLocationRangeVariableConcept).getIndividuals();
                
                for(int i = 0; i < roleIndividuals.getRangeIndividuals().size(); i++)
                {
                    String newDomainIndividual = roleIndividuals.getDomainIndividuals().get(i);
                    String newRangeIndividual = roleIndividuals.getRangeIndividuals().get(i);
                    
                    for(int j = 0; j < rangeIndividuals.size(); j++)                    
                        if (rangeIndividuals.get(j).equals(newRangeIndividual))
                        {
                            for(int k = 0; k < oldListVariablesIndividuals.size(); k++)                            
                                listVariablesIndividuals.get(k).addIndividual(oldListVariablesIndividuals.get(k).getIndividual(j));                        
                            listVariablesIndividuals.get(listVariablesIndividuals.size() - 1).addIndividual(newDomainIndividual);
                        }
                }
            }
            else if ((iLocationDomainVariableConcept >= 0) && (iLocationRangeVariableConcept >= 0))
            {
                ArrayList<String> domainIndividuals = oldListVariablesIndividuals.get(iLocationDomainVariableConcept).getIndividuals();
                ArrayList<String> rangeIndividuals = oldListVariablesIndividuals.get(iLocationRangeVariableConcept).getIndividuals();
                
                for(int i = 0; i < roleIndividuals.getDomainIndividuals().size(); i++)
                {
                    String newDomainIndividual = roleIndividuals.getDomainIndividuals().get(i);
                    String newRangeIndividual = roleIndividuals.getRangeIndividuals().get(i);
                    
                    for(int j = 0; j < domainIndividuals.size(); j++)                    
                        if ((domainIndividuals.get(j).equals(newDomainIndividual)) && (rangeIndividuals.get(j).equals(newRangeIndividual)))                        
                            for(int k = 0; k < oldListVariablesIndividuals.size(); k++)                            
                                listVariablesIndividuals.get(k).addIndividual(oldListVariablesIndividuals.get(k).getIndividual(j));                        
                }
            }
        }
        
        return listVariablesIndividuals;
    }
    
    
    
    
    private void computeComputationSupportNew()
    {
        ArrayList<Atom> listAtoms = this.pattern.getListAtoms();
        
        ArrayList<Atom> tempRule = new ArrayList<Atom>();
        
        for(int i = 0; i < listAtoms.size(); i++)
        {
            Atom atom = listAtoms.get(i);
            
            if (atom instanceof ConceptAtom)
            {
                ConceptAssertion conceptAssertion = ConceptProcessing.getConceptAssertionsFromFrequentConcepts(atom.getIRI());                
                this.computationSupportNew = addVariableIndividualsNew(tempRule, this.computationSupportNew, conceptAssertion, ((ConceptAtom) atom).getVariable());
                tempRule.add(atom);
            }
            else if (atom instanceof RoleAtom)
            {
                RoleAssertion roleAssertion = RoleProcessing.getRoleAssertionsFromFrequentRoles(atom.getIRI());
                this.computationSupportNew = addVariableIndividualsNew(tempRule, this.computationSupportNew, roleAssertion, ((RoleAtom) atom).getDomainVariable(), ((RoleAtom) atom).getRangeVariable());
                tempRule.add(atom);
            }
        }
        
        System.out.println("OK");
    }
    
    private void computeSupportExtensionNew()
    {
        Atom headAtom = this.pattern.getHeadAtom();
        
        if (headAtom instanceof ConceptAtom)        
            this.supportExtensionConceptNew = new LinkedHashSet<String>(getIndividualsFromComputationSupport(this.computationSupportNew, ((ConceptAtom) headAtom).getVariable()));
        else if (headAtom instanceof RoleAtom)
        {
            ArrayList<String> domainIndividuals = getIndividualsFromComputationSupport(this.computationSupportNew, ((RoleAtom) headAtom).getDomainVariable());
            ArrayList<String> rangeIndividuals = getIndividualsFromComputationSupport(this.computationSupportNew, ((RoleAtom) headAtom).getRangeVariable());
            
            for(int i = 0; i < domainIndividuals.size(); i++)
                this.supportExtensionRoleNew.add(new RoleIndividual(domainIndividuals.get(i), rangeIndividuals.get(i)));
        }
    }
    
    private void computeSupportNew()
    {
        if (this.pattern.getHeadAtom() instanceof ConceptAtom)        
            this.iSupportNew = this.supportExtensionConceptNew.size();
        else if (this.pattern.getHeadAtom() instanceof RoleAtom)
            this.iSupportNew = this.supportExtensionRoleNew.size();
    }
    
    private void computeHeadCoverageNew()
    {
        Atom headAtom = this.pattern.getHeadAtom();
        
        int sizeOfHeadAtom = -1;
        
        if (headAtom instanceof ConceptAtom)
            sizeOfHeadAtom = ConceptProcessing.getConceptAssertionsFromFrequentConcepts(headAtom.getIRI()).getIndividuals().getIndividualsName().size();
        else if (headAtom instanceof RoleAtom)
            sizeOfHeadAtom = RoleProcessing.getRoleAssertionsFromFrequentRoles(headAtom.getIRI()).getIndividuals().getIndividuals().size();
        
        if (sizeOfHeadAtom == 0)
            this.dblHeadCoverageNew = 0;
        else
            this.dblHeadCoverageNew = (double) this.iSupportNew / sizeOfHeadAtom;        

    }
    
    private void computeConfidenceAndPCAConfidenceNew()
    {
        ArrayList<VariableIndividuals> conficenceComputationSupport = new ArrayList<VariableIndividuals>();        
        ArrayList<Atom> listBodyAtoms = this.pattern.getBodyAtoms();
        ArrayList<Atom> tempRule = new ArrayList<Atom>();
        
        for(int i = 0; i < listBodyAtoms.size(); i++)
        {
            Atom atom = listBodyAtoms.get(i);
            
            if (atom instanceof ConceptAtom)
            {
                ConceptAssertion conceptAssertion = ConceptProcessing.getConceptAssertionsFromFrequentConcepts(atom.getIRI());                
                conficenceComputationSupport = addVariableIndividualsNew(tempRule, conficenceComputationSupport, conceptAssertion, ((ConceptAtom) atom).getVariable());
                tempRule.add(atom);
            }
            else if (atom instanceof RoleAtom)
            {
                RoleAssertion roleAssertion = RoleProcessing.getRoleAssertionsFromFrequentRoles(atom.getIRI());
                conficenceComputationSupport = addVariableIndividualsNew(tempRule, conficenceComputationSupport, roleAssertion, ((RoleAtom) atom).getDomainVariable(), ((RoleAtom) atom).getRangeVariable());
                tempRule.add(atom);
            }
        }
        
        Set<String> supportConfidenceExtensionConcept = new LinkedHashSet<String>();
        Set<RoleIndividual> supportConfidenceExtensionRole = new LinkedHashSet<RoleIndividual>();
        
        Set<RoleIndividual> supportPCAConfidenceExtensionRole = new LinkedHashSet<RoleIndividual>();
        
        Atom headAtom = this.pattern.getHeadAtom();
        
        if (headAtom instanceof ConceptAtom)
            supportConfidenceExtensionConcept = new LinkedHashSet<String>(getIndividualsFromComputationSupport(conficenceComputationSupport, ((ConceptAtom) headAtom).getVariable()));
        else if (headAtom instanceof RoleAtom)
        {
            ArrayList<String> domainIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getDomainVariable());
            ArrayList<String> rangeIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getRangeVariable());
            
            for(int i = 0; i < domainIndividuals.size(); i++)
            {
                if (rangeIndividuals.isEmpty())
                    System.out.println("LOIR");
                if (domainIndividuals.isEmpty())
                    System.out.println("LOID");
                
                supportConfidenceExtensionRole.add(new RoleIndividual(domainIndividuals.get(i), rangeIndividuals.get(i)));
            }
            
            RoleAssertion roleAssertion = RoleProcessing.getRoleAssertionsFromFrequentRoles(headAtom.getIRI());
            conficenceComputationSupport = addVariableIndividualsNew(tempRule, conficenceComputationSupport, roleAssertion, ((RoleAtom) headAtom).getDomainVariable(), Global.variableForPCA);           
            
            ArrayList<String> domainPCAIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getDomainVariable());
            ArrayList<String> rangePCAIndividuals = getIndividualsFromComputationSupport(conficenceComputationSupport, ((RoleAtom) headAtom).getRangeVariable());
            
            for(int i = 0; i < domainPCAIndividuals.size(); i++)
                supportPCAConfidenceExtensionRole.add(new RoleIndividual(domainPCAIndividuals.get(i), rangePCAIndividuals.get(i)));
        }
        
        int iDenominatorOfConfidence = -1;
        
        if (this.pattern.getHeadAtom() instanceof ConceptAtom)        
            iDenominatorOfConfidence = supportConfidenceExtensionConcept.size();
        else if (this.pattern.getHeadAtom() instanceof RoleAtom)
            iDenominatorOfConfidence = supportConfidenceExtensionRole.size();
        
        if (iDenominatorOfConfidence == 0)
            this.dblConfidenceNew = 0.0;
        else
            this.dblConfidenceNew = (double) this.iSupportNew / iDenominatorOfConfidence;
        
        int iDenominatorOfPCAConfidence = -1;
        
        if (this.pattern.getHeadAtom() instanceof ConceptAtom)        
            iDenominatorOfPCAConfidence = Global.allIndividuals.size();
        else if (this.pattern.getHeadAtom() instanceof RoleAtom)
            iDenominatorOfPCAConfidence = supportPCAConfidenceExtensionRole.size();
        
        if (iDenominatorOfPCAConfidence == 0)
            this.dblPCAConfidenceNew = 0.0;
        else        
            this.dblPCAConfidenceNew = (double) this.iSupportNew / iDenominatorOfPCAConfidence;
    }
    
   
    private ArrayList<VariableIndividuals> addVariableIndividualsNew(ArrayList<Atom> listAtoms,
                                                                  ArrayList<VariableIndividuals> oldListVariablesIndividuals,
                                                                  ConceptAssertion conceptAssertion, 
                                                                  String strVariable)
    {
        if (oldListVariablesIndividuals.isEmpty())
        {
            ArrayList<VariableIndividuals> listVariablesIndividuals = new ArrayList<VariableIndividuals>();
            
            VariableIndividuals newVariableIndividuals = new VariableIndividuals(strVariable, new ArrayList<String>(conceptAssertion.getIndividuals().getIndividualsName()));
            listVariablesIndividuals.add(newVariableIndividuals);
            return listVariablesIndividuals;
        }
        else
        {   
            Queue openQueueVariable = new LinkedList(); 
            Set<String> variablesAreConsidered =  new LinkedHashSet<String>();
            ArrayList<Atom> tempPattern = new ArrayList<Atom>();
            
            ArrayList<VariableIndividuals> listVariablesIndividuals = new ArrayList<VariableIndividuals>(oldListVariablesIndividuals);            
            VariableIndividuals newVariableIndividuals = new VariableIndividuals(strVariable, new ArrayList<String>(conceptAssertion.getIndividuals().getIndividualsName()));
            
            if (retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, newVariableIndividuals) > -1)
            {
                openQueueVariable.add(strVariable);
                
                while (!openQueueVariable.isEmpty())
                {
                    String mainVariable = (String) openQueueVariable.poll();
                    
                    for (int i = 0; i < listAtoms.size(); i++)                    
                        if (((Atom) listAtoms.get(i)) instanceof RoleAtom) 
                        {
                            RoleAtom tempRoleAtom = (RoleAtom) listAtoms.get(i);
                            
                            if  (tempRoleAtom.getDomainVariable().equals(mainVariable))
                            {
                                if (!variablesAreConsidered.contains(tempRoleAtom.getRangeVariable()))
                                {
                                    int iDomainLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, mainVariable);
                                    int iRangeLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, tempRoleAtom.getRangeVariable());
                                    
                                    VariableIndividuals newDomainVariableIndividuals = listVariablesIndividuals.get(iDomainLocation);
                                    VariableIndividuals newRangeVariableIndividuals = getNewVariableIndividualsFromRoleAtom(tempRoleAtom, newDomainVariableIndividuals);
                                    
                                    if (iRangeLocation >= 0)
                                        retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, newRangeVariableIndividuals);
                                    else
                                        listVariablesIndividuals.add(newRangeVariableIndividuals);
                                    
                                    openQueueVariable.add(tempRoleAtom.getRangeVariable());
                                    variablesAreConsidered.add(tempRoleAtom.getRangeVariable());
                                } 
                            }
                            else if (tempRoleAtom.getRangeVariable().equals(mainVariable)) 
                            {
                                if (!variablesAreConsidered.contains(tempRoleAtom.getDomainVariable()))
                                {
                                    int iDomainLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, tempRoleAtom.getDomainVariable());
                                    int iRangeLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, mainVariable);
                                    
                                    VariableIndividuals newRangeVariableIndividuals = listVariablesIndividuals.get(iRangeLocation);
                                    VariableIndividuals newDomainVariableIndividuals = getNewVariableIndividualsFromRoleAtom(tempRoleAtom, newRangeVariableIndividuals);
                                    
                                    if (iDomainLocation >= 0)
                                        retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, newDomainVariableIndividuals);
                                    else
                                        listVariablesIndividuals.add(newDomainVariableIndividuals);
                                    
                                    openQueueVariable.add(tempRoleAtom.getDomainVariable());
                                    variablesAreConsidered.add(tempRoleAtom.getDomainVariable());
                                }
                            }
                        }
                }
            }
        
            return listVariablesIndividuals;
        }
    
    }
    
    private ArrayList<VariableIndividuals> addVariableIndividualsNew(ArrayList<Atom> listAtoms,            
                                                                  ArrayList<VariableIndividuals> oldListVariablesIndividuals,
                                                                  RoleAssertion roleAssertion, 
                                                                  String strDomainVariable, String strRangeVariable)
    {
        if (oldListVariablesIndividuals.isEmpty())
        {
            ArrayList<VariableIndividuals> listVariablesIndividuals = new ArrayList<VariableIndividuals>();
            
            VariableIndividuals newDomainVariableIndividuals = new VariableIndividuals(strDomainVariable, roleAssertion.getIndividuals().getDomainIndividuals());
            VariableIndividuals newRangeVariableIndividuals = new VariableIndividuals(strRangeVariable, roleAssertion.getIndividuals().getRangeIndividuals());
            listVariablesIndividuals.add(newDomainVariableIndividuals);
            listVariablesIndividuals.add(newRangeVariableIndividuals);
            
            return listVariablesIndividuals;
        }
        else
        {
            Queue openQueueVariable = new LinkedList(); 
            Set<String> variablesAreConsidered =  new LinkedHashSet<String>();
            ArrayList<Atom> tempPattern = new ArrayList<Atom>();
            
            ArrayList<VariableIndividuals> listVariablesIndividuals = new ArrayList<VariableIndividuals>(oldListVariablesIndividuals);            
            VariableIndividuals domainVariableIndividuals = new VariableIndividuals(strDomainVariable, new ArrayList<String>(roleAssertion.getIndividuals().getDomainIndividuals()));
            VariableIndividuals rangeVariableIndividuals = new VariableIndividuals(strRangeVariable, new ArrayList<String>(roleAssertion.getIndividuals().getRangeIndividuals()));
            
            int iLocationDomainVariableIndividuals = retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, domainVariableIndividuals);
            int iLocationRangeVariableIndividuals = retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, rangeVariableIndividuals);

            if ((iLocationDomainVariableIndividuals >= 0) && (iLocationRangeVariableIndividuals < 0))
            {
                VariableIndividuals localDomainVariableIndividuals = listVariablesIndividuals.get(iLocationDomainVariableIndividuals);                
                VariableIndividuals localRangeVariableIndividuals = getNewVariableIndividualsFromRoleAtom(new RoleAtom(roleAssertion.getIRIRole(), strDomainVariable, strRangeVariable), localDomainVariableIndividuals);
                listVariablesIndividuals.add(localRangeVariableIndividuals);
            }
            else if ((iLocationDomainVariableIndividuals < 0) && (iLocationRangeVariableIndividuals >= 0))
            {
                VariableIndividuals localRangeVariableIndividuals = listVariablesIndividuals.get(iLocationRangeVariableIndividuals);                
                VariableIndividuals localDomainVariableIndividuals = getNewVariableIndividualsFromRoleAtom(new RoleAtom(roleAssertion.getIRIRole(), strDomainVariable, strRangeVariable), localRangeVariableIndividuals);
                listVariablesIndividuals.add(localDomainVariableIndividuals);
            }
            else if ((iLocationDomainVariableIndividuals >= 0) && (iLocationRangeVariableIndividuals >= 0))
            {
                VariableIndividuals localDomainVariableIndividuals = listVariablesIndividuals.get(iLocationDomainVariableIndividuals);                
                VariableIndividuals localRangeVariableIndividuals = getNewVariableIndividualsFromRoleAtom(new RoleAtom(roleAssertion.getIRIRole(), strDomainVariable, strRangeVariable), localDomainVariableIndividuals);
                retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, localRangeVariableIndividuals);
            }
            
            openQueueVariable.add(strDomainVariable);
            openQueueVariable.add(strRangeVariable);
            
            while (!openQueueVariable.isEmpty())
            {
                String mainVariable = (String) openQueueVariable.poll();

                for (int i = 0; i < listAtoms.size(); i++)                    
                    if (((Atom) listAtoms.get(i)) instanceof RoleAtom) 
                    {
                        RoleAtom tempRoleAtom = (RoleAtom) listAtoms.get(i);

                        if  (tempRoleAtom.getDomainVariable().equals(mainVariable))
                        {
                            if (!variablesAreConsidered.contains(tempRoleAtom.getRangeVariable()))
                            {
                                int iDomainLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, mainVariable);
                                int iRangeLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, tempRoleAtom.getRangeVariable());

                                VariableIndividuals newDomainVariableIndividuals = listVariablesIndividuals.get(iDomainLocation);
                                VariableIndividuals newRangeVariableIndividuals = getNewVariableIndividualsFromRoleAtom(tempRoleAtom, newDomainVariableIndividuals);

                                if (iRangeLocation >= 0)
                                    retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, newRangeVariableIndividuals);
                                else
                                    listVariablesIndividuals.add(newRangeVariableIndividuals);

                                openQueueVariable.add(tempRoleAtom.getRangeVariable());
                                variablesAreConsidered.add(tempRoleAtom.getRangeVariable());
                            } 
                        }
                        else if (tempRoleAtom.getRangeVariable().equals(mainVariable)) 
                        {
                            if (!variablesAreConsidered.contains(tempRoleAtom.getDomainVariable()))
                            {
                                int iRangeLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, mainVariable);
                                int iDomainLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, tempRoleAtom.getDomainVariable());                                

                                VariableIndividuals newRangeVariableIndividuals = listVariablesIndividuals.get(iRangeLocation);
                                VariableIndividuals newDomainVariableIndividuals = getNewVariableIndividualsFromRoleAtom(tempRoleAtom, newRangeVariableIndividuals);

                                if (iDomainLocation >= 0)
                                    retainVariableIndividualsInListVariableIndividuals(listVariablesIndividuals, newDomainVariableIndividuals);
                                else
                                    listVariablesIndividuals.add(newDomainVariableIndividuals);

                                openQueueVariable.add(tempRoleAtom.getDomainVariable());
                                variablesAreConsidered.add(tempRoleAtom.getDomainVariable());
                            }
                        }
                    }
            }
            
            return listVariablesIndividuals;
        }
    }
    
    /**
     * Check if the variable in input is already contained in the list of variables individuals and, in this case,return the position.
     * @param listVariablesIndividuals the list of variables of the individuals
     * @param strVariable the name of the variable that has to be checked
     * @return the position of the variable which has the same name of the variable in input
     */
    private int checkVariableInListVariableIndividuals(ArrayList<VariableIndividuals> listVariablesIndividuals, String strVariable)
    {
        if (!listVariablesIndividuals.isEmpty())            
        {
            for(int i = 0; i < listVariablesIndividuals.size(); i++)
            {
                VariableIndividuals variableIndividuals = listVariablesIndividuals.get(i);
                if (variableIndividuals.getNameVariable().equals(strVariable))
                    return i;
            }
            
        }
        
        return -1;
    }
    
    private int retainVariableIndividualsInListVariableIndividuals(ArrayList<VariableIndividuals> listVariablesIndividuals, 
                                                                    VariableIndividuals newVariableIndividuals)
    {
        if (!listVariablesIndividuals.isEmpty())            
        {
            Atom headAtom = this.pattern.getHeadAtom();
            
            if (headAtom instanceof ConceptAtom)
            {        
                int iLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, newVariableIndividuals.getNameVariable());
                
                if (iLocation >= 0)
                {
                    ArrayList<String> individuals = new ArrayList<String>(newVariableIndividuals.getIndividuals());
                    individuals.retainAll(listVariablesIndividuals.get(iLocation).getIndividuals());

                    listVariablesIndividuals.get(iLocation).change(newVariableIndividuals.getNameVariable(), individuals);
                    
                    return iLocation;
                }
            }
            else if (headAtom instanceof RoleAtom)
            {
                if ( (newVariableIndividuals.getNameVariable().equals(((RoleAtom) headAtom).getDomainVariable())) ||
                     (newVariableIndividuals.getNameVariable().equals(((RoleAtom) headAtom).getRangeVariable())) )
                {
                    int iDomainLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, ((RoleAtom) headAtom).getDomainVariable());
                    int iRangeLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, ((RoleAtom) headAtom).getRangeVariable());
                    
                    if ((iDomainLocation >= 0) && (iRangeLocation >= 0))
                    {
                        ArrayList<String> listDomainIndividuals = listVariablesIndividuals.get(iDomainLocation).getIndividuals();
                        ArrayList<String> listRangeIndividuals = listVariablesIndividuals.get(iRangeLocation).getIndividuals();
                        ArrayList<String> listNewIndividuals = newVariableIndividuals.getIndividuals();
                        
                        ArrayList<String> newlistDomainIndividuals = new ArrayList<String>();
                        ArrayList<String> newlistRangeIndividuals = new ArrayList<String>();
                        
                        if (newVariableIndividuals.getNameVariable().equals(((RoleAtom) headAtom).getDomainVariable()))
                        {
                            for (int i = 0; i < listNewIndividuals.size(); i++)                            
                                for (int j = 0; j < listDomainIndividuals.size(); j++)                                
                                    if (listDomainIndividuals.get(j).equals(listNewIndividuals.get(i)))
                                    {
                                        newlistDomainIndividuals.add(listDomainIndividuals.get(j));
                                        newlistRangeIndividuals.add(listRangeIndividuals.get(j));
                                    }
                            
                            listVariablesIndividuals.get(iDomainLocation).change(((RoleAtom) headAtom).getDomainVariable(), newlistDomainIndividuals);
                            listVariablesIndividuals.get(iRangeLocation).change(((RoleAtom) headAtom).getRangeVariable(), newlistRangeIndividuals);
                            
                            return iDomainLocation;
                        }
                        else if (newVariableIndividuals.getNameVariable().equals(((RoleAtom) headAtom).getRangeVariable()))
                        {
                            for (int i = 0; i < listNewIndividuals.size(); i++)                            
                                for (int j = 0; j < listRangeIndividuals.size(); j++)                                
                                    if (listRangeIndividuals.get(j).equals(listNewIndividuals.get(i)))
                                    {
                                        newlistDomainIndividuals.add(listDomainIndividuals.get(j));
                                        newlistRangeIndividuals.add(listRangeIndividuals.get(j));
                                    }
                            
                            listVariablesIndividuals.get(iDomainLocation).change(((RoleAtom) headAtom).getDomainVariable(), newlistDomainIndividuals);
                            listVariablesIndividuals.get(iRangeLocation).change(((RoleAtom) headAtom).getRangeVariable(), newlistRangeIndividuals);
                            
                            return iRangeLocation;
                        }      
                        
                        return -1;
                    }
                }
                
                int iLocation = checkVariableInListVariableIndividuals(listVariablesIndividuals, newVariableIndividuals.getNameVariable());

                if (iLocation >= 0)
                {
                    ArrayList<String> individuals = new ArrayList<String>(newVariableIndividuals.getIndividuals());
                    individuals.retainAll(listVariablesIndividuals.get(iLocation).getIndividuals());

                    listVariablesIndividuals.get(iLocation).change(newVariableIndividuals.getNameVariable(), individuals);

                    return iLocation;
                }
                
            }
        }
        
        return -1;
    }
    
    private VariableIndividuals getNewVariableIndividualsFromRoleAtom(RoleAtom roleAtom, VariableIndividuals inputVariableIndividuals)
    {
        RoleAssertion roleAssertion = RoleProcessing.getRoleAssertionsFromFrequentRoles(roleAtom.getIRI());
        ArrayList<String> domainIndividuals = roleAssertion.getIndividuals().getDomainIndividuals();
        ArrayList<String> rangeIndividuals = roleAssertion.getIndividuals().getRangeIndividuals();
        
        ArrayList<String> inputIndividuals = inputVariableIndividuals.getIndividuals();
        ArrayList<String> outputIndividuals = new ArrayList<String>();
        
        if (roleAtom.getDomainVariable().equals(inputVariableIndividuals.getNameVariable()))
        {
            for (int i = 0; i < domainIndividuals.size(); i++)            
                if (inputIndividuals.contains(domainIndividuals.get(i)))
                    outputIndividuals.add(rangeIndividuals.get(i));            
            
            return new VariableIndividuals(roleAtom.getRangeVariable(), outputIndividuals);
        }
        else if (roleAtom.getRangeVariable().equals(inputVariableIndividuals.getNameVariable()))
        {
            for (int i = 0; i < rangeIndividuals.size(); i++)            
                if (inputIndividuals.contains(rangeIndividuals.get(i)))
                    outputIndividuals.add(domainIndividuals.get(i));   
            
            return new VariableIndividuals(roleAtom.getDomainVariable(), outputIndividuals);
        }
        
        return null;
    }
}
