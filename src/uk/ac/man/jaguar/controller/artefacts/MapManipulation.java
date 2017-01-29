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
package uk.ac.man.jaguar.controller.artefacts;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.FeedbackPlanApplication;
import uk.ac.man.jaguar.controller.artefacts.map.MapSelectionGreedy;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.controller.operators.map.MapResult;
import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.AnnotationsCollector;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.DatabaseAccessInfo;
import uk.ac.man.jaguar.util.DatabaseUtils;
import uk.ac.man.jaguar.util.GroundTruth;
import uk.ac.man.jaguar.util.LogWriter;
import uk.ac.man.jaguar.util.MappingUtil;
import uk.ac.man.jaguar.util.MatchUtil;

/**
 *
 * @author osornogf
 */
public class MapManipulation implements IArtefactManipulation {
	/**
	 * Just the mappings' list. But we have to identify which
	 * mappings are active after each episode.
	 */
    public ArrayList<MappingObject> mappingsList;
    public static int mappingNumber;
    public static int lastIdinTable[];
    public static boolean lastIdWasRead;
	int notRetrievedTPTuples;
	int notRetrievedFPTuples;  
	int notRetrievedTNTuples;
	int notRestrievedDuplicates;
	int falseNegatives;
	int notRetrievedTPTuplesFb;
	int notRetrievedFPTuplesFb;
	int notRetrievedTNTuplesFb;
	
    public MapManipulation() {   	
    	//I need to know how many tables contains the global schema
		//ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesComplete;
        //mappingResults = null;
        mappingNumber = 0;
        lastIdinTable = new int[20000];
        lastIdWasRead = false;
    	notRetrievedTPTuples = 0;
    	notRetrievedFPTuples = 0;  
    	notRetrievedTNTuples = 0;
    	notRetrievedTPTuplesFb = 0;
    	notRetrievedFPTuplesFb = 0;
    	notRetrievedTNTuplesFb = 0;    	
        mappingsList = new ArrayList<>();
    }

    /**
     * In this method we count the ground truth tp, fp and duplicates for the selected mappings
     * 
     * 
     * @param i
     * @param feedbackType
     */
    @SuppressWarnings("unused")
	@Override
    public void updateAnnotations(Integration i, int feedbackType) {
    	if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tApplyFeedback.updateAnnotations(Map) Start. Elapsed Time ="+elapsedTime);        	
        }
        //update annotations means to calculate the precision, recall and f-measure of the mappings
        //based on the existing feedback
        //we DON'T create new mappings here
    	
        //I will try to store the feedback in such a way that if a mapping disappears,
        //I don't have to lose the feedback obtained.
    	
        int truePositives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;
        
        //Accumulated
        int truePositivesGT = 0; 
        int falsePositivesGT = 0;
        int trueNegativesGT = 0;
        int falseNegativesGT = 0;
        int duplicatesGT = 0;
        
        double precision;
        double recall;
        double fmeasure;
        
        int numMappings = i.mapFeedback.getMappingCounter();
        
        MapManipulation mappingsManipulation = (MapManipulation) i.mappingsManipulation;
        
        /**
         * Here I will receive instances of the selected mappings. I should not modify the original variables
         * so I have to use new variables defined here. 
         */
        MapSelectionGreedy mapSelectionGreedy = new MapSelectionGreedy();
        
        ArrayList<MappingObject> IM = new ArrayList<>(); // IM represents a set of mappings.
        
        //The complete counting of mappings will count all the generated mappings, and all their records of each mapping
        //even though they are repeated in other mappings. 
        System.out.println("\t\tComplete counting of mappings. Generated mappings:");
        for(int n=0; n<mappingsManipulation.mappingsList.size(); n++)// for each mapping
        {
        	MappingObject mappingObject = mappingsManipulation.mappingsList.get(n);
        	if(mappingObject.isActive() && mappingObject.getSizeOfResults()>0)
        	{
        		IM.add(mappingObject);//IM will be a new set of mapping.
        		MappingResultsSet mappingResultsSet = i.mapFeedback.getMapResultsSet(mappingObject.getProvenance());//this method is not working.
        		ArrayList<Record> results = mappingResultsSet.getRecords();
        		
        		int correct=0;
        		int incorrect=0;
        		int duplicates = 0;
        		for(int j=0; j<results.size(); j++)
        		{
        			Record record = results.get(j); 
        			
        			Record duplicateRecord = getDuplicateRecord(i, record);    
        			
        			if(duplicateRecord!=null)
        			{
        				
	        			if(duplicateRecord.hasERFeedback)//therefore, is duplicate, has feedback and counts 
	        			{
	        				if(duplicateRecord.isDuplicateAndThisRecordCounts())
	        				{
	        					if(record.getGroundTruthValue().compareTo("tp")==0)
			        				correct++;
			        			if(record.getGroundTruthValue().compareTo("fp")==0)
			        				incorrect++;	        					
	        				}
	        			}
	        			else
	        			{
	        				duplicates++;//No feedback---> to duplicated
	        			}
        			}
        			else
        			{
	        			if(record.getGroundTruthValue().compareTo("tp")==0)
	        				correct++;
	        			if(record.getGroundTruthValue().compareTo("fp")==0)
	        				incorrect++;
        			}
        		}
        		if(correct>0)
        		{	//these are the generated mappings. 
        			System.out.println("\t\t(co="+correct+"\t in="+incorrect+"\t du="+duplicates+") res="+results.size()+"\t"+mappingObject.getTableName());
        		}
        	}
        }
        // M is the result of the Greedy Algorithm.
        ArrayList<MappingObject> M = 
        		mapSelectionGreedy.selectMappings(i,IM, i.mapFeedback.getMappingResultsSetList(), JaguarConstants.K_GREEDY);
        
        /**
         * We calculate the combined GT precision. 
         * I assume that M comes in the order in which the mapping in position 0 retrieves more and so on.
         */
        ArrayList<MappingObject> revisedMappings = new ArrayList<>();
        System.out.println("\t\tMappings values registered:");
        for (int r = 0; r<M.size(); r++) //For all the mappings
        {
        	//These variables will count the tuples *not previously retrieved*
        	//of each mapping.
        	notRetrievedTPTuples = 0;
        	notRetrievedFPTuples = 0;
        	notRestrievedDuplicates = 0;
        	
        	/**
        	 * M.get(r): 								current mappingObject
        	 * revisedMapings:    						selected mapping objects so far
        	 * i.mapFeedback.getMapFeedbackSetList():	all mappings' feedback
        	 */
        	getNotRetrieved(i,M.get(r), 
        			revisedMappings,  
        			i.mapFeedback.getMappingResultsSetList()
        			);
        	
        	revisedMappings.add(M.get(r));
        	
            truePositivesGT += notRetrievedTPTuples;
            falsePositivesGT += notRetrievedFPTuples;
            duplicatesGT += notRestrievedDuplicates;
            
            
            System.out.println("\t\ttp="+notRetrievedTPTuples+"\t  fp="+notRetrievedFPTuples+"\t  du="+notRestrievedDuplicates+"\t"+M.get(r).getTableName());
            //These are the accumulated sum of the GT results i.e. the final results.
            //System.out.println("\t\t"+M.get(r).getTableName()+" truePositivesGT="+truePositivesGT+"  falsePositivesGT="+falsePositivesGT+"  duplicatesGT="+duplicatesGT);
        }
        
        //System.out.println("Average Map Precision=" + averagePrecision);
        //System.out.println("Number of mappings used=" + mappingsConsideredForAvPrecision);
        //System.out.println("Total Number of Mappings=" + numMappings);
        
        i.mapFeedback.totalTruePositives = truePositivesGT;
        i.mapFeedback.totalFalsePositives = falsePositivesGT;   
        i.mapFeedback.totalTrueNegatives = trueNegativesGT;
        i.mapFeedback.totalFalseNegatives = falseNegativesGT;
        i.mapFeedback.duplicatesGT = duplicatesGT;
        
        i.mapFeedback.numMapForAveragePrecision = M.size();
        i.mapFeedback.totalNumMap = numMappings;
        i.mapFeedback.gtAveragePrecision = 0;
        
        //System.out.println("truePositivesGT="+truePositivesGT);
        //System.out.println("falsePositivesGT="+falsePositivesGT);
        //System.out.println("duplicatesGT="+duplicatesGT);
        
        falseNegativesGT = JaguarVariables.groundTruthSize-truePositivesGT;
        
        i.mapFeedback.truePositivesGT = truePositivesGT;
        i.mapFeedback.falsePositivesGT = falsePositivesGT;
        i.mapFeedback.trueNegativesGT = trueNegativesGT;
        i.mapFeedback.falseNegativesGT = falseNegativesGT;
        i.mapFeedback.duplicatesGT = duplicatesGT;
        
