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

package uk.ac.man.jaguar.controller.feedback.map;

import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.feedback.AbstractFeedback;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.MappingUtil;

/**
 *
 * @author osornogf
 */
public class MapFeedback extends AbstractFeedback{

    public final int FEEDBACK_T0_COLLECT_PER_MAPPING = 352;

    public int totalTruePositives;
    public int totalFalsePositives;
    public int totalTrueNegatives;
    public int totalFalseNegatives;
    public int totalDuplicates;
    
    public int numMapForAveragePrecision;
    public int totalNumMap;
    public int fbPropToMatch;
	public int truePositivesGT;
	public int falsePositivesGT;
	public int trueNegativesGT;
	public int falseNegativesGT;
	public int duplicatesGT;
	public int mappingCounter;
	
    public double gtAveragePrecision;

    
    // Array List of results.
    // Each object is for the results of one mapping.
    // There can be several mappings for one single table.
    ArrayList<MappingResultsSet> mappingResultsSetList;
    ArrayList<TableFeedbackSet> tableFeedbackSet;

    public MapFeedback()
    {
    	mappingCounter = 0;
        mappingResultsSetList = new ArrayList<MappingResultsSet>();        
        tableFeedbackSet = new ArrayList<>();
        ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesComplete;
        for(int j=0; j<tablesNames.size(); j++)
        {
        	TableFeedbackSet tableFeedback = new TableFeedbackSet();
        	tableFeedback.setTableName(tablesNames.get(j));
        	tableFeedbackSet.add(tableFeedback);
        }
    }
    
    public ArrayList<TableFeedbackSet> getTableFeedbackSet() {
		return tableFeedbackSet;
	}

	public void setTableFeedbackSet(ArrayList<TableFeedbackSet> tableFeedbackSet) {
		this.tableFeedbackSet = tableFeedbackSet;
	}

	public int getMappingCounter() {
        return mappingCounter;
    }

    public void setMappingCounter(int mappingCounter) {
        this.mappingCounter = mappingCounter;
    }

    public void addMappingResults(MappingResultsSet mapFeedbackSet)
    {

    	mappingResultsSetList.add(mapFeedbackSet);

        mappingCounter++;
    }
    
    public void setMapFeedbackSet(MappingResultsSet mapFeedbackSet)
    {
    	int provenance = mapFeedbackSet.getProvenance();
    	for(int k=0; k<mappingResultsSetList.size(); k++)
    	{
    		mapFeedbackSet = mappingResultsSetList.get(k);
    		if(mapFeedbackSet.getProvenance()==provenance)
    		{
    			mappingResultsSetList.set(k, mapFeedbackSet);
    		}
    	}
    }

    public MappingResultsSet getMapFeedbackSetAt(int index)
    {
    	return mappingResultsSetList.get(index);
    }

    public ArrayList<MappingResultsSet> getMappingResultsSetList()
    {
    	return mappingResultsSetList;
    }

    /**
     * Propagation to only one record.
     * @param feedback Feedback being propagated.
     * @param record Target record.
     */
    public void propagateFeedbackToMap(String feedback, Record record)
    {
        int key = record.getProvenance();

        MappingResultsSet mapFeedbackSet = getMapResultsSet(record.getProvenance());


        if(mapFeedbackSet!=null)
        {
    		ConfigurationReader conf = new ConfigurationReader();
    		ArrayList<String> tablesNames = conf.getStringsFromFile("GlobalSchemaTables.dat");

    		for(int t=0; t<tablesNames.size(); t++)
    		{
	            ArrayList<Record> records = mapFeedbackSet.getRecords();
	            for(int k=0; k<records.size(); k++)
	            {
	                if(records.get(k).compareTo(record)==0)
	                {
	                    if(records.get(k).getFeedbackValue().compareTo("")==0)
	                    {
	                        records.get(k).setFeedbackValue(feedback);

	                        System.out.println("ER feedback propagated to record=" + records.get(k).getAttributesValues().toString());
	                    }
	                }
	            }
    		}
        }
        else
        {
            //System.out.println("Mapping not found in propagation"+key);
        }


    }
    /**
     * 
     * @param record
     * @return
     */
    public String getFeedbackOfRecord(Record record)
    {
        String feedbackValue = "";
        int provenance = record.getProvenance();

        MappingResultsSet mapFeedbackSet = getMapResultsSet(provenance);
        MappingUtil mappingUtil = new MappingUtil();
        if(mapFeedbackSet!=null)
        {
    		ConfigurationReader conf = new ConfigurationReader();
    		//ArrayList<String> tablesNames = conf.getStringsFromFile("GlobalSchemaTables.dat");
    		ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesTakenOnBoard;
    		for(int t=0; t<tablesNames.size(); t++)
    		{
	            ArrayList<Record> records = mapFeedbackSet.getRecords();

	            for(int k=0; k<records.size(); k++)
	            {
	            	if(mappingUtil.compareRecords(records.get(k), record)==0)
	                {
	                    feedbackValue = records.get(k).getFeedbackValue();
	                }
	            }
    		}
        }
        else
        {
            //System.out.println("Mapping not found searching for a record. key="+provenance);
        }
        return feedbackValue;
    }

