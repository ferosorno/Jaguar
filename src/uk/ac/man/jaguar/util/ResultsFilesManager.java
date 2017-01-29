/*#################################
* class:  ResultsFilesManager.java
* author: Fernando Osorno-Gutierrez
* date:   24 Jun 2014
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.model.FeedbackPlan;

public class ResultsFilesManager {
	

	double precision[][];
	
	public ResultsFilesManager()
	{
		precision = new double[JaguarVariables.feedbackPlansNumber][JaguarVariables.budgetAmounts];
	}
	
	int row=0;
	int column=0;
	
	
	public void putResults(
    		double[] mapFeedbackPrecision,
    		double[] mapFeedbackRecall,
    		double[] mapFeedbackFMeasure,
    		FeedbackPlan feedbackPlan,
    		int annotationsCounter)
    {
		
		column = column % JaguarVariables.budgetAmounts;
		precision[row][column]=mapFeedbackPrecision[annotationsCounter];
		
		column ++;
		
		if(column == JaguarVariables.budgetAmounts)
			row ++;
    }
	
	/*
	 * This method only appends text to the variable text.
	 */
    public void saveResults(String file)
    {
    	StringBuffer text = new StringBuffer();
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
        	
            String caption ="";
            for(int i=0; i<JaguarVariables.feedbackPlansNumber; i++)
            {
            	for(int j=0; j<JaguarVariables.budgetAmounts; j++)
            	{
            		String precisionStr = (new Double(precision[i][j])).toString();
            		if(precisionStr.length()>4)
            			precisionStr = precisionStr.substring(0,4);
            		
            		text.append(i+"\t"+precisionStr);
            	}
            	text.append("\n");
            }
            
            text.append(caption);
            
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writePrecisionRecallFMeasureToPipesFile(
    		String file,
    		double[] mapFeedbackPrecision,
    		double[] mapFeedbackRecall,
    		double[] mapFeedbackFMeasure,
    		FeedbackPlan feedbackPlan,
    		int annotationsCounter)
    {
    	StringBuffer text = new StringBuffer();
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
        	
            String caption ="{";
            for(int k=0;k<feedbackPlan.getFeedbackPlanList().size(); k++)
            {
            	String episode="<";
            	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==0)
            		episode = episode + "Match";
            	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==1)
            		episode = episode + "Map";
            	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==2)
            		episode = episode + "ER";        	
            	episode = episode + "," + feedbackPlan.getFeedbackPlanList().get(k).getAmount();
            	episode = episode + ">";        	
            	caption = caption + episode;
            	if(k<feedbackPlan.getFeedbackPlanList().size()-1)
            		caption = caption + ",";
            }
            
            caption += "}";
            
            text.append(caption);
            text.append("|");
            text.append(mapFeedbackPrecision[annotationsCounter-1]);
            text.append("|");
            text.append(mapFeedbackRecall[annotationsCounter-1]);
            text.append("|");
            text.append(mapFeedbackFMeasure[annotationsCounter-1]);
            text.append("\n");
            out.print(text);
            
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
