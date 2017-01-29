/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.man.jaguar.model;

import java.util.ArrayList;

/**
 *
 * @author osornogf
 */
public class DuplicatePair {
	
	boolean propagated;	
	boolean hasFeedback;
	
	public int tableNumber;

	public double similarity;	
	
    public String provenanceRecord1;
    public String provenanceRecord2;
    
    public String key;
    public boolean feedbackValue;
    public boolean initialAnnotationValue;
    
    public Record recordObject1;
    public Record recordObject2;
    
    public ArrayList<String> record1Values;
    public ArrayList<String> record2Values;
    public ArrayList<Record> recordObjectList1;
    public ArrayList<Record> recordObjectList2;
    
    
    public DuplicatePair(DuplicatePair duplicatePair)
    {
    	this.recordObject1 = new Record(duplicatePair.getRecordObject1());
    	this.recordObject2 = new Record(duplicatePair.getRecordObject2());
    	
    	this.propagated = duplicatePair.propagated;
    	this.hasFeedback = duplicatePair.hasFeedback;
    	
    	this.tableNumber = duplicatePair.tableNumber;
    	this.similarity = duplicatePair.similarity;
    	
    	this.provenanceRecord1 = duplicatePair.provenanceRecord1;
    	this.provenanceRecord2 = duplicatePair.provenanceRecord2;
    	
    	this.key = duplicatePair.key;
    	
    	this.feedbackValue = duplicatePair.feedbackValue;
    	this.initialAnnotationValue = duplicatePair.initialAnnotationValue;
    	
    	this.record1Values = new ArrayList<String>(duplicatePair.record1Values);
    	this.record2Values = new ArrayList<String>(duplicatePair.record2Values);
    }
    
    public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public DuplicatePair() {
		this.hasFeedback = false;
    	recordObjectList1 = new ArrayList<>();
    	recordObjectList2 = new ArrayList<>();
	}
    
    /**
	 * @return the recordObjectList1
	 */
	public ArrayList<Record> getRecordObjectList1() {
		return recordObjectList1;
	}


	/**
	 * @param recordObjectList1 the recordObjectList1 to set
	 */
	public void setRecordObjectList1(ArrayList<Record> recordObjectList1) {
		this.recordObjectList1 = recordObjectList1;
	}
	public void addRecordObjectList1(Record record) {
		this.recordObjectList1.add(record);
	}
	
	/**
	 * @return the recordObjectList2
	 */
	public ArrayList<Record> getRecordObjectList2() {
		return recordObjectList2;
	}
	
	/**
	 * @param recordObjectList2 the recordObjectList2 to set
	 */
	public void setRecordObjectList2(ArrayList<Record> recordObjectList2) {
		this.recordObjectList2 = recordObjectList2;
	}
	public void addRecordObjectList2(Record record) {
		this.recordObjectList2.add(record);
	}
	
	public boolean getInitialAnnotationValue() {
        return initialAnnotationValue;
    }

    public void setInitialAnnotationValue(boolean initialAnnotationValue) {
        this.initialAnnotationValue = initialAnnotationValue;
    }
    boolean hasFeedbackValue=false;

    public boolean isHasFeedbackValue() {
        return hasFeedbackValue;
    }

    public void setHasFeedbackValue(boolean hasFeedbackValue) {
        this.hasFeedbackValue = hasFeedbackValue;
    }
    
    public boolean isPropagated() {
        return propagated;
    }

    public void setPropagated(boolean propagated) {
        this.propagated = propagated;
    }
    
    
    public Record getRecordObject1() {
        return recordObject1;
    }

    public void setRecordObject1(Record recordObject1) {
        this.recordObject1 = recordObject1;
    }

    public Record getRecordObject2() {
        return recordObject2;
    }

    public void setRecordObject2(Record recordObject2) {
        this.recordObject2 = recordObject2;
    }
    
    public String getProvenanceRecord1() {
    	String result = "";
    	for(int j=0; j<recordObjectList1.size(); j++)
    	{
    		Record record = recordObjectList1.get(j);
    		result += record.getProvenance()+",";
    		
    		
    	}
    	
    	for(int j=0; j<recordObjectList1.size(); j++)
    	{
    		Record record = recordObjectList1.get(j);
    		//System.out.println(record.getAttributesValues().toString());
    	}
        return result;
    }

    public void setProvenanceRecord1(String provenanceRecord1) {
        this.provenanceRecord1 = provenanceRecord1;
    }

    public String getProvenanceRecord2() {
    	String result = "";
    	for(int j=0; j<recordObjectList2.size(); j++)
    	{
    		Record record = recordObjectList2.get(j);
    		result += record.getProvenance()+",";
    	}
    	
    	for(int j=0; j<recordObjectList1.size(); j++)
    	{
    		Record record = recordObjectList2.get(j);
    		//System.out.println(record.getAttributesValues().toString());
    	}
        return result;
    }

    public void setProvenanceRecord2(String provenanceRecord2) {
        this.provenanceRecord2 = provenanceRecord2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getRecord1Values() {
        return record1Values;
    }

    public void setRecord1Values(ArrayList<String> record1) {
        this.record1Values = record1;
    }
       
    public ArrayList<String> getRecord2Values() {
        return record2Values;
    }

    public void setRecord2Values(ArrayList<String> record2) {
        this.record2Values = record2;
    }

    public boolean getFeedbackValue() {
        return feedbackValue;
    }

    public void setFeedbackValue(boolean feedbackValue) {
    	this.hasFeedback = true;
    	this.hasFeedbackValue=true;
    	this.feedbackValue = feedbackValue;
    	
    	this.getRecordObject1().setHasERFeedback(true);
		this.getRecordObject1().setErFeedback(feedbackValue);
    	
		this.getRecordObject2().setHasERFeedback(true);
		this.getRecordObject2().setErFeedback(feedbackValue);
        
        //Decide which record counts.
        if(feedbackValue)
        {
        	if(recordObject1.getGroundTruthValue().compareTo("tp")==0)
            {
            	recordObject1.setDuplicateAndThisRecordCounts(true);
            	recordObject2.setDuplicateAndThisRecordCounts(false);
            }
        	else if(recordObject2.getGroundTruthValue().compareTo("tp")==0)
        	{
        		recordObject1.setDuplicateAndThisRecordCounts(false);
            	recordObject2.setDuplicateAndThisRecordCounts(true);
        	}
        	else
        	{//Anyway one has to count
        		recordObject1.setDuplicateAndThisRecordCounts(true);
            	recordObject2.setDuplicateAndThisRecordCounts(false);
        	}
        }
        else
        {//Not a pair, both count. However, it shouldn't matter.
        	//recordObject1.setDuplicateAndThisRecordCounts(true);
        	//recordObject2.setDuplicateAndThisRecordCounts(true);
        }
    }
	
	public int getTableNumber() {
		return tableNumber;
	}
	
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	public boolean isHasFeedback() {
		return hasFeedback;
	}
	public void setHasFeedback(boolean hasFeedback) {
		this.hasFeedback = hasFeedback;
	}
	
}
