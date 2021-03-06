/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MPattern;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import MCommon.Global;
import MKnowledge.KnowledgeBase;
import MOutput.OutputInformation;

/**
 *
 * @author tdminh
 */
public class CheckPattern 
{
    private KnowledgeBase kb;
    ArrayList<Pattern> listPatterns;
    
    /**
     * Add to the list of patterns, the patterns in the rule generated by the genetic algorithm.
     */
    public CheckPattern()
    {
        try
        {
            this.kb = new KnowledgeBase(Global.IRI_INPUT_STRATIFIED);
        
            FileReader inputFile = new FileReader("C:/Users/valer/Desktop/Internship/Data_owl/Financial_30/Financial_30_9.txt");
            
            BufferedReader brInputFile = new BufferedReader(inputFile);
            
            listPatterns = new ArrayList<Pattern>();
            
            String line = "";            
            while ((line = brInputFile.readLine()) != null)
            {
                String strPattern = line.substring(line.indexOf(".") + 1, line.lastIndexOf("&") + 1).trim();
                double dblMetric = Double.valueOf(line.substring(line.lastIndexOf("&") + 1).trim());
                Pattern pattern = new Pattern(this.kb, strPattern);
                pattern.setMetric(dblMetric);
                listPatterns.add(pattern);
                
            }
            
            brInputFile.close();  
        }
        catch (IOException e)
        {
            e.getStackTrace();
            System.out.println(e);
        }  
    }
    
    /**
     * For each pattern in the list, associate a new thread which check if the pattern is consistent. 
     */
    public void checkForListPatterns()
    {     
        Global.listCheckedPatterns = new ArrayList<Pattern>();
        
        int count = 0;
        int countGroup = 0;
        
        for(int i=0; i<this.listPatterns.size(); i++)
        {
        	System.out.println(listPatterns.get(i));
            if (Global.NUMBER_OF_THREAD < Global.MAX_THREAD)
            {                
                Global.arrThreadCheckPattern[Global.NUMBER_OF_THREAD] = null;
                Global.arrThreadCheckPattern[Global.NUMBER_OF_THREAD] = new ThreadCheckPattern(this.listPatterns.get(i));
                Global.arrThreadCheckPattern[Global.NUMBER_OF_THREAD].start();
                Global.NUMBER_OF_THREAD++;
                count++;
            }
                
            if (Global.NUMBER_OF_THREAD == Global.MAX_THREAD)
            {
                countGroup++;
                System.out.println("Group: " + countGroup);
                
                Global.threadCheckThread = null;
                Global.threadCheckThread = new ThreadCheckThread();
                Global.threadCheckThread.start();
                
                try
                {
                    Global.iNumberOfThreadCompleted = 0;
                    Global.iNumberOfThreadRunning = Global.NUMBER_OF_THREAD;
                    
                    for(int j = 0; j < Global.NUMBER_OF_THREAD; j++)                     
                        Global.arrThreadCheckPattern[j].join();
                    
                    Global.threadCheckThread.join();
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                catch(java.lang.ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }

                Global.NUMBER_OF_THREAD = 0;
                count = 0;
            }
        }
        
        if (count > 0)
        {
            countGroup++;            
            System.out.println("Group: " + countGroup);
            
            Global.threadCheckThread = null;
            Global.threadCheckThread = new ThreadCheckThread();
            Global.threadCheckThread.start();
            
            try
            {
                Global.iNumberOfThreadCompleted = 0;
                Global.iNumberOfThreadRunning = count;
                
                for(int j = 0; j < count; j++)
                    Global.arrThreadCheckPattern[j].join();                    
                
                Global.threadCheckThread.join();
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
            catch(java.lang.ArrayIndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }

            Global.NUMBER_OF_THREAD = 0;
            count = 0;
        }
        
        for(int i=0; i<Global.listCheckedPatterns.size(); i++)
        {
            OutputInformation.showPatternWithNumber(Global.listCheckedPatterns.get(i), i + 1, true);
            System.out.println(i+1);
        }
        
    }
    
    
}
