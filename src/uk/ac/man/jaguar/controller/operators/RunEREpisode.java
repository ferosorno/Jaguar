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

package uk.ac.man.jaguar.controller.operators;

import java.util.ArrayList;





import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.artefacts.ERManipulation;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.artefacts.match.LevenshteinDistanceStrategy;
import uk.ac.man.jaguar.controller.feedback.er.ERFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.ERUtil;
import uk.ac.man.jaguar.util.LogWriter;
import uk.ac.man.jaguar.util.MappingUtil;
/**
 *
 * @author osornogf
 */
public class RunEREpisode implements IRunEpisode{
	
	int totalNumberOfPairsFound=0;
	int totalNumberOfDuplicateRecords=0;
    /**
     * This method performs the following actions
     * - 
     * @param i
     */
    @Override
    public void runEpisode(Integration i)
    {
    	if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tRun ER Episode Start. Elapsed Time = "+elapsedTime);
        }
        /**
         * In this method we are going to make the following:
         * - First we take all the mappings, one by one, and execute them, we will get a
         * set of results on memory. Here, I need an strategy to use the database and don't have to store
         * all the data in memory.
         * - Then, I have to implement the Jaccard similarity between records, and maybe generate an
         * strategy to don't have to compare R x R records, or at least, to minimize the execution time.
         * 
         */
    	totalNumberOfPairsFound = 0;
    	i.erFeedback.duplicatePairList = null;
    	//if(i.erFeedback.duplicatePairList==null)
    	i.erFeedback.duplicatePairList = new ArrayList<DuplicatePair>();
    	
