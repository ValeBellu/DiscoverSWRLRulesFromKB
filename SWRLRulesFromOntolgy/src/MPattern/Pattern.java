/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MPattern;

import MAtom.Atom;
import MAtom.ConceptAtom;
import MAtom.RoleAtom;
import MCommon.Global;
import MDataProcessing.ConceptProcessing;
import MDataProcessing.RoleProcessing;
import MPattern.Computation.PatternComputation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author tdminh
 */
public class Pattern
{
    private ArrayList<Atom> listAtoms;
    private Atom headAtom;
    private ArrayList<Atom> bodyAtoms;
    
    private ArrayList<ConceptAtom> listConceptsInBody;
    private ArrayList<RoleAtom> listRolesInBody;
    private Set<String> setListVariales;
    private ArrayList<String> freshVariable;
    
    private int countZ;     
    
    private PatternComputation patternComputation;
    
    /**
     * Initialize the pattern.
     */
    public Pattern()
    {
        initialize();
    }    
    
    /**
     * Create a pattern with the atoms of the pattern in input.
     * @param pattern
     */
    public Pattern(Pattern pattern)
    {
        this.listAtoms = new ArrayList<Atom>(pattern.getListAtoms());
        
        if (pattern.getHeadAtom() instanceof ConceptAtom)
            this.headAtom = new ConceptAtom(pattern.getHeadAtom().getIRI(), ((ConceptAtom)pattern.getHeadAtom()).getVariable());
        else if (pattern.getHeadAtom() instanceof RoleAtom)
            this.headAtom = new RoleAtom(pattern.getHeadAtom().getIRI(), ((RoleAtom)pattern.getHeadAtom()).getDomainVariable()
                                                                       , ((RoleAtom)pattern.getHeadAtom()).getRangeVariable());
            
        this.bodyAtoms = new ArrayList<Atom>(pattern.getBodyAtoms());
        
        this.listConceptsInBody = new ArrayList<ConceptAtom>(pattern.getListConceptsInBody());
        this.listRolesInBody = new ArrayList<RoleAtom>(pattern.getListRolesInBody());
        this.setListVariales = new LinkedHashSet<String>(pattern.getListVariables());
        this.freshVariable = new ArrayList<String>(pattern.getFreshVariable());
        
        this.countZ = pattern.getCountZ();
    }
    
    /**
     * Create a pattern with the variables and the IRI of the head atom in input.
     * @param headAtomInput
     */
    public Pattern(Atom headAtomInput)
    {
        this.listAtoms = new ArrayList<Atom>();        
        this.bodyAtoms = new ArrayList<Atom>();
        
        this.listConceptsInBody = new ArrayList<ConceptAtom>();
        this.listRolesInBody = new ArrayList<RoleAtom>();
        this.setListVariales = new LinkedHashSet<String>();
        this.freshVariable = new ArrayList<String>();
        
        this.countZ = 1;        
        
        if (headAtomInput instanceof ConceptAtom)
        {
            ConceptAtom headForListAtoms = new ConceptAtom(headAtomInput.getIRI(), ((ConceptAtom) headAtomInput).getVariable());
            
            this.listAtoms.add(headForListAtoms);
            this.headAtom = new ConceptAtom(headAtomInput.getIRI(), ((ConceptAtom) headAtomInput).getVariable());
            this.setListVariales.add(((ConceptAtom) headAtomInput).getVariable());
        }
        else if (headAtomInput instanceof RoleAtom)
        {
            RoleAtom headForListAtoms = new RoleAtom(headAtomInput.getIRI(), ((RoleAtom) headAtomInput).getDomainVariable()
                                                                           , ((RoleAtom) headAtomInput).getRangeVariable());
            
            this.listAtoms.add(headForListAtoms);
            this.headAtom = new RoleAtom(headAtomInput.getIRI(), ((RoleAtom) headAtomInput).getDomainVariable()
                                                               , ((RoleAtom) headAtomInput).getRangeVariable());
            this.setListVariales.add(((RoleAtom) headAtomInput).getDomainVariable());
            this.setListVariales.add(((RoleAtom) headAtomInput).getRangeVariable());
        }
    }
    
    /**
     * This method creates a pattern with a random length, using the list of frequent atoms.
     * In particular, it adds a new atom in the pattern (a role atom or a concept atom) until the maximum length is reached.
     * @return true if there are at least two atoms in the pattern. 
     */
    public boolean autoCreatePattern()
    {
        initialize();
        
        ArrayList<IRI> listIRIFrequentConcepts = new ArrayList<IRI>(Global.allIRIFrequentConceptsStratified);
        ArrayList<IRI> listIRIFrequentRoles = new ArrayList<IRI>(Global.allIRIFrequentRolesStratified);
        
        //random length of the pattern
        int patternLength = Global.myRandom.generateInt(2, Global.MAX_LENGTH_PATTERN);
        
        int i = 1;
        int count = 0;
        
        while(i <= patternLength)
        {
        	//the pattern could be composed by a role atom or a concept atom
            switch(Global.myRandom.generateInt(1, 2))
            {  //create a concept atom from the list of frequent concepts and add it to the list of atoms
                case 1:                        
                    int iConceptLocation = Global.myRandom.generateInt(1, listIRIFrequentConcepts.size());
                    ConceptAtom conceptAtom = new ConceptAtom(listIRIFrequentConcepts.get(iConceptLocation - 1));

                    if (addAtom(conceptAtom)) i++;                    
                    break;
                //create a role atom from the list of frequent roles and add it to the list of atom
                case 2: 
                	
                    int iRoleLocation = Global.myRandom.generateInt(1, listIRIFrequentRoles.size());
                    RoleAtom roleAtom = new RoleAtom(listIRIFrequentRoles.get(iRoleLocation - 1));
                    
                    if (addAtom(roleAtom)) i++;
                    break;
            }
            
            if (count++ > Global.MAX_LOOP_FOR_CHECK) break;
        }
        
        if ((listAtoms.size() >= 2) && adjustPattern())      
            return true;
        else
            return false;
    }
    
