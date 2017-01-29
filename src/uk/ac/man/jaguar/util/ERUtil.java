/*#################################
* class:  ERUtil.java
* author: Fernando Osorno-Gutierrez
* date:   7 May 2015
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.util.ArrayList;

import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.Record;

public class ERUtil {
public void addPairToFeedbackPairList(Integration i, DuplicatePair duplicatePair)
{
	//i.erFeedback.feedbackPairList.add(duplicatePair);  
	
	boolean pairExists = false;
	for(int j=0; j<i.erFeedback.feedbackPairList.size(); j++)
	{
		DuplicatePair feedbackPair = i.erFeedback.feedbackPairList.get(j);
		if(compareDuplicatePairs(duplicatePair, feedbackPair))
		{
			pairExists = true;
			break;
		}
	}
	if(!pairExists)
	{
		i.erFeedback.feedbackPairList.add(duplicatePair);
	}
}

public boolean compareDuplicatePairs(DuplicatePair duplicatePair1, DuplicatePair duplicatePair2)
{
	boolean result = true;
	Record record1A = duplicatePair1.getRecordObject1();
	Record record1B = duplicatePair1.getRecordObject2();
	
	Record record2A = duplicatePair2.getRecordObject1();
	Record record2B = duplicatePair2.getRecordObject2();
	
	boolean pair1Equal = true;
	boolean pair2Equal = true;
	
	ArrayList<String> attributes1A = record1A.getAttributesValues();
	ArrayList<String> attributes1B = record1B.getAttributesValues();
	
	ArrayList<String> attributes2A = record2A.getAttributesValues();
	ArrayList<String> attributes2B = record2B.getAttributesValues();
	
	if(attributes1A.size() != attributes2A.size())
		result = false;
	
	if(duplicatePair1.getTableNumber() != duplicatePair2.getTableNumber())
		result = false;
	else
	{
		for(int j=0; j<attributes1A.size(); j++)
		{
			if(attributes1A.get(j).compareTo(attributes2A.get(j))!=0)
			{
				pair1Equal = false;
			}
		}
		if(record1A.getProvenance()!= record2A.getProvenance())
			pair1Equal = false;
		
		
		for(int j=0; j<attributes1B.size(); j++)
		{
			if(attributes1B.get(j).compareTo(attributes2B.get(j))!=0)
			{
				pair2Equal = false;
			}
		}
		if(record1B.getProvenance()!= record2B.getProvenance())
			pair2Equal = false;
		
		
		if(pair1Equal==false || pair2Equal==false)
		{
			pair1Equal = true;
			pair2Equal = true;
			for(int j=0; j<attributes1A.size(); j++)
			{
				if(attributes1A.get(j).compareTo(attributes2B.get(j))!=0)
				{
					pair1Equal = false;
				}
			}
			if(record1A.getProvenance()!= record2B.getProvenance())
				pair1Equal = false;
			
			for(int j=0; j<attributes1B.size(); j++)
			{
				if(attributes1B.get(j).compareTo(attributes2A.get(j))!=0)
				{
					pair2Equal = false;
				}
			}
			if(record1B.getProvenance()!= record2A.getProvenance())
				pair2Equal = false;
			
			if(pair1Equal==false || pair2Equal==false)
				result = false;
			
		}
	}
	return result;
}
/**
 * 
 * @param list
 * @param recordA
 * @param recordB
 * @return
 */
public boolean containsPair(ArrayList<DuplicatePair> list, Record recordA, Record recordB)
{
	boolean result = false;
	MappingUtil mapUtil = new MappingUtil();
	
	for(int k=0; k<list.size(); k++)
	{
		DuplicatePair pair = list.get(k);
		Record recordX = pair.getRecordObject1();
		Record recordY = pair.getRecordObject2();
		
		//String string2 = recordX.getRecordConcatenatedText()+recordY.getRecordConcatenatedText();
		
		if(
			(mapUtil.compareRecords(recordA, recordX)==0 &&/*order: A-X, B-Y*/
			//recordA.getProvenance() == recordX.getProvenance() &&
			mapUtil.compareRecords(recordB, recordY)==0 //&&
	    	//recordB.getProvenance() == recordY.getProvenance() 
	    	) 
			||	
			(mapUtil.compareRecords(recordA, recordY)==0 &&/*order: A-Y, B-X*/
			//recordA.getProvenance() == recordY.getProvenance() &&
			mapUtil.compareRecords(recordB, recordX)==0 //&&
	    	//recordB.getProvenance() == recordX.getProvenance() 
	    	)
		)
		{
			result = true;
			break;
		}
	}
	return result;
}

public DuplicatePair getPair(ArrayList<DuplicatePair> list, Record recordA, Record recordB)
{
	DuplicatePair result = null;
	MappingUtil mapUtil = new MappingUtil();
	
	for(int k=0; k<list.size(); k++)
	{
		DuplicatePair pair = list.get(k);
		Record recordX = pair.getRecordObject1();
		Record recordY = pair.getRecordObject2();
		
		
		
		if(
			(mapUtil.compareRecords(recordA, recordX)==0 &&/*order: A-X, B-Y*/
			recordA.getProvenance() == recordX.getProvenance() &&
			mapUtil.compareRecords(recordB, recordY)==0 &&
	    	recordB.getProvenance() == recordY.getProvenance() ) 
			||	
			(mapUtil.compareRecords(recordA, recordY)==0 &&/*order: A-Y, B-X*/
			recordA.getProvenance() == recordY.getProvenance() &&
			mapUtil.compareRecords(recordB, recordX)==0 &&
	    	recordB.getProvenance() == recordX.getProvenance() )
		)
		{
			result = pair;
			break;
		}
	}
	return result;
}
public DuplicatePair getERFeedbackInstance(ArrayList<DuplicatePair> erFeedbackArrayList, Record recordA, Record recordB)
{
	DuplicatePair erFeedbackInstanceResult = null;
	String string1 = recordA.getRecordConcatenatedText()+recordB.getRecordConcatenatedText();
	for(int k=0; k<erFeedbackArrayList.size(); k++)
	{
		erFeedbackInstanceResult = erFeedbackArrayList.get(k);
		Record recordX = erFeedbackInstanceResult.getRecordObject1();
		Record recordY = erFeedbackInstanceResult.getRecordObject2(); 
		String string2 = recordX.getRecordConcatenatedText()+recordY.getRecordConcatenatedText();
		if(string1.compareTo(string2)==0)
		{
			break;
		}
	}
	return erFeedbackInstanceResult;
}
}
