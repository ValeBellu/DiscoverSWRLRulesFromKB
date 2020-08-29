package MKnowledge;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import MAtom.ConceptAtom;
import MAtom.RoleAtom;
import MCommon.Global;
import MPattern.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A class for loading and checking the populated ontology
 */
public class KnowledgeBase 
{
    private OWLOntologyManager ontologyManager;
    private OWLDataFactory dataFactory;	
    private OWLOntology ontology;
    private IRI iri;
    
    private PelletReasoner reasonerPellet;
    private Reasoner reasonerHermit;
    
    private PrefixOWLOntologyFormat prefix;
    
    /**
	 * Load the populated ontology.
     * @param iri is the IRI of the ontology to be loaded
	 */
    public KnowledgeBase(IRI iri)
    {
        try 
        {
            this.iri = iri;
            this.ontologyManager = OWLManager.createOWLOntologyManager();
            this.dataFactory = this.ontologyManager.getOWLDataFactory();
            this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(iri);
            this.reasonerPellet = PelletReasonerFactory.getInstance().createReasoner(this.ontology);
            this.reasonerHermit = new Reasoner(this.ontology);
            
            this.prefix = (PrefixOWLOntologyFormat) this.ontologyManager.getOntologyFormat(this.ontology); 
            this.prefix.setDefaultPrefix(Global.BASE_URL); 
        } 
        catch (OWLOntologyCreationException e) 
        {	
            e.printStackTrace();
	}
    }    
    
