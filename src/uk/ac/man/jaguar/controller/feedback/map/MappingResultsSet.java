/* * Copyright (C) 2014 Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
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
import java.util.Random;
import java.util.Collections;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;



import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.feedback.operators.SyntheticFeedbackGeneratorMap;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.ConfigurationReader;

/**
 *
 * @author osornogf
 */
public class MappingResultsSet {
	
    public boolean hasRecords=false;
    public boolean mappingUsedForPrecision = false;
    public boolean feedbackComplete;
    
    public int provenance;
    public int feedbackObtainedForThisMap;
    public int relationsNumber;
    
    public double precision;
    
    public String tableName = null;
    
    public ArrayList<Record> records;
    public ArrayList<String>[] attributesNames;
    
    
    
    public MappingResultsSet() {
    	ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesComplete;
    	relationsNumber = tablesNames.size();
    	records = new ArrayList<>();
        this.precision = 0.0d;
        this.provenance = -1;
        this.feedbackObtainedForThisMap = 0;
        this.feedbackComplete = false;
        attributesNames = new ArrayList[relationsNumber];
    }    
    
    public void addRecord(Record newRecord)
    {
    	this.records.add(newRecord);
    }
    public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public MappingResultsSet(int provenance, ArrayList<Record> records, double precision) {
        this.provenance = provenance;
        this.records = records;
        this.precision = precision;
        if(this.records!=null)
            hasRecords = true;
        attributesNames = new ArrayList[relationsNumber];   
    }    
    
    public ArrayList<String> getAttributesNames(int relationNumber) {
		return attributesNames[relationNumber];
	}

	public void setAttributesNames(ArrayList<String> attributesNames,int relationNumber) {
		this.attributesNames[relationNumber] = attributesNames;
	}

	public boolean isFeedbackComplete() {
    	int recordsNumber = records.size();
		
		
		int feedbackNumber=0;
    	for(int k=0; k<records.size(); k++)
    	{
    		Record r = records.get(k);
    		if(r.hasMapFeedback())
    		{
    			feedbackNumber++;
    		}
    	}
    	
    	if(feedbackNumber<recordsNumber)
    		feedbackComplete=false;
    	else
    		feedbackComplete=true;
    	
    	if(records.size()==0)
    		feedbackComplete=true;
    	
		return feedbackComplete;
	}

	public void setFeedbackComplete(boolean feedbackComplete) {
		this.feedbackComplete = feedbackComplete;
	}

	public int truePositivesCount;
    public int getTruePositivesCount() {
		return truePositivesCount;
	}

	public void setTruePositivesCount(int truePositivesCount) {
		this.truePositivesCount = truePositivesCount;
	}

	public int getFalsePositivesCount() {
		return falsePositivesCount;
	}

	public void setFalsePositivesCount(int falsePositivesCount) {
		this.falsePositivesCount = falsePositivesCount;
	}

	public int falsePositivesCount;
    
    public boolean isMappingUsedForPrecision() {
        return mappingUsedForPrecision;
    }

    public void setMappingUsedForPrecision(boolean mappingUsedForPrecision) {
        this.mappingUsedForPrecision = mappingUsedForPrecision;
    }
    public boolean isHasRecords() {
        return hasRecords;
    }
    
    public void setHasRecords(boolean hasRecords) {
        this.hasRecords = hasRecords;
    }
    
    public int getFeedbackObtainedForThisMap() {
        return feedbackObtainedForThisMap;
    }

    public void setFeedbackObtainedForThisMap(int feedbackObtainedForThisMap) {
        this.feedbackObtainedForThisMap = feedbackObtainedForThisMap;
    }
    public void setFeedbackObtainedForThisMapPlus1() {
        this.feedbackObtainedForThisMap++;
    }


    public int getProvenance() {
        return provenance;
    }

    public void setProvenance(int provenance) {
        this.provenance = provenance;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }
    public int countRecordsNoFeedback(){
    	int result=0;
    	for(int i=0; i<records.size(); i++)
    	{
    		if(records.get(i).hasMapFeedback()==false)
    			result++;
    	}
    	return result;
    }
    public Record getRecordAt(int pos,int relationNumber)
    {
    	return records.get(pos);
    }
    
    public Record getAnyRecordWithoutFeedback()
    {
    	Record result = null;

    	for(int k=0; k<records.size(); k++)
    	{
    		Record r = records.get(k);
    		if(r.hasMapFeedback()==false)
    		{
    			result = r;
    			k=records.size();//to break.
    		}
    	}
    	return result;
    }
    
    public void setRecords(ArrayList<Record> records) {
        this.records= records;
        if(this.records!=null)
            hasRecords = true;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public void freeResources()
    {
        provenance = -1;
        
        records.clear();
        records = null;
        
        attributesNames =  null;
        
        tableName = null;    	
    }
    public void sortRecords()
    {
    	/*int correct=0;
    	int incorrect=0;
    	for(int j=0; j<records.size(); j++)
    	{
    		Record record = records.get(j);
    		String value = record.getGroundTruthValue();
    		if(value.compareTo("tp")==0)
    			correct++;
    		if(value.compareTo("fp")==0)
    			incorrect++;
    	}*/
    	Collections.shuffle(records, new Random(System.nanoTime()));
    }
    
    public boolean isMappingActive(Integration i)
    {
    	boolean result=false;
    	
    	MapManipulation mappingsManipulation = (MapManipulation)i.mappingsManipulation;
        
        //mappingsManipulation.setActiveFalseAllExistingMappings(); //I don't have to deactivate because there is nothing to deactivate.
        if(mappingsManipulation.mappingsList ==null)
        	mappingsManipulation.mappingsList = new ArrayList<MappingObject>();
        
        //mappingsManipulation.mappingsList = null;//I start new mappings everytime.
        for(int r=0;r<mappingsManipulation.mappingsList.size();r++)
        {
        	if(this.provenance==mappingsManipulation.mappingsList.get(r).getProvenance())
        	result = mappingsManipulation.mappingsList.get(r).isActive();
        }
        
        return result;
    }
    
    
}
