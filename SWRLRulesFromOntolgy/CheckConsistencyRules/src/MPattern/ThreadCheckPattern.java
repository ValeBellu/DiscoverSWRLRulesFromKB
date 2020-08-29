/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MPattern;

import MCommon.Global;
import MKnowledge.KnowledgeBase;

/**
 *
 * Class used to manage the pattern's consistency. The patterns are managed by a different threads.
 */
public class ThreadCheckPattern extends Thread
{
    private Pattern pattern;
    private boolean exit=false; 
    
    /**
     * Initialize a tread for check the pattern in input.
     * @param pattern
     */
    public ThreadCheckPattern(Pattern pattern)
    {
        this.pattern = pattern;
    }
    
    // for stopping the thread 
    public void termine() 
    { 
        exit = true; 
    } 
    
    /**
     * Count the number of patterns which are consistent.
     */
    public void run()
    {
        if (checkForOnePattern(this.pattern))
        {
            System.out.println("Consistent");
            Global.listCheckedPatterns.add(this.pattern);
            Global.iNumberOfConsistent++;
        }
        else
        {
            System.out.println("Inconsistent");
            Global.iNumberOfInconsistent++;
        }
        
        Global.iNumberOfThreadCompleted++;
        
        if (Global.iNumberOfThreadCompleted == Global.iNumberOfThreadRunning)
        {
            if (Global.threadCheckThread.isAlive())            
                Global.threadCheckThread.termine();
        }
    }
    
    
    /**
     * Check if the pattern in input is consistent with the knowldedge base.
     * @param pattern which needs to be check.
     * @return true if the pattern is consistent with the knowledge base.
     */
    public boolean checkForOnePattern(Pattern pattern)
    {       
        try 
    	{   
            KnowledgeBase kb_stratified = new KnowledgeBase(Global.IRI_INPUT_STRATIFIED);
            KnowledgeBase kb_full = new KnowledgeBase(Global.IRI_INPUT_FULL);
            
            if (kb_stratified.addPatternHorn(pattern))
            {
                if (kb_full.addPatternHorn(pattern))
                    return true;
                else
                    return false;
            }
            else
                return false;
            
        }
        catch (NullPointerException | java.lang.OutOfMemoryError | org.semanticweb.owlapi.reasoner.InconsistentOntologyException r)
        {
            r.printStackTrace();
            return false;
        }
    }
}
