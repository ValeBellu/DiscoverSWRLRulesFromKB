/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MPattern.Computation;

import MPattern.Pattern;
import java.util.Comparator;

/**
 *
 * Class using to compare the fitness value of two different patterns.
 */
public class PatternComparator implements Comparator<Pattern>
{
	/**
	 * Compare the fitness value of the pattern "a" with the fitness value of the pattern "b".
	 * The fitness value is the sum between the head coverage and the PCA confidence.
	 * @param a
	 * @param b 
	 * @return 0, if the two fitness values of the patterns are equals; -1, if the fitness value of "a" is greater or equal than the fitness value of "b"; 1, otherwise.
	 * 
	 */
    public int compare(Pattern a, Pattern b) 
    {
        double aPattern = a.getPatternComputation().getFitnessValue();
        double bPattern = b.getPatternComputation().getFitnessValue();
        
        if (aPattern == bPattern)
            return 0;
        else if (aPattern >= bPattern)
            return -1;
        else 
            return 1;
    }
}