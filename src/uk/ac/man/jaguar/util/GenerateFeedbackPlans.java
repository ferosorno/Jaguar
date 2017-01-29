/*#################################
* class:  GenerateFeedbackPlans.java
* author: Fernando Osorno-Gutierrez
* date:   13 Jun 2015
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.util.ArrayList;

import uk.ac.man.jaguar.manager.controller.ECJCaller;
import uk.ac.man.jaguar.model.Episode;
import uk.ac.man.jaguar.model.FeedbackPlan;

public class GenerateFeedbackPlans {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ECJCaller ecjCaller = new ECJCaller();
		ArrayList<String> plansGen = ecjCaller.generateFeedbackPlans(10,0);
		
		
		
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
