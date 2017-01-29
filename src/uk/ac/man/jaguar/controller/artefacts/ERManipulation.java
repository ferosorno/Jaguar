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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.FeedbackPlanApplication;
import uk.ac.man.jaguar.controller.artefacts.er.PairOfRecords;
import uk.ac.man.jaguar.controller.feedback.er.ERFeedback;
import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.AnnotationsCollector;
import uk.ac.man.jaguar.util.LogWriter;

/**
 *
 * @author osornogf
 */
public class ERManipulation  implements IArtefactManipulation{

    //public static Hashtable<String,PairOfRecords> erPairsOfRecords;
	
    @SuppressWarnings("unused")
	/**
     * Counts the number of confirmed correct duplicate pairs and confirmed incorrect duplicate pairs.
     * @param i
     * @param feedbackType
     */
    @Override
    public void updateAnnotations(Integration i, int feedbackType)
    {
    	if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tApplyFeedback.updateAnnotations(ER) Start. Elapsed Time = "+elapsedTime);        	
        }
        int truePositives=0;
        int falsePositives=0;
        double precision;
        //ArrayList<ERFeedbackInstance> erFeedbackArrayList = new ArrayList<>(ERFeedback.erFeedback.erFeedbackHashTable.values());
        ArrayList<DuplicatePair> duplicatePairList = (i.erFeedback.duplicatePairList);
        //ArrayList<ERFeedbackInstance> erFeedbackArrayList = new ArrayList<>();
        ///if(!ERFeedback.erFeedback.erFeedbackHashTable.isEmpty())
        //    erFeedbackArrayList = (ArrayList<ERFeedbackInstance>)ERFeedback.erFeedback.erFeedbackHashTable.elements();
        //for(int k=0; k<ERFeedback.erFeedback.erFeedbackHashTable.size();k++)//For all the results
        
        for(int k=0; k<duplicatePairList.size();k++)
        {
            DuplicatePair instance = duplicatePairList.get(k);
            boolean feedbackValue = instance.getFeedbackValue();
            //System.out.println("ER feedback=" + feedbackValue);
            if(feedbackValue)
                truePositives++;
            if(!feedbackValue)
                falsePositives++;
            //System.out.println(k+". feedbackValue="+feedbackValue);
        }
        
        //System.out.println("erFeedbackArrayList.size="+erFeedbackArrayList.size());
        precision= (double)truePositives/(truePositives+falsePositives);
        /*System.out.println("ER Precision="+precision);
        System.out.println("Feedback given="+(truePositives+falsePositives));*/
        
        //System.out.println("true positives="+truePositives);
        //System.out.println("false positives="+falsePositives);
        
        i.erFeedback.truePositives=truePositives;
        i.erFeedback.falsePositives=falsePositives;
        
		if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
		{
			AnnotationsCollector annotationsCollector = FeedbackPlanApplication.annotationsCollector;
	        annotationsCollector.updateERAnnotations(
	        		i.erFeedback.truePositives,
	        		i.erFeedback.falsePositives,
	        		i.erFeedback.fbPropToMap,
	        		i.erFeedback.fbUniqueRecPropToMap
	        		);
		}
        truePositives=0;
        falsePositives=0;
        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt","ApplyFeedback.updateAnnotations(ER)");
        }
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tApplyFeedback.updateAnnotations(ER) End. Elapsed Time = "+elapsedTime);        	
        }
    }
    public static void main(String args[])
    {
    	ArrayList<String> arr1 = new ArrayList<String>();
    	arr1.add("1blessings");
    	arr1.add("intelligencer");
    	arr1.add("proportionable");
    	arr1.add("latches");
    	arr1.add("fleec");
    	arr1.add("partake");
    	arr1.add("chatter");
    	//arr1.add("ottoman");
    	//arr1.add();
    	
    	
    	ArrayList<String> arr2 = new ArrayList<String>();
    	arr2.add("blessings");
    	arr2.add("intelligencer");
    	arr2.add("proportionable");
    	arr2.add("latches");
    	arr2.add("fleec");
    	arr2.add("partake");
    	arr2.add("chatter");
    	//arr2.add("ottoman");
    	
    	
    	System.out.println(ERManipulation.calculateJaccardSimilarityRow(arr1, arr2));
    }
    public static double calculateJaccardSimilarityRow(ArrayList<String> row1, ArrayList<String> row2)
    {
        //First convert the arrays to hash sets
        HashSet<String> h1 = new HashSet<>();
        //Convert row 1
        for(int k=0; k<row1.size(); k++)
        {
        	if(row1.get(k).compareTo("")!=0)
        		h1.add((String)row1.get(k));
        }
        HashSet<String> h2 = new HashSet<>();
        //System.out.println("h2 "+h2);
        for(int q=0; q<row2.size(); q++)
        {
        	if(row2.get(q).compareTo("")!=0)
        		h2.add((String)row2.get(q));
        }
        //String s1="h1 "+h1.toString();
        //String s2="h2 "+h2.toString();
        int sizeh1 = h1.size();
        //Retains all elements in h1 that are contained in h2 ie intersection
        h1.retainAll(h2);
        //h1 now contains the intersection of h1 and h2
        //System.out.println("intersection "+h1);
        h2.removeAll(h1);
        //h2 now contains unique elements
        //System.out.println("Unique in h2 "+h2);
        //Unique
        int union = sizeh1 + h2.size();
        int intersection = h1.size();
        double jaccardsim=(double)intersection/union;
        return jaccardsim;
    }
	@Override
	public void updateAnnotationsBootstrapping(Integration i, int feedbackType) {
		// TODO Auto-generated method stub
		
	}
	

}
