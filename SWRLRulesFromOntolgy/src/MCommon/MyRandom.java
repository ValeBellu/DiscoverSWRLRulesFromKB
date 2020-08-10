/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MCommon;

import java.util.Calendar;
import java.util.Random;

/**
 *
 * Class which manages the random generation of numbers.
 */
public class MyRandom 
{
    private Random random;    
    
    /**
     * Set the current time.
     */
    public MyRandom()
    {
        Calendar cal = Calendar.getInstance();        
        random = new Random(cal.getTimeInMillis());        
    }  
    
    /**
     * Generate an random integer number from 1 to n. 
     * @param n
     * @return the number generated.
     */
    public int generateInt(int n)
    {        
        int iTempRandom = random.nextInt(n) + 1;           
        return iTempRandom;
    }
    
    /**
     * Generate an random integer number from the head to the tail. 
     * @param head
     * @param tail
     * @return the generated number.
     */
    public int generateInt(int head, int tail)
    {        
        int iTempRandom = generateInt(tail - head + 1);        
        return iTempRandom + head - 1;
    }
}
