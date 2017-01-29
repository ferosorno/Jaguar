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

package uk.ac.man.jaguar.model;

import java.util.ArrayList;

/**
 *
 * @author osornogf
 */
public class Record {

	public boolean hasMapFeedback; 
    
	public int provenance;
    public int entityNumber;
    
    public String recordId;
    public String feedbackValue="";
    public String initialFeedbackValue="";
    public String groundTruthValue="";
    
    public ArrayList<String> attributesValues;
    public ArrayList<String> attributesNames;
    
    //er variables
    public boolean isDuplicate;
    public boolean hasERFeedback=false;
    public boolean erFeedback=false;
    public boolean isCountedAsDuplicate=false;
    //this flag is taken into account only if it is duplicate and has 
    //received feedback.
    public boolean duplicateAndThisRecordCounts=false;
    
    
    
    
    public Record(Record record)
    {
    	this.hasMapFeedback = record.hasMapFeedback;
    	this.provenance = record.provenance;
    	this.recordId = record.recordId;
    	this.feedbackValue = record.feedbackValue;
    	this.initialFeedbackValue = record.initialFeedbackValue;
    	this.groundTruthValue = record.groundTruthValue;
    	this.attributesValues = new ArrayList<String>(record.attributesValues);
    	//this.attributesNames = new ArrayList<String>(record.attributesNames);
    	this.isDuplicate = record.isDuplicate;
    	this.hasERFeedback=record.hasERFeedback;
    	this.erFeedback=record.erFeedback;
    	record.duplicateAndThisRecordCounts=record.duplicateAndThisRecordCounts;
    }
    
    
    public boolean isDuplicateAndThisRecordCounts() {
		return duplicateAndThisRecordCounts;
	}

	public void setDuplicateAndThisRecordCounts(boolean duplicateAndThisRecordCounts) {
		this.duplicateAndThisRecordCounts = duplicateAndThisRecordCounts;
	}

	public boolean isHasERFeedback() {
		return hasERFeedback;
	}

	public void setHasERFeedback(boolean hasERFeedback) {
		this.hasERFeedback = hasERFeedback;
	}

	public boolean isErFeedback() {
		return erFeedback;
	}

	public void setErFeedback(boolean erFeedback) {
		this.erFeedback = erFeedback;
	}

	public int getEntityNumber() {
		return entityNumber;
	}

	public void setEntityNumber(int entityNumber) {
		this.entityNumber = entityNumber;
	}

	public String getGroundTruthValue() {
		return groundTruthValue;
	}

	public void setGroundTruthValue(String groundTruthValue) {
		this.groundTruthValue = groundTruthValue;
	}

	public Record() {
        attributesValues = new ArrayList<String>();
        provenance=-1;
        recordId="";
    }

    public String getInitialFeedbackValue() {
        return initialFeedbackValue;
    }

    public void setInitialFeedbackValue(String initialFeedbackValue) {
        this.initialFeedbackValue = initialFeedbackValue;
    }
    
    public String getFeedbackValue() {
        return feedbackValue;
    }

    public void setFeedbackValue(String feedbackValue) {
        this.feedbackValue = feedbackValue;
    }

    public boolean hasMapFeedback() {
        return hasMapFeedback;
    }

    public void setHasMapFeedback(boolean feedbackReceived) {
        this.hasMapFeedback = feedbackReceived;
    }
    
    public ArrayList<String> getAttributesNames() {
        return attributesNames;
    }

    public void setAttributesNames(ArrayList<String> attributesNames) {
        this.attributesNames = attributesNames;
    }
    
    public String getRecordConcatenatedText()
    {
        String text="";
        for(int i=0; i<attributesValues.size(); i++)
        {
            text += attributesValues.get(i).trim();
        }
        return text;
    }
    
    public ArrayList<String> getAttributesValues() {
        return attributesValues;
    }

    public void setAttributesValues(ArrayList<String> attributesValues) {
        this.attributesValues = attributesValues;
    }

    public void addAttribute(String attribute)
    {
        this.attributesValues.add(attribute);
    }
    public void addAttributeName(String attributeName)
    {
        this.attributesNames.add(attributeName);
    }    
    public int getProvenance() {
        return provenance;
    }

    public void setProvenance(int mappingProvenance) {
        this.provenance = mappingProvenance;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    
    public int compareTo(Record record2)
    {
        int result = 0;
        
        if(this.attributesValues.size()!=record2.getAttributesValues().size())
            result = -1;
        else
            for(int k=0; k<this.attributesValues.size(); k++)
            {
                if(this.attributesValues.get(k).compareTo(record2.getAttributesValues().get(k))!=0)
                {
                    result = -1;
                }

            }
        
        if(this.provenance!=record2.provenance)
            result = -1;
        
        return result;
    }

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}
    
}
