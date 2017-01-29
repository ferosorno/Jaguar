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
package test.ac.man.jaguar.controller.operators;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;

import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.artefacts.MatchManipulation;
import uk.ac.man.jaguar.controller.artefacts.match.LevenshteinDistanceStrategy;
import uk.ac.man.jaguar.controller.feedback.match.MatchFeedback;
import uk.ac.man.jaguar.controller.operators.IRunEpisode;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.LogWriter;
import uk.ac.man.jaguar.util.MatchUtil;

/**
 *
 * @author osornogf
 */
public class RunMatchEpisodeTest  implements IRunEpisode{

    
    /**
     * In this method we have to create the matchesManipulation 
     * Spicy uses the term correspondences to refer to matchesManipulation.
     * We have to create a data structure with the matches. However, if we have 
     * existing feedback, the structure should take on board that feedback.
     * 
     * In theory, this is just creation.
     * 
     * @param i
     */
    @Override
    public void runEpisode(Integration i)//I assume that I have a mapping task created, but without correspondences
    {
        MatchUtil matchUtil = new MatchUtil();
        //matchUtil.resetMatches(i);
        ConfigurationReader configReader = new ConfigurationReader();
        
        ArrayList<String> matchGroundTruth = configReader.getStringsFromFile(
   			 JaguarConstants.CONFIGURATION_PATH + "//" + 
   					 JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +   					         		
        		"match-groundtruth.dat");
        
        
        //I need to have a mapping task to create the matchings.
        i.loadMappingTask();
        
        
        
        ArrayList<ValueCorrespondence> valueCoArrTP = new ArrayList<>();
        ArrayList<ValueCorrespondence> valueCoArrFP = new ArrayList<>();
        //if(JaguarVariables.matchHasRan == false)
       // {
	        try{
	        	//JaguarVariables.matchHasRan = true;
	            //search path expression
	            GeneratePathExpression genPathExp=new GeneratePathExpression();
	            int numberOfTablesSource=i.mappingTask.getSourceProxy().getSchema().getChildren().size();//number of tables
	            //String sourceName = i.mappingTask.getSourceProxy().getSchema().getLabel();
	            for(int numberOfTablesSourceIndex=0;numberOfTablesSourceIndex<numberOfTablesSource;numberOfTablesSourceIndex++)//for number of tables in source
	            {
	                int numberOfAttrsSource = i.mappingTask.getSourceProxy().getSchema().getChild(numberOfTablesSourceIndex).getChild(0).getChildren().size();
	                String sourceTableName = i.mappingTask.getSourceProxy().getSchema().getChild(numberOfTablesSourceIndex).getLabel();                                        
	                for(int numberOfAttrsSourceIndex=0;numberOfAttrsSourceIndex<numberOfAttrsSource;numberOfAttrsSourceIndex++)
	                {
	                	
	                    int numberOfTablesTarget = i.mappingTask.getTargetProxy().getSchema().getChildren().size(); 
	                    //String targetName = i.mappingTask.getTargetProxy().getSchema().getLabel();
	                    for(int numberOfTablesTargetIndex=0;numberOfTablesTargetIndex<numberOfTablesTarget;numberOfTablesTargetIndex++)
	                    {
	                        int numberOfAttrsTarget = i.mappingTask.getTargetProxy().getSchema().getChild(numberOfTablesTargetIndex).getChild(0).getChildren().size();
	                        String targetTableName = i.mappingTask.getTargetProxy().getSchema().getChild(numberOfTablesTargetIndex).getLabel();
	                        for(int numberOfAttrsTargetIndex=0; numberOfAttrsTargetIndex<numberOfAttrsTarget; numberOfAttrsTargetIndex++)
	                        {
	                            PathExpression sourcePath = (genPathExp.generatePathFromRoot(i.mappingTask.getSourceProxy().getSchema().getChild(numberOfTablesSourceIndex).getChild(0).getChild(numberOfAttrsSourceIndex)));
	                            if(JaguarConstants.SYSTEMOUT)
	                            {
		                            System.out.println("value1="+i.mappingTask.getSourceProxy().getSchema().getChild(numberOfTablesSourceIndex).getLabel());
		                            System.out.println("value2="+i.mappingTask.getSourceProxy().getSchema().getChild(numberOfTablesSourceIndex).getChild(0).getChild(numberOfAttrsSourceIndex).getLabel());	                            	
	                            }
	                            PathExpression targetPath = (genPathExp.generatePathFromRoot(i.mappingTask.getTargetProxy().getSchema().getChild(numberOfTablesTargetIndex).getChild(0).getChild(numberOfAttrsTargetIndex)));
	                            
	                            String str1 = sourcePath.getLastStep();
	                            String str2 = targetPath.getLastStep();
	                            ValueCorrespondence valueCo = new ValueCorrespondence(sourcePath, targetPath);
	                            
	                            //saca el table name
	                            //ArrayList<PathExpression> pa =(ArrayList) valueCo.getSourceProxyPaths();
	                            double confidence = LevenshteinDistanceStrategy.computeLevenshteinDistance(str1, str2);
	                            
	                            valueCo.setConfidence(confidence);
	                            
	                            double threshold = JaguarVariables.matchThreshold;
	                           
	                            //These represent the Positives identified by the Edit Distance. 
	                            if(str1.compareTo("id") !=0 && str2.compareTo("id") !=0	                            		
	                            		&& str1.startsWith("id") == false
	                            		&& str2.startsWith("id") == false
	                            		)
	                            {
	                            	if(JaguarConstants.SYSTEMOUT)
	                            	{
	                                	System.out.println("Match: ["+str1+"],["+str2+"]\t"+confidence);
	                                    System.out.println("confidence="+confidence);
	                                    System.out.println("threshold="+threshold);                           
	                                    System.out.println("confidence="+confidence);   	                            		
	                            	}
	                            	
	                            	
	                                //These represent the True Positives identified by the Edit Distance. 
	                                if(confidence > threshold)
	                                {
	                                                                	
	                                	/**
	                                	 * Creates the Feedback structure the first time.
	                                	 */
	                                    if(!i.matchFeedback.positivesIdentified)
	                                    {
	                                    	ValueCorrespondence valueCorrespondence = new ValueCorrespondence(valueCo);
	                                    	if(confidence == 1 || i.matchFeedback.feedbackCorrespondenceListPositives.size()==0)
	                                    		
	                                    		i.matchFeedback.feedbackCorrespondenceListPositives.add(valueCorrespondence);
	                                    	
	                                    	else
	                                    	{
	                                    		boolean added=false;
	                                    		for(int k=0;k<i.matchFeedback.feedbackCorrespondenceListPositives.size();k++)
	                                    		{
	                                    			if(i.matchFeedback.feedbackCorrespondenceListPositives.get(k).getConfidence()<=confidence)
	                                    			{
	                                    				
	                                    				
	                                    				//i.matchFeedback.feedbackCorrespondenceListPositives.add(k,valueCorrespondence);
	                                    				
	                                    				
	                                    				
	                                    				added=true;
	                                    				break;
	                                    			}	                                    			
	                                    		}
	                                    		if(added == false)
	                                    		{
	                                    			//i.matchFeedback.feedbackCorrespondenceListPositives.add(valueCorrespondence);	                                    			
	                                    		}
	                                    	}
	                                    }
	                                    
	                                    //I HAVE TO CHECK IF THE MATCH HAS FEEDBACK.
	                                    /**
	                                     * Will search for feedback from the second time it passes here.
	                                     */
	                                    if(i.matchFeedback.positivesIdentified==true)
	                                    {
	    	                            	
		                                    for(int k=0; k<i.matchFeedback.feedbackCorrespondenceListPositives.size();k++)
		                                    {
		                                    	String sourceTableName2 	= i.matchFeedback.feedbackCorrespondenceListPositives.get(k).getSourcePaths().get(0).getPathSteps().get(1);
		                                    	String sourceAttributeName2 = i.matchFeedback.feedbackCorrespondenceListPositives.get(k).getSourcePaths().get(0).getLastStep();
		                                    	String targetTableName2 	= i.matchFeedback.feedbackCorrespondenceListPositives.get(k).getTargetPath().getPathSteps().get(1);
		                                    	String targetAttributeName2 = i.matchFeedback.feedbackCorrespondenceListPositives.get(k).getTargetPath().getLastStep();
		                                    	

		                                    	if(sourceTableName.compareTo(sourceTableName2)==0 &&
		                                    			targetTableName.compareTo(targetTableName2)==0 &&
		                                    			str1.compareTo(sourceAttributeName2)==0 &&
		                                    			str2.compareTo(targetAttributeName2)==0
		                                    			)
		                                    	{
			                                    		                                    		
		                                    		if(valueCo.getConfidence()!=(i.matchFeedback.feedbackCorrespondenceListPositives.get(k)).getConfidence())
		                                    		{
		                                    			System.out.println("The match has feedback.");
		                                    			System.out.println(sourceTableName2+"."+sourceAttributeName2+"-->"+targetTableName2+"."+targetAttributeName2+"  ["+(i.matchFeedback.feedbackCorrespondenceListPositives.get(k)).getConfidence()+"]");
				                                    	System.out.println(sourceTableName+"."+str1+"-->"+targetTableName+"."+str2+"  ["+valueCo.getConfidence()+"]");
				                                    	System.out.println();		
				                                    	
			                                    		valueCo.setConfidence(i.matchFeedback.feedbackCorrespondenceListPositives.get(k).getConfidence());
			                                    		k=i.matchFeedback.feedbackCorrespondenceListPositives.size();		                                    			
		                                    		}
		                                    	}
		                                    }
	                                    }
	                                    
	                                    //to print feedback order
	                                    //System.out.println(sourceTableName+","+str1+","+targetTableName+","+str2);
	                                    
	                                    //I have to identify if they are TP or FP
	                                    if(matchUtil.isInMatchGroundTruth(matchGroundTruth,sourceTableName,str1,targetTableName,str2,1))
	                                    {
	                                        ValueCorrespondence valueCorrespondence2 = new ValueCorrespondence(valueCo);
	                                        i.matchFeedback.truePositives.add(valueCorrespondence2);
	                                        i.matchFeedback.truePositivesCount++;
	                                        
	                                        valueCoArrTP.add(valueCo);
	                                    }
	                                    else //These represent the False Positives identified by the Edit Distance. 
	                                    {
	                                        ValueCorrespondence valueCorrespondence2 = new ValueCorrespondence(valueCo);
	                                        i.matchFeedback.falsePositives.add(valueCorrespondence2);//all are true because all will
	                                        // be used to make mappings.
	                                        System.out.println("fp="+valueCo.toString());
	                                        i.matchFeedback.falsePositivesCount++;
	    	                            	//if(valueCo.getConfidence()>0)
	    	                            	//	valueCoArrFP.add(valueCo);
	                                    }
	                                    if(JaguarConstants.SYSTEMOUT)
	                                    	System.out.println("Match: {["+str1+"],["+str2+"],["+confidence+"]}");
	                                    
	                                }
	                                else
	                                {
	                                	//if(confidence>1)//added here
	                                	//{
	                                	/*
	                                		if(JaguarConstants.SYSTEMOUT)
	                                			System.out.println("NOT Match: {["+str1+"],["+str2+"],["+confidence+"]}");
		                                    if(!i.matchFeedback.negativesIdentified)
		                                        i.matchFeedback.feedbackCorrespondenceListNegatives.add(valueCo);
		                                    ValueCorrespondence valueCorrespondence2 = new ValueCorrespondence(valueCo);
		                                    //let's assume they are relevant but not retrieved, i.e. FN 
		                                    i.matchFeedback.trueNegatives.add(valueCorrespondence2);
		                                    i.matchFeedback.trueNegativesCount++; 
		                                    */
		                                    //i.mappingTask.addCandidateCorrespondence(valueCo); //new 
	                                	//}
	                                } 
	                                
	                            }
	                        }
	                    }
	                }
	            }
	            i.matchFeedback.positivesIdentified=true;
	            i.matchFeedback.negativesIdentified=true;
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    
	        //This variable is used as limit to provide feedback
	        i.matchFeedback.positivesFound = i.matchFeedback.feedbackCorrespondenceListPositives.size();
	        
	        
	        i.matchFeedback.negativesFound = i.matchFeedback.feedbackCorrespondenceListNegatives.size();
	         
	        
	        ArrayList<ValueCorrespondence> valueCoArr = new ArrayList<>();
	        
	        valueCoArr.addAll(valueCoArrTP);
	        valueCoArr.addAll(valueCoArrFP);
	        
	       
	        
	        i.unloadMappingTask();
	        
	        ((MatchManipulation)i.matchesManipulation).setMatchesList(valueCoArr);
	        
	        //JaguarVariables.count++;
	       
	        
	        if(!JaguarConstants.SYSTEMOUT)
	        {
	            System.out.println("i.matchFeedback.positivesFound="+i.matchFeedback.positivesFound);
	            System.out.println("i.matchFeedback.negativesFound="+i.matchFeedback.negativesFound);            
	            System.out.println("i.matchFeedback.truePositivesCount="+i.matchFeedback.truePositivesCount);
	            System.out.println("i.matchFeedback.falsePositivesCount="+i.matchFeedback.falsePositivesCount);        	
	        }
	        
	        if(JaguarConstants.LOGGING)
	        {
	            LogWriter logWriter = new LogWriter();
	            logWriter.writeToFile("Log.txt","Run Match Episode");        	
	        }
	        
	        if(JaguarConstants.ELAPSEDTIMEOUT)
	        {
	    		long currentTime   = System.currentTimeMillis();
	    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
	    		System.out.println("\tRun Match Episode. Elapsed Time = "+elapsedTime);        	
	        }
		
    }
}
