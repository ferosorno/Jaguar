/*#################################
 * class:  ECJCaller.java
* author: Fernando Osorno-Gutierrez
* date:   9 Jun 2014
* updated: 18 Feb 2015
* #################################
**********************************/

package uk.ac.man.jaguar.manager.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.RunGivenPlans;
import uk.ac.man.jaguar.model.Episode;
import uk.ac.man.jaguar.model.FeedbackPlan;
import uk.ac.man.jaguar.util.ConfigurationReader;
import ec.Evolve;
import ec.util.InitializationTools;

public class ECJCaller{
	
	public static void main(String args[])
	{
		ECJCaller ecjCaller = new ECJCaller();
		
		ArrayList<String> plans = ecjCaller.generateFeedbackPlans(10,0);
		
		for(int i=0; i<plans.size(); i++)
		{
			System.out.println(plans.get(i));
		}
		System.out.println("done!");
	}
	public static void runGeneratedFeedbackPlan(String args[])
	{
    	ConfigurationReader configurationReader = new ConfigurationReader();
    	
		for(int j=0; j<1; j++)
		{
			long startTime   = System.currentTimeMillis();
			
			ECJCaller e = new ECJCaller();
			e.call();
			FeedbackPlan fp = e.readGeneticAlgorithmFeedbackPlan();
			
			JaguarVariables.environments = configurationReader.getEnvironmentsFromFile();
			configurationReader.loadEnvironmentConfiguration(j);
			//Main.runFeedbackPlanApplication(fp);
			
			if(JaguarConstants.ELAPSEDTIMEOUT)
			{
				long endTime   = System.currentTimeMillis();
				long totalTime = (endTime - startTime)/1000;
				System.out.println(j+".Run ECJCaller. Start time="+totalTime);			
			}
		}
	}
	
	
	public ArrayList<String> generateFeedbackPlans(int numberOfPlans, int startingPosition)
	{    	
    	ArrayList<String> result = new ArrayList<String>();
		ECJCaller e = new ECJCaller();
		
    	double[] budget = e.calculateBudgetArray();
    	
		for(int j=startingPosition; j<numberOfPlans; j++)
		{
			long startTime   = System.currentTimeMillis();
			
			InitializationTools.budget = budget[j];
				
			e.call();			
			String fp = e.readStringGeneticAlgorithmFeedbackPlanAndFitness();		
			if(j<9)
				e.saveGeneratedFeedbackPlan(fp+"\n");
			else
				e.saveGeneratedFeedbackPlan(fp);
			
			System.out.println("Generated:"+fp);
			
			result.add(fp);
			
		}
		return result;
	}
	
	public void call()
	{
    	//Evolve evolve = new Evolve();
    	String args2[] = new String[2];
    	args2[0]="-file";
    	args2[1]="C:/Users/osornogf/Documents/workspace2/ecj/ec/app/feedbackplanapplication/ga.params";
    	ec.Evolve.main(args2);
	}
	
	public FeedbackPlan readGeneticAlgorithmFeedbackPlan()
	{
		FeedbackPlan feedbackPlan = new FeedbackPlan();
		BufferedReader br = null;
		try {
		    String sCurrentLine;
		    String feedbackPlanString = "";
		    br = new BufferedReader(new FileReader("out.stat"));

		    String beforeLastLine = "";
		    
		    while ((sCurrentLine = br.readLine()) != null) {

		    	beforeLastLine = feedbackPlanString;
		        feedbackPlanString = sCurrentLine;
		    }
		    
		    beforeLastLine = beforeLastLine.replace("Fitness: ", "");
		    
		    feedbackPlanString = feedbackPlanString.replace("Plan: :", "");
		    
            String logLine = feedbackPlanString+"|"+beforeLastLine;
            //saveGeneticAlgorithmResults(logLine);
            
		    boolean hasMoreEpisodes=true;
		    int endIndex=1;
		    int beginIndex=0;
		    while(hasMoreEpisodes==true)
		    {
		    	endIndex = feedbackPlanString.indexOf(']');
		    	
		    	String episode = feedbackPlanString.substring(beginIndex, endIndex+1);	
		    	
		    	feedbackPlanString = feedbackPlanString.substring(endIndex+1, feedbackPlanString.length());
		    	feedbackPlanString = feedbackPlanString.trim();
		    	
		    	if(feedbackPlanString.length()<1)
		    	{
		    		hasMoreEpisodes=false;
		    	}
		    	int type = (new Integer(episode.substring(1,2))).intValue();
		    	int amount = (new Integer(episode.substring(3,endIndex))).intValue();
		    	Episode episodeFP = new Episode(type,amount);
		    	feedbackPlan.addEpisode(episodeFP);
		    	
		    }
		    
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (br != null)br.close();
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		}
		return feedbackPlan;
	}
	
	public String readStringGeneticAlgorithmFeedbackPlanAndFitness()
	{
		
		BufferedReader br = null;
		try {
		    String sCurrentLine;
		    String feedbackPlanString = "";
		    br = new BufferedReader(new FileReader("out.stat"));

		    String beforeLastLine = "";
		    
		    while ((sCurrentLine = br.readLine()) != null) {

		    	beforeLastLine = feedbackPlanString;
		        feedbackPlanString = sCurrentLine;
		    }
		    
		    return feedbackPlanString.replace("Plan: :", "");
		    
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (br != null)br.close();
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		}
		
		return null;
	}
	
	public void saveGeneticAlgorithmResults(String line)
	{
		StringBuffer text = new StringBuffer();
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("GenPrecision.txt", true)))) {
        	
        	text.append(line);
            out.println(text);
            
        }catch (IOException e) {
            e.printStackTrace();
        }		
	}
	
	/**
	 * Saves the text in line to the file generatedplans.txt
	 * @param line
	 */
	public void saveGeneratedFeedbackPlan(String line)
	{
		StringBuffer text = new StringBuffer();
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("generatedplans.txt", true)))) {
        	
        	text.append(line);
            out.print(text);
            
        }catch (IOException e) {
            e.printStackTrace();
        }		
	}
	public double[] calculateBudgetArray()
	{
		double[] result = new double[10];
		
		for(int j=0; j<10;j++)
		{
			result[j] = 
					Math.round(
					( 
					(InitializationTools.ENVIRONMENTMATCHES*3)+
					(InitializationTools.RESULTSSIZE*3)+
					(InitializationTools.ENVIRONMENTPAIRSOFRECORDS*3)
					)
					
					*( (j+1)*(0.1) ) );			
		}
		
		
		return result;
	}
}