    /**
     * This method adds the atom in input to the list of atoms of the pattern.
     * 
     * @param atom
     * @return true, if the atom is added; false otherwise.
     */
    public boolean addAtom(Atom atom)
    {
    	//the atom is a concept atom
        if (atom instanceof ConceptAtom)
        {
        	//there is no atoms in the list of atoms
            if (this.listAtoms.size() == 0)
                return addConceptAtomWithHeadEmpty((ConceptAtom) atom);
            //there is only one atom in the list
            else if (this.listAtoms.size() == 1)
            {
            	//the head atom is a concept atom
                if (this.headAtom instanceof ConceptAtom)
                    return addConceptAtomWithHeadConceptAtom((ConceptAtom)atom);
                //the head atom is a role atom
                else if (this.headAtom instanceof RoleAtom)
                    return addConceptAtomWithHeadRoleAtom((ConceptAtom)atom);
                else
                    return false;
            }
            //the list of atoms has a size greater than one
            else if(this.listAtoms.size() > 1)
            {
            	//the head atom is a concept atom
                if (this.headAtom instanceof ConceptAtom)
                    return addConceptAtomWithHeadConceptAtomAndBigPatternSize((ConceptAtom)atom);
                //the head atom is a role atom
                else if (this.headAtom instanceof RoleAtom)
                    return addConceptAtomWithHeadRoleAtomAndBigPatternSize((ConceptAtom)atom);
                else
                    return false;
            }
        }
        //the atom to be added is a role atom
        //the others conditions are the same that in the case in which the atom is a concept atom (above)
        else if (atom instanceof RoleAtom)
        {
            if (this.listAtoms.size() == 0)
                return addRoleAtomWithListEmpty((RoleAtom)atom);
            else if (this.listAtoms.size() == 1)
            {
                if (headAtom instanceof ConceptAtom)
                    return addRoleAtomWithHeadConceptAtom((RoleAtom)atom);
                else if (headAtom instanceof RoleAtom)
                    return addRoleAtomWithHeadRoleAtom((RoleAtom)atom);
                else
                    return false;
            }
            else if(this.listAtoms.size() > 1)
            {
                if (headAtom instanceof ConceptAtom)
                    return addRoleAtomWithHeadConceptAtomAndBigPatternSize((RoleAtom)atom);
                else if (headAtom instanceof RoleAtom)
                    return addRoleAtomWithHeadRoleAtomAndBigPatternSize((RoleAtom)atom);
                else
                    return false;
            }
        }
        
        return true;
    }    
    /**
     * Remove the atoms from the pattern and then adjust the variables of the remained atoms in the pattern to ensure the language bias.
     * @param iQuantityOfRemoving the number of atoms to be removed
     * @return true if the pattern is adjusted.
     */
    public boolean removeAtomContainsLastFreshVariable(int iQuantityOfRemoving)
    {
        for (int i = 1; i <= iQuantityOfRemoving; i++)
        {     
            Atom lastAtomInListAtoms = this.listAtoms.get(this.listAtoms.size() - 1);
            
            this.listAtoms.remove(this.listAtoms.size() - 1);
            this.bodyAtoms.remove(this.bodyAtoms.size() - 1);
            
            if (lastAtomInListAtoms instanceof ConceptAtom)
            {
                this.listConceptsInBody.remove(this.listConceptsInBody.size() - 1);
                
                if (this.countZ == 1)
                {
                    if (this.headAtom instanceof RoleAtom)
                    {
                        int countFreshVariableDomain = 0;
                        int countFreshVariableRange = 0;
                        
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForDomainOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForRangeOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                            }

                            if ((countFreshVariableDomain >= 2) && (countFreshVariableRange >= 2)) break;
                        }
                        
                        if (countFreshVariableDomain == 1)
                            this.freshVariable.add(Global.variableForDomainOfHeadRole);
                        else if (countFreshVariableRange == 1)
                            this.freshVariable.add(Global.variableForRangeOfHeadRole);
                    }
                }
                else if (this.countZ == 2)
                {
                    if (this.headAtom instanceof ConceptAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);                        
                        int countFreshVariable = 0;
                    
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if (countFreshVariable >= 2) break;
                        }
                    
                        if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                        }
                        else if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                    }
                    else if (this.headAtom instanceof RoleAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);
                        this.freshVariable = new ArrayList<String>();
                        
                        int countFreshVariableDomain = 0;
                        int countFreshVariableRange = 0;
                        int countFreshVariable = 0;                        
                        
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForDomainOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForRangeOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                                
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if ((countFreshVariableDomain >= 2) && (countFreshVariableRange >= 2) && (countFreshVariable >= 2)) break;
                        }
                        
                        if (countFreshVariableDomain == 1)
                            this.freshVariable.add(Global.variableForDomainOfHeadRole);
                        else if (countFreshVariableRange == 1)
                            this.freshVariable.add(Global.variableForRangeOfHeadRole);
                        if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                        else if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                        }
                    }
                }
                else // this.countZ > 2
                {
                    if (this.headAtom instanceof ConceptAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);                        
                        int countFreshVariable = 0;
                    
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if (countFreshVariable >= 2) break;
                        }
                    
                        if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                        }
                        else if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                    }
                    else if (this.headAtom instanceof RoleAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);
                        
                        int countFreshVariable = 0;                        
                        
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {                        
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if (countFreshVariable >= 2) break;
                        }
                        
                        if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                        else if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                        }
                    }
                }
            }
            else if (lastAtomInListAtoms instanceof RoleAtom)
            {
                this.listRolesInBody.remove(this.listRolesInBody.size() - 1);
                
                if (this.countZ == 1)
                {
                    if (this.headAtom instanceof RoleAtom)
                    {
                        int countFreshVariableDomain = 0;
                        int countFreshVariableRange = 0;
                        
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForDomainOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForRangeOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                            }

                            if ((countFreshVariableDomain >= 2) && (countFreshVariableRange >= 2)) break;
                        }
                        
                        if (countFreshVariableDomain == 1)
                            this.freshVariable.add(Global.variableForDomainOfHeadRole);
                        else if (countFreshVariableRange == 1)
                            this.freshVariable.add(Global.variableForRangeOfHeadRole);
                    }
                }
                else if (this.countZ == 2)
                {
                    if (this.headAtom instanceof ConceptAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);                        
                        int countFreshVariable = 0;
                    
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if (countFreshVariable >= 2) break;
                        }
                    
                        if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                        }
                        else if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                    }
                    else if (this.headAtom instanceof RoleAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);
                        this.freshVariable = new ArrayList<String>();
                        
                        int countFreshVariableDomain = 0;
                        int countFreshVariableRange = 0;
                        int countFreshVariable = 0;                        
                        
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForDomainOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForDomainOfHeadRole) )
                                    countFreshVariableDomain++;
                                
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(Global.variableForRangeOfHeadRole) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(Global.variableForRangeOfHeadRole) )
                                    countFreshVariableRange++;
                                
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if ((countFreshVariableDomain >= 2) && (countFreshVariableRange >= 2) && (countFreshVariable >= 2)) break;
                        }
                        
                        if (countFreshVariableDomain == 1)
                            this.freshVariable.add(Global.variableForDomainOfHeadRole);
                        else if (countFreshVariableRange == 1)
                            this.freshVariable.add(Global.variableForRangeOfHeadRole);
                        if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                        else if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                        }
                    }
                }
                else // this.countZ > 2
                {
                    if (this.headAtom instanceof ConceptAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);                        
                        int countFreshVariable = 0;
                    
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if (countFreshVariable >= 2) break;
                        }
                    
                        if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                        }
                        else if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                    }
                    else if (this.headAtom instanceof RoleAtom)
                    {
                        String strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);
                        
                        int countFreshVariable = 0;                        
                        
                        for(int j = 0; j < this.listAtoms.size(); j++)
                        {
                            Atom atomInListAtoms = this.listAtoms.get(j);

                            if (atomInListAtoms instanceof ConceptAtom)
                            {                        
                                if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }
                            else if (atomInListAtoms instanceof RoleAtom)
                            {
                                if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                     ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                    countFreshVariable++;
                            }

                            if (countFreshVariable >= 2) break;
                        }
                        
                        if (countFreshVariable == 1)
                        {
                            if (!this.freshVariable.contains(strFreshVariable))
                                this.freshVariable.add(strFreshVariable);
                        }
                        else if (countFreshVariable == 0)
                        {
                            this.setListVariales.remove(strFreshVariable);
                            this.freshVariable.remove(strFreshVariable);
                            this.countZ--;
                            
                            strFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);
                        
                            int countFreshVariableNext = 0;

                            for(int j = 0; j < this.listAtoms.size(); j++)
                            {
                                Atom atomInListAtoms = this.listAtoms.get(j);

                                if (atomInListAtoms instanceof ConceptAtom)
                                {
                                    if ( ((ConceptAtom) atomInListAtoms).getVariable().equals(strFreshVariable) )
                                        countFreshVariableNext++;
                                }
                                else if (atomInListAtoms instanceof RoleAtom)
                                {
                                    if ( ((RoleAtom) atomInListAtoms).getDomainVariable().equals(strFreshVariable) || 
                                         ((RoleAtom) atomInListAtoms).getRangeVariable().equals(strFreshVariable) )
                                        countFreshVariableNext++;
                                }

                                if (countFreshVariableNext >= 2) break;
                            }

                            if (countFreshVariableNext == 1)
                                if (!this.freshVariable.contains(strFreshVariable))
                                    this.freshVariable.add(strFreshVariable);
                        }
                    
                    }
                }
                
            }
        }
        
        return adjustPattern();
    }
    
    /**
     * After finishing creating a pattern, if that pattern is not closed, it adjusts the pattern to become a closed pattern to respecting the language bias.
     * @return true if the pattern is adjusted
     */
    public boolean adjustPattern()
    {
    	//there are no fresh variables, and so the pattern is adjusted
        if (this.freshVariable.size() == 0)        
            return true;
        //there are only one fresh variable
        else if (this.freshVariable.size() == 1)
        {            
            while (!this.freshVariable.isEmpty())
            {//begin while
            	//remove the local fresh variable
                String localFreshVariable = this.freshVariable.get(0);
                this.freshVariable.remove(localFreshVariable);

                //if the head atom of the pattern to be adjusted is a role atom
                //then check if it contains a variable equal to the fresh variable in the domain or in the range
                //if it is true, then it is not possible to adjust the pattern
                if (this.headAtom instanceof RoleAtom)
                    if ( (((RoleAtom) this.headAtom).getDomainVariable().equals(localFreshVariable)) ||
                         (((RoleAtom) this.headAtom).getRangeVariable().equals(localFreshVariable)) )
                        return false;

                //position of the atoms in which the fresh variable is contained
                int iLocationFreshVariableInListAtoms = getLocationAtomContainsFreshVariableInListAtom(localFreshVariable);
                int iLocationFreshVariableInListRolesInBody = getLocationAtomContainsFreshVariableInListRolesInBody(localFreshVariable);
                int iLocationFreshVariableInBodyAtoms = getLocationAtomContainsFreshVariableInBodyAtom(localFreshVariable);

                //the returned position is not in the head atom
                if (iLocationFreshVariableInListAtoms > 1)
                {                
                	//remove the fresh variable from the list of atoms, from the body atoms and from the list of roles in the body
                    RoleAtom roleAtom = (RoleAtom) this.listAtoms.get(iLocationFreshVariableInListAtoms);
                    this.listAtoms.remove(iLocationFreshVariableInListAtoms);
                    this.bodyAtoms.remove(iLocationFreshVariableInBodyAtoms);
                    this.listRolesInBody.remove(iLocationFreshVariableInListRolesInBody);                

                    //the variable in the domain of the role created with the atom contained the fresh variable is equal to the fresh variable
                    if (roleAtom.getDomainVariable().equals(localFreshVariable))
                    {
                        this.setListVariales.remove(localFreshVariable);
                        ArrayList<String> domainListVariables = new ArrayList<String>(this.setListVariales);
                        domainListVariables.remove(roleAtom.getRangeVariable());

                        if (this.headAtom instanceof ConceptAtom)
                        {                    
                            if  (RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) 
                                domainListVariables.remove(((ConceptAtom)this.headAtom).getVariable());
                        }
                        else if (this.headAtom instanceof RoleAtom)
                        {
                            if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.headAtom.getIRI())) ||
                                (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.headAtom.getIRI())) )
                                if (roleAtom.getRangeVariable().equals(((RoleAtom)this.headAtom).getRangeVariable()))
                                    domainListVariables.remove(((RoleAtom)this.headAtom).getDomainVariable());
                        }

                        for(int i = 0; i < this.listConceptsInBody.size(); i++)
                            if (RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.listConceptsInBody.get(i).getIRI())) 
                                domainListVariables.remove(this.listConceptsInBody.get(i).getVariable());

                        for(int i = 0; i < this.listRolesInBody.size(); i++)
                            if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) ||
                                (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) )
                                if (roleAtom.getRangeVariable().equals(this.listRolesInBody.get(i).getRangeVariable()))
                                    domainListVariables.remove(this.listRolesInBody.get(i).getDomainVariable());

                        //there are variables in the domain of the role
                        if (domainListVariables.size() > 0)
                        {
                            String strVariable1 = domainListVariables.get(Global.myRandom.generateInt(1, domainListVariables.size()) - 1);
                            String strVariable2 = roleAtom.getRangeVariable();

                            RoleAtom newRoleAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
                            this.listAtoms.add(newRoleAtom);
                            this.bodyAtoms.add(newRoleAtom);
                            this.listRolesInBody.add(newRoleAtom);

                            if (this.countZ > 1) this.countZ--; 

                            return true;
                        }
                        else
                        {                        
                            if (this.countZ > 2)
                            {
                                this.countZ--;
                                String localNewFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);
                                int countToFindNewFreshVariable = 0;

                                for(int i = 0; i < this.bodyAtoms.size(); i++)
                                {
                                    Atom atom = this.bodyAtoms.get(i);
                                    if (atom instanceof ConceptAtom)
                                    {
                                        if (((ConceptAtom) atom).getVariable().equals(localNewFreshVariable)) 
                                            countToFindNewFreshVariable++;
                                    }
                                    else if (atom instanceof RoleAtom)
                                    {
                                        if ( (((RoleAtom) atom).getDomainVariable().equals(localNewFreshVariable)) ||  
                                             (((RoleAtom) atom).getRangeVariable().equals(localNewFreshVariable)) )
                                            countToFindNewFreshVariable++;
                                    }

                                    if (countToFindNewFreshVariable > 1) break;
                                }

                                if (countToFindNewFreshVariable > 1)
                                    return true;
                                else
                                    this.freshVariable.add(localNewFreshVariable);
                            }
                        }
                    }
                    //the variable in the range variable is equal to the fresh variable
                    else if (roleAtom.getRangeVariable().equals(localFreshVariable))
                    {
                        this.setListVariales.remove(localFreshVariable);
                        ArrayList<String> rangeListVariables = new ArrayList<String>(this.setListVariales);
                        rangeListVariables.remove(roleAtom.getDomainVariable());

                        if (this.headAtom instanceof ConceptAtom)
                        {                    
                            if  (RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) 
                                rangeListVariables.remove(((ConceptAtom)this.headAtom).getVariable());
                        }
                        else if (this.headAtom instanceof RoleAtom)
                        {
                            if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.headAtom.getIRI())) ||
                                (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.headAtom.getIRI())) )
                                if (roleAtom.getDomainVariable().equals(((RoleAtom)this.headAtom).getDomainVariable()))
                                    rangeListVariables.remove(((RoleAtom)this.headAtom).getRangeVariable());
                        }

                        for(int i = 0; i < this.listConceptsInBody.size(); i++)
                            if (RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.listConceptsInBody.get(i).getIRI())) 
                                rangeListVariables.remove(this.listConceptsInBody.get(i).getVariable());

                        for(int i = 0; i < this.listRolesInBody.size(); i++)
                            if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) ||
                                (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) )
                                if (roleAtom.getDomainVariable().equals(this.listRolesInBody.get(i).getDomainVariable()))
                                    rangeListVariables.remove(this.listRolesInBody.get(i).getRangeVariable());

                        if (rangeListVariables.size() > 0)
                        {
                            String strVariable1 = roleAtom.getDomainVariable();
                            String strVariable2 = rangeListVariables.get(Global.myRandom.generateInt(1, rangeListVariables.size()) - 1);

                            RoleAtom newRoleAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
                            RoleAtom newBodyAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
                            RoleAtom newRoleInBody = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);

                            this.listAtoms.add(newRoleAtom);
                            this.bodyAtoms.add(newBodyAtom);
                            this.listRolesInBody.add(newRoleInBody);
                            
                            if (this.countZ > 1) this.countZ--; 

                            return true;
                        }
                        else
                        {                        
                            if (this.countZ > 2)
                            {
                                this.countZ--;
                                String localNewFreshVariable = Global.variableForFresh + String.valueOf(this.countZ - 1);
                                int countToFindNewFreshVariable = 0;

                                for(int i = 0; i < this.bodyAtoms.size(); i++)
                                {
                                    Atom atom = this.bodyAtoms.get(i);
                                    if (atom instanceof ConceptAtom)
                                    {
                                        if (((ConceptAtom) atom).getVariable().equals(localNewFreshVariable)) 
                                            countToFindNewFreshVariable++;
                                    }
                                    else if (atom instanceof RoleAtom)
                                    {
                                        if ( (((RoleAtom) atom).getDomainVariable().equals(localNewFreshVariable)) ||  
                                             (((RoleAtom) atom).getRangeVariable().equals(localNewFreshVariable)) )
                                            countToFindNewFreshVariable++;
                                    }

                                    if (countToFindNewFreshVariable > 1) break;
                                }

                                if (countToFindNewFreshVariable > 1)
                                    return true;
                                else
                                    this.freshVariable.add(localNewFreshVariable);
                            }
                        }
                    }
                }
            } // end while
        }
        return false;
    }
    
    /**
     * Fix the metrics for the pattern: in particular, computation of the computation support, support extension, support, head coverage and Confidence and PCA Confidence.
     */
    public void compute()
    {
        patternComputation = new PatternComputation(this);
    }
    
    /**
     * Check if the list of atoms of the pattern is empty.
     * @return true, if the list is empty.
     */
    public boolean isEmpty()
    {
        return this.listAtoms.size() == 0;
    }
    
    /**
     * Check if the pattern that called the method is equal to the pattern in input (same atoms and same variables)
     * @param pattern
     * @return true if they are equals, false otherwise.
     */
    public boolean equals(Pattern pattern)
    {
        ArrayList<Atom> thisListAtoms = new ArrayList<Atom>(this.listAtoms);
        ArrayList<Atom> listAtomsInput = new ArrayList<Atom>(pattern.getListAtoms());
        
        //the dimension of the list of atoms of the pattern that called the method and 
        //the list of atoms of the atom in input are equals
        if (thisListAtoms.size() == listAtomsInput.size())
        {
        	//the head atoms are equals
            if (thisListAtoms.get(0).equals(listAtomsInput.get(0)))
            {
                int i = 1;
                
                while (i < thisListAtoms.size())
                {
                    Atom atomThis = thisListAtoms.get(i);
                    
                    int j = 1;
                    boolean blBreak = false;
                    
                    while(j < listAtomsInput.size())
                    {
                        Atom atomInput = listAtomsInput.get(j);
                        
                        if ((atomThis instanceof ConceptAtom) && (atomInput instanceof ConceptAtom))
                        {
                            if ((atomThis.equals(atomInput)) && (((ConceptAtom) atomThis).getVariable().equals(((ConceptAtom) atomInput).getVariable())))
                            {
                                thisListAtoms.remove(i);
                                listAtomsInput.remove(j);
                                blBreak = true;
                                break;
                            }
                        }
                        else if ((atomThis instanceof RoleAtom) && (atomInput instanceof RoleAtom))
                        {
                            if ((atomThis.equals(atomInput)) && 
                                 (((RoleAtom) atomThis).getDomainVariable().equals(((RoleAtom) atomInput).getDomainVariable())) &&
                                 (((RoleAtom) atomThis).getRangeVariable().equals(((RoleAtom) atomInput).getRangeVariable()))   
                                )
                            {
                                thisListAtoms.remove(i);
                                listAtomsInput.remove(j);
                                blBreak = true;
                                break;
                            }
                        }
                        
                        j++;
                    }
                    
                    if (!blBreak) return false;                    
                }
                
                if ((thisListAtoms.size() == 1) && (listAtomsInput.size() == 1))
                    return true;
                else
                    return false;
            }           
            else
                return false;
        }
        else
            return false;
    }
    
    /**
     * Get the list of atoms of the pattern.
     * @return the list.
     */
    public ArrayList<Atom> getListAtoms()
    {
        return this.listAtoms;
    }
    
    /**
     * 
     * @return the head atom.
     */
    public Atom getHeadAtom()
    {
        return this.headAtom;
    }
    
    /**
     * 
     * @return the body atoms.
     */
    public ArrayList<Atom> getBodyAtoms()
    {
        return this.bodyAtoms;
    }
    
    /**
     * 
     * @return the list of concepts in the body of the atom.
     */
    public ArrayList<ConceptAtom> getListConceptsInBody()
    {
        return this.listConceptsInBody;
    }
    
    /**
     * @return the list of roles in the body of the atom.
     */
    public ArrayList<RoleAtom> getListRolesInBody()
    {
        return this.listRolesInBody;
    }
    
    /**
     * 
     * @return the list of variables of the atom.
     */
    public Set<String> getListVariables()
    {
        return this.setListVariales;
    }
    
    /**
     * 
     * @return the fresh variable.
     */
    public ArrayList<String> getFreshVariable()
    {
        return this.freshVariable;
    }
        
    /**
     * @return the number of fresh variables.
     */
    public int getCountZ()
    {
        return this.countZ;
    }
    
    /**
     * 
     * @return the pattern computation
     */
    public PatternComputation getPatternComputation()
    {
        return this.patternComputation;
    }
    
    /**
     * Initialization of a pattern
     */
    private void initialize()
    {
        this.listAtoms = new ArrayList<Atom>();
        this.headAtom = null;
        this.bodyAtoms = new ArrayList<Atom>();
        
        this.listConceptsInBody = new ArrayList<ConceptAtom>();
        this.listRolesInBody = new ArrayList<RoleAtom>();
        this.setListVariales = new LinkedHashSet<String>();
        this.freshVariable = new ArrayList<String>();
        
        this.countZ = 1;
        
        this.patternComputation = null;
    }
    
    /**
     * Add the concept atom to the list of atoms and add the variables to the list of variables
     * @param conceptAtom
     * @return true
     */
    private boolean addConceptAtomWithHeadEmpty(ConceptAtom conceptAtom)
    {
        ConceptAtom newConceptAtom = new ConceptAtom(conceptAtom.getIRI(), Global.variableForHeadConcept);
        ConceptAtom newHeadAtom = new ConceptAtom(conceptAtom.getIRI(), Global.variableForHeadConcept);        
        
        this.listAtoms.add(newConceptAtom);
        this.headAtom = newHeadAtom;
        this.setListVariales.add(Global.variableForHeadConcept);
        
        return true;
    }
    
    /**
     * Add the concept atom to the list of atoms.
     * @param conceptAtom
     * @return true if the the concept atom is not subsumed by the head atom and so the concept atom created with the variables of the head is added to the list of atom, 
     * return false otherwise.
     */
    private boolean addConceptAtomWithHeadConceptAtom(ConceptAtom conceptAtom)
    {
    	//the concept atom is not subsumed by the head atom then 
        if (!ConceptProcessing.isConceptSubsumedBy(conceptAtom.getIRI(), this.headAtom.getIRI()))
        {
            ConceptAtom newConceptAtom = new ConceptAtom(conceptAtom.getIRI(), ((ConceptAtom)this.headAtom).getVariable());
            ConceptAtom newBodyAtom = new ConceptAtom(conceptAtom.getIRI(), ((ConceptAtom)this.headAtom).getVariable());
            ConceptAtom newConceptInBody = new ConceptAtom(conceptAtom.getIRI(), ((ConceptAtom)this.headAtom).getVariable());
            
            this.listAtoms.add(newConceptAtom);
            this.bodyAtoms.add(newBodyAtom);
            this.listConceptsInBody.add(newConceptInBody);
            
            return true;
        }                                                
        return false;
    }
    
    /**
     * The concept atom with a random variable for the body (from the set of variables) is added to the list of concept atoms.
     * A fresh variable is created using the variable in the range of the head, if this is equal to the random variable used for the concept atom.
     * A fresh variable is created using the variable in the domain of the head, otherwise.
     * @param conceptAtom
     * @return true
     */
    private boolean addConceptAtomWithHeadRoleAtom(ConceptAtom conceptAtom)
    {
        ArrayList<String> listVariables = new ArrayList<String>(this.setListVariales);
         
        //from the set of variables, take a random variable
        String strVariable = listVariables.get(Global.myRandom.generateInt(1, listVariables.size()) - 1);

        ConceptAtom newConceptAtom = new ConceptAtom(conceptAtom.getIRI(), strVariable);
        ConceptAtom newBodyAtom = new ConceptAtom(conceptAtom.getIRI(), strVariable);
        ConceptAtom newConceptInBody = new ConceptAtom(conceptAtom.getIRI(), strVariable);

        this.listAtoms.add(newConceptAtom);
        this.bodyAtoms.add(newBodyAtom);
        this.listConceptsInBody.add(newConceptInBody);
        
        //The variable is equal to the variable in the domain of the head atom
        //In this case the variable in the range of the head is added as a fresh variable
        if (strVariable.equals(((RoleAtom) this.headAtom).getDomainVariable()))
            this.freshVariable.add(((RoleAtom) this.headAtom).getRangeVariable());
        else
            this.freshVariable.add(((RoleAtom) this.headAtom).getDomainVariable());

        return true;        
    }
    
    /**
     * Add the concept atom to the list of atoms.
     * @param conceptAtom to be added
     * @return true if the concept atom with the right variable is added to the list of atoms in the body, 
     * false if there are no variables for this atom and so it is not added.
     */
    private boolean addConceptAtomWithHeadConceptAtomAndBigPatternSize(ConceptAtom conceptAtom)
    {
        ArrayList<String> newListVariables = new ArrayList<String>(this.setListVariales);
        
        //If the concept atom is subsumed by the head atom, then remove the variable of the head atom from the list of variable
        if (ConceptProcessing.isConceptSubsumedBy(conceptAtom.getIRI(), this.headAtom.getIRI()))
            newListVariables.remove(((ConceptAtom) this.headAtom).getVariable());
        
        for(int i = 0; i < this.listConceptsInBody.size(); i++)    
        	 //If the concept atom is subsumed by one of the concepts in the body, or viceversa, 
        	// then remove the variable of this concept of the body from the list of variables
            if  ( (ConceptProcessing.isConceptSubsumedBy(conceptAtom.getIRI(), this.listConceptsInBody.get(i).getIRI())) ||                          
                  (ConceptProcessing.isConceptSubsumedBy(this.listConceptsInBody.get(i).getIRI(), conceptAtom.getIRI())) )
                newListVariables.remove(this.listConceptsInBody.get(i).getVariable());

        for(int i = 0; i < this.listRolesInBody.size(); i++)
        {
        	//If the concept atom subsumes one of the concepts in a role domain of the body, or in a role range, 
        	// then remove the variable of the concept of the body from the list of variables
            if (RoleProcessing.isDomainOfRoleSubsumedByConcept(this.listRolesInBody.get(i).getIRI(), conceptAtom.getIRI()))
                newListVariables.remove(this.listRolesInBody.get(i).getDomainVariable());
            if (RoleProcessing.isRangeOfRoleSubsumedByConcept(this.listRolesInBody.get(i).getIRI(), conceptAtom.getIRI()))
                newListVariables.remove(this.listRolesInBody.get(i).getRangeVariable());
        }

        if (newListVariables.size() > 0)
        {
        	//take a random variable from the remain list of variables and remove it from the fresh variables
            String strVariable = newListVariables.get(Global.myRandom.generateInt(1, newListVariables.size()) - 1);
            this.freshVariable.remove(strVariable);  
            
            ConceptAtom newConceptAtom = new ConceptAtom(conceptAtom.getIRI(), strVariable);
      
              
            //add the concept atom with the right variable to the list of atoms in the body
            this.listAtoms.add(newConceptAtom);
            this.bodyAtoms.add(newConceptAtom);
            this.listConceptsInBody.add(newConceptAtom);
            
            return true;
        }
        else
            return false;                    
    }
    
    /**
     * Add the concept atom to the list of atoms.
     * @param conceptAtom to be added
     * @return true if the concept atom with the right variable is added to the list of atoms in the body, 
     * false if there are no variables for this atom and so it is not added.
     */
    private boolean addConceptAtomWithHeadRoleAtomAndBigPatternSize(ConceptAtom conceptAtom)
    {
//    	similar to the method above
    	
        ArrayList<String> listVariables = new ArrayList<String>(this.setListVariales);
        
        for(int i = 0; i < this.listConceptsInBody.size(); i++)                        
            if  ( (ConceptProcessing.isConceptSubsumedBy(conceptAtom.getIRI(), this.listConceptsInBody.get(i).getIRI())) ||                          
                  (ConceptProcessing.isConceptSubsumedBy(this.listConceptsInBody.get(i).getIRI(), conceptAtom.getIRI())) )
                listVariables.remove(this.listConceptsInBody.get(i).getVariable());
        
        for(int i = 0; i < this.listRolesInBody.size(); i++)
        {
            if (RoleProcessing.isDomainOfRoleSubsumedByConcept(this.listRolesInBody.get(i).getIRI(), conceptAtom.getIRI()))
                listVariables.remove(this.listRolesInBody.get(i).getDomainVariable());
            if (RoleProcessing.isRangeOfRoleSubsumedByConcept(this.listRolesInBody.get(i).getIRI(), conceptAtom.getIRI()))
                listVariables.remove(this.listRolesInBody.get(i).getRangeVariable());
        }
        if (listVariables.size() > 0)
        {            
            String strVariable = listVariables.get(Global.myRandom.generateInt(1, listVariables.size()) - 1);
                        
            this.freshVariable.remove(strVariable);
            
            ConceptAtom newConceptAtom = new ConceptAtom(conceptAtom.getIRI(), strVariable);
           // ConceptAtom newBodyAtom = new ConceptAtom(conceptAtom.getIRI(), strVariable);
           // ConceptAtom newConceptInBody = new ConceptAtom(conceptAtom.getIRI(), strVariable);
                        
            this.listAtoms.add(newConceptAtom);
            this.bodyAtoms.add(newConceptAtom);
            this.listConceptsInBody.add(newConceptAtom);
            
            return true;
        }
        else
            return false;                    
    }
    
    /**
     * Add the role atom to the list of atoms.
     * @param roleAtom to be added.
     * @return true, since the role atom is added.
     */
    private boolean addRoleAtomWithListEmpty(RoleAtom roleAtom)
    {
        RoleAtom newRoleAtom = new RoleAtom(roleAtom.getIRI(), Global.variableForDomainOfHeadRole, Global.variableForRangeOfHeadRole);
        RoleAtom newHeadAtom = new RoleAtom(roleAtom.getIRI(), Global.variableForDomainOfHeadRole, Global.variableForRangeOfHeadRole);
                
        //add the new role to the list of atoms
        this.listAtoms.add(newRoleAtom);
        this.headAtom = newHeadAtom;
        this.setListVariales.add(Global.variableForDomainOfHeadRole);
        this.setListVariales.add(Global.variableForRangeOfHeadRole);
        
        return true;
    }
    
    /**
     * Add the role atom to the list of atoms.
     * @param roleAtom to be added
     * @return false if the domain and the range of the role are subsumed by the head atom
     */
    private boolean addRoleAtomWithHeadConceptAtom(RoleAtom roleAtom)
    {
        RoleAtom newRoleAtom = new RoleAtom(roleAtom.getIRI());
//        RoleAtom newBodyAtom = new RoleAtom(roleAtom.getIRI());
//        RoleAtom newRoleInBody = new RoleAtom(roleAtom.getIRI());
        
        String newVariable = Global.variableForFresh + String.valueOf(this.countZ);
        
        if ( (RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) &&
            (RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) )
          return false;
        else 
        {    
            String strHeadVariable = ((ConceptAtom)this.headAtom).getVariable();            
            this.countZ++;
            
            if ( (!RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) &&
            (RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) )
            {
                newRoleAtom.setDomainVariable(strHeadVariable);
                newRoleAtom.setRangeVariable(newVariable);   
                /*newBodyAtom.setDomainVariable(strHeadVariable);
                newBodyAtom.setRangeVariable(newVariable);   
                newRoleInBody.setDomainVariable(strHeadVariable);
                newRoleInBody.setRangeVariable(newVariable);*/ 
            }
            else if ( (RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) &&
                 (!RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) )
            {
                newRoleAtom.setDomainVariable(newVariable);
                newRoleAtom.setRangeVariable(strHeadVariable);        
                /*newBodyAtom.setDomainVariable(newVariable);
                newBodyAtom.setRangeVariable(strHeadVariable);   
                newRoleInBody.setDomainVariable(newVariable);
                newRoleInBody.setRangeVariable(strHeadVariable);  */
            }
            else if ( (!RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) &&
                 (!RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) )
            {
                switch(Global.myRandom.generateInt(1, 2))
                {
                    case 1: newRoleAtom.setDomainVariable(strHeadVariable);
                            newRoleAtom.setRangeVariable(newVariable);
                           /* newBodyAtom.setDomainVariable(strHeadVariable);
                            newBodyAtom.setRangeVariable(newVariable);
                            newRoleInBody.setDomainVariable(strHeadVariable);
                            newRoleInBody.setRangeVariable(newVariable);*/
                            break;
                    case 2: newRoleAtom.setDomainVariable(newVariable);
                            newRoleAtom.setRangeVariable(strHeadVariable);
                            /*newBodyAtom.setDomainVariable(newVariable);
                            newBodyAtom.setRangeVariable(strHeadVariable);
                            newRoleInBody.setDomainVariable(newVariable);
                            newRoleInBody.setRangeVariable(strHeadVariable);*/
                            break;
                }
            }
            
            this.listAtoms.add(newRoleAtom);
            this.bodyAtoms.add(newRoleAtom);
            this.listRolesInBody.add(newRoleAtom);

            this.setListVariales.add(newVariable);
            this.freshVariable.add(newVariable);

            return true;
        }
    }
    
    /**
     * Add the role atom to the list of atoms.
     * @param roleAtom to be added.
     * @return true, the role atom is added.
     */
    private boolean addRoleAtomWithHeadRoleAtom(RoleAtom roleAtom)
    {        
        ArrayList<String> listVariables = new ArrayList<String>(this.setListVariales);
        
        String newVariable = Global.variableForFresh + String.valueOf(this.countZ);
        listVariables.add(newVariable);

        //Choose a variable from the list of variables, containing also the new variable created, and then remove the variabeìle chosen from the list
        String strVariable1 = listVariables.get(Global.myRandom.generateInt(1, listVariables.size()) - 1);
        listVariables.remove(strVariable1); 
        //Choose another variable, but it is not removed from the list
        String strVariable2 = listVariables.get(Global.myRandom.generateInt(1, listVariables.size()) - 1); 

        //the role atom to be added is a sub property of the head atom
        if (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.headAtom.getIRI()))
        {   
            if (strVariable1.equals(((RoleAtom)this.headAtom).getDomainVariable()) && 
                strVariable2.equals(((RoleAtom)this.headAtom).getRangeVariable()))
            {
                listVariables.remove(strVariable2);

                switch(Global.myRandom.generateInt(1, 2))
                {
                    case 1: 
                            strVariable1 = listVariables.get(Global.myRandom.generateInt(1, listVariables.size()) - 1);
                            break;
                        
                    case 2: strVariable2 = listVariables.get(Global.myRandom.generateInt(1, listVariables.size()) - 1);
                            break;
                }
            }                           
        }
        //This version do not consider the invert case.   
        RoleAtom newRoleAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
       // RoleAtom newBodyAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
       // RoleAtom newRoleInBody = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
        
        this.listAtoms.add(newRoleAtom);
        this.bodyAtoms.add(newRoleAtom);
        this.listRolesInBody.add(newRoleAtom);
        
        //the new variable is equal to one of the variable created
        if (strVariable1.equals(newVariable) || strVariable2.equals(newVariable))
        {
            this.setListVariales.add(newVariable);
            
            if (strVariable1.equals(newVariable))
                if (strVariable2.equals(((RoleAtom)this.headAtom).getDomainVariable()))
                    this.freshVariable.add(((RoleAtom)this.headAtom).getRangeVariable());
                else
                    this.freshVariable.add(((RoleAtom)this.headAtom).getDomainVariable());
            else if (strVariable2.equals(newVariable))
                if (strVariable1.equals(((RoleAtom)this.headAtom).getDomainVariable()))
                    this.freshVariable.add(((RoleAtom)this.headAtom).getRangeVariable());
                else
                    this.freshVariable.add(((RoleAtom)this.headAtom).getDomainVariable());               
            
            this.freshVariable.add(newVariable);
            this.countZ++; 
        }

        return true;
    }
    
    /**
     * Add the role atom to the list of atoms.
     * @param roleAtom to be added.
     * @return true, the role atom is added.
     */
    private boolean addRoleAtomWithHeadConceptAtomAndBigPatternSize(RoleAtom roleAtom)
    {        
        ArrayList<String> domainListVariables = new ArrayList<String>(this.setListVariales);
        ArrayList<String> rangeListVariables = new ArrayList<String>(this.setListVariales);
        
        //the domain of the role atom to be added is subsumed by the head atom: 
        //remove the variable of the head atom from the list of the variable in the domain              
        if (RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI()))
            domainListVariables.remove(((ConceptAtom)this.headAtom).getVariable()); 
        //the same, with the range
        if  (RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.headAtom.getIRI())) 
            rangeListVariables.remove(((ConceptAtom)this.headAtom).getVariable());
        
        //the same, but instead of comparing with the head atom, it compares with the body concepts
        for(int i = 0; i < this.listConceptsInBody.size(); i++) {
        	
            if (RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.listConceptsInBody.get(i).getIRI()))
                domainListVariables.remove(this.listConceptsInBody.get(i).getVariable());
            if (RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.listConceptsInBody.get(i).getIRI())) 
                rangeListVariables.remove(this.listConceptsInBody.get(i).getVariable());
        }
        
        String localFreshVariable = "";
        String strVariable1 = "";
        String strVariable2 = "";
        
        //there is no fresh variable
        if (this.freshVariable.size() == 0)
        {
        	//there are no variables in the domain and range lists: return false
            if ((domainListVariables.size() <= 0) && (rangeListVariables.size() <= 0))
                return false;
            
            //there are variables only in the domain list
            else if ((domainListVariables.size() > 0) && (rangeListVariables.size() <= 0)) {
                localFreshVariable = Global.variableForFresh + String.valueOf(this.countZ);
                strVariable1 = domainListVariables.get(Global.myRandom.generateInt(1, domainListVariables.size()) - 1);
                strVariable2 = localFreshVariable;
            }
            
            //there are variables only in the range list
            else if ((domainListVariables.size() <= 0) && (rangeListVariables.size() > 0)) {
                localFreshVariable = Global.variableForFresh + String.valueOf(this.countZ);
                strVariable1 = localFreshVariable;
                strVariable2 = rangeListVariables.get(Global.myRandom.generateInt(1, rangeListVariables.size()) - 1);
            }
            
            //there are variable in the domain and in the range list
            else if ((domainListVariables.size() > 0) && (rangeListVariables.size() > 0)) {
                Map<String, Set<String>> mapRemoveVariables = new HashMap<String, Set<String>>();
                //for every role in the body 
                for(int i = 0; i < this.listRolesInBody.size(); i++) {
                	//check if it is a super property or a sub property of the role atom in input
                    if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) ||
                        (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI()))) {
                        Set<String> mapKeys = mapRemoveVariables.keySet();

                        if (mapKeys.contains(this.listRolesInBody.get(i).getDomainVariable()))
                        {
                            Set<String> mapRanges = mapRemoveVariables.get(this.listRolesInBody.get(i).getDomainVariable());
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.replace(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                        else
                        {
                            Set<String> mapRanges = new LinkedHashSet<String>();
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.put(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                    }
                }
                //set of variables in the domain of the roles in the body
                Set<String> mapRemoveVariablesKeys = mapRemoveVariables.keySet();
                
                Map<String, Set<String>> mapVariables = new HashMap<String, Set<String>>();                
                localFreshVariable = Global.variableForFresh + String.valueOf(this.countZ);
                
                for (int i = 0; i < domainListVariables.size(); i++)
                {
                	//variable in position i
                    String domainVariable = domainListVariables.get(i);
                    Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                    //if it is contained in the set of variables in the domain of the roles in the body,
                    //then remove the set of variables in the range of the role body from the set of variables in the range of the role in input 
                    if (mapRemoveVariablesKeys.contains(domainVariable))
                    {
                        Set<String> mapRemoveVariableValues = mapRemoveVariables.get(domainVariable);
                        mapRanges.removeAll(mapRemoveVariableValues);
                    }
                    //in any case, remove the domain variable from the set of variables in the range of the role
                    mapRanges.remove(domainVariable);
                    //add the local fresh variable to this set
                    mapRanges.add(localFreshVariable);
                    //to the domain variable correspond the new maps range
                    mapVariables.put(domainVariable, mapRanges);        
                }
                
                Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);  
                //to the local fresh variable corresponds the new maps range
                mapVariables.put(localFreshVariable, mapRanges);
                
                ArrayList<String> variableKeys = new ArrayList<String>(mapVariables.keySet());
                strVariable1 = variableKeys.get(Global.myRandom.generateInt(1, variableKeys.size()) - 1);
                ArrayList<String> variableValues = new ArrayList<String>(mapVariables.get(strVariable1));
                strVariable2 = variableValues.get(Global.myRandom.generateInt(1, variableValues.size()) - 1);
            }
        }
        else
        {   //there are fresh variables 
        	//there are variables in the domain and there are no variables in the range list
            if ((domainListVariables.size() > 0) && (rangeListVariables.size() == 0))
            {
            	//take the fresh variable in the first position and remove it from the variables in the domain
                localFreshVariable = this.freshVariable.get(0);
                domainListVariables.remove(localFreshVariable);               
                
                if (domainListVariables.size() > 0)
                {
                    strVariable1 = domainListVariables.get(Global.myRandom.generateInt(1, domainListVariables.size()) - 1);
                    strVariable2 = localFreshVariable;
                }
                else
                    return false;
            }
            //there are no variables in the domain but there are variables in the range
            else if ((domainListVariables.size() == 0) && (rangeListVariables.size() > 0))
            {
                localFreshVariable = this.freshVariable.get(0);
                rangeListVariables.remove(localFreshVariable);               
                
                //check the size of the range list variables, after removing the local fresh variable
                if (rangeListVariables.size() > 0)
                {
                    strVariable1 = localFreshVariable;
                    strVariable2 = rangeListVariables.get(Global.myRandom.generateInt(1, rangeListVariables.size()) - 1);
                }
                else
                    return false;
            }
            //there are variables in both the domain and the range lists
            else if ((domainListVariables.size() > 0) && (rangeListVariables.size() > 0))
            {
                Map<String, Set<String>> mapRemoveVariables = new HashMap<String, Set<String>>();
        
                for(int i = 0; i < this.listRolesInBody.size(); i++)
                {
                    if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) ||
                        (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) )                    
                    {
                        Set<String> mapKeys = mapRemoveVariables.keySet();

                        if (mapKeys.contains(this.listRolesInBody.get(i).getDomainVariable()))
                        {
                            Set<String> mapRanges = mapRemoveVariables.get(this.listRolesInBody.get(i).getDomainVariable());
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.replace(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                        else
                        {
                            Set<String> mapRanges = new LinkedHashSet<String>();
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.put(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                    }
                }
                
                Set<String> mapRemoveVariablesKeys = mapRemoveVariables.keySet();
                
                Map<String, Set<String>> mapVariables = new HashMap<String, Set<String>>();                
                localFreshVariable = this.freshVariable.get(0);
                
                for (int i = 0; i < domainListVariables.size(); i++)
                {
                    String domainVariable = domainListVariables.get(i);
                    
                    if (mapRemoveVariablesKeys.contains(domainVariable))
                    {
                        Set<String> mapRemoveVariableValues = mapRemoveVariables.get(domainVariable);
                        Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                        mapRanges.removeAll(mapRemoveVariableValues);
                        mapRanges.remove(domainVariable);    
                        if (!mapRanges.isEmpty())
                            mapVariables.put(domainVariable, mapRanges);
                    }
                    else
                    {
                        Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                        mapRanges.remove(domainVariable);                
                        if (!mapRanges.isEmpty())
                            mapVariables.put(domainVariable, mapRanges);
                    }   
                }
                
                if ((!mapVariables.keySet().contains(localFreshVariable)) && (!domainListVariables.contains(localFreshVariable)))
                {
                    Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                    mapRanges.remove(localFreshVariable);
                    if (!mapRanges.isEmpty())
                        mapVariables.put(localFreshVariable, mapRanges);
                }
                
                ArrayList<String> variableKeys = new ArrayList<String>(mapVariables.keySet());
                
                if (variableKeys.size() > 0)
                {                
                    strVariable1 = variableKeys.get(Global.myRandom.generateInt(1, variableKeys.size()) - 1);
                    ArrayList<String> variableValues = new ArrayList<String>(mapVariables.get(strVariable1));
                    strVariable2 = variableValues.get(Global.myRandom.generateInt(1, variableValues.size()) - 1);
                }
                else
                    return false;
            }
        }
        
        RoleAtom newRoleAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
//        RoleAtom newBodyAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
//        RoleAtom newRoleInBody = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);

        this.listAtoms.add(newRoleAtom);
        this.bodyAtoms.add(newRoleAtom);
        this.listRolesInBody.add(newRoleAtom);

        if (strVariable1.equals(localFreshVariable) || strVariable2.equals(localFreshVariable))            
            if (this.freshVariable.size() == 0)
            {                
                this.setListVariales.add(localFreshVariable);
                this.freshVariable.add(localFreshVariable);
                this.countZ++; 
            }
            else                
                this.freshVariable.remove(localFreshVariable);                

        return true;
    }
    
    /**
     * Add the role atom in the pattern in which the head is a role atom.
     * @param roleAtom
     * @return true if the role atom is added.
     */
    private boolean addRoleAtomWithHeadRoleAtomAndBigPatternSize(RoleAtom roleAtom)
    {        
        ArrayList<String> domainListVariables = new ArrayList<String>(this.setListVariales);
        ArrayList<String> rangeListVariables = new ArrayList<String>(this.setListVariales);
        
        if ( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.headAtom.getIRI())) ||
             (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.headAtom.getIRI())) )
        {
            domainListVariables.remove(((RoleAtom)this.headAtom).getDomainVariable());
            rangeListVariables.remove(((RoleAtom)this.headAtom).getRangeVariable());
        }
        
        for(int i = 0; i < this.listConceptsInBody.size(); i++)
        {
            if (RoleProcessing.isDomainOfRoleSubsumedByConcept(roleAtom.getIRI(), this.listConceptsInBody.get(i).getIRI()))
                domainListVariables.remove(this.listConceptsInBody.get(i).getVariable());
            if (RoleProcessing.isRangeOfRoleSubsumedByConcept(roleAtom.getIRI(), this.listConceptsInBody.get(i).getIRI())) 
                rangeListVariables.remove(this.listConceptsInBody.get(i).getVariable());
        }
        
        String localFreshVariable = "";
        String strVariable1 = "";
        String strVariable2 = "";
        
        if (this.freshVariable.size() == 0)
        {
            if ((domainListVariables.size() == 0) && (rangeListVariables.size() == 0))
                return false;
            else if ((domainListVariables.size() > 0) && (rangeListVariables.size() == 0))
            {
                localFreshVariable = Global.variableForFresh + String.valueOf(this.countZ);
                strVariable1 = domainListVariables.get(Global.myRandom.generateInt(1, domainListVariables.size()) - 1);
                strVariable2 = localFreshVariable;
            }
            else if ((domainListVariables.size() == 0) && (rangeListVariables.size() > 0))
            {
                localFreshVariable = Global.variableForFresh + String.valueOf(this.countZ);
                strVariable1 = localFreshVariable;
                strVariable2 = rangeListVariables.get(Global.myRandom.generateInt(1, rangeListVariables.size()) - 1);
            }
            else if ((domainListVariables.size() > 0) && (rangeListVariables.size() > 0))
            {
                Map<String, Set<String>> mapRemoveVariables = new HashMap<String, Set<String>>();
        
                for(int i = 0; i < this.listRolesInBody.size(); i++)
                {
                    if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) ||
                        (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) )                    
                    {
                        Set<String> mapKeys = mapRemoveVariables.keySet();

                        if (mapKeys.contains(this.listRolesInBody.get(i).getDomainVariable()))
                        {
                            Set<String> mapRanges = mapRemoveVariables.get(this.listRolesInBody.get(i).getDomainVariable());
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.replace(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                        else
                        {
                            Set<String> mapRanges = new LinkedHashSet<String>();
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.put(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                    }
                }
                
                Set<String> mapRemoveVariablesKeys = mapRemoveVariables.keySet();
                
                Map<String, Set<String>> mapVariables = new HashMap<String, Set<String>>();                
                localFreshVariable = Global.variableForFresh + String.valueOf(this.countZ);
                
                for (int i = 0; i < domainListVariables.size(); i++)
                {
                    String domainVariable = domainListVariables.get(i);
                    
                    if (mapRemoveVariablesKeys.contains(domainVariable))
                    {
                        Set<String> mapRemoveVariableValues = mapRemoveVariables.get(domainVariable);
                        Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                        mapRanges.removeAll(mapRemoveVariableValues);
                        mapRanges.remove(domainVariable);
                        mapRanges.add(localFreshVariable);
                        mapVariables.put(domainVariable, mapRanges);
                    }
                    else
                    {
                        Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                        mapRanges.remove(domainVariable);
                        mapRanges.add(localFreshVariable);                        
                        mapVariables.put(domainVariable, mapRanges);
                    }   
                }
                
                Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);                
                mapVariables.put(localFreshVariable, mapRanges);
                
                ArrayList<String> variableKeys = new ArrayList<String>(mapVariables.keySet());
                strVariable1 = variableKeys.get(Global.myRandom.generateInt(1, variableKeys.size()) - 1);
                ArrayList<String> variableValues = new ArrayList<String>(mapVariables.get(strVariable1));
                strVariable2 = variableValues.get(Global.myRandom.generateInt(1, variableValues.size()) - 1);
            }
        }
        else
        {
            if ((domainListVariables.size() > 0) && (rangeListVariables.size() == 0))
            {
                localFreshVariable = this.freshVariable.get(Global.myRandom.generateInt(1, this.freshVariable.size()) - 1);
                domainListVariables.remove(localFreshVariable);               
                
                if (domainListVariables.size() > 0)
                {
                    strVariable1 = domainListVariables.get(Global.myRandom.generateInt(1, domainListVariables.size()) - 1);
                    strVariable2 = localFreshVariable;
                }
                else
                    return false;
            }
            else if ((domainListVariables.size() == 0) && (rangeListVariables.size() > 0))
            {
                localFreshVariable = this.freshVariable.get(Global.myRandom.generateInt(1, this.freshVariable.size()) - 1);
                rangeListVariables.remove(localFreshVariable);               
                
                if (rangeListVariables.size() > 0)
                {
                    strVariable1 = localFreshVariable;
                    strVariable2 = rangeListVariables.get(Global.myRandom.generateInt(1, rangeListVariables.size()) - 1);
                }
                else
                    return false;
            }
            else if ((domainListVariables.size() > 0) && (rangeListVariables.size() > 0))
            {
                Map<String, Set<String>> mapRemoveVariables = new HashMap<String, Set<String>>();
        
                for(int i = 0; i < this.listRolesInBody.size(); i++)
                {
                    if( (RoleProcessing.isRoleSubProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) ||
                        (RoleProcessing.isRoleSuperProperty(roleAtom.getIRI(), this.listRolesInBody.get(i).getIRI())) )                    
                    {
                        Set<String> mapKeys = mapRemoveVariables.keySet();

                        if (mapKeys.contains(this.listRolesInBody.get(i).getDomainVariable()))
                        {
                            Set<String> mapRanges = mapRemoveVariables.get(this.listRolesInBody.get(i).getDomainVariable());
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.replace(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                        else
                        {
                            Set<String> mapRanges = new LinkedHashSet<String>();
                            mapRanges.add(this.listRolesInBody.get(i).getRangeVariable());
                            mapRemoveVariables.put(this.listRolesInBody.get(i).getDomainVariable(), mapRanges);
                        }
                    }
                }
                
                Set<String> mapRemoveVariablesKeys = mapRemoveVariables.keySet();
                
                Map<String, Set<String>> mapVariables = new HashMap<String, Set<String>>();                
                localFreshVariable = this.freshVariable.get(Global.myRandom.generateInt(1, this.freshVariable.size()) - 1);
                
                for (int i = 0; i < domainListVariables.size(); i++)
                {
                    String domainVariable = domainListVariables.get(i);
                    
                    if (mapRemoveVariablesKeys.contains(domainVariable))
                    {
                        Set<String> mapRemoveVariableValues = mapRemoveVariables.get(domainVariable);
                        Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                        mapRanges.removeAll(mapRemoveVariableValues);
                        mapRanges.remove(domainVariable);
                        if (!mapRanges.isEmpty())                            
                            mapVariables.put(domainVariable, mapRanges);
                    }
                    else
                    {
                        Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                        mapRanges.remove(domainVariable);
                        if (!mapRanges.isEmpty())
                            mapVariables.put(domainVariable, mapRanges);
                    }   
                }
                
                if ((!mapVariables.keySet().contains(localFreshVariable)) && (!domainListVariables.contains(localFreshVariable)))
                {
                    Set<String> mapRanges = new LinkedHashSet<String>(rangeListVariables);
                    mapRanges.remove(localFreshVariable);
                    if (!mapRanges.isEmpty())
                        mapVariables.put(localFreshVariable, mapRanges);
                }
                
                ArrayList<String> variableKeys = new ArrayList<String>(mapVariables.keySet());
                
                if (!variableKeys.isEmpty())
                {                
                    strVariable1 = variableKeys.get(Global.myRandom.generateInt(1, variableKeys.size()) - 1);
                    ArrayList<String> variableValues = new ArrayList<String>(mapVariables.get(strVariable1));
                    strVariable2 = variableValues.get(Global.myRandom.generateInt(1, variableValues.size()) - 1);
                }
                else
                    return false;
            }
        }
        
        RoleAtom newRoleAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
//        RoleAtom newBodyAtom = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);
//        RoleAtom newRoleInBody = new RoleAtom(roleAtom.getIRI(), strVariable1, strVariable2);

        this.listAtoms.add(newRoleAtom);
        this.bodyAtoms.add(newRoleAtom);
        this.listRolesInBody.add(newRoleAtom);

        if (this.freshVariable.size() == 0)
        {                
            if (strVariable1.equals(localFreshVariable) || strVariable2.equals(localFreshVariable)) 
            {
                this.setListVariales.add(localFreshVariable);
                this.freshVariable.add(localFreshVariable);
                this.countZ++; 
            }
        }
        else if (this.freshVariable.size() == 1)
        {
            if (strVariable1.equals(localFreshVariable) || strVariable2.equals(localFreshVariable))
                this.freshVariable.remove(localFreshVariable);
        }
        else if (this.freshVariable.size() == 2)
        {
            if (this.freshVariable.contains(strVariable1))
                this.freshVariable.remove(strVariable1);
            
            if (this.freshVariable.contains(strVariable2))
                this.freshVariable.remove(strVariable2);
        }

        return true;
    }
    
    /**
     * This method returns the position of the role atom which contains a variable equal to the fresh variable.
     * @param strFreshVariable
     * @return the position of the role atom
     */
    private int getLocationAtomContainsFreshVariableInListAtom(String strFreshVariable)
    {
        if (this.listAtoms.size() > 0)        
            for (int i = 0; i < this.listAtoms.size(); i++)
                if (this.listAtoms.get(i) instanceof RoleAtom)                
                    if ( (((RoleAtom) this.listAtoms.get(i)).getDomainVariable().equals(strFreshVariable)) ||
                         (((RoleAtom) this.listAtoms.get(i)).getRangeVariable().equals(strFreshVariable)) )
                        return i;
        return -1;
    }
    
    /**
     * This method returns the position of the role atom in the body which contains a variable equal to the fresh variable.
     * @param strFreshVariable
     * @return the position of the atom
     */
    private int getLocationAtomContainsFreshVariableInBodyAtom(String strFreshVariable)
    {
        if (this.bodyAtoms.size() > 0)        
            for (int i = 0; i < this.bodyAtoms.size(); i++)
                if (this.bodyAtoms.get(i) instanceof RoleAtom)                
                    if ( (((RoleAtom) this.bodyAtoms.get(i)).getDomainVariable().equals(strFreshVariable)) ||
                         (((RoleAtom) this.bodyAtoms.get(i)).getRangeVariable().equals(strFreshVariable)) )
                        return i;
        return -1;
    }
    
    /**
     * This method returns the position of the role atom in the body which contains a variable equal to the fresh variable.
     * @param strFreshVariable
     * @return the position of the role atom
     */
    private int getLocationAtomContainsFreshVariableInListRolesInBody(String strFreshVariable)
    {
        if (this.listRolesInBody.size() > 0)        
            for (int i = 0; i < this.listRolesInBody.size(); i++)
                if ( (this.listRolesInBody.get(i).getDomainVariable().equals(strFreshVariable)) ||
                     (this.listRolesInBody.get(i).getRangeVariable().equals(strFreshVariable)) )
                    return i;
        return -1;
    }
}