    public void reset()
    {
        totalTruePositives = 0;
        totalFalsePositives = 0;
        totalTrueNegatives = 0;
        totalFalseNegatives = 0;
        totalDuplicates = 0;
        numMapForAveragePrecision = 0;
        totalNumMap = 0;
        gtAveragePrecision = 0;
    }
    
    public MappingResultsSet getMapResultsSet(int provenance)
    {
    	MappingResultsSet result = null;
    	for(int k=0; k<mappingResultsSetList.size(); k++)
    	{
    		MappingResultsSet mappingResultsSet = mappingResultsSetList.get(k); 
    		int mapFeedbackSetProvenance = mappingResultsSet.getProvenance();
    		
    		if(mapFeedbackSetProvenance == provenance)
    		{
    			result = mappingResultsSet;
    			break;
    		}
    	}
    	return result;
    }

    public void setMappingResultsSetList(
			ArrayList<MappingResultsSet> mappingResultsSetList) {
		this.mappingResultsSetList = mappingResultsSetList;
	}

    /* 
     * We save the feedback to tableFeedbackSet because mappingResultSetList is regenerated
     * after other episode and we don't want to lose the feedback.
     * 
     */
	public void saveFeedbackToTableFeedbackSet(Record record)
    {
    	int entityNumber = record.getEntityNumber();
    	MappingUtil mapUtil = new MappingUtil();
    	TableFeedbackSet tableFeedback = tableFeedbackSet.get(entityNumber);
    	Boolean recordExists = false;
    	for(int i=0; i<tableFeedback.getRecords().size(); i++)
    	{
    		Record recordList = tableFeedback.getRecords().get(i);
    		if(mapUtil.compareRecords(record, recordList)==0)
    		{
    			recordExists = true;
    		}
    	}
    	if(recordExists==false)
    	{
    		((tableFeedbackSet.get(entityNumber)).getRecords()).add(record);
    	}
    }

	/**
	 * This method searches for previous equal records with feedback. 
	 * If there is an equal record with feedback, then the new record
	 * copies the feedback.
	 * @param record
	 */
    public void setFeedbackIfExists(Record record)
    {
    	int entityNumber = record.getEntityNumber();
    	MappingUtil mapUtil = new MappingUtil();
    	TableFeedbackSet tableFeedback = tableFeedbackSet.get(entityNumber);
    	for(int i=0; i<tableFeedback.getRecords().size(); i++)
    	{
    		Record recordList = tableFeedback.getRecords().get(i);
    		if(mapUtil.compareRecords(record, recordList)==0)
    		{
    			record.setHasMapFeedback(true);
    			record.setFeedbackValue(recordList.getFeedbackValue());
    		}
    	}
    }
    
    public void deletePreviousResults()
    {
    	for(int r=0; r<mappingResultsSetList.size(); r++)
    		(mappingResultsSetList.get(r)).freeResources();
    	mappingResultsSetList = null;
    	System.gc();
    	mappingResultsSetList = new ArrayList<MappingResultsSet>();
    }
}
