/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MPattern;

import java.util.logging.Level;
import java.util.logging.Logger;

import MCommon.Global;

/**
 *
 * @author tdminh
 */
public class ThreadCheckThread extends Thread
{
	 private boolean exit=false; 
	
	 public void termine() 
	    { 
	        exit = true; 
	    } 
	
    public void run()
    {
    	while (!exit) {
    		try {
    			Thread.sleep(Global.THREAD_SLEEP);
    		} catch (InterruptedException ex) {
    			Logger.getLogger(ThreadCheckThread.class.getName()).log(Level.SEVERE, null, ex);
    		}

    		if (Global.THREAD_SLEEP > 0)
    		{        
    			for(int i=0; i<Global.MAX_THREAD;i++)
    			{
    				if (Global.arrThreadCheckPattern[i].isAlive())
    				{
    					System.out.println("Stopped");
    					Global.arrThreadCheckPattern[i].termine();
    					Global.iNumberOfError++;
    				}
    				else
    					System.out.println("Finished");
    			}
    		}
    	}
    }
}
