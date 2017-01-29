/*#################################
* class:  RunExperimentV1.java
* author: Fernando Osorno-Gutierrez
* date:   10 Jun 2014
* #################################
**********************************/

package uk.ac.man.jaguar.manager.controller;

import java.util.ArrayList;

import uk.ac.man.jaguar.controller.RunGivenPlans;
import uk.ac.man.jaguar.model.FeedbackPlan;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.FeedbackPlansGenerator;

public class RunExperimentV1 {

	public static void main(String[] args) {
		
		
		ArrayList<String> environments = new ArrayList<>();
		RunGivenPlans runFeedbackPlanApplication = new RunGivenPlans();
		
    	ConfigurationReader configurationReader = new ConfigurationReader();
    	environments = configurationReader.getStringsFromFile("environments.dat"); 
    	ConfigurationReader.environment = environments.get(0);
    	//double[] P = {0.05d,0.1d,0.15d,0.2d,0.25d};
    	
    	for(int e=0; e<20; e++)
    	{
    		
    		//int budget = 4631+80;//Budget to annotaet all.
    		
    		//for(int p=0;p<P.length;p++)
    		/*for(int p=0;p<1;p++)
    		{
    			
				FeedbackPlan feedbackPlan = new FeedbackPlan();
				
				int budgetPortion = (int) P[p]*budget;
				
	    		FeedbackPlansGenerator feedbackPlansGenerator = new FeedbackPlansGenerator();
	    		ArrayList<FeedbackPlan> feedbackPlans= feedbackPlansGenerator.generateRandomFeedbackPlans(1, budgetPortion);
	    		
	    		for(int k=0; k<feedbackPlans.size(); k++)
	    		{
	    			runFeedbackPlanApplication.runFeedbackPlanApplication((FeedbackPlan)feedbackPlans.get(k));
	    		}
		    	
    		}*/
		    
		    ECJCaller ecjCaller = new ECJCaller();//Genetic Algorithm Feedback Plan
	        ecjCaller.call();
		    FeedbackPlan geneticAlgorithmFeedbackPlan = ecjCaller.readGeneticAlgorithmFeedbackPlan();
		    
		    //runFeedbackPlanApplication.runFeedbackPlanApplication(geneticAlgorithmFeedbackPlan);
		    
    	}
    	
	}
	
}


