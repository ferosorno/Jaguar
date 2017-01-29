
package uk.ac.man.jaguar.controller.feedback.er;

import java.util.ArrayList;
import java.util.Hashtable;

import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.feedback.AbstractFeedback;
import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.MappingUtil;

/**
 *
 * @author osornogf
 */
public class ERFeedback extends AbstractFeedback{
	
	/**
	 * Variables used to store the feedback.
	 */
    public int truePositives;
    public int falsePositives;
    public int fbPropToMap;
    public int fbUniqueRecPropToMap;
    //Y aqui voy a guardar el Feedback1 ER
    //public static ERFeedback erFeedback= new ERFeedback();
    //public  Hashtable<String,ERFeedbackInstance> erFeedbackHashTable;
    
    public  ArrayList<DuplicatePair> duplicatePairList; 
    public  ArrayList<DuplicatePair> feedbackPairList;
    
    public int feedbackCollected=0;	
	
    public ERFeedback()
    {
    	duplicatePairList = new ArrayList<DuplicatePair>();
    	feedbackPairList = new ArrayList<DuplicatePair>();
        feedbackCollected=0;
    }
    
    public int getFeedbackCollected() {
        return feedbackCollected;
    }
    
    public void setFeedbackCollected(int feedbackCollected) {
        this.feedbackCollected = feedbackCollected;
    }
    
    public ArrayList<DuplicatePair> getFeedbackPairList() {
		return feedbackPairList;
	}

	public void setFeedbackPairList(ArrayList<DuplicatePair> feedbackPairList) {
		this.feedbackPairList = feedbackPairList;
	}

	public void reset()
    {
        truePositives = 0;
        falsePositives = 0;
        duplicatePairList = null;
        //ERFeedback.erFeedback.erFeedbackHashTable= new Hashtable<String, ERFeedbackInstance>();
        //erFeedback= new ERFeedback();
    }
	/**
	 * This method searches for previous equal records with feedback. 
	 * If there is an equal record with feedback, then the new record
	 * copies the feedback.
	 * @param record
	 */
    public void setFeedbackIfExists(DuplicatePair duplicate)
    {
    	for(int j=0; j<feedbackPairList.size(); j++)
    	{
    		DuplicatePair duplicate2 = feedbackPairList.get(j);
    		if(comparePairs(duplicate,duplicate2))
    		{
    			duplicate.setFeedbackValue(duplicate2.getFeedbackValue());
    			break;
    		}
    	}
    }
    
    public boolean comparePairs(DuplicatePair duplicate1, DuplicatePair duplicate2)
    {
    	MappingUtil mapUtil = new MappingUtil();
    	boolean result=false;
    	
    	Record record1 = duplicate1.getRecordObject1();
    	Record recurd2 = duplicate1.getRecordObject2();
    	
    	Record record3 = duplicate2.getRecordObject1();
    	Record record4 = duplicate2.getRecordObject2();
    	
    	if(mapUtil.compareRecords(record1, record3)==0 && 
    			mapUtil.compareRecords(recurd2, record4)==0)
    	{
    		result = true;
    	}
    	
    	if(mapUtil.compareRecords(record1, record4)==0 && 
    			mapUtil.compareRecords(recurd2, record3)==0)
    	{
    		result = true;
    	}
    	
    	return result;
    }
}
