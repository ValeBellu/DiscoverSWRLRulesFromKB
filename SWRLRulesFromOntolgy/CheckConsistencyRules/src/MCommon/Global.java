/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MCommon;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.IRI;

import MPattern.Pattern;
import MPattern.ThreadCheckPattern;
import MPattern.ThreadCheckThread;

/**
 *
 * @author tdminh
 */
public class Global 
{
    public static final String BASE_URL = "http://www.biopax.org/examples/glycolysis#";
    /**
	 * The ontology stratified to be loaded
	 */
    public static final String FILE_NAME_STRATIFIED = "file:///C:/Users/valer/Desktop/Internship/Data_owl/Financial_30.owl";
    
    /**
	 * The full ontology to be loaded
	 */
    public static final String FILE_NAME_FULL = "file:///C:/Users/valer/Desktop/Internship/Data_owl/Financial_Full.owl";
    
    /**
	 * The IRI of the stratified ontology
	 */
    public static final IRI IRI_INPUT_STRATIFIED = IRI.create(FILE_NAME_STRATIFIED);
    
    /**
	 * The IRI of the full ontology
	 */
    public static final IRI IRI_INPUT_FULL = IRI.create(FILE_NAME_FULL);
    
    /**
	 * The rules to be checked
	 */
    public static String INPUT_PATTERNS_IN_FILE = "file:///C:/Users/valer/Desktop/Internship/Data_owl/Financial_30/Financial_30_9.txt";    
    
    /**
	 * The file with the final rules
	 */
    public static String OUTPUT_PATTERNS_IN_FILE = "file:///C:/Users/valer/Desktop/Internship/Data_owl/Financial_30/Financial_30_9_consistent_";    
    
    /**
	 * The OWL resoner used
	 */
    public static final String TYPE_OF_REASONER = "Hermit";
    
    /**
	 * The maximum number of threads
	 */
    public static final int MAX_THREAD = 10;
    
    public static int NUMBER_OF_THREAD = 0;
    
    public static int THREAD_SLEEP = 0;
    
    public static int iNumberOfConsistent = 0;
    public static int iNumberOfInconsistent = 0;
    public static int iNumberOfError = 0;
    
    public static ArrayList<Pattern> listCheckedPatterns = null;
    public static ThreadCheckPattern[] arrThreadCheckPattern = new ThreadCheckPattern[Global.MAX_THREAD];
    
    public static ThreadCheckThread threadCheckThread = null;
    
    public static int iNumberOfThreadCompleted = 0;
    public static int iNumberOfThreadRunning = 0;
    
    /**
	 * @return the name of the resource described by the IRI
	 */
    public static String cutNameOfIRI(String str)
    {
        String[] parts = str.split("#");		
        return parts[1]; 
    }
}