    /**
	 * Check if the set of axioms is consistent, thanks to reasoners.
	 * @return true if they are consistet, false otherwise.
	 */
    public boolean isConsistent()
    {
        try
        {        
            if (Global.TYPE_OF_REASONER.equals("Pellet"))                
                return reasonerPellet.isConsistent();        
            else if (Global.TYPE_OF_REASONER.equals("Hermit"))
                return reasonerHermit.isConsistent();
            else
                return false;
        }
        catch(org.mindswap.pellet.exceptions.InternalReasonerException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(org.semanticweb.owlapi.reasoner.TimeOutException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(java.lang.OutOfMemoryError e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * The ontology manager is the one who manages and creates the ontology.
     * @return the ontology manager.
     */
    public OWLOntologyManager getOntologyManager()
    {
        return this.ontologyManager;
    }
    
    /**
     * The data factory creates and manages the OWL API objects.
     * @return the data factory
     */
    public OWLDataFactory getDataFactory()
    {
        return this.dataFactory;
    }
    
    /**
     * @return the ontology
     */
    public OWLOntology getOntology()
    {
        return this.ontology;
    }
    
    /**
     * 
     * @return the IRI
     */
    public IRI getIRI()
    {
        return this.iri;
    }
    
    /**
     * 
     * @return the reasoner Pellet
     */
    public PelletReasoner getPelletReasoner()
    {
    	return this.reasonerPellet;
    }
    
    /**
     * 
     * @return the reasoner Hermit
     */
    public Reasoner getHermitReasoner()
    {
        return this.reasonerHermit;
    }
    
    /**
     * 
     * @return the prefix 
     */
    public PrefixOWLOntologyFormat getPrefix()
    {
        return this.prefix;
    }
    
    /**
     * Check if the rule correspondent to the pattern is consistent with the ontology.
     * In particular, the rule is added to the ontology as a new axiom and the reasoner check if 
     * the ontology is still consistent.
     * @param pattern 
     * @return true if the ontology is still consistent.
     */
    public boolean addPatternHorn(Pattern pattern)
    {
        try 
        {            
            SWRLAtom head = null;
            Set<SWRLAtom> body = new LinkedHashSet<SWRLAtom>();
            
            //with the atom in the pattern, it creates the head and the body
            for(int i = 0; i < pattern.size(); i++)
            {
                if (i == 0)
                {
                    if (pattern.get(0) instanceof ConceptAtom)
                    {
                        OWLClass conceptAtom = this.dataFactory.getOWLClass(((ConceptAtom) pattern.get(i)).getIRI());
                        SWRLVariable var = this.dataFactory.getSWRLVariable(IRI.create(((ConceptAtom) pattern.get(i)).getVariable()));
                        head = this.dataFactory.getSWRLClassAtom(conceptAtom, var);
                    }
                    else if (pattern.get(0) instanceof RoleAtom)
                    {
                        OWLObjectProperty roleAtom = this.dataFactory.getOWLObjectProperty(((RoleAtom) pattern.get(i)).getIRI());
                        SWRLVariable var1 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getDomainVariable()));
                        SWRLVariable var2 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getRangeVariable()));
                        head = this.dataFactory.getSWRLObjectPropertyAtom(roleAtom, var1, var2);
                    }
                }
                else
                {
                    if (pattern.get(i) instanceof ConceptAtom)
                    {
                        OWLClass conceptAtom = this.dataFactory.getOWLClass(((ConceptAtom) pattern.get(i)).getIRI());
                        SWRLVariable var = this.dataFactory.getSWRLVariable(IRI.create(((ConceptAtom) pattern.get(i)).getVariable()));
                        body.add(this.dataFactory.getSWRLClassAtom(conceptAtom, var));
                    }
                    else if (pattern.get(i) instanceof RoleAtom)
                    {
                        OWLObjectProperty roleAtom = this.dataFactory.getOWLObjectProperty(((RoleAtom) pattern.get(i)).getIRI());
                        SWRLVariable var1 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getDomainVariable()));
                        SWRLVariable var2 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getRangeVariable()));
                        body.add(this.dataFactory.getSWRLObjectPropertyAtom(roleAtom, var1, var2));
                    
                    }
                }                
            }
            
            //It generates a swrl rule with this pattern
            SWRLRule swrlRule = this.dataFactory.getSWRLRule(body, Collections.singleton(head));
            //the new rule is added as axiom to the ontology
            this.ontologyManager.applyChange(new AddAxiom(this.ontology, swrlRule));

            //check if now the ontology is consistent
            if (Global.TYPE_OF_REASONER.equals("Pellet")) 
            {   
                this.reasonerPellet = null;
                
                OWLReasonerConfiguration config = new SimpleConfiguration();            
                this.reasonerPellet = PelletReasonerFactory.getInstance().createReasoner(this.ontology, config);
                //this.reasonerPellet.getKB().setTimeout(5000);
                
            }
            else if (Global.TYPE_OF_REASONER.equals("Hermit"))
            {
                this.reasonerHermit = null;
                this.reasonerHermit = new Reasoner(this.ontology);
            }
            else
                return false;            
            
            if (!this.isConsistent())
                return false;
            else            
                return true;
        }        
        catch(org.mindswap.pellet.exceptions.InternalReasonerException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(org.semanticweb.owlapi.reasoner.TimeOutException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    /*public boolean addPatternHorn(Pattern pattern)
    {
        try 
        {            
            SWRLAtom head = null;
            Set<SWRLAtom> body = new LinkedHashSet<SWRLAtom>();
            
            for(int i = 0; i < pattern.size(); i++)
            {
                if (i == 0)
                {
                    if (pattern.get(0) instanceof ConceptAtom)
                    {
                        OWLClass conceptAtom = this.dataFactory.getOWLClass(((ConceptAtom) pattern.get(i)).getIRI());
                        SWRLVariable var = this.dataFactory.getSWRLVariable(IRI.create(((ConceptAtom) pattern.get(i)).getVariable()));
                        head = this.dataFactory.getSWRLClassAtom(conceptAtom, var);
                    }
                    else if (pattern.get(0) instanceof RoleAtom)
                    {
                        OWLObjectProperty roleAtom = this.dataFactory.getOWLObjectProperty(((RoleAtom) pattern.get(i)).getIRI());
                        SWRLVariable var1 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getDomainVariable()));
                        SWRLVariable var2 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getRangeVariable()));
                        head = this.dataFactory.getSWRLObjectPropertyAtom(roleAtom, var1, var2);
                    }
                }
                else
                {
                    if (pattern.get(i) instanceof ConceptAtom)
                    {
                        OWLClass conceptAtom = this.dataFactory.getOWLClass(((ConceptAtom) pattern.get(i)).getIRI());
                        SWRLVariable var = this.dataFactory.getSWRLVariable(IRI.create(((ConceptAtom) pattern.get(i)).getVariable()));
                        body.add(this.dataFactory.getSWRLClassAtom(conceptAtom, var));
                    }
                    else if (pattern.get(i) instanceof RoleAtom)
                    {
                        OWLObjectProperty roleAtom = this.dataFactory.getOWLObjectProperty(((RoleAtom) pattern.get(i)).getIRI());
                        SWRLVariable var1 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getDomainVariable()));
                        SWRLVariable var2 = this.dataFactory.getSWRLVariable(IRI.create(((RoleAtom) pattern.get(i)).getRangeVariable()));
                        body.add(this.dataFactory.getSWRLObjectPropertyAtom(roleAtom, var1, var2));
                    
                    }
                }                
            }
            
            SWRLRule swrlRule = this.dataFactory.getSWRLRule(body, Collections.singleton(head));		
            this.ontologyManager.applyChange(new AddAxiom(this.ontology, swrlRule));

            if (Global.TYPE_OF_REASONER.equals("Pellet")) 
            {   
                this.reasonerPellet = null;
                
                OWLReasonerConfiguration config = new SimpleConfiguration();            
                this.reasonerPellet = PelletReasonerFactory.getInstance().createReasoner(this.ontology, config);
                //this.reasonerPellet.getKB().setTimeout(5000);
                
            }
            else if (Global.TYPE_OF_REASONER.equals("Hermit"))
            {
                this.reasonerHermit = null;
                this.reasonerHermit = new Reasoner(this.ontology);
            }
            else
                return false;            
            
            if (!this.isConsistent())
                return false;
            else            
                return true;
        }        
        catch(org.mindswap.pellet.exceptions.InternalReasonerException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(org.semanticweb.owlapi.reasoner.TimeOutException e)
        {
            e.printStackTrace();
            return false;
        }
    }*/
}