    	for(int j=0; j<JaguarVariables.globalSchemaTablesComplete.size(); j++)
    	{
            identifyDuplicatePairsPerTable(i, j);
    	}
    	System.out.println("totalNumberOfPairsFound="+totalNumberOfPairsFound);
    	System.out.println("totalNumberOfDuplicateRecords="+totalNumberOfDuplicateRecords);
    	//ArrayList<DuplicatePair> list = i.erFeedback.duplicatePairList;
    	/*for(int j=0; j<list.size(); j++)
    	{
    		DuplicatePair instance = list.get(j);
    		System.out.print(JaguarVariables.globalSchemaTablesComplete.get(instance.getTableNumber())+" sim="+instance.getSimilarity()+" - ");
    		System.out.print("Record1: "+instance.getRecordObject1().getAttributesValues().toString()+"-");
    		int prov = instance.getRecordObject1().getProvenance();
    		System.out.print(prov+"-");
    		System.out.print("Record2: "+instance.getRecordObject2().getAttributesValues().toString()+"-");
    		prov = instance.getRecordObject2().getProvenance();
    		System.out.println(prov);
    	}*/
        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt","Run ER Episode");
        }
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tRun ER Episode End. Elapsed Time = "+elapsedTime);
        }
    }
    
    /**
     * Identifies the groups of Records of one table in "tableNumber".
     * 
     * @param i
     * @param tableNumber
     */
    public void identifyDuplicatePairsPerTable(Integration i, int tableNumber)
    {
        int numberOfGroupsFound = 0;
        //int numMappings = i.mapFeedback.getMappingCounter();
        ArrayList<Record> recordsMappingsTable = getListOfRecordsAllMappings(i, tableNumber);
        //System.out.println("list of records size="+recordsAllMappings.size());
        //convert the ArrayList to an array
        Record[] recordInfoArray;
        recordInfoArray = new Record[recordsMappingsTable.size()];
        int index=0;
        for(index=0; index<recordsMappingsTable.size(); index++)
        {
        	//System.out.println("Record arry="+recordsMappingsTable.get(index).getAttributesValues().toString());
            recordInfoArray[index] = recordsMappingsTable.get(index);
        }
        
        //int groupsWithOneSimilarity=0;
        //int groupsIdentifiedAsMatches=0;
        double jaccardSimilarity=0;
        //for all records
        for(int x=0; x<index-1; x++)
        {
            for(int y=x+1; y<index; y++)
            {
                boolean isPossible = true;
                jaccardSimilarity=0;
                
                Record recordA = recordInfoArray[x];
                Record recordB = recordInfoArray[y];
                /*if(recordA.getProvenance()==recordB.getProvenance())
                {
                	isPossible = false;
                }
                else
                {*/
                int numberOfAttributesA = recordA.getAttributesValues().size();
                
                ArrayList<String> attributesA = new ArrayList<>();
                ArrayList<String> attributesB = new ArrayList<>();
                
                MappingUtil mapUtil = new MappingUtil();
                
                if(mapUtil.compareRecords(recordA, recordB)!=0)
                if(recordA.getAttributesValues().size()==recordB.getAttributesValues().size() )
                {
                	int notNullsA=0;
                	int notNullsB=0;
	            	for(int k=0; k<numberOfAttributesA; k++)
	            	{
	                	
	                	String attributeValueA = recordA.getAttributesValues().get(k).trim();
	                	
	                	if(attributeValueA.compareTo("")==0)
	                	{
	                		isPossible=false;
	                		notNullsA++;
	                	}
	                	else
	                	{
	                		attributesA.add(attributeValueA);
	                	}
	                	
	                	String attributeValueB = recordB.getAttributesValues().get(k).trim();
	                	if(attributeValueB.compareTo("")==0)
	                	{
	                		isPossible=false;
	                		notNullsB++;
	                	}
	                	else
	                	{
	                		attributesB.add(attributeValueB);
	                	}
	                	
	                	//double levenshteinDistance = LevenshteinDistanceStrategy.computeLevenshteinDistance(attributeValueA, attributeValueB); 
	                	//if(levenshteinDistance<0.1)
	                    //    isPossible=false;
	            	}
	            	if(notNullsA>1 || notNullsB>1)
	            		isPossible=false;
	            	
	            	if(attributesA.size()==1 || attributesB.size()==1)
	            		isPossible=false;
                }
                else
                {
                	isPossible=false;
                	//System.out.println("attributes of different size");
                }
                
                jaccardSimilarity = ERManipulation.calculateJaccardSimilarityRow(attributesA, attributesB);
                //}
	            
                /*System.out.println("isPossible="+isPossible);*/
	                
                if(jaccardSimilarity == Double.NaN)
                	jaccardSimilarity=0;
                
                if(jaccardSimilarity!=1 && jaccardSimilarity >= JaguarConstants.K_ENTITYRESOLUTION && isPossible==true)
                //if(similarity >= 0.010)
                {
                	//if(recordA.getEntityNumber()==0 && recordB.getEntityNumber()==0 && isPossible==true)
                    //{
                    	//System.out.println(recordA.getAttributesValues().toString());
                    	//System.out.println(recordB.getAttributesValues().toString());
                    	//System.out.println("jaccardSimilarity="+jaccardSimilarity);
                    	//System.out.println();
                    //}
                	/**I have to check if there is feedback for this duplicate record. **/
                	//System.out.println("jaccardSimilarity="+jaccardSimilarity);
                    numberOfGroupsFound++;
                    recordA.setDuplicate(true);
                    recordB.setDuplicate(true);
                    addPairToStruct(i, recordA, recordB,  jaccardSimilarity, tableNumber);
                    if(!recordA.isCountedAsDuplicate)
                    {
                    	totalNumberOfDuplicateRecords++;
                    	recordA.isCountedAsDuplicate=true;
                    }
                    if(!recordB.isCountedAsDuplicate)
                    {
                    	totalNumberOfDuplicateRecords++;
                    	recordB.isCountedAsDuplicate=true;
                    }
                }
            }
        }
        //System.out.println("numberOfGroupsFound="+numberOfGroupsFound);
        //System.out.println("groupsIdentifiedAsMatches="+groupsIdentifiedAsMatches);
        //System.out.println("Size of Duplicate Groups of Records List="+i.erFeedback.duplicatePairList.size());
        //System.out.println("Size of Feedback Groups of Records List="+i.erFeedback.feedbackPairList.size());
        totalNumberOfPairsFound += numberOfGroupsFound;
    }
    
    /**
     * This method is called in RunMapEpisode to create the duplicatePairList.
     * 
     * @param duplicatePairList
     * @param recordA
     * @param recordB
     * @param similarity
     */
    public void addPairToStruct(Integration i, Record recordA, Record recordB,  double similarity, int tableNumber)
    {
    	ERUtil erUtil = new ERUtil();
        //String keyProvenance = recordA.getRecordConcatenatedText()+recordA.getProvenance()+recordB.getRecordConcatenatedText()+recordB.getProvenance();
    	//String yek = recordB.getRecordConcatenatedText()+recordA.getRecordConcatenatedText();
    	recordA.setDuplicate(true);
    	//recordA.setHasERFeedback(false);
    	//recordA.setErFeedback(false);
    	//recordA.setDuplicateAndThisRecordCounts(false);
    	recordB.setDuplicate(true);
    	//recordB.setHasERFeedback(false);
    	//recordB.setErFeedback(false);
    	//recordB.setDuplicateAndThisRecordCounts(false);
    	
    	if(!erUtil.containsPair(i.erFeedback.duplicatePairList, recordA,recordB))
        {
            DuplicatePair duplicatePair = erUtil.getPair(i.erFeedback.feedbackPairList,recordA,recordB);
            if(duplicatePair!=null)//add existing pair to the temporal duplicatePair list.
            {
            	//System.out.println("Adding pair with fbk.");
            	i.erFeedback.duplicatePairList.add(duplicatePair);
            }
            else
            {//It doesn't exist. We create a new pair without feedback.
            	//System.out.println("Adding new pair.");
            	duplicatePair = new DuplicatePair();
            	duplicatePair.setSimilarity(similarity);
            	duplicatePair.setTableNumber(tableNumber);
            	duplicatePair.setRecordObject1(recordA);
            	duplicatePair.setRecordObject2(recordB);
            	
            	if(similarity==1)
            		duplicatePair.setInitialAnnotationValue(true);
            	else
            		duplicatePair.setInitialAnnotationValue(false);
            	
            	i.erFeedback.duplicatePairList.add(duplicatePair);
            }
            
            i.erFeedback.setFeedbackIfExists(duplicatePair);
           
        }
        else
        {
        	//System.out.println("adding existing objects er");
        	//Else, it exists and adds the records
        	/*DuplicatePair duplicatePair = getERFeedbackInstance(i.erFeedback.duplicatePairList,recordA,recordB);
        	if(duplicatePair==null)
        	{
        		duplicatePair = getERFeedbackInstance(i.erFeedback.duplicatePairList,recordB,recordA);
        	}
        	
        	duplicatePair.addRecordObjectList1(recordA);
            duplicatePair.addRecordObjectList2(recordB);*/
        }
    }
    
    /**
     * Create a list of records from the mappings for "tableNumber"
     * One list for each table.
     * It is just making a list of records, and it should be one per each table.
     */
    public ArrayList<Record> getListOfRecordsAllMappings(Integration i, int tableNumber)
    {
    	ArrayList<Record> recordsAllMappings = new ArrayList<>();
    	//System.out.println("numMappings before=" + numMappings);
    	int numMappings = ((MapManipulation)i.mappingsManipulation).getMappingsListSize();
    	//System.out.println("numMappings after=" + numMappings);
    	//For all mappings.
    	for(int j=0; j<numMappings; j++)
        {
    		//get the mapping at position m
    		MappingObject mappingObject = ((MapManipulation)i.mappingsManipulation).getMappingObjectAt(j);
    		
    		//get the results of the mapping
            MappingResultsSet mapFeedbackSet = i.mapFeedback.getMapResultsSet(mappingObject.getProvenance());
            
            if(mapFeedbackSet!=null)
            {
            	//we only consider mappings for the table "tableNumber"
            	if(mappingObject.getTableNumber() == tableNumber)
            	{
            		if(mappingObject.isActive() && mappingObject.selected==true)///and was selected???? probably yesss!!
            		{
			            if(mappingObject.sizeOfResults>0)
			            {
			                ArrayList<Record> mapRecords = mapFeedbackSet.getRecords();
			                
			                for(int k=0; k<mapRecords.size(); k++)
			                {
			                    Record mapRecord = mapRecords.get(k);
			                    recordsAllMappings.add(mapRecord);
			                }
			            }
            		}
            	}
            }
        }
    	return recordsAllMappings;
    }
    
}
