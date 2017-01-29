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
import uk.ac.man.jaguar.model.FeedbackPlan;
import uk.ac.man.jaguar.model.Step;
import uk.ac.man.jaguar.util.CalculateBudgetAmounts;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.FeedbackPlansGenerator;
import uk.ac.man.jaguar.util.LogWriter;
import java.util.ArrayList;

/**
 *
 * @author osornogf
 */
public class RunRandomPlans {
	
	FeedbackPlansGenerator feedbackPlansGenerator = null;
	ArrayList<FeedbackPlan> feedbackPlans = null;
	static ArrayList<Step> workflow = null;
	
	static ArrayList<String> dataList;
	
	public static void main(String args[])
	{
		LogWriter logWriter = new LogWriter();
		
		int b[];
		CalculateBudgetAmounts calculateBudget = new CalculateBudgetAmounts();
		b = calculateBudget.getBudget();
		
		for(int j=0; j<10;j++)
			System.out.print(b[j]+",");
		System.out.println();
		
		JaguarVariables.initialTime = System.currentTimeMillis();
		
		dataList = new ArrayList<String>();
		
		RunRandomPlans m = new RunRandomPlans();
		ConfigurationReader configurationReader = new ConfigurationReader();
		JaguarVariables.environments = configurationReader.getEnvironmentsFromFile();
		configurationReader.loadEnvironmentConfiguration(0);
		
		JaguarVariables.budgetAmounts = 10;
		JaguarVariables.feedbackPlansNumber = 5;
		
		int feedbackPlanNumber=1;//constant
		for(int r=0; r<JaguarVariables.budgetAmounts; r++)
		{
			for(int s=0; s<JaguarVariables.feedbackPlansNumber; s++)
			{
				System.out.println("Feedback Plan "+feedbackPlanNumber);
				m.runRandomPlan(b[r]);
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
}