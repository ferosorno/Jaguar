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

package uk.ac.man.jaguar.controller.feedback.operators;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.feedback.er.ERFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.controller.feedback.match.MatchFeedback;
import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Episode;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.ERUtil;
import uk.ac.man.jaguar.util.LogWriter;
import uk.ac.man.jaguar.util.MappingUtil;
import uk.ac.man.jaguar.util.MatchUtil;


/**
 *
 * @author osornogf
 */
public class FeedbackCollectionBroker {
	
	MappingUtil mappingUtil = null;
	FeedbackDistribution feedbackDistribution = null;
	MappingResultsSet mappingResultsSet = null;
	/**
	 * In this method we receive an Episode with an indication of an amount of feedback
	 * to collect, and we collect the feedback of the given type.
	 * @param episode
	 * @param i
	 */
    public void collectFeedback(Episode episode, Integration i)
    {
        /**
         * In this method, we are going to obtain the feedback.
         */
        collectSyntheticFeedback(episode, i);
        //System.out.println("FeedbackCollectionBroker.collectFeedback(Episode episode)");
        //System.out.println("episode.type="+episode.type+" episode.amount="+episode.amount);
    }
    
    /**
     *
     * In thks method we are gokng to simulate the collectkon ofeedbackValue feedbackValueeedback by 
     * generatkng random feedbackValueeedback. Actually, this class should serve as  a "collector", 
     * not as a generator. But here we are only going to simulate that we
     *  "collect" or "generate" the feedbackValueeedback.
     * 
     * @param episode
     * @param i
     * @return 
     */
    public void collectSyntheticFeedback(Episode episode, Integration i)
    {
        int feedbackType = episode.type;
        if(feedbackType==0)
        {
        	collectSyntheticFeedbackMatchPositives(episode, i);
        }
        if(feedbackType==1)
        {
        	collectSyntheticFeedbackMap(episode, i);
        }
        if(feedbackType==2)
        {
        	collectSyntheticFeedbackER(episode, i);
        }
    }
    
    
    /**
     * In this method we select samples of matches to obtain feedback.
     * In this version, we collect feedback on POSITIVE matches,
     * 
     * @param episode
     * @param i
     */
    public void collectSyntheticFeedbackMatchPositives(Episode episode, Integration i)
    {
    	if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(Match) Start. Elapsed Time = "+elapsedTime);            	
        }
        //Here it is where we have to select the samples of data to obtain feedback.
        //We have to store feedback only on MatchFeedback.feedbackCorrespondenceListPositives.
    	MatchUtil matchUtil = new MatchUtil();
    	ConfigurationReader configReader = new ConfigurationReader();
        ArrayList<String> matchGroundTruth = configReader.getStringsFromFile(
        		JaguarConstants.CONFIGURATION_PATH + "\\" +
        	    JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +
        	    "match-groundtruth.dat"
        		); 
               
        ArrayList<String> customFeedbackOrderArr =null;
        if(JaguarConstants.CUSTOM_MATCHFEEDBACK_ORDER)
        	customFeedbackOrderArr = configReader.getStringsFromFile(
            		JaguarConstants.CONFIGURATION_PATH + "\\" +
                    	    JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +        			
        			"match-feedback-order.dat");
        
        int feedbackProvided = 0;
        
        // We get the accumulated feedback. It is /3 because 3 = 1 vote.
        int feedbackToProvide = episode.amount/3;
        
        boolean weCanProvideFeedback=true;
        
        //counter of feedback.
        int counter=0;
        StringTokenizer matchToken = null;
        String match =null;
		String sourceTableName = null;
		String sourceAttributeName = null;
		String targetTableName = null;
		String targetAttributeName = null;        
        String sourceTableName2 = null;
		String sourceAttributeName2 = null;
		String targetTableName2 = null;
		String targetAttributeName2 = null;        
		ValueCorrespondence valueCorrespondence = null;

		//System.out.println("i.matchFeedback.positivesFound="+i.matchFeedback.positivesFound);
    	//System.out.println("i.matchFeedback.feedbackCollectedPositives="+i.matchFeedback.feedbackAmountCollectedPositives);
         
        while(weCanProvideFeedback && feedbackToProvide>0)//while I can provide feedback
        {
        	
        	//i.matchFeedback.feedbackCollectedPositives is int that indicates the position
        	//of the feedback provided so far.
            if(i.matchFeedback.feedbackAmountCollectedPositives < i.matchFeedback.positivesFound)
            {

            		for(int j=0; j<i.mappingTask.getCandidateCorrespondences().size(); j++)
                	{
            			
            			match = null;
            			matchToken = null;
                		match = customFeedbackOrderArr.get(i.matchFeedback.feedbackAmountCollectedPositives);                		
                		matchToken = new StringTokenizer(match,",");
                		
                		sourceTableName = null;
                		sourceAttributeName = null;
                		targetTableName = null;
                		targetAttributeName = null;
                		sourceTableName = matchToken.nextToken();
                		sourceAttributeName = matchToken.nextToken();
                		targetTableName = matchToken.nextToken();
                		targetAttributeName = matchToken.nextToken();
            		

            			valueCorrespondence = i.mappingTask.getCandidateCorrespondences().get(j);
            			
            	        sourceTableName2 = null;
 	                    sourceAttributeName2 = null;
 	                    targetTableName2 = null;
 	                    targetAttributeName2 = null;
 	                    
            	        sourceTableName2 = valueCorrespondence.getSourcePaths().get(0).getPathSteps().get(1);
 	                    sourceAttributeName2 = valueCorrespondence.getSourcePaths().get(0).getLastStep();
 	                    targetTableName2 = valueCorrespondence.getTargetPath().getPathSteps().get(1);
 	                    targetAttributeName2 = valueCorrespondence.getTargetPath().getLastStep();
 	                   
 	                    
 	                    if(		sourceTableName.compareTo(sourceTableName2)==0 &&
 	                    		sourceAttributeName.compareTo(sourceAttributeName2)==0 &&
 	                    		targetTableName.compareTo(targetTableName2)==0 &&
 	                    		targetAttributeName.compareTo(targetAttributeName2)==0
 	                    		)
 	                    {
 	                    	
 	                    	if(matchUtil.isInMatchGroundTruth(matchGroundTruth, sourceTableName, sourceAttributeName, targetTableName, targetAttributeName, 1))
 		                    {
 	    	                    
 		                        (i.mappingTask.getCandidateCorrespondences().get(j)).setConfidence(1);
 		                       //System.out.println("Providing match fbk="+sourceTableName+"."+sourceAttributeName+"->"+targetTableName+"."+targetAttributeName);
 		                    }
 		                    else
 		                    {
 		                    	
 		                    	(i.mappingTask.getCandidateCorrespondences().get(j)).setConfidence(0);
 		                        

 		                    }
 	                    	
 	                    	j = i.mappingTask.getCandidateCorrespondences().size();
 	                    	
 	                    }
            		}
            		
            		i.matchFeedback.feedbackAmountCollectedPositives++;
            		feedbackProvided++;


            }
            else//we have collected enough
            {
            	weCanProvideFeedback=false;
            }
            
           if(feedbackProvided==feedbackToProvide || i.matchFeedback.feedbackAmountCollectedPositives==i.matchFeedback.positivesFound)
           {
        	   weCanProvideFeedback=false;
           }
        }
        
        //System.out.println("match feedbackProvided="+feedbackProvided);
        //System.out.println("match feedbackToProvide="+feedbackToProvide);
        int feedbackNotUsed = feedbackToProvide - feedbackProvided;
        //System.out.println("match feedbackNotUsed="+feedbackNotUsed);
        
        for(int k=0; k<i.mappingTask.getCandidateCorrespondences().size(); k++)
        {
        	if(i.mappingTask.getCandidateCorrespondences().get(k).getConfidence()==0)
        	{
        		i.mappingTask.getCandidateCorrespondences().remove(k);        		
        	}
        }
        