		if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
		{
			AnnotationsCollector annotationsCollector = FeedbackPlanApplication.annotationsCollector; 
			
	        annotationsCollector.updateMapAnnotations(
	        		truePositivesGT,
	        		falsePositivesGT,
	        		trueNegativesGT,
	        		falseNegativesGT,
	        		duplicatesGT,
	        		i.mapFeedback.numMapForAveragePrecision,
	        		i.mapFeedback.totalNumMap,
	        		i.mapFeedback.gtAveragePrecision,
	        		i.mapFeedback.fbPropToMatch	        		
	        		);
		}
        
        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt", "ApplyFeedback.updateAnnotations(Map)");        	
        }
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tApplyFeedback.updateAnnotations(Map) End. Elapsed Time = "+elapsedTime);  
        }
    }
    
    /**
     * Searches if there is a duplicated record.
     * 
     * @param i
     * @param record
     * @return
     */
    public Record getDuplicateRecord(Integration i, Record record)
    {
    	Record result = null;
    	DuplicatePair duplicatePair;
    	MappingUtil mapUtil = new MappingUtil();
    	
    	
    	for(int j=0; j<i.erFeedback.duplicatePairList.size();j++)
    	//for(int j=0; j<i.erFeedback.feedbackPairList.size();j++)
    	{
    		duplicatePair = i.erFeedback.duplicatePairList.get(j);
    		//duplicatePair = i.erFeedback.feedbackPairList.get(j);
    		Record record1 = duplicatePair.getRecordObject1();
    		Record record2 = duplicatePair.getRecordObject2();
    		if(mapUtil.compareRecords(record, record1)==0)// && record.getProvenance()==record1.getProvenance()
    		{
    			result = record1;
    			break;
    		} else
    		if(mapUtil.compareRecords(record, record2)==0)// && record.getProvenance()==record2.getProvenance()
    		{
    			result = record2;
    			break;
    		}
    	}
    	
    	return result;
    	
    }
    @SuppressWarnings("unused")
	@Override
    public void updateAnnotationsBootstrapping(Integration i, int feedbackType) {
        
        int truePositives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;
        //int falseNegatives = 0;
        
        //Accumulated
        int truePositivesGT = 0; 
        int falsePositivesGT = 0;
        int trueNegativesGT = 0;
        int falseNegativesGT = 0;
        int duplicatesGT = 0;
        
        double precision;
        double recall;
        double fmeasure;
        
        int numMappings = i.mapFeedback.getMappingCounter();
        
        //ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesComplete;
        
        MapManipulation mappingsManipulation = (MapManipulation) i.mappingsManipulation;
        
        MapSelectionGreedy mapSelectionGreedy = new MapSelectionGreedy();
        
        ArrayList<MappingObject> IM = new ArrayList<>(); // IM represents a set of mappings.
        
        for(int n=0; n<mappingsManipulation.mappingsList.size(); n++)// for each mapping
        {
        	MappingObject mappingObject = mappingsManipulation.mappingsList.get(n);
        	
        	if(mappingObject.isActive() && mappingObject.getSizeOfResults()>0)
        	{
        		//System.out.println("ADDING AN ACTIVE MAPPING TO IM");
        		
        		IM.add(mappingObject); // IM will be a new set of mapping.
        	}
        	else
        	{
        		//System.out.println("SKIPPING AN inACTIVE MAPPINT TO IM");
        	}
        }
        
        // M is the result of the Greedy Algorithm.
        ArrayList<MappingObject> M = 
        		mapSelectionGreedy.selectMappings(i,IM, i.mapFeedback.getMappingResultsSetList(), 0.03d);
        
        /**
         * 
         * Then, I will use the objects received to identify the local objects of the selected mappings.                  
         * 
         */
        if(JaguarConstants.SYSTEMOUT)
        {
        	for(int e=0; e<M.size(); e++)
        	{
        		System.out.println("MAPPING FOUND WITH GREEDY="+(M.get(e)).getTableName()+" prov="+(M.get(e)).getProvenance());
        	}
        }
        
        /**
         * We calculate the combined GT precision. 
         * I assume that M comes in the order in which the mapping in position 0 retrieves more and so on.
         */
        ArrayList<MappingObject> revisedMappings = new ArrayList<>();
        for (int r = 0; r<M.size(); r++) //For all the mappings
        {   
        	//These variables will count the tuples *not previously retrieved*
        	//of each mapping.
        	notRetrievedTPTuples = 0;
        	notRetrievedFPTuples = 0;
        	notRetrievedTNTuples = 0;
        	
        	/**
        	 * M.get(r): 								current mappingObject
        	 * revisedMapings:    								selected mapping objects so far
        	 * i.mapFeedback.getMapFeedbackSetList():	all mappings' feedback
        	 */
        	getNotRetrieved(i,M.get(r),
        			revisedMappings,
        			i.mapFeedback.getMappingResultsSetList()
        			);
        			//,notRetrievedTPTuples,notRetrievedFPTuples);        	
        	revisedMappings.add(M.get(r));
        	
            truePositivesGT += notRetrievedTPTuples;
            falsePositivesGT += notRetrievedFPTuples;
            trueNegativesGT += notRetrievedTNTuples;
            
            duplicatesGT += notRestrievedDuplicates;
            
            System.out.println("Bootstrapping: "+M.get(r).getTableName()+" truePositivesGT="+truePositivesGT+"  falsePositivesGT="+falsePositivesGT+"  duplicatesGT="+duplicatesGT);
        }
        
        
        i.mapFeedback.totalTruePositives = truePositivesGT;
        i.mapFeedback.totalFalsePositives = falsePositivesGT;   
        i.mapFeedback.totalTrueNegatives = trueNegativesGT;
        i.mapFeedback.totalFalseNegatives = falseNegativesGT;
        
        i.mapFeedback.numMapForAveragePrecision = M.size();
        i.mapFeedback.totalNumMap=numMappings;
        i.mapFeedback.gtAveragePrecision=0;
        
        
        
        //System.out.println("tp="+truePositivesGT);
        //System.out.println("fp="+falsePositivesGT);
        //System.out.println("precision="+(double)((truePositivesGT)/(truePositivesGT+falsePositivesGT)));
        
        falseNegativesGT = JaguarVariables.groundTruthSize-truePositivesGT;
        
        i.mapFeedback.truePositivesGT = truePositivesGT;
        i.mapFeedback.falsePositivesGT = falsePositivesGT;
        i.mapFeedback.trueNegativesGT = trueNegativesGT;
        i.mapFeedback.falseNegativesGT = falseNegativesGT;
        i.mapFeedback.duplicatesGT = duplicatesGT;
        
		if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
		{
			AnnotationsCollector annotationsCollector = FeedbackPlanApplication.annotationsCollector; 
			
			/*It is GT Precision, we send GT variables.*/
	        annotationsCollector.updateMapAnnotations(
	        		truePositivesGT,
	        		falsePositivesGT,
	        		trueNegativesGT,
	        		falseNegativesGT,
	        		duplicatesGT,
	        		i.mapFeedback.numMapForAveragePrecision,
	        		i.mapFeedback.totalNumMap,
	        		i.mapFeedback.gtAveragePrecision,
	        		i.mapFeedback.fbPropToMatch	        		
	        		);        
		}
        
        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt", "ApplyFeedback.updateAnnotations(Map)");        	
        }
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tApplyFeedback.updateAnnotations(Map). Elapsed Time = "+elapsedTime);  
        }
    }
    
    
    
    
    public MappingObject createMappingObject(String mappingSql) {
        MappingObject mappingObject = new MappingObject();
        //mappingObject.setProvenance("" + MapManipulation.mappingNumber);
        loadTGDs(mappingObject, mappingSql);
        loadResultOfExchange(mappingObject, mappingSql);
        createSelectString(mappingObject);
        return mappingObject;
    }
    
    
    /**
     * Makes the individual mappings for the tables of the Global Schema.
     * One mapping per table.
     * 
     * @return
     */
    public ArrayList<MappingObject> createMappingObjects(String mappingSql, MappingTask mappingTask)
    {
    	ArrayList<MappingObject> mapObjList = new ArrayList<>();
        ArrayList<String> tgdsAll = loadTGDs(mappingSql);
        ArrayList<String> resultOfExchangeList = loadResultOfExchange(mappingSql);    
        
        List<FORule> forules = mappingTask.getMappingData().getRewrittenRules();
        
    	for(int j=0; j<resultOfExchangeList.size(); j++)
    	{
    		MappingObject mappingObject = new MappingObject();
    		
    		mappingObject.setResultOfExchange(resultOfExchangeList.get(j));
    		
    		String tableName = mappingObject.getResultOfExchangeAt(0).substring(12, 
    				mappingObject.getResultOfExchangeAt(0).indexOf("("));
    		
    		String ruleName =  mappingObject.getResultOfExchangeAt(0).substring(
    				mappingObject.getResultOfExchangeAt(0).indexOf("rule_"), 
    				mappingObject.getResultOfExchangeAt(0).indexOf("."));
    		
    		for(int k=0; k<forules.size(); k++)
    		{
    			if(forules.get(k).getId().toLowerCase().compareTo(ruleName)==0)
    			{
    				//System.out.println("ruleName found="+ruleName);
    				mappingObject.setForule(forules.get(k));
    				//simRulesNumber++;
    			}
    		}
    		
    		/*if(simRulesNumber>1)
    		{
    			System.out.println("MORE THAN ONE RULE!!");
    		}*/
    		
    		mappingObject.setRuleName(ruleName);    		
    		mappingObject.setTableName(tableName);
    		MappingUtil mappingUtil = new MappingUtil();
    		mappingObject.setTableNumber(mappingUtil.getEntityNumber(tableName));
    		ArrayList<String> tgdsSingle = getTgdsSingle(tgdsAll, resultOfExchangeList.get(j));
    		
    		boolean hasExcept = false;
        	for(int k=0; k<tgdsSingle.size(); k++)
        	{
        		if(tgdsSingle.get(k).contains("EXCEPT"))
        			hasExcept = true;
        	}
        	mappingObject.setHasEXCEPT(hasExcept);
        	
        	ArrayList<String> SSAttributes =null;
    		if(!hasExcept)
    			SSAttributes = getSSAttributes(tgdsSingle);
    		else
    			SSAttributes = new ArrayList<String>();
    		
    		
    		boolean noNulls = true;
    		for(int k=0;k<SSAttributes.size();k++)
    		{
    			String attribute = SSAttributes.get(k);
    			if(attribute.startsWith("null"))
    				noNulls = false;
    		}
    		mappingObject.setNoNullsAttributes(noNulls);
    		mappingObject.setSSAttributes(SSAttributes);
    		
    		mappingObject.setTgds(tgdsSingle);    		
    		createSelectStringSingle(mappingObject);    		
    		mapObjList.add(mappingObject);
    	}
        
    	return mapObjList;
    }
    public ArrayList<String> getSSAttributes(ArrayList<String> tgdsSingle)
    {
    	ArrayList<String> ssAttributes = new ArrayList<String>();
    	ArrayList<String> gsTables = new ArrayList<String>();
    	boolean hasExcept = false;
    	for(int j=0; j<tgdsSingle.size(); j++)
    	{
    		if(tgdsSingle.get(j).contains("EXCEPT"))
    			hasExcept = true;
    	}
    	if(!hasExcept)
    	{
	    	for(int j=0; j<tgdsSingle.size(); j++)
	    	{
	    		StringTokenizer st = new StringTokenizer(tgdsSingle.get(j),"\r\n");
	    		boolean hasReadIdLine = false;
	    		while(st.hasMoreTokens())
	    		{
	    			String line = st.nextToken();
	    			line = line.trim();
	    			//System.out.println(line);
	    			if(line.startsWith("null"))
	    			{
	    				if(hasReadIdLine == true)
	    					ssAttributes.add(line.substring(0, line.indexOf(" ")));
	    				else
	    					hasReadIdLine = true;
	    			}
	    			else if(line.startsWith("rel"))
	    			{
	    				ssAttributes.add(line.substring(0, line.indexOf(" ")));
	    			}
	    			else if(line.startsWith("from"))
	    			{
	    				line = line.replace("from ", "");
	    				if(line.contains(","))
	    				{
		    				StringTokenizer gsTablesTokenizer = new StringTokenizer(line,",");
		    				while(gsTablesTokenizer.hasMoreTokens())
		    				{
		    					gsTables.add(gsTablesTokenizer.nextToken().trim());
		    				}
	    				}
	    				else
	    				{
	    					gsTables.add(line);
	    				}
	    			}
	    		}
	    	}
	    	
	    	for(int j=0; j<ssAttributes.size(); j++)
	    	{
	    		String attribute = ssAttributes.get(j);
	    		if(!attribute.startsWith("null"))
	    		{
		    		String tempTableAttribute = attribute.substring(0, attribute.indexOf("."));
		    		//System.out.println("attribute:"+attribute);
		    		for(int k=0;k<gsTables.size(); k++)
		    		{
		    			String tableAs = gsTables.get(k);
		    			//System.out.println("tempTableAttribute:"+tempTableAttribute);
		    			//System.out.println("Table as:"+tableAs);
		    			String table = tableAs.substring(0, tableAs.indexOf(" "));	    			
		    			//System.out.println("Table:"+table);
		    			if(tableAs.contains(tempTableAttribute))
		    			{
		    				attribute = attribute.replace(tempTableAttribute, table);
		    			}
		    		}
		    		
		    		/*//to eliminate the table
		    		attribute = attribute.replace(tempTableAttribute, "");
		    		attribute = attribute.replaceAll(".", "");*/
		    		
		    		//System.out.println("new attribute:"+attribute);
		    		ssAttributes.set(j, attribute);
	    		}
	    	}
	    	/*for(int j=0; j<ssAttributes.size(); j++)
	    	{
	    		System.out.println("final: "+ssAttributes.get(j));
	    	}
	    	*/
    	}
    	else{
    		
    	}
    	
    	return ssAttributes;
    }
    
    public void addMappingObject(MappingObject mappingObject) {
    	mappingObject.setProvenance(MapManipulation.mappingNumber);
        mappingsList.add(mappingObject);
        MapManipulation.mappingNumber++;
    }
    
    public boolean mappingExists(MappingObject mappingObject) {
        boolean result = false;
        if (mappingsList != null) {
            MatchUtil matchUtil = new MatchUtil();
            ArrayList<ValueCorrespondence> valueCorrespondences = mappingObject.getValueCorrespondences();
            for (int k = 0; k < mappingsList.size(); k++) {
                MappingObject localMappingObject = mappingsList.get(k);
                ArrayList<ValueCorrespondence> localValueCorrespondences = localMappingObject.getValueCorrespondences();
                //System.out.println("list1:"+localValueCorrespondences.size()+" list2:"+valueCorrespondences.size());
                if (matchUtil.correspondencesListsCompare(localValueCorrespondences, valueCorrespondences)) {
                    result = true;
                }
            }
        }
        return result;
    }
    
    public boolean singleMappingExistsFORules(MappingObject mappingObject)
    {
        boolean result = false;
        if (mappingsList != null) {
            for (int k = 0; k < mappingsList.size(); k++) {            	
                MappingObject existingMappingObject = mappingsList.get(k);
                FORule forule1 = existingMappingObject.getForule();
                FORule forule2 = mappingObject.getForule();                
                //if (forule1.hasEqualViews(forule2) && forule2.hasEqualViews(forule1)) {
                if(compareMappingsFORules(mappingObject,existingMappingObject)) {	
                	//System.out.println("Equal "+forule1.getId()+"-"+forule2.getId());
                    result = true;
                    /*for(int s=0; s<forule1.getCoveredCorrespondences().size(); s++)
                    {
                    	System.out.println(forule1.getCoveredCorrespondences().get(s).toString());
                    }*/
                    //System.out.println("*"+forule1.getSimpleSourceView().toString()+"*");
                    //System.out.println("*"+forule1.getTargetView().toString()+"*");                    
                    //System.out.println("*"+forule1.getSimpleSourceView().getAlgebraTree()+"*");
                    //System.out.println("*"+forule1.getTargetView().getAlgebraTree()+"*");
                }
                else
                {
                	//System.out.println("Different "+forule1.getId()+"-"+forule2.getId());
                }
            }
        }
        return result;
    }
    /**
     * This method compares two mapping objects based on:
     * - The source schema attributes,
     * - the global schema table name.
     * 
     * @param mappingObject1
     * @return
     */
    public boolean singleMappingExistsSSAttributes(MappingObject mappingObject1)//just checks if the mappingObject1 exists.
    {
        boolean result = false;
        ArrayList<String> ssAttributes1 = mappingObject1.getSSAttributes();
        //if (mappingsList != null) {
        /*if(ssAttributes1.size()==0)
        {
        	System.out.println("attributes size = 0");
        }*/
        
            for (int k = 0; k < mappingsList.size(); k++) {// for each existing mapping
                MappingObject mappingObject2 = mappingsList.get(k);
                ArrayList<String> ssAttributes2 = mappingObject2.getSSAttributes();
                
                
                /*if(mappingObject1.getTableName().compareTo("restt")==0 && mappingObject2.getTableName().compareTo("restt")==0)
                {
                	if(ssAttributes1.size() == ssAttributes2.size() && ssAttributes2.size()!=0)
                	{
                		System.out.println("Comparing mapping");
                		boolean nullsex=false;
	                	for(int j=0; j<ssAttributes1.size(); j++)
	                	{
	                		String attribute1 = ssAttributes1.get(j);
	                		String attribute2 = ssAttributes2.get(j);
	                		System.out.println(""+attribute1+"."+attribute2);
	                		if(attribute1.compareTo("null")==0 || attribute2.compareTo("null")==0)
	                			nullsex=true;
	                	}
	                	if(nullsex!=true)
	                		System.out.println("NO NULLS");
	                	System.out.println("result="+result);
	                    System.out.println();
                	}
                }*/
                
                boolean equalAttributes = true;
                if(ssAttributes1.size() == ssAttributes2.size())
                {
                	for(int j=0; j<ssAttributes1.size(); j++)
                	{
                		String attribute1 = ssAttributes1.get(j);
                		//StringTokenizer st1 = new StringTokenizer(attribute1,".");
                		//st1.nextToken();
                		//attribute1 = st1.nextToken();
                		
                		String attribute2 = ssAttributes2.get(j);
                		if(attribute1.compareTo(attribute2)!=0)
                		{
                			//if(mappingObject2.getTableName().compareTo("testt")==0 || mappingObject1.getTableName().compareTo("testt")==0)
                			//	System.out.println("Not attr1="+attribute1+" attr2="+attribute2);
                			equalAttributes = false;
                			//break;
                		}
                		//else
                		//	System.out.println("Yes attr1="+attribute1+" attr2="+attribute2);
                	}
                	
                }
                else
                {
                	equalAttributes = false;
                }
                
                if(equalAttributes == true && (mappingObject1.getTableNumber() == mappingObject2.getTableNumber()) )
                { //if equal to current existing mapping...
                	
                	//System.out.println("exist table1="+mappingObject1.getTableName()+" table2="+mappingObject2.getTableName());
                	result = true;
                	
                }
               // else
                //	System.out.println("no exist");
            }
            
            
            
            
        //}
        return result;
    }
    /**
     * 
     * @param mappingObject1
     * @return
     */
    public boolean singleMappingExistsResultOfExchange(MappingObject mappingObject1)
    {
        boolean result = false;
        String resultOfExchange1 = mappingObject1.getResultOfExchangeAt(0);
        
        for (int k = 0; k < mappingsList.size(); k++) {// for each existing mapping
            MappingObject mappingObject2 = mappingsList.get(k);
            String resultOfExchange2 = mappingObject2.getResultOfExchangeAt(0);
            
            if(resultOfExchange1.compareTo(resultOfExchange2) == 0)// && (mappingObject1.getTableNumber() == mappingObject2.getTableNumber()) 
            {
            	
            	result = true;
            }
            
            
        }
        
     
        return result;
    }
    
    public MappingObject getMappingObject(Integration i, int provenance) {
        MappingObject result = null;
        MapManipulation mappingsManipulation = (MapManipulation) i.mappingsManipulation;
        for (int k = 0; k < mappingsManipulation.mappingsList.size(); k++) {
            if (provenance == mappingsManipulation.mappingsList.get(k).getProvenance()) {
                result = mappingsManipulation.mappingsList.get(k);
            }
        }
        return result;
    }

    public MappingObject getMappingObjectAt(int position)
    {
    	return mappingsList.get(position);
    }
    /**
     * This method receives the sql script that is generated by the Spicy system
     * and obtains the TGDs from the script, we assume that a TGD is a sql
     * script that creates an intermediate table.
     *
     * This method will store one TGD in each position of tgds vector.
     * @param mappingObject
     * @param sqlScript
     */
    public void loadTGDs(MappingObject mappingObject, String sqlScript) {
        //System.out.println("sqlScript=*"+sqlScript+"*");
        StringTokenizer stringTokenizer = new StringTokenizer(sqlScript, "\n");
        String lineClean = "";
        boolean hadCreateSentence = false;
        boolean isLong = true;
        int timesR = 0;
        while (stringTokenizer.hasMoreTokens()) {
        	
            String line = stringTokenizer.nextToken();
            if (line.startsWith("create")) {
                hadCreateSentence = true;
            }
            if (line.startsWith("-")) {
                timesR++;
                if (timesR == 2 && hadCreateSentence == false) {
                    isLong = false;// long mapping version
                }

            }
            
            if (!isInIgonerdLines(line))//If this line should NOT be ignored,
            {
                lineClean += (line + "\n");//we add it.
            }
        }

        int count = lineClean.length() - lineClean.replace(";", "").length();
        
        int insertCount = lineClean.length() - lineClean.replace("insert", "").length();
        
        int tgdCount = (lineClean.length() - lineClean.replace("create table ", "").length())/13;
        
        //System.out.println("tgdCount="+tgdCount);
        
        if(insertCount > 6)//6 = number of chars in "insert"
        	count = count - 3;
        else
        	count = count - 2;//2 ";" of more, the last two of the file

        //System.out.println("isLong="+isLong);
        //System.out.println("lineClean=*"+lineClean+"*");
        int beginIndex = 0;//c from create
        int endIndex = 0;
        //System.out.println("count="+count);
        
        //for(int k=0; k<count; k++)
        for(int k=0; k<tgdCount; k++)
        {
        	
        	//System.out.println("k="+k);
	        beginIndex = lineClean.indexOf("create", endIndex);
	        endIndex = lineClean.indexOf(';', beginIndex);
	        //System.out.println("beginIndex="+beginIndex +" endIndex="+endIndex);
	        String tgd = lineClean.substring(beginIndex, endIndex);
	        //System.out.println("tgd="+tgd);
	        
	        String tgdName = (String) tgd.substring(13,tgd.indexOf(" AS ", 14));
	        
	        tgdName = tgdName.replaceAll("work\\.", "").trim();
	        
	        //LogWriter lw = new LogWriter();
	        //lw.writeToFile("tgds.txt", tgdName);
	        
	        //System.out.println("TGD NAME="+tgdName);
	        
	        addTGD(mappingObject.getTgds(), tgd);        	
        }
        
    }

    public void addTGD(ArrayList tgds, String tgd) {
        String cleanTGD = tgd.replaceAll("work\\.", "");
        cleanTGD = cleanTGD.replaceAll("source\\.", "");
        cleanTGD = cleanTGD.replaceAll("target\\.", "");
        cleanTGD = cleanTGD + ";";
        tgds.add(cleanTGD);
    }

    public void createSelectString(MappingObject mappingObject) {
    	int index = 0;
        StringTokenizer stringTokenizer = new StringTokenizer(mappingObject.getResultOfExchangeAt(index), "\n");

        String insertRow = "";
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (token.startsWith("insert")) {
                insertRow = token;
            }
        }
        //We obtain the attributes and the table name
        insertRow = insertRow.substring(12);
        String attributesStr = insertRow;
        StringTokenizer parenthesisTokenizer = new StringTokenizer(insertRow, "(");
        String tableName = parenthesisTokenizer.nextToken();
        //System.out.println("attributesStr="+attributesStr);
        attributesStr = attributesStr.replace(tableName + "(", "");
        attributesStr = attributesStr.replace(")", "");
        StringTokenizer attributesTokenizer = new StringTokenizer(attributesStr, ",");
        ArrayList<String> newAttributes = new ArrayList<>();
        while (attributesTokenizer.hasMoreTokens()) {
            String attribute = attributesTokenizer.nextToken().trim();
            //System.out.println(attribute);
            newAttributes.add(attribute);
        }
        String selectQuery = "SELECT ";
        for (int i = 0; i < newAttributes.size(); i++) {
            selectQuery += newAttributes.get(i);
            if (i != newAttributes.size() - 1) {
                selectQuery += ", ";
            }
        }

        selectQuery += " FROM " + tableName + " WHERE ";

        String wheres = "";
        for (int k = 0; k < newAttributes.size(); k++) {
            if (k != newAttributes.size() - 1) {
                wheres += newAttributes.get(k) + " IS NOT NULL and ";
            } else {
                wheres += newAttributes.get(k) + " IS NOT NULL;";
            }
        }
        selectQuery += wheres;
        mappingObject.setAttributes(newAttributes);
        mappingObject.setSelectString(selectQuery);
    }

    public void loadWorkingTables(String sqlScript) {
        StringTokenizer stringTokenizer = new StringTokenizer(sqlScript, "\n");
        String line2 = "";
        while (stringTokenizer.hasMoreTokens()) {
            String line = stringTokenizer.nextToken();
            if (!isInIgonerdLines(line)) {
                line2 += (line + "\n");
            }
        }
    }

    public boolean isInIgonerdLines(String line) {
        boolean result = false;
        if (line.startsWith("-")) {
            result = true;

        }

        if (line.startsWith("delete from")) {
            result = true;
        }

        if (line.startsWith("BEGIN TRANSACTION")) {
            result = true;
        }

        if (line.startsWith("SET CONSTRAINTS")) {
            result = true;
        }

        if (line.startsWith("COMMIT;")) {
            result = true;
        }

        return result;
    }

    public String cleanResultExchange(String resultExchange) {
        String cleanResultExchange = resultExchange.replaceAll("work\\.", "");
        cleanResultExchange = cleanResultExchange.replaceAll("source\\.", "");
        cleanResultExchange = cleanResultExchange.replaceAll("target\\.", "");
        //int beginIndex = cleanResultExchange.indexOf("cast");
        //int endIndex = cleanResultExchange.indexOf(",");
        //String chunk = cleanResultExchange.substring(beginIndex, endIndex+1);
        //cleanResultExchange = cleanResultExchange.replaceAll(chunk.trim(), "");
        //System.out.println("cleanResultExchange="+cleanResultExchange);
        return cleanResultExchange;
    }

    public void loadResultOfExchange(MappingObject mappingObject, String sqlScript) {
        //System.out.println("in loadResultExchange="+sqlScript);
        
        ArrayList<String> resultOfExchange = new ArrayList<String>();
        //Count how many inserts (one per target schema table)
        int insertsNum = (sqlScript.length() - sqlScript.replace("insert", "").length()) / 6;// insert has 6 characters, then I count the inserts
        
        //System.out.println("insertsNum = "+insertsNum);
        int insertsCounter = 0;
        String sqlScript2 = sqlScript;
        for(int j=0; j<insertsNum; j++)
        {
            int beginIndex = sqlScript.indexOf("insert");
            int endIndex = sqlScript.indexOf(";", beginIndex);
            String insert = sqlScript.substring(beginIndex, endIndex);
            
            //System.out.println("insert resultOfExchange="+insert);
            
            resultOfExchange.add(insert); 
            int newLength = sqlScript.length();
            sqlScript = sqlScript.substring(endIndex+1, newLength);
        }
        
        sqlScript = sqlScript2;
        int beginIndex = sqlScript.indexOf("insert");
        int endIndex = sqlScript.indexOf(";", beginIndex);
        
        for(int j=0; j<insertsNum; j++)
        {
	        
	        //String resultExchange = sqlScript.substring(beginIndex, endIndex);
	        
	        StringTokenizer st = new StringTokenizer(resultOfExchange.get(j), "\n");
	        String newResultExchange = "";	       
	        
	        boolean idHasBeenDeleted = false;
	        while (st.hasMoreTokens()) {
	            String token = st.nextToken().trim();
	            
	            if (token.startsWith("insert")) {
	            	
	                
	                String tableName = token.substring(token.indexOf(".")+1, token.length());
	                //ArrayList<String> attributesNames = configurationReader.getStringsFromFile(configurationReader.getStringsFromFile("GlobalSchemaTables.dat").get(j)+"Attribs.dat");
	                ArrayList<String> attributesNames = JaguarVariables.globalSchemaTablesAttribsComplete.get(tableName);//here they should be complete
	                String attributesRow = "";
	                for (int k = 0; k < attributesNames.size(); k++) {
	                    String attribute = attributesNames.get(k);  
	                    if (attribute.compareTo("provenance") != 0) {
	                        attributesRow += attribute + ", ";
	                    }
	                }
	                attributesRow = attributesRow.substring(0, attributesRow.length() - 2);
	                //System.out.println("attributesRow="+attributesRow);
	                token += "(" + attributesRow + ")";//these will have to be read from 
	            }
	            //the configuration of all the global schema's attributes taken on board
	            if (token.startsWith("cast") && idHasBeenDeleted == false) {
	                idHasBeenDeleted = true;
	                //newResultExchange+=token+"\n";
	            } else 
	            {
	                if (token.startsWith("select")) 
	                {
	                    idHasBeenDeleted = false;
	                }
	                
	                newResultExchange += token + "\n";
	            }
	            
	        }
	        //System.out.println("newResultExchange=*"+newResultExchange+"*");
	        mappingObject.setResultOfExchange(cleanResultExchange(newResultExchange));
	        			
        }
    }
    
	public void estimateGroundTruthPrecision(MappingObject mappingObject) {

		double groundTruthPrecision = 0d;
		int groundTruthTruePositives = 0;
		int groundTruthFalsePositives = 0;
		ResultSet rs=null;
		ResultSet rs2=null;
		mappingObject.getAttributes();
		ArrayList<ArrayList<String>> rows = new ArrayList<>();
		try {

			DatabaseAccessInfo accessInfo = DatabaseUtils
					.getDatabaseAccessInfo("Results");
			Connection connection = DatabaseUtils.createDBConnection(
					accessInfo.getUri(), accessInfo.getUserName(),
					accessInfo.getPassword());
			Class.forName("org.postgresql.Driver");
			connection.setAutoCommit(false);
			Statement stmt = connection.createStatement();
			Statement stmt2 = connection.createStatement();
			ConfigurationReader configurationReader = new ConfigurationReader();
			
			ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesComplete;
			
			for(int t=0; t<tablesNames.size(); t++)
			{
				ArrayList<String> attributesNames = JaguarVariables.globalSchemaTablesAttribsTakenOnBoard.get(tablesNames.get(t));
				
	            String attributes="";
	            
	            attributes = attributesNames.get(0);
	            
	            for(int j=1; j<attributesNames.size(); j++)
	            {
	                attributes += ", " + attributesNames.get(j);
	            }
	            attributes = "id, " + attributes;
	            
	            String sql="SELECT "+attributes+" FROM "+tablesNames.get(t)+" WHERE provenance='"+mappingObject.getProvenance()+"';";			
	            
				rs = stmt.executeQuery(sql);
				
				while (rs.next()) {
					//System.out.println("In resultset cycle");
					ArrayList<String> row = new ArrayList<>();
					// int id = rs.getInt("id");
					// row.add(""+id);
					// String attribute = rs.getString("attribute");
	
					for (int k = 0; k < attributesNames.size(); k++) {
						String attribute = (String) rs.getObject(attributesNames
								.get(k));
						if (attribute != null)
							attribute = attribute.trim();					
						row.add(attribute);
					}
					
					//System.out.println(row.toString());
					
	                String where=" WHERE ";
	                for(int q=0; q<attributesNames.size(); q++)
	                {
	                    if(q<attributesNames.size()-1)
	                        where += attributesNames.get(q) + "='" + row.get(q) + "' AND ";
	                    else
	                        where += attributesNames.get(q) + " ='" + row.get(q) +"';";
	                }
	                
	                String sql2 = "SELECT " + attributes + " FROM "+tablesNames.get(t) + "_gt " + where ;		
	                
	                rs2 = stmt2.executeQuery(sql2);
					boolean isInGroundTruth=false;
	    			while (rs2.next()) {
	    				isInGroundTruth=true;
	    				//System.out.println("IS IN GROUND TRUTH!!");
	    			}
					if(isInGroundTruth==true)
					{
						groundTruthTruePositives++;
					}
					else
					{
						groundTruthFalsePositives++;
					}
				}
			}
			if(rs!=null)
				rs.close();
			if(rs2!=null)
				rs2.close();
			stmt.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			//System.exit(0);
		}
		if(groundTruthTruePositives+groundTruthFalsePositives!=0)
			groundTruthPrecision = (double)groundTruthTruePositives/(groundTruthTruePositives+groundTruthFalsePositives);
		mappingObject.setGtTruePositives(groundTruthTruePositives);
		mappingObject.setGtFalsePositives(groundTruthFalsePositives);
		mappingObject.setGroundTruthPrecision(groundTruthPrecision);
	}
	
	public void estimateMappingGroundTruthPrecision(MappingObject mappingObject) {

		double groundTruthPrecision = 0d;
		int groundTruthTruePositives = 0;
		int groundTruthFalsePositives = 0;

		String tableName = mappingObject.getResultOfExchangeAt(0).substring(12,
				mappingObject.getResultOfExchangeAt(0).indexOf("("));
		
		mappingObject.getAttributes();
		
		try {

			DatabaseAccessInfo accessInfo = DatabaseUtils
					.getDatabaseAccessInfo("Results");
			
			Connection connection = DatabaseUtils.createDBConnection(
					accessInfo.getUri(), accessInfo.getUserName(),
					accessInfo.getPassword());

			Class.forName("org.postgresql.Driver");
			connection.setAutoCommit(false);
			Statement stmt = connection.createStatement();
			Statement stmt2 = connection.createStatement();
			
			ArrayList<String> attributesNames = JaguarVariables.globalSchemaTablesAttribsTakenOnBoard.get(tableName);
			
            String attributes="";
            
            attributes = attributesNames.get(0);
            
            for(int j=1; j<attributesNames.size(); j++)
            {
                attributes += ", " + attributesNames.get(j);
            }
            
            
            attributes = "id, " + attributes;
            
            String sql="SELECT "+attributes+" FROM "+tableName+" WHERE provenance='"+mappingObject.getProvenance()+"';";			
            
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				//System.out.println("In resultset cycle");
				ArrayList<String> row = new ArrayList<>();
				// int id = rs.getInt("id");
				// row.add(""+id);
				// String attribute = rs.getString("attribute");

				for (int k = 0; k < attributesNames.size(); k++) {
					String attribute = (String) rs.getObject(attributesNames
							.get(k));
					if (attribute != null)
						attribute = attribute.trim();					
					row.add(attribute);
				}
				
				//System.out.println(row.toString());
				
                String where=" WHERE ";
                for(int q=0; q<attributesNames.size(); q++)
                {
                    if(q<attributesNames.size()-1)
                        where += attributesNames.get(q) + "='" + row.get(q) + "' AND ";
                    else
                        where += attributesNames.get(q) + " ='" + row.get(q) +"';";
                }
                
               
                String sql2 = "SELECT " + attributes + " FROM " + tableName + "_gt " + where ;		
                
                
				//System.out.println("GT sql2="+sql2);
				
                ResultSet rs2 = stmt2.executeQuery(sql2);
				boolean isInGroundTruth=false;
    			while (rs2.next()) {
    				isInGroundTruth=true;
    				//System.out.println("IS IN GROUND TRUTH!!");
    			}
				if(isInGroundTruth==true)
				{
					groundTruthTruePositives++;
				}
				else
				{
					groundTruthFalsePositives++;
				}
			
			
			
			
			
			}
			//rs.close();
			//stmt.close();
			//connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			// System.exit(0);
		}

		if(groundTruthTruePositives+groundTruthFalsePositives!=0)
			groundTruthPrecision = (double)groundTruthTruePositives/(groundTruthTruePositives+groundTruthFalsePositives);
		
		mappingObject.setGtTruePositives(groundTruthTruePositives);
		mappingObject.setGtFalsePositives(groundTruthFalsePositives);
		
		mappingObject.setGroundTruthPrecision(groundTruthPrecision);
	}
	
		
	

	/**
	 * We will return the number of tuples that have not been covered by mappings in revisedMappings, but that are retrieved by mapping mappingObject.
	 * Method called in MapManipulations.updateAnnotations(..)
	 * 
	 * @param j The Mapping we are asking for.
	 * @param maxs The Mappings that have retrieved tuples.
	 * @param mappingResultSetList Object containing the information of the retrieved tuples.
	 * @return
	 */
	public void getNotRetrieved(Integration i,MappingObject mappingObject, ArrayList<MappingObject> revisedMappings,
			ArrayList<MappingResultsSet> mappingResultSetList)
	{
		MappingResultsSet mappingResultSet = null;
		int similarPositiveRecords=0;
		int notsimilarPositiveRecords=0;
		int similarFalsePositiveRecords=0;
		int notsimilarFalsePositiveRecords=0;	
		int similarTrueNegativeRecords=0;
		int notsimilarTrueNegativeRecords=0;
		int retrievedTPTuples=0;
		int retrievedFPTuples=0;
		int retrievedTNTuples =0;
		/**
		 * We search for the mappingObject feedback info.
		 */
		for(int x=0; x<mappingResultSetList.size(); x++)
		{
			if(mappingResultSetList.get(x).getProvenance() == mappingObject.getProvenance()) 
			{
				mappingResultSet = mappingResultSetList.get(x);
			}
		}
		
		ArrayList<MappingResultsSet> mappingResultSetListLocal = new ArrayList<>();
		
		//Create a local set of results with the results of the revised mappings.
		for(int x=0; x<mappingResultSetList.size(); x++)
		{
			for(int y=0; y<revisedMappings.size(); y++)
			{
				if(mappingResultSetList.get(x).getProvenance() == revisedMappings.get(y).getProvenance())
				{
					mappingResultSetListLocal.add(mappingResultSetList.get(x));
				}
			}
		}
		
		MappingUtil mappingUtil = new MappingUtil();
		
		//System.out.println("Comparing mapping="+mapFeedbackSetK.getProvenance() +" in pos="+k+" rec.siz="+mapFeedbackSetK.getRecords().size());
		
		//for records of the mapping.
		for(int r=0; r<mappingResultSet.getRecords().size(); r++)
		{
			Record record = mappingResultSet.getRecords().get(r);
			boolean isCovered = false;
			
			if(record.getGroundTruthValue().compareTo("tp")==0) 
			{
				for(int s=0; s<mappingResultSetListLocal.size(); s++)
				{
					ArrayList<Record> mappingResultSetLocal = (mappingResultSetListLocal.get(s)).getRecords();
					
					for(int u=0;u<mappingResultSetLocal.size();u++)
					{
						if(mappingUtil.compareRecords(record, mappingResultSetLocal.get(u))==0)
						{
							similarPositiveRecords++;
							isCovered = true;
						}
						else
						{
							notsimilarPositiveRecords++;
						}
					}
				}
				Record duplicateRecord = getDuplicateRecord(i, record);
				if(isCovered==false)
				{
					//notRetrievedTPTuples++;
					/*else
						retrievedTPTuples++;*/
	    			if(duplicateRecord!=null){//I know it is TP 
	        			if(duplicateRecord.hasERFeedback)//therefore, is duplicate, has feedback and counts 
	        			{
	        				if(duplicateRecord.isErFeedback())
	        				{
	        					if(duplicateRecord.isDuplicateAndThisRecordCounts())
	        					{
	        						//notRestrievedDuplicates++;
	        						notRetrievedTPTuples++;
	        						//System.out.println("tp,isCovered=false,duplicateRecord!=null,duplicateRecord.isErFeedback()=true,counts=true");
	        					}
	        				}
	        				else
	        				{
	        					notRetrievedTPTuples++;
	        				}
	        			}
	        			else
	        			{
	        				//System.out.println("tp,isCovered=false,duplicateRecord!=null,duplicateRecord.isErFeedback()=false");
	        				notRestrievedDuplicates++;//Direct, No feedback---> to duplicated
	        			}
	    			}
	    			else
	    			{
	    				notRetrievedTPTuples++;
	    			}
				}
			}
			else if(record.getGroundTruthValue().compareTo("fp")==0)
			{
				for(int s=0; s<mappingResultSetListLocal.size(); s++)
				{
					ArrayList<Record> recordsOfSet = (mappingResultSetListLocal.get(s)).getRecords();
					for(int u=0;u<recordsOfSet.size();u++)
					{
						if(mappingUtil.compareRecords(record, recordsOfSet.get(u))==0)
						{
							similarFalsePositiveRecords++;
							isCovered = true;
						}
						else
						{
							notsimilarFalsePositiveRecords++;
						}
					}
				}
				/*if(record.getAttributesValues().toString().compareTo("[restrains, immoderate, leak, TIMANDRA, timandra]")==0)
				{
					System.out.println("record");
					System.out.println("record:"+record.getAttributesValues().toString()+" prov="+record.getProvenance());
					System.out.println("has map feedback:"+record.isHasERFeedback());
					System.out.println("map feedback:"+record.feedbackValue);
					System.out.println("is duplicate:"+record.isDuplicate());
					System.out.println("has er feedback:"+record.hasERFeedback);
					System.out.println("er feedback:"+record.erFeedback);
					System.out.println("er counts:"+record.duplicateAndThisRecordCounts);
					System.out.println();
				}*/
				Record duplicateRecord = getDuplicateRecord(i, record);
				if(isCovered==false)
				{
					//System.out.println("is covered==false");
	    			if(duplicateRecord!=null){
	    				//System.out.println("duplicateRecord!=null");
	    				/*
	    				System.out.println(duplicateRecord.getAttributesValues().toString());
	    				if(duplicateRecord.getAttributesValues().toString().compareTo("[restrains, immoderate, leak, TIMANDRA, timandra]")==0)
	    				{
	    					System.out.println("duplicateRecord");
	    					System.out.println("record:"+duplicateRecord.getAttributesValues().toString()+" prov="+duplicateRecord.getProvenance());
	    					System.out.println("has map feedback:"+duplicateRecord.isHasERFeedback());
	    					System.out.println("map feedback:"+duplicateRecord.feedbackValue);
	    					System.out.println("is duplicate:"+duplicateRecord.isDuplicate());
	    					System.out.println("has er feedback:"+duplicateRecord.hasERFeedback);
	    					System.out.println("er feedback:"+duplicateRecord.erFeedback);
	    					System.out.println("er counts:"+duplicateRecord.duplicateAndThisRecordCounts);
	    					System.out.println();
	    				}*/
	    				
	        			if(duplicateRecord.hasERFeedback)//therefore, is duplicate, has feedback and counts 
	        			{
	        				//System.out.println("Has ER feedback.");
	        				if(duplicateRecord.isErFeedback())
	        				{
	        					if(duplicateRecord.isDuplicateAndThisRecordCounts())
	        					{
	        						//notRestrievedDuplicates++;
	        						notRetrievedFPTuples++;
	        						//System.out.println("fp,isCovered=false,duplicateRecord!=null,duplicateRecord.isErFeedback()=true,counts=true");
	        						//System.out.println("notRetrievedFPTuples++");
	        					}
	        				}
	        				else
	        				{
	        					//System.out.println("value of erFeedback= false.");
	        					notRetrievedFPTuples++;
	        				}
	        				
	        			}
	        			else
	        			{
	        				//System.out.println("notRestrievedDuplicates++");
	        				//System.out.println("Has NOT ER feedback.");
	        				
	        				notRestrievedDuplicates++;//No feedback---> to duplicated
	        				//System.out.println("fp,isCovered=false,duplicateRecord!=null,duplicateRecord.isErFeedback()=false");
	        			}
	    			}
	    			else
	    			{
	    				//System.out.println("duplicateRecord==null then its a duplicate record");
	    				//System.out.println("notRetrievedFPTuples++");
	    				notRetrievedFPTuples++;
	    				
	    			}
	    			
				}
				/*else
				{
					retrievedFPTuples++;
				}*/
				
				/*else
				{
					if(record.getGroundTruthValue().compareTo("tn")==0)
					{
						//System.out.println("Checking record tn");
						for(int s=0; s<mappingResultSetListLocal.size(); s++)
						{
							ArrayList<Record> recordsOfSet = (mappingResultSetListLocal.get(s)).getRecords();
							
							for(int u=0;u<recordsOfSet.size();u++)
							{
								Record recordOfSet = recordsOfSet.get(u);
								if(mappingUtil.compareRecords(record, recordOfSet)==0)
								{
									similarTrueNegativeRecords++;  
									isCovered = true;
								}
								else
								{
									notsimilarTrueNegativeRecords++;  
								}
							}
						}
						if(isCovered==false)
						{
							notRetrievedTNTuples++;
						}
						else
						{
							retrievedTNTuples++;
						}						
					}
				}*/
				
			}

		}
	}
	
	/**
	 * In this function I will calculate the combined precision taking into account the feedback obtained.
	 * 
	 * @param mappingObject
	 * @param mappingObjectSet
	 * @param mapFeedbackSetList
	 */
	public void getNotRetrievedFb(MappingObject mappingObject, ArrayList<MappingObject> mappingObjectSet,
			ArrayList<MappingResultsSet> mapFeedbackSetList)
	{
		int notCovered=0;
		int max1Pos=-1;
		
		MappingResultsSet mapFeedbackSet=null;
		for(int x=0; x<mapFeedbackSetList.size(); x++)
		{
			if(mapFeedbackSetList.get(x).getProvenance() == mappingObject.getProvenance())
			{
				mapFeedbackSet = mapFeedbackSetList.get(x);//this is m
			}
		}
		
		ArrayList<MappingResultsSet> MapFeedbackSetListLocal = new ArrayList<>();// This is M
		for(int x=0; x<mapFeedbackSetList.size(); x++)
		{
			for(int y=0; y<mappingObjectSet.size(); y++)
			{
				if(mapFeedbackSetList.get(x).getProvenance() == mappingObjectSet.get(y).getProvenance())
				{
					MapFeedbackSetListLocal.add(mapFeedbackSetList.get(x));
				}
			}
		}
		
		MappingUtil mappingUtil = new MappingUtil();
		
		//System.out.println("Comparing mapping="+mapFeedbackSetK.getProvenance() +" in pos="+k+" rec.siz="+mapFeedbackSetK.getRecords().size());
		
		int similarPositiveRecords=0;
		int notsimilarPositiveRecords=0;
		
		int similarFalsePositiveRecords=0;
		int notsimilarFalsePositiveRecords=0;
		
		int similarTrueNegativeRecords=0;
		int notsimilarTrueNegativeRecords=0;
		
		int retrievedTPTuplesFb=0;			
		int retrievedFPTuplesFb=0;
		int retrievedTNTuplesFb=0;
		
		for(int r=0; r<mapFeedbackSet.getRecords().size(); r++)
		{
			Record record = mapFeedbackSet.getRecords().get(r);
			
			boolean isCovered = false;
			
			String recordValue = "";
			
			recordValue = record.getGroundTruthValue();

			if(recordValue.compareTo("tp")==0)
			{
				//System.out.println("Checking record tp");
				for(int s=0; s<MapFeedbackSetListLocal.size(); s++)
				{
					ArrayList<Record> recordsOfSet = (MapFeedbackSetListLocal.get(s)).getRecords();
					
					for(int u=0;u<recordsOfSet.size();u++)
					{
						Record recordOfSet = recordsOfSet.get(u);
						if(mappingUtil.compareRecords(record, recordOfSet)==0)
						{
							similarPositiveRecords++;
							isCovered = true;
						}
						else
						{
							notsimilarPositiveRecords++;
						}
					}
				}
				if(isCovered==false)
				{
					notRetrievedTPTuplesFb++;
				}
				else
				{
					retrievedTPTuplesFb++;
				}
			}
			else
			{
				//if(record.getFeedbackValue().compareTo("fp")==0)
				if(recordValue.compareTo("fp")==0)
				{
					//System.out.println("Checking record fp");
					for(int s=0; s<MapFeedbackSetListLocal.size(); s++)
					{
						ArrayList<Record> recordsOfSet = (MapFeedbackSetListLocal.get(s)).getRecords();
						
						for(int u=0;u<recordsOfSet.size();u++)
						{
							if(mappingUtil.compareRecords(record, recordsOfSet.get(u))==0)
							{
								similarFalsePositiveRecords++;
								isCovered = true;
							}
							else
							{
								notsimilarFalsePositiveRecords++;
							}
						}
					}
					if(isCovered==false)
					{
						notRetrievedFPTuplesFb++;
					}
					else
					{
						retrievedFPTuplesFb++;
					}
				}
				else
				{
					if(recordValue.compareTo("tn")==0)
					{
						//System.out.println("Checking record tn");
						for(int s=0; s<MapFeedbackSetListLocal.size(); s++)
						{
							ArrayList<Record> recordsOfSet = (MapFeedbackSetListLocal.get(s)).getRecords();
							
							for(int u=0;u<recordsOfSet.size();u++)
							{
								Record recordOfSet = recordsOfSet.get(u);
								if(mappingUtil.compareRecords(record, recordOfSet)==0)
								{
									similarTrueNegativeRecords++;
									isCovered = true;
								}
								else
								{
									notsimilarTrueNegativeRecords++;
								}
							}
						}
						if(isCovered==false)
						{
							notRetrievedTNTuplesFb++;
						}
						else
						{
							retrievedTNTuplesFb++;
						}
					}
				}
			}
		}
	}
	
	public void countFalseNegatives(ArrayList<MappingResultsSet> mapFeedbackSetList, ArrayList<MappingObject> M, 
			GroundTruth groundTruth)
	{
		int tp=0;
		ArrayList<Record> countBuffer = new ArrayList<Record>();
		MappingUtil mapUtil = new MappingUtil();
		for(int j=0; j<M.size(); j++)//for all selected mappings
		{
			MappingObject mapping = M.get(j);
			
			for(int k=0; k<mapFeedbackSetList.size(); k++)//for all mappings' results
			{
				MappingResultsSet mapFeedbackSet = mapFeedbackSetList.get(k);
				if(mapping.getProvenance() == mapFeedbackSet.getProvenance())//if mapping = results
				{
					ArrayList<Record> records = mapFeedbackSet.getRecords();
					for(int q=0; q<records.size(); q++)//for all records
					{
						Record recordA = records.get(q);
						int isEqual=-1;
						for(int y=0; y<countBuffer.size(); y++)//has record been counted before
						{
							Record recordB = countBuffer.get(y);
							isEqual = mapUtil.compareRecords(recordA, recordB);
							if(isEqual==0)
							{
								//System.out.println("Checking fb values: r1="+recordA.getFeedbackValue()+" r2="+recordB.getFeedbackValue());
								break;
							}
						}
						if(isEqual!=0)//if recordA hasn't been counted before
						{
							countBuffer.add(recordA);
							if(groundTruth.isInGroundTruth(recordA))
							{
								String val = recordA.getGroundTruthValue();
								
								if(val.compareTo("tp")==0)
								{
									tp++;
								}
							}							
						}

					}
					
				}
			}
		}
		falseNegatives = groundTruth.getGroundTruth().size() - tp;
	}
    public ArrayList<String> loadTGDs(String sqlScript) {
    	ArrayList<String> tgds = new ArrayList<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(sqlScript, "\n");
        String lineClean = "";
        boolean hadCreateSentence = false;
        boolean isLong = true;
        int timesR = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String line = stringTokenizer.nextToken();
            if (line.startsWith("create")) {
                hadCreateSentence = true;
            }
            if (line.startsWith("-")) {
                timesR++;
                if (timesR == 2 && hadCreateSentence == false) {
                    isLong = false;// long mapping version
                }
            }
            if (!isInIgonerdLines(line))//If this line should NOT be ignored,
            {
                lineClean += (line + "\n");//we add it.
            }
        }

        int count = lineClean.length() - lineClean.replace(";", "").length();
        
        int insertCount = lineClean.length() - lineClean.replace("insert", "").length();
        
        int tgdCount = (lineClean.length() - lineClean.replace("create table ", "").length())/13;
        
        //System.out.println("tgdCount="+tgdCount);
        
        if(insertCount > 6)//6 = number of chars in "insert"
        	count = count - 3;
        else
        	count = count - 2;//2 ";" of more, the last two of the file
        
        //System.out.println("isLong="+isLong);
        //System.out.println("lineClean=*"+lineClean+"*");
        int beginIndex = 0;//c from create
        int endIndex = 0;
        //System.out.println("count="+count);
        
        //for(int k=0; k<count; k++)
        for(int k=0; k<tgdCount; k++)
        {
        	//System.out.println("k="+k);
	        beginIndex = lineClean.indexOf("create", endIndex);
	        endIndex = lineClean.indexOf(';', beginIndex);
	        //System.out.println("beginIndex="+beginIndex +" endIndex="+endIndex);
	        String tgd = lineClean.substring(beginIndex, endIndex);
	        //System.out.println("tgd="+tgd);
	        
	        //String tgdName = (String) tgd.substring(13,tgd.indexOf(" AS ", 14));
	        
	        //tgdName = tgdName.replaceAll("work\\.", "").trim();
	        
	        //LogWriter lw = new LogWriter();
	        //lw.writeToFile("tgds.txt", tgdName);
	        
	        //System.out.println("TGD NAME="+tgdName);
	        String cleanTGD = tgd.replaceAll("work\\.", "");
	        cleanTGD = cleanTGD.replaceAll("source\\.", "");
	        cleanTGD = cleanTGD.replaceAll("target\\.", "");
	        cleanTGD = cleanTGD + ";";
	        tgds.add(cleanTGD);
        }
        return tgds;
    }
    public ArrayList<String> loadResultOfExchange(String sqlScript) {
        //System.out.println("in loadResultExchange="+sqlScript);
        ArrayList<String> resultOfExchange = new ArrayList<String>();
        ArrayList<String> resultOfExchangeClean = new ArrayList<String>();
        //Count how many inserts (one per target schema table)
        //insert has 6 characters, then I count the inserts
        int insertsNum = (sqlScript.length() - sqlScript.replace("insert", "").length()) / 6;
        //System.out.println("insertsNum = "+insertsNum);
        //int insertsCounter = 0;
        String sqlScript2 = sqlScript;
        for(int j=0; j<insertsNum; j++)
        {
            int beginIndex = sqlScript.indexOf("insert");
            int endIndex = sqlScript.indexOf(";", beginIndex);
            String insert = sqlScript.substring(beginIndex, endIndex);
            
            //System.out.println("insert resultOfExchange="+insert);
            //We take off the ";" and save the query.
            resultOfExchange.add(insert.replaceAll(";", ""));
            int newLength = sqlScript.length();
            sqlScript = sqlScript.substring(endIndex+1, newLength);
        }
        
        sqlScript = sqlScript2;
        //int beginIndex = sqlScript.indexOf("insert");
        //int endIndex = sqlScript.indexOf(";", beginIndex);
        
        for(int j=0; j<insertsNum; j++)
        {
	        
	        //String resultExchange = sqlScript.substring(beginIndex, endIndex);
	        int unionNum = (resultOfExchange.get(j).length() -  resultOfExchange.get(j).replaceAll("UNION", "").length() )/5;
	        //System.out.println("unionNum="+unionNum);
	        
	        StringTokenizer st = new StringTokenizer(resultOfExchange.get(j), "\n");
	        String newResultExchange = "";
	        //System.out.println("tokens="+st.countTokens());
	        boolean idHasBeenDeleted = false;
	        String attributesRow="";
	        while (st.hasMoreTokens()) {
	            String token = st.nextToken().trim();
	            
	            /**
	             * Creating the insert row with attributes.
	             */
	            if(token.startsWith("insert")) {
	            	//System.out.println(token);               
	                String tableName = token.substring(token.indexOf(".")+1, token.length());
	                	               
	                ArrayList<String> attributesNames = JaguarVariables.globalSchemaTablesAttribsComplete.get(tableName);//here they should be complete
	                
	                attributesRow = attributesNames.get(0);
	                for (int k = 1; k < attributesNames.size(); k++) {
	                    String attribute = attributesNames.get(k);
	                    attributesRow += ", " + attribute;
	                }
	                
	                //System.out.println("attributesRow="+attributesRow);
	                attributesRow = token+ "(" + attributesRow + ")";//these will have to be read from.
	                
	            }
	            
	            /**
	             * The configuration of all the global schema's attributes taken on board.
	             * Ignores the token of id.
	             */
	            if (token.startsWith("cast") && idHasBeenDeleted == false) 
	            {
	            	//System.out.println("Ignored token="+token);
	                idHasBeenDeleted = true;
	            }
	            else
	            {
		            /**
		             * Replacing a UNION row by an insert row. 
		             */
		            if(token.startsWith("UNION") || token.startsWith("insert"))
		            {
		            	token = attributesRow + "\n";
		            }
		            
	                if (token.startsWith("select"))
	                {
	                    idHasBeenDeleted = false;
	                }
	                
	                if (token.startsWith("cast") || token.startsWith("select") || token.startsWith("from"))
	                {
	                	token = token + "\n";	
	                }
	                
		            /**
		             * Adding a ; and a separator |.
		             */
		            if(token.startsWith("work"))
		            {
		            	token = token + ";|";		            			            	
		            }	
		            
	                newResultExchange +=  token;
	            }
	        }
	        StringTokenizer resExSeparatorTokenizer = new StringTokenizer(newResultExchange,"|");
	        while(resExSeparatorTokenizer.hasMoreTokens())
	        {
	        	String singleResEx = resExSeparatorTokenizer.nextToken();
	        	//System.out.println("newResultExchange=*"+cleanResultExchange(singleResEx)+"*");
	        	resultOfExchangeClean.add(cleanResultExchange(singleResEx));
	        }
        }        
        return resultOfExchangeClean;
    }
    
    public ArrayList<String> getTgdsSingle(ArrayList<String> tgdsAll,String resultOfExchange)
    {
    	ArrayList<String> tgdsSingle = new ArrayList<>();
    	ArrayList<String> tgdsNames = new ArrayList<>();
    	/**
    	 * Getting the TGDs names.
    	 */
    	StringTokenizer st1 = new StringTokenizer(resultOfExchange,"(");    	
    	while(st1.hasMoreTokens())
    	{
    		String tokStr = st1.nextToken();
    		if(tokStr.startsWith("TARGET_VALUES"))
    		{
    			String tgdName = tokStr.substring(0, tokStr.indexOf("."));
    			boolean exist=false;
    			for(int j=0; j<tgdsNames.size(); j++)
    			{
    				if(tgdsNames.get(j).compareTo(tgdName)==0)
    				{
    					exist=true;
    				}
    			}
    			if(exist==false)
    				tgdsNames.add(tgdName);
    		}
    	}
    	/**
    	 * Getting the TGDs SQL scripts.
    	 */
    	for(int j=0; j<tgdsNames.size(); j++)
    	{
    		String tgdName = tgdsNames.get(j);
    		for(int k=0; k<tgdsAll.size(); k++)
    		{
    			String tgd = tgdsAll.get(k);
    			if(tgd.contains(tgdName))
    			{
    				tgdsSingle.add(tgd);
    			}
    		}
    	}
    	/**
    	 * Getting the TGDs working tables.
    	 */
    	ArrayList<String> froms = new ArrayList<>();
    	for(int j=0; j<tgdsSingle.size(); j++)
    	{
    		String tgd = tgdsSingle.get(j);
    		String from = tgd.substring(tgd.indexOf("from")+5,tgd.length() );
    		//System.out.println("froms="+from);
    		if(!from.contains("where") && !from.contains(" AS "))
    		{
    			from = from.substring(0, from.length()-1);
    		}    		
    		//System.out.println("new froms="+from);
    		froms.add(from);    		
    	}
    	
    	
    	for(int j=0; j<froms.size(); j++)
    	{
    		String fromName = froms.get(j);
    		for(int k=0; k<tgdsAll.size(); k++)
    		{
    			String tgd = tgdsAll.get(k);
    			
    			if(tgd.contains(" "+fromName+" "))
    			{
    				tgdsSingle.add(tgd);
    			}
    		}
    	}    	    	
    	return tgdsSingle;    	
    }
    
    
    
 public String createSelectStringSingle(MappingObject mappingObject) {

        StringTokenizer stringTokenizer = new StringTokenizer(mappingObject.getResultOfExchangeAt(0), "\n");
        String selectString="";
        String insertRow = "";
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (token.startsWith("insert")) {
                insertRow = token;
            }
        }
        //We obtain the attributes and the table name
        insertRow = insertRow.substring(12);
        String attributesStr = insertRow;
        StringTokenizer parenthesisTokenizer = new StringTokenizer(insertRow, "(");
        String tableName = parenthesisTokenizer.nextToken();
        //System.out.println("attributesStr="+attributesStr);
        attributesStr = attributesStr.replace(tableName + "(", "");
        attributesStr = attributesStr.replace(")", "");
        StringTokenizer attributesTokenizer = new StringTokenizer(attributesStr, ",");
        ArrayList<String> newAttributes = new ArrayList<>();
        while (attributesTokenizer.hasMoreTokens()) {
            String attribute = attributesTokenizer.nextToken().trim();
            //System.out.println(attribute);
            newAttributes.add(attribute);
        }
        String selectQuery = "SELECT ";
        for (int i = 0; i < newAttributes.size(); i++) {
            selectQuery += newAttributes.get(i);
            if (i != newAttributes.size() - 1) {
                selectQuery += ", ";
            }
        }
        
        selectQuery += " FROM " + tableName + " WHERE ";
        
        String wheres = "";
        for (int k = 0; k < newAttributes.size(); k++) {
            if (k != newAttributes.size() - 1) {
                wheres += newAttributes.get(k) + " IS NOT NULL and ";
            } else {
                wheres += newAttributes.get(k) + " IS NOT NULL;";
            }
        }
        selectQuery += wheres;
        mappingObject.setAttributes(newAttributes);
        mappingObject.setSelectString(selectQuery);
        
        return selectString;
    }
    public void setActiveFalseAllExistingMappings()
    {
    	for(int i=0; i<mappingsList.size(); i++)
    	{
    		mappingsList.get(i).setActive(false);
    	}
    }
    public void setActiveTrueToExistingMapping(MappingObject mappingObject)
    {
    	//Variables used in the previous comparison method:
    	/*String table1 = mappingObject.getTableName();	
        FORule forule1 = mappingObject.getForule();*/
    	for(int i=0; i<mappingsList.size(); i++)
    	{
    		MappingObject mapObject = mappingsList.get(i);
    		/*FORule forule2 = mapObject.getForule();
    		String table2 = mapObject.getTableName();
    		if (forule1.hasEqualViews(forule2)
    				&& forule1.compareTo(forule2)==0
    				&& table1.compareTo(table2)==0)
    			*/
    		/*
    		 * compareMappings2(...) uses the SS attributes' names to recognize the 
    		 * mappings.
    		 */
    		if(compareMappingsSSAttributes(mappingObject,mapObject))
    		{
    			(mappingsList.get(i)).setActive(true);
    			//System.out.println("Activating mapping:"+mappingObject.getTableName());
    			//if(mappingObject.getTableName().compareTo("testt")==0)
    			//	System.out.println(mappingObject.getResultOfExchangeAt(0));
    		}
    	}
    }
    
    public void setActiveTrueToExistingMappingUsingResultOfExchange(MappingObject mappingObject)
    {
    	String r1 = mappingObject.getResultOfExchangeAt(0);
    	for(int i=0; i<mappingsList.size(); i++)
    	{
    		MappingObject mapObject = mappingsList.get(i);
    		String r2 = mapObject.getResultOfExchangeAt(0);
    		if (r1.compareTo(r2)==0) {
    			(mappingsList.get(i)).setActive(true);
    		}
    	}
    }
    public int getMappingsListSize()
    {
    	return mappingsList.size();
    }
    
    /**
     * Method to cmpare two mappings based on their FORules.
     * 
     * @param mapping1
     * @param mapping2
     * @return
     */
    public boolean compareMappingsFORules(MappingObject mapping1, MappingObject mapping2)
    {
    	FORule forule1 = mapping1.getForule();
    	FORule forule2 = mapping2.getForule();
    	//if(forule1.hasEqualViews(forule2) && forule2.hasEqualViews(forule1))
    	if(forule1.hasEqualViews(forule2))
    		return true;
    	else
    		return false;
    }
    /**
     * Method to compare the mappings based on their SS attributes. 
     * 
     * @param mapping1
     * @param mapping2
     * @return true if the mappings' SS attributes are equal, false otherwise. 
     */
    public boolean compareMappingsSSAttributes(MappingObject mapping1, MappingObject mapping2)
    {
    	ArrayList<String> ssAttributes1 = mapping1.getSSAttributes();
    	ArrayList<String> ssAttributes2 = mapping2.getSSAttributes();
    	boolean equalAttributes = true;
    	if(ssAttributes1.size() != ssAttributes2.size())
    	{
    		return false;
    	}
    	else if(mapping1.getTableNumber() != mapping2.getTableNumber())
    	{
    		return false;
    	}
    	else
    	{
    		for(int j=0; j<ssAttributes1.size(); j++)
    		{
    			String attribute1 = ssAttributes1.get(j);
    			String attribute2 = ssAttributes2.get(j);
    			if(attribute1.compareTo(attribute2)!=0)
    			{
    				equalAttributes = false;
    				j = ssAttributes1.size();
    			}
    		}
    	}
    	return equalAttributes;
    }
   
}
