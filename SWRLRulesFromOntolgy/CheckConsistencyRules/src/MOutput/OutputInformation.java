/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MOutput;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import MAtom.Atom;
import MAtom.ConceptAtom;
import MAtom.RoleAtom;
import MPattern.Pattern;

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
    
    public OutputInformation()
    {
    }
    
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
     * Show in the output file the pattern.
     * @param strText is the pattern (rule) to show.
     */
    public static void showPattern(Pattern pattern, boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        for(int j = 0; j < pattern.size(); j++)
        {
            Atom atom = (Atom) pattern.get(j);

            if (j == 0)
            {
                if (atom instanceof ConceptAtom)                
                    output.print(atom.getName() + "(" +  ((ConceptAtom) atom).getVariable() + ") <= ");
                else if (atom instanceof RoleAtom)                
                    output.print(atom.getName() + "(" + ((RoleAtom) atom).getDomainVariable() + ", " + ((RoleAtom) atom).getRangeVariable() + ") <= ");                
            }
            else
            {
                if (atom instanceof ConceptAtom)                
                    output.print(atom.getName() + "(" +  ((ConceptAtom) atom).getVariable() + ") & ");
                else if (atom instanceof RoleAtom)                
                    output.print(atom.getName() + "(" + ((RoleAtom) atom).getDomainVariable() + ", " + ((RoleAtom) atom).getRangeVariable() + ") & ");                
            }
        }
        output.print("\t");            
        output.println(pattern.getMetric());
    }
    
    /**
     * Show in the output file the pattern with the number associated.
     * @param strText is the pattern (rule) to show.
     */
    public static void showPatternWithNumber(Pattern pattern, int number, boolean type)
    {
        PrintStream output = OutputInformation.outputType(type);
        
        output.print(String.valueOf(number) + ". ");
        
        for(int j = 0; j < pattern.size(); j++)
        {
            Atom atom = (Atom) pattern.get(j);

            if (j == 0)
            {
                if (atom instanceof ConceptAtom)                
                    output.print(atom.getName() + "(" +  ((ConceptAtom) atom).getVariable() + ") <= ");
                else if (atom instanceof RoleAtom)                
                    output.print(atom.getName() + "(" + ((RoleAtom) atom).getDomainVariable() + ", " + ((RoleAtom) atom).getRangeVariable() + ") <= ");                
            }
            else
            {
                if (atom instanceof ConceptAtom)                
                    output.print(atom.getName() + "(" +  ((ConceptAtom) atom).getVariable() + ") & ");
                else if (atom instanceof RoleAtom)                
                    output.print(atom.getName() + "(" + ((RoleAtom) atom).getDomainVariable() + ", " + ((RoleAtom) atom).getRangeVariable() + ") & ");                
            }
        }
        output.print("\t");            
        output.println(pattern.getMetric());
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
    
}