        //System.out.println("New Size i.mappingTask.getCandidateCorrespondences()="+i.mappingTask.getCandidateCorrespondences().size());
        //System.out.println("i.matchFeedback.feedbackCollectedPositives="+i.matchFeedback.feedbackAmountCollectedPositives);
        
        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt","CollectFeedback(Match)");
        }
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(Match) End. Elapsed Time = "+elapsedTime);            	
        }
    }
    
    
    
    public void collectSyntheticFeedbackMatchPositives2(Episode episode, Integration i)
    {

        //Here it is where we have to select the samples of data to obtain feedback.
        //We have to store feedback only on MatchFeedback.feedbackCorrespondenceListPositives.
    	MatchUtil matchUtil = new MatchUtil();
    	ConfigurationReader configReader = new ConfigurationReader();
        ArrayList<String> matchGroundTruth = configReader.getStringsFromFile(
        		JaguarConstants.CONFIGURATION_PATH + "\\" +
        	    JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +
        	    "match-groundtruth.dat"
        		); 
               
        ArrayList<String> customFeedbackOrderArr =null;
        if(JaguarConstants.CUSTOM_MATCHFEEDBACK_ORDER)
        	customFeedbackOrderArr = configReader.getStringsFromFile(
            		JaguarConstants.CONFIGURATION_PATH + "\\" +
                    	    JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +        			
        			"match-feedback-order.dat");
        
        //System.out.println();
        
        int feedbackProvided = 0;
        
        // We get the accumulated feedback. It is /3 because 3 = 1 vote.
        int feedbackToProvide = episode.amount/3;
        //int feedbackToProvide = ( (episode.amount + i.accumulatedMatchFeedback) / 3);
        //i.accumulatedMatchFeedback = ( episode.amount + i.accumulatedMatchFeedback ) % 3;
        
        //System.out.println("before feedbackToProvide="+feedbackToProvide);
        
        //System.out.println("limit="+limit);
        
        boolean provideFeedback=true;
        
        //counter of feedback.
        int counter=0;
        StringTokenizer matchToken = null;
        String match =null;
		String sourceTableName = null;
		String sourceAttributeName = null;
		String targetTableName = null;
		String targetAttributeName = null;        
        String sourceTableName2 = null;
		String sourceAttributeName2 = null;
		String targetTableName2 = null;
		String targetAttributeName2 = null;        
		ValueCorrespondence valueCorrespondence = null;

		//System.out.println("i.matchFeedback.positivesFound="+i.matchFeedback.positivesFound);
    	//System.out.println("i.matchFeedback.feedbackCollectedPositives="+i.matchFeedback.feedbackAmountCollectedPositives);
         
        while(provideFeedback && feedbackToProvide>0)//while I can provide feedback
        {
        	
        	//i.matchFeedback.feedbackCollectedPositives is int that indicates the position
        	//of the feedback provided so far.
            if(i.matchFeedback.feedbackAmountCollectedPositives < i.matchFeedback.positivesFound)
            {
            	if(JaguarConstants.CUSTOM_MATCHFEEDBACK_ORDER)//custom order from file
            	{
            		
            		for(int j=0; j<i.mappingTask.getCandidateCorrespondences().size(); j++)
                	{	
            			
            			match = null;
            			matchToken = null;
                		match = customFeedbackOrderArr.get(i.matchFeedback.feedbackAmountCollectedPositives);                		
                		matchToken = new StringTokenizer(match,",");
                		
                		sourceTableName = null;
                		sourceAttributeName = null;
                		targetTableName = null;
                		targetAttributeName = null;
                		sourceTableName = matchToken.nextToken();
                		sourceAttributeName = matchToken.nextToken();
                		targetTableName = matchToken.nextToken();
                		targetAttributeName = matchToken.nextToken();
                		
            			//valueCorrespondence = null;
            			valueCorrespondence = i.mappingTask.getCandidateCorrespondences().get(j);
            			
            	        sourceTableName2 = null;
 	                    sourceAttributeName2 = null;
 	                    targetTableName2 = null;
 	                    targetAttributeName2 = null;
 	                    
            	        sourceTableName2 = valueCorrespondence.getSourcePaths().get(0).getPathSteps().get(1);
 	                    sourceAttributeName2 = valueCorrespondence.getSourcePaths().get(0).getLastStep();
 	                    targetTableName2 = valueCorrespondence.getTargetPath().getPathSteps().get(1);
 	                    targetAttributeName2 = valueCorrespondence.getTargetPath().getLastStep();
 	                   
 	                    //System.out.println(sourceTableName+"."+sourceAttributeName+","+targetTableName+"."+targetAttributeName);
 	                    //System.out.println(sourceTableName2+"."+sourceAttributeName2+","+targetTableName2+"."+targetAttributeName2);
 	                    //System.out.println();
 	                    
 	                    if(		sourceTableName.compareTo(sourceTableName2)==0 &&
 	                    		sourceAttributeName.compareTo(sourceAttributeName2)==0 &&
 	                    		targetTableName.compareTo(targetTableName2)==0 &&
 	                    		targetAttributeName.compareTo(targetAttributeName2)==0
 	                    		)
 	                    {
 	                    	
 	                    	if(matchUtil.isInMatchGroundTruth(matchGroundTruth, sourceTableName, sourceAttributeName, targetTableName, targetAttributeName, 1))
 		                    {
 	    	                    //System.out.println(sourceTableName+"."+sourceAttributeName+","+targetTableName+"."+targetAttributeName);
 	    	                    //System.out.println(sourceTableName2+"."+sourceAttributeName2+","+targetTableName2+"."+targetAttributeName2);
 	    	                    //System.out.println();
 		                        
 		                        (i.mappingTask.getCandidateCorrespondences().get(j)).setConfidence(1);
 		                        //System.out.println("Updating confidence 1");
 		                    }
 		                    else
 		                    {
 		                    	
 		                    	(i.mappingTask.getCandidateCorrespondences().get(j)).setConfidence(0);
 		                        
 		                        //System.out.println("Updating confidence 0");
 		                    }
 	                    	
 	                    	j = i.mappingTask.getCandidateCorrespondences().size();
 	                    }
            		}
            		
            		i.matchFeedback.feedbackAmountCollectedPositives++;
            		feedbackProvided++;
	                //System.out.println("Feedback cycle");
            		
            	}
            	else//normal automatic order
            	{
            		valueCorrespondence = null;
                    valueCorrespondence = 
                        i.matchFeedback.feedbackCorrespondenceListPositives.get(i.matchFeedback.feedbackAmountCollectedPositives);
                    
                    sourceTableName = null;
                    sourceAttributeName = null;
                    targetTableName = null;
                    targetAttributeName = null;
                    sourceTableName = valueCorrespondence.getSourcePaths().get(0).getPathSteps().get(1);
                    sourceAttributeName = valueCorrespondence.getSourcePaths().get(0).getLastStep();
                    targetTableName = valueCorrespondence.getTargetPath().getPathSteps().get(1);
                    targetAttributeName = valueCorrespondence.getTargetPath().getLastStep();
                    
                    //System.out.println((i.matchFeedback.feedbackCorrespondenceListPositives.get(i.matchFeedback.feedbackCollectedPositives)).toString());
                    if(matchUtil.isInMatchGroundTruth(matchGroundTruth, sourceTableName, sourceAttributeName, targetTableName, targetAttributeName, 1))
                    {
                        //valueCorrespondence.setConfidence(1.0d);                        
                    	//System.out.println("Updating confidence 1");
                        //System.out.println(sourceTableName+"."+sourceAttributeName+" --> "+targetTableName+"."+targetAttributeName);	                        
                        (i.matchFeedback.feedbackCorrespondenceListPositives.get(i.matchFeedback.feedbackAmountCollectedPositives)).setConfidence(1);                        
                    }
                    else
                    {
                    	//System.out.println("Updating confidence 0");
                        //System.out.println(sourceTableName+"."+sourceAttributeName+" --> "+targetTableName+"."+targetAttributeName);
                        //valueCorrespondence.setConfidence(0.0d);
                        (i.matchFeedback.feedbackCorrespondenceListPositives.get(i.matchFeedback.feedbackAmountCollectedPositives)).setConfidence(0);
                        //we just change the confidence, but don't move any correspondence of stack
                        //I don't do truePositivesCount++ because it is already in the true positives
                    }
                    
                    //We return the correspondence to the feedback list
                    //i.matchFeedback.feedbackCorrespondenceListPositives.set(i.matchFeedback.feedbackCollectedPositives, valueCorrespondence);
                    
                    i.matchFeedback.feedbackAmountCollectedPositives++;
                    feedbackProvided++;
            	}
            }
            else//we have collected enough
            {
            	//System.out.println("feedbackCollectedPositives="+i.matchFeedback.feedbackCollectedPositives +" positivesFound="+ i.matchFeedback.positivesFound);
            	provideFeedback=false;
            }
            
           //System.out.println("feedbackToProvide="+feedbackToProvide+" feedbackProvided="+feedbackProvided);
           if(feedbackProvided==feedbackToProvide || i.matchFeedback.feedbackAmountCollectedPositives==i.matchFeedback.positivesFound)
           {
        	   provideFeedback=false;
           }
        }
        
        
        for(int k=0; k<i.mappingTask.getCandidateCorrespondences().size(); k++)
        {
        	if(i.mappingTask.getCandidateCorrespondences().get(k).getConfidence()==0)
        	{
        		i.mappingTask.getCandidateCorrespondences().remove(k);
        		k--;
        	}
        }
        
        
        //System.out.println("New Size i.mappingTask.getCandidateCorrespondences()="+i.mappingTask.getCandidateCorrespondences().size());
        //System.out.println("i.matchFeedback.feedbackCollectedPositives="+i.matchFeedback.feedbackAmountCollectedPositives);
        
        
        
        ///System.out.println("after totalFeedbackProvided="+totalFeedbackProvided);
        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt","CollectFeedback(Match)");
        	
        }
        
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(Match). Elapsed Time = "+elapsedTime);            	
        }
    
    }
    
    
    /**
     * In this method we select samples of matches to obtain feedback.
     * In this version, we collect feedback on POSITIVE AND NEGATIVE matches.
     * 
     * @param episode
     * @param i
     */
    public void collectSyntheticFeedbackMatchPositivesAndNegatives(Episode episode, Integration i)
    {

        //Here it is where we have to select the samples of data to obtain feedback.
        //We have to store feedback only on MatchFeedback.feedbackCorrespondenceListPositives
    	MatchUtil matchUtil = new MatchUtil();
    	ConfigurationReader configReader = new ConfigurationReader();
        ArrayList<String> matchGroundTruth = configReader.getStringsFromFile(
        		JaguarConstants.CONFIGURATION_PATH + "\\" +
                	    JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +
                	    "match-groundtruth.dat"        		
        		); 
        
        int limit = i.matchFeedback.positivesFound + i.matchFeedback.negativesFound;
        
        //int totalFeedbackProvided = MatchFeedback.feedbackCollectedPositives +
        //        MatchFeedback.feedbackCollectedNegatives;
        
        int totalFeedbackProvided = 0;
        
        int feedbackToProvide = episode.amount;
        
        //System.out.println("before totalFeedbackProvided="+totalFeedbackProvided);
                
        //System.out.println("limit="+limit);
        
        boolean hasProvidedAllFeedback=false;
        
        int counter=0;
        
        while(hasProvidedAllFeedback==false)
        {
        	//System.out.println("feedbackToProvide="+feedbackToProvide);
        	
            boolean providedFeedback=false;
            if(totalFeedbackProvided<limit)
                if(feedbackToProvide%2==0)
                {
                    if(i.matchFeedback.feedbackAmountCollectedPositives < i.matchFeedback.positivesFound)
                    {
                        ValueCorrespondence valueCorrespondence = 
                            i.matchFeedback.feedbackCorrespondenceListPositives.get(i.matchFeedback.feedbackAmountCollectedPositives);                        
                        String sourceTableName = valueCorrespondence.getSourcePaths().get(0).getPathSteps().get(1);
                        String sourceAttributeName = valueCorrespondence.getSourcePaths().get(0).getLastStep();
                        String targetTableName = valueCorrespondence.getTargetPath().getPathSteps().get(1);
                        String targetAttributeName = valueCorrespondence.getTargetPath().getLastStep();                            

                        if(matchUtil.isInMatchGroundTruth(matchGroundTruth, sourceTableName, sourceAttributeName, targetTableName, targetAttributeName, 1))
                        {
                            valueCorrespondence.setConfidence(1.0d);
                        }
                        else
                        {
                            valueCorrespondence.setConfidence(0.0d);
                            //we just change the confidence, but dont move any correspondence of stack
                            //I don't do truePositivesCount++ because it is already in the true positives
                        }
                        //We return the correspondence to the feedback list
                        i.matchFeedback.feedbackCorrespondenceListPositives.set(i.matchFeedback.feedbackAmountCollectedPositives, valueCorrespondence);
                        i.matchFeedback.feedbackAmountCollectedPositives++;
                        
                        totalFeedbackProvided++;
                        providedFeedback=true;
                    }
                }
                else
                {
                    if(i.matchFeedback.feedbackCollectedNegatives<i.matchFeedback.negativesFound)
                    {
                        ValueCorrespondence valueCorrespondence = 
                            i.matchFeedback.feedbackCorrespondenceListNegatives.get(i.matchFeedback.feedbackCollectedNegatives);
                        
                        String sourceTableName = valueCorrespondence.getSourcePaths().get(0).getPathSteps().get(1);
                        String sourceAttributeName = valueCorrespondence.getSourcePaths().get(0).getLastStep();
                        String targetTableName = valueCorrespondence.getTargetPath().getPathSteps().get(1);
                        String targetAttributeName = valueCorrespondence.getTargetPath().getLastStep(); 
                        
                        if(matchUtil.isInMatchGroundTruth(matchGroundTruth, sourceTableName, sourceAttributeName, targetTableName, targetAttributeName, 1))
                        {
                            valueCorrespondence.setConfidence(1.0d);                            
                        }
                        else
                        {
                            valueCorrespondence.setConfidence(0.0d);
                            i.matchFeedback.updateConfidenceIn("tn",valueCorrespondence,0d,i.matchFeedback);                         
                        }
                        
                        i.matchFeedback.feedbackCorrespondenceListNegatives.set(i.matchFeedback.feedbackCollectedNegatives, valueCorrespondence);
                        i.matchFeedback.feedbackCollectedNegatives++;
                        
                        totalFeedbackProvided++; 
                        providedFeedback=true;
                    }
            }

           counter++;
           
           if(feedbackToProvide==totalFeedbackProvided)
           {
        	   hasProvidedAllFeedback = true;
           }
        }

        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt","CollectFeedback(Match)");        	
        }
        
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(Match). Elapsed Time = "+elapsedTime);            	
        }

    }
    /**
     * This method should be an interface to obtain feedback.
     * Currently, this method generates synthetic feedback and uses the feedback
     * to annotate the records.
     * @param episode
     * @param i
     */
    public void collectSyntheticFeedbackMap(Episode episode, Integration i)
    {
    	if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(Map) Start. Elapsed Time = "+elapsedTime);            	
        }
    	mappingUtil = new MappingUtil();
    	
    	LogWriter logWriterStat = new LogWriter();
    	logWriterStat.writeToFile("FeedbackCounting.txt", "Episode: ["+String.valueOf(episode.getType())+","+String.valueOf(episode.getAmount())+"]");
    	
    	/**
    	 * Obtains the mappings' results with feedback.
    	 */
        ArrayList<MappingResultsSet> mapFeedbackSetList = i.mapFeedback.getMappingResultsSetList();
		
		ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesTakenOnBoard;
        
		feedbackDistribution = null;
        feedbackDistribution = new FeedbackDistribution();
        
        int k;
        
        String localFeedbackValue="";
        
        /*Mechanism to save accumulated feedback.*/
        int feedbackAmount = episode.getAmount() / 3;
        //int feedbackAmount = ( (episode.getAmount() + i.accumulatedMapFeedback) / 3);//3votes = 1 decision
        //i.accumulatedMapFeedback = ( episode.getAmount() + i.accumulatedMapFeedback ) % 3;    //for the remaining    
        
        logWriterStat.writeToFile("FeedbackCounting.txt", "Map Feedback Amount: "+String.valueOf(feedbackAmount));
        k = 0;//k is used to iterate over the mappings.
        
        int numOfMappings = mapFeedbackSetList.size();
        int numOfMappingsComplete = 0;
        //System.out.println("numOfMappings="+numOfMappings);
        //System.out.println("feedbackAmount before="+feedbackAmount);
       
        int recCounts=0;
        for(int r=0; r<numOfMappings; r++)
        {
        	mappingResultsSet = null;
        	
        	mappingResultsSet = mapFeedbackSetList.get(r);
        	recCounts += mappingResultsSet.countRecordsNoFeedback();
        }
        
        logWriterStat.writeToFile("FeedbackCounting.txt", "Records: "+String.valueOf(recCounts));
        
        //System.out.println("Initial Records count="+recCounts);
        
        while(feedbackAmount>0)
        {
        	mappingResultsSet = null;
        	//System.out.println("k="+k);
        	mappingResultsSet = mapFeedbackSetList.get(k);
        	
        	if(mappingResultsSet.isFeedbackComplete()==false)
        	{
        		Record record = mappingResultsSet.getAnyRecordWithoutFeedback();
        		
        		if(record!=null)
        		{
        			localFeedbackValue = mappingUtil.getGroundTruthValue(mappingResultsSet.getAttributesNames(
        					record.getEntityNumber()), 
        					record.getAttributesValues(),
        					tablesNames.get(record.getEntityNumber())
        					);
        			
            		if(localFeedbackValue.compareTo("fp")==0)
            			localFeedbackValue = "tn";//We assume that feedback will confirm incorrect, so it is TN (because feedback confirmed it as incorrect,
            		//so we know and are sure it is incorrect (true neg).
            		
            		JaguarVariables.countMapAnnotations++;
            		
            		/*System.out.print("Map Feedback:");
            		for(int j=0; j<record.getAttributesValues().size(); j++)
            		{
            			System.out.print("["+record.getAttributesValues().get(j)+"],");
            		}
            		System.out.println();*/
            		
            		record.setFeedbackValue(localFeedbackValue);
            		record.setHasMapFeedback(true);
            		//makes sum +1 to count the feedback given.
            		mappingResultsSet.setFeedbackObtainedForThisMapPlus1();
            		//It propagates feedback to other records in the same result set.
            		feedbackDistribution.propagateFbToRecords(1,localFeedbackValue,record,mapFeedbackSetList,i);
            		feedbackAmount--;
            		//stores the feedback for future use.
            		i.mapFeedback.saveFeedbackToTableFeedbackSet(record);
            		//System.out.println("Giving Map Feedback:" + record.getAttributesValues().toString());
        		}
        		else
        		{
        			System.out.println("Record null");
        		}
        	}
        	else
        	{
        		//System.out.println("Feedback COMPLETE");
        		numOfMappingsComplete++;
        	}
        	
        	//for(l=0; l<fbkPerMapping[k]; l++)
        	//{
        		//Record record = mapFeedbackSet.getRecords().get(j);
        		//Record record = mapFeedbackSet.getRecordAt(pos+l);
        	//}
        	//System.out.println("Mapping:"+k);
        	//System.out.println("Feedback Amount:"+feedbackAmount);
        	k++;
        	
        	k = k % numOfMappings;
        	//System.out.println("Number of mappings feedback complete:"+numOfMappingsComplete);
        	if(numOfMappingsComplete==numOfMappings)
        	{
        		//System.out.println("numOfMappingsComplete==numOfMappings");
        		break;
        	}
        	
        	if(k==0)  //Every time k iterates, numOfMappingsComplete is set to 0 to count again.
        	{
        		numOfMappingsComplete=0;
        	}
        	
            //mapFeedbackSet.setFeedbackObtainedForThisMap(pos + l);
            //mapFeedbackSetList.set(k, mapFeedbackSet);
            //i.mapFeedback.setMapFeedbackSet(mapFeedbackSet);
        	//System.out.println("In map feedback while:"+feedbackAmount);
        }
        
        logWriterStat.writeToFile("FeedbackCounting.txt", "Map Feedback Amount After: "+String.valueOf(feedbackAmount));
        recCounts=0;
        for(int r=0; r<numOfMappings; r++)
        {
        	mappingResultsSet = mapFeedbackSetList.get(r);
        	recCounts += mappingResultsSet.countRecordsNoFeedback();
        }
        
        logWriterStat.writeToFile("FeedbackCounting.txt", "Records: "+String.valueOf(recCounts));        
        
        //System.out.println("remaining feedbackAmount after="+feedbackAmount);
        /*System.out.println("Total Map Feedback="+JaguarVariables.countMapAnnotations);
        System.out.println("Total Feedback propagated countMapFbckPropagated="+JaguarVariables.countMapFbckPropagated);*/
        
        if(JaguarConstants.LOGGING)
        {
        	LogWriter logWriter = new LogWriter();
        	logWriter.writeToFile("Log.txt","CollectFeedback(Map)");
        }
        
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(Map) End. Elapsed Time = "+elapsedTime);            	
        }
    }
    /**
     * Collect the feedback on ER.
     * Put the feedback in the current feedbackPairList.
     * 
     * @param episode
     * @param i
     */
	public void collectSyntheticFeedbackER(Episode episode, Integration i)
    {
		if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(ER) Start. Elapsed Time = "+elapsedTime);            	
        }
        ///Here I would have to obtain a sample of the existing integration objects

		//System.out.println("episode.amount="+episode.amount);
		ERUtil erUtil = new ERUtil();
        int feedbackAmount = episode.getAmount()/3;

        
        int feedbackProvided = 0;
        for(int k=0; k<feedbackAmount ; k++)//We still have pairs where to give feedback.
        {
            //System.out.println("Obtaining feedback for ER");
            //Vector<ERFeedbackInstance> erFeedbackVector = new Vector(ERFeedback.erFeedback.erFeedbackHashTable.values());
        	//long seed = System.nanoTime();
    		//Collections.shuffle((ArrayList)(i.erFeedback.duplicatePairList),new Random(seed));
        	
        	ArrayList<DuplicatePair> duplicatePairList = i.erFeedback.duplicatePairList;
            
            
            for(int r=0;r<duplicatePairList.size();r++)
            {
            	//System.out.println("");
                DuplicatePair duplicatePair = (DuplicatePair)duplicatePairList.get(r); 
                
                if(duplicatePair.isHasFeedback()==false )
                {
                    //I am taking only the true positives from the initial value.
                    //But I think this is too complicated, I should better only take the positives in a separate variable.
                    
                    //if(!duplicatePair.getInitialAnnotationValue()) //this is the initial value found by the jaccard similarity
                	if(duplicatePair.getSimilarity() >= JaguarConstants.K_ENTITYRESOLUTION)
                	{
                        //And then we just simulate that we give correct feedback.
                        duplicatePair.setFeedbackValue(true);
                    }
                    else
                    {
                        duplicatePair.setFeedbackValue(false);
                    }
                	
                    duplicatePair.setHasFeedback(true);
                    feedbackProvided++;
                    
                    // If it doesn't have feedback, for sure that it doesn't exist in the
                    // feedbackPair list. Then I don't need to check if it exists, just add it.
                    
                    erUtil.addPairToFeedbackPairList(i,duplicatePair);
                    //break the cycle.
                    break;
                }
            }
        }
        //i.erFeedback.feedbackCollected = i.erFeedback.feedbackCollected+feedbackProvided;        
        //System.out.println("er feedbackAmount="+feedbackAmount);
        //System.out.println("er feedbackProvided="+feedbackProvided);
        //int remainingFeddback=feedbackAmount-feedbackProvided;
        //System.out.println("er remainingFeddback="+remainingFeddback);
        
        if(JaguarConstants.LOGGING)
        {
        	LogWriter logWriter = new LogWriter();
        	logWriter.writeToFile("Log.txt","CollectFeedback(ER)");
        }
        
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tCollectFeedback(ER) End. Elapsed Time = "+elapsedTime);
        }
    }
}
