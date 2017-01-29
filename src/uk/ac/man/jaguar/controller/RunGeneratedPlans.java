/*#################################
* class:  RunGeneratedPlans.java
* author: Fernando Osorno-Gutierrez
* date:   10 Jul 2015
* #################################
**********************************/

/*
 * Copyright (C) 2014 Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.man.jaguar.controller;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.FeedbackPlanApplication;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.feedback.er.ERFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.match.MatchFeedback;
import uk.ac.man.jaguar.controller.operators.RunMapEpisode;
import uk.ac.man.jaguar.manager.controller.ECJCaller;
import uk.ac.man.jaguar.model.Episode;
import uk.ac.man.jaguar.model.FeedbackPlan;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Step;
import uk.ac.man.jaguar.util.AnnotationsCollector;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.DatabaseUtils;
import uk.ac.man.jaguar.util.ExitTrappedException;
import uk.ac.man.jaguar.util.FeedbackPlansGenerator;
import uk.ac.man.jaguar.util.LogWriter;

import java.util.ArrayList;
import java.util.Random;

import ec.Evolve;
import ec.vector.MyGene;

/**
 *
 * @author osornogf
 */
public class RunGeneratedPlans {
	
	FeedbackPlansGenerator feedbackPlansGenerator = null;
	ArrayList<FeedbackPlan> feedbackPlans = null;
	static ArrayList<Step> workflow = null;
	
	static ArrayList<String> dataList;
	
	public static void main(String args[])
	{
		LogWriter logWriter = new LogWriter();
	
		JaguarVariables.initialTime = System.currentTimeMillis();
		
		dataList = new ArrayList<String>();
		
		RunGeneratedPlans m = new RunGeneratedPlans();
		ConfigurationReader configurationReader = new ConfigurationReader();  		
		JaguarVariables.environments = configurationReader.getEnvironmentsFromFile();
		configurationReader.loadEnvironmentConfiguration(0);
		
		FeedbackPlan feedbackPlan0g;
		FeedbackPlan feedbackPlan1g;
		FeedbackPlan feedbackPlan2g;
		FeedbackPlan feedbackPlan3g;
		FeedbackPlan feedbackPlan4g;
		FeedbackPlan feedbackPlan5g;
		FeedbackPlan feedbackPlan6g;
		FeedbackPlan feedbackPlan7g;
		FeedbackPlan feedbackPlan8g;		
		FeedbackPlan feedbackPlan9g;
		
		ArrayList<FeedbackPlan> plans = new ArrayList<FeedbackPlan>();
		RunGivenPlans ma = new RunGivenPlans();
		
		ECJCaller ecjCaller = new ECJCaller();
		ArrayList<String> plansGen = ecjCaller.generateFeedbackPlans(   10,0   );
		
		feedbackPlan0g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(0));
		feedbackPlan1g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(1));
		feedbackPlan2g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(2));
		feedbackPlan3g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(3));
		feedbackPlan4g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(4));
		feedbackPlan5g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(5));
		feedbackPlan6g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(6));
		feedbackPlan7g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(7));
		feedbackPlan8g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(8));
		feedbackPlan9g=ma.parseGeneticAlgorithmFeedbackPlan(plansGen.get(9));
		
		plans.add(feedbackPlan0g);
		plans.add(feedbackPlan1g);
		plans.add(feedbackPlan2g);
		plans.add(feedbackPlan3g);
		plans.add(feedbackPlan4g);
		plans.add(feedbackPlan5g);
		plans.add(feedbackPlan6g);
		plans.add(feedbackPlan7g);
		plans.add(feedbackPlan8g);
		plans.add(feedbackPlan9g);
		
		JaguarVariables.budgetAmounts = 1;// default = 1.
		JaguarVariables.feedbackPlansNumber = 10;
		int feedbackPlanNumber=1;//Always starts at 1.
		for(int r=0; r<JaguarVariables.budgetAmounts; r++)
		{
			for(int s=0; s<JaguarVariables.feedbackPlansNumber; s++)
			{
				System.out.println("Feedback Plan "+feedbackPlanNumber);
				m.runGivenPlan(plans,s);
				dataList.add(FeedbackPlanApplication.getData(s));
				logWriter.writeToFile("TableCount.dat",FeedbackPlanApplication.getData(s));
				feedbackPlanNumber++;
			}
		}
		
		for(int r=0; r<dataList.size(); r++)
		{
			System.out.println(dataList.get(r));
		}
		
		if(JaguarConstants.ELAPSEDTIMEOUT)
		{
			long endTime   = System.currentTimeMillis();
			long totalTime = (endTime - JaguarVariables.initialTime)/1000;
			System.out.println("Run seconds time="+totalTime);
		}
	}
	
    public void runGivenPlan(ArrayList<FeedbackPlan> plans, int s)
    {
	    FeedbackPlanApplication.runFeedbackPlanApplication(plans.get(s));
    }
    
    public void runRandomPlan(int budget)
    {
    	FeedbackPlansGenerator feedbackPlansGenerator = null;
		feedbackPlansGenerator = new FeedbackPlansGenerator();	    		
		feedbackPlans = null;
		feedbackPlans = feedbackPlansGenerator.generateRandomFeedbackPlans(1, budget);
		for(int k=0; k<feedbackPlans.size(); k++)
		{
			FeedbackPlanApplication.runFeedbackPlanApplication((FeedbackPlan)feedbackPlans.get(k));
		}
    }

	public FeedbackPlan parseGeneticAlgorithmFeedbackPlan(String feedbackPlanString)
	{
		FeedbackPlan feedbackPlan = new FeedbackPlan();
		try {
		    feedbackPlanString = feedbackPlanString.replace("Plan: :", "");
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
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return feedbackPlan;
	}
}