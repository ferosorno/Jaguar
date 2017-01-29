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
public class RunGivenPlans {
	
	FeedbackPlansGenerator feedbackPlansGenerator = null;
	ArrayList<FeedbackPlan> feedbackPlans = null;
	static ArrayList<Step> workflow = null;
	
	static ArrayList<String> dataList;
	
	public static void main(String args[])
	{
		LogWriter logWriter = new LogWriter();
		JaguarVariables.initialTime = System.currentTimeMillis();
		
		dataList = new ArrayList<String>();
		
		RunGivenPlans m = new RunGivenPlans();
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
		FeedbackPlan feedbackPlan10g;
		FeedbackPlan feedbackPlan11g;
		FeedbackPlan feedbackPlan12g;
		FeedbackPlan feedbackPlan13g;
		FeedbackPlan feedbackPlan14g;
		FeedbackPlan feedbackPlan15g;
		FeedbackPlan feedbackPlan16g;
		FeedbackPlan feedbackPlan17g;
		FeedbackPlan feedbackPlan18g;		
		FeedbackPlan feedbackPlan19g;
		
		ArrayList<FeedbackPlan> plans = new ArrayList<FeedbackPlan>();
		RunGivenPlans ma = new RunGivenPlans();
		
		//FeedbackPlan feedbackPlanTest = ma.parseGeneticAlgorithmFeedbackPlan("");
		//FeedbackPlan feedbackPlanTest = ma.parseGeneticAlgorithmFeedbackPlan("");
		//plans.add(feedbackPlanTest);
		//plans.add(feedbackPlanTest2);
		
		/**
		 * GENERATED. the input can be static or generated here.
		 */
		feedbackPlan0g=ma.parseGeneticAlgorithmFeedbackPlan("[1,64] [0,1400] [1,82] [2,65]");//[1,139] [0,6055] [1,6] [0,1541] [1,120] [2,379]
		feedbackPlan1g=ma.parseGeneticAlgorithmFeedbackPlan("[1,207] [0,3232] [1,145] [2,447]");//[0,6] [1,318] [0,7463] [1,72] [2,381]//[0,76] [1,202] [0,7457] [1,122] [2,383]
		//[1,222] [0,7386] [1,81] [0,126] [1,43] [2,382]
		feedbackPlan2g=ma.parseGeneticAlgorithmFeedbackPlan("[1,212] [0,6837] [1,231] [0,163] [2,115]");
		feedbackPlan3g=ma.parseGeneticAlgorithmFeedbackPlan("[1,228] [0,9201] [1,1] [0,44] [1,296] [2,308]");
		feedbackPlan4g=ma.parseGeneticAlgorithmFeedbackPlan("[0,6033] [1,12] [0,3840] [1,148] [0,647] [1,1578] [2,339]");
		feedbackPlan5g=ma.parseGeneticAlgorithmFeedbackPlan("[1,18] [0,5303] [1,20] [0,4193] [1,99] [0,1693] [1,2912] [2,878]");
		feedbackPlan6g=ma.parseGeneticAlgorithmFeedbackPlan("[0,9467] [1,43] [0,617] [1,7349] [2,160]");
		feedbackPlan7g=ma.parseGeneticAlgorithmFeedbackPlan("[0,9359] [1,70] [0,669] [1,9516] [2,541]");
		feedbackPlan8g=ma.parseGeneticAlgorithmFeedbackPlan("[0,3] [1,108] [0,11452] [1,539] [0,50] [1,10391] [0,2] [2,128] [0,2]");
		feedbackPlan9g=ma.parseGeneticAlgorithmFeedbackPlan("[1,77] [0,13479] [1,125] [0,1028] [1,9032] [2,1453]");
		
		feedbackPlan10g=ma.parseGeneticAlgorithmFeedbackPlan("[0,484] [1,408] [0,255] [2,866] [1,408] [2,127]");
		feedbackPlan11g=ma.parseGeneticAlgorithmFeedbackPlan("[1,306] [2,76] [0,994] [1,102] [0,102] [2,25] [0,688] [1,255]");
		feedbackPlan12g=ma.parseGeneticAlgorithmFeedbackPlan("[2,662] [1,1886]");
		feedbackPlan13g=ma.parseGeneticAlgorithmFeedbackPlan("[1,229] [2,51] [0,204] [1,612] [0,204] [1,459] [0,229] [1,229] [0,331]");
		feedbackPlan14g=ma.parseGeneticAlgorithmFeedbackPlan("[2,637] [0,688] [2,357] [1,204] [0,280] [2,382]");
		feedbackPlan15g=ma.parseGeneticAlgorithmFeedbackPlan("[0,841] [2,790] [0,178] [1,178] [2,255] [1,306]");
		feedbackPlan16g=ma.parseGeneticAlgorithmFeedbackPlan("[2,510] [0,612] [2,586] [1,637] [0,76] [1,127]");
		feedbackPlan17g=ma.parseGeneticAlgorithmFeedbackPlan("[1,153] [0,2064] [1,204] [2,127]");
		feedbackPlan18g=ma.parseGeneticAlgorithmFeedbackPlan("[1,484] [2,51] [0,357] [1,484] [2,153] [1,153] [2,866]");
		feedbackPlan19g=ma.parseGeneticAlgorithmFeedbackPlan("[1,433] [2,76] [1,102] [2,510] [0,306] [2,76] [0,51] [2,382] [1,255] [2,357]");
		
		
		
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
		
		plans.add(feedbackPlan10g);
		plans.add(feedbackPlan11g);
		plans.add(feedbackPlan12g);
		plans.add(feedbackPlan13g);
		plans.add(feedbackPlan14g);
		plans.add(feedbackPlan15g);
		plans.add(feedbackPlan16g);
		plans.add(feedbackPlan17g);
		plans.add(feedbackPlan18g);
		plans.add(feedbackPlan19g);
		
		JaguarVariables.feedbackPlansNumber = 10;
		int feedbackPlanNumber=1;
		
		for(int s=0; s<JaguarVariables.feedbackPlansNumber; s++)
		{
			System.out.println("Feedback Plan "+feedbackPlanNumber);
			m.runGivenPlan(plans, s);
			dataList.add(FeedbackPlanApplication.getData(s));
			logWriter.writeToFile("TableCount.dat",FeedbackPlanApplication.getData(s));
			feedbackPlanNumber++;
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