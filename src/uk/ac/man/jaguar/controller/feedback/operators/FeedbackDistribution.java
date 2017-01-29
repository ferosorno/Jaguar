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

package uk.ac.man.jaguar.controller.feedback.operators;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;

import java.util.ArrayList;
import java.util.Vector;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.FeedbackPlanApplication;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.feedback.er.ERFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.controller.feedback.match.MatchFeedback;
import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.AnnotationsCollector;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.LogWriter;
import uk.ac.man.jaguar.util.MappingUtil;
import uk.ac.man.jaguar.util.MatchUtil;

/**
 * 
 * @author osornogf
 */
public class FeedbackDistribution {

	/**
	 * This method receives the feedback from Crowdsourcing and puts it in the
	 * integration artefacts.
	 * 
	 * @param feedback
	 * @param i
	 */
	public void applyFeedback(int feedbackType, Integration i) {
		switch (feedbackType) {
		case 0:
			i.matchesManipulation.updateAnnotations(i, feedbackType);
			break;
		case 1:
			i.mappingsManipulation.updateAnnotations(i, feedbackType);
			break;
		case 2:
			i.recordsManipulation.updateAnnotations(i, feedbackType);
		default:
			break;
		}
	}

	@SuppressWarnings("unused")
	public void propagateFeedback(int feedbackType, Integration i) {
		
		AnnotationsCollector annotationsCollector = FeedbackPlanApplication.annotationsCollector;
		
		if (feedbackType == 0) {
			if(JaguarConstants.ELAPSEDTIMEOUT)
	        {
	    		long currentTime   = System.currentTimeMillis();
	    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
	    		System.out.println("\tPropagate Feedback(Match) Start. Elapsed Time = "+elapsedTime);        	
	        }
			propagateFeedbackMatch(feedbackType, i);
			if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
			{
				annotationsCollector.updateMatchAnnotations(
						i.matchFeedback.truePositivesCount,
						i.matchFeedback.falsePositivesCount,
						i.matchFeedback.trueNegativesCount);
			}
			if(JaguarConstants.ELAPSEDTIMEOUT)
	        {
	    		long currentTime   = System.currentTimeMillis();
	    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
	    		System.out.println("\tPropagate Feedback(Match) End. Elapsed Time = "+elapsedTime);        	
	        }
		}
		if (feedbackType == 1) {
			if(JaguarConstants.ELAPSEDTIMEOUT)
	        {
	    		long currentTime   = System.currentTimeMillis();
	    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
	    		System.out.println("\tPropagate Feedback(Map) Start. Elapsed Time = "+elapsedTime);        	
	        }
			propagateFeedbackMap(feedbackType, i);
			if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
			{
		        annotationsCollector.updateMapAnnotations(
		        		i.mapFeedback.totalTruePositives,
		        		i.mapFeedback.totalFalsePositives,
		        		i.mapFeedback.totalTrueNegatives,
		        		i.mapFeedback.totalFalseNegatives,
		        		i.mapFeedback.totalDuplicates,
		        		i.mapFeedback.numMapForAveragePrecision,
		        		i.mapFeedback.totalNumMap,
		        		i.mapFeedback.gtAveragePrecision,
		        		i.mapFeedback.fbPropToMatch);	
			}
			if(JaguarConstants.ELAPSEDTIMEOUT)
	        {
	    		long currentTime   = System.currentTimeMillis();
	    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
	    		System.out.println("\tPropagate Feedback(Map) End. Elapsed Time = "+elapsedTime);        	
	        }
		}
		if (feedbackType == 2) {
			if(JaguarConstants.ELAPSEDTIMEOUT)
	        {
	    		long currentTime   = System.currentTimeMillis();
	    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
	    		System.out.println("\tPropagate Feedback(ER) Start. Elapsed Time = "+elapsedTime);        	
	        }
			propagateFeedbackER(feedbackType, i);
			if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
			{
				annotationsCollector.updateERAnnotations(
						i.erFeedback.truePositives, 
						i.erFeedback.falsePositives,
						i.erFeedback.fbPropToMap,
						i.erFeedback.fbUniqueRecPropToMap);
			}
			if(JaguarConstants.ELAPSEDTIMEOUT)
	        {
	    		long currentTime   = System.currentTimeMillis();
	    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
	    		System.out.println("\tPropagate Feedback(ER) End. Elapsed Time = "+elapsedTime);        	
	        }
		}
	}

	public void propagateFeedbackMatch(int feedbackType, Integration i) {
		
		if(JaguarConstants.LOGGING)
		{
			LogWriter logWriter = new LogWriter();
			logWriter.writeToFile("Log.txt", "Propagate Feedback(Match)");
		}
		
        
	}

	public void propagateFeedbackMap(int feedbackType, Integration i) {
		// this should propagate to match, but not sure that with
		// updateAnnotations Method
		//System.out.println("In propagateFeedbackMap(...)");
		MatchUtil matchUtil = new MatchUtil();
		
		MapManipulation mappingsManipulation = (MapManipulation) i.mappingsManipulation;
		for (int k = 0; k < mappingsManipulation.mappingsList.size(); k++) {
			MappingObject mappingObject = mappingsManipulation.mappingsList.get(k);
			
			if (mappingObject.getPrecision()>0.7) // If mapping's precision is > 0.7 
			{
				ArrayList<ValueCorrespondence> candidateCorrespondences = mappingObject.getValueCorrespondences();
				
				//System.out.println("Propagating map: "+ mappingObject.getProvenance());
				//System.out.println("MatchFeedback.feedbackCollectedPositives before=" + MatchFeedback.feedbackCorrespondenceListPositives.size());
				
				/*
				for (int s = 0; s < i.matchFeedback.feedbackCorrespondenceListPositives.size(); s++) 
				{
					if (i.matchFeedback.feedbackCorrespondenceListPositives.get(s).getConfidence() == 1)
						System.out.println("Correspondence="+ i.matchFeedback.feedbackCorrespondenceListPositives.get(s).toString());
				}
				*/
				
				for (int j = 0; j < candidateCorrespondences.size(); j++) { //for all mapping's correspondences
					//System.out.println("Correspondence="+candidateCorrespondences.get(j).toString());
					ValueCorrespondence valueCorrespondence = candidateCorrespondences.get(j);
					for (int q = 0; q < i.matchFeedback.feedbackCorrespondenceListPositives.size(); q++) 
					{
						ValueCorrespondence valueCorrespondence2 = i.matchFeedback.feedbackCorrespondenceListPositives.get(q);
						if (matchUtil.compareValueCorrespondences(valueCorrespondence, valueCorrespondence2)) 
						{							
							double prevConfidence = valueCorrespondence2.getConfidence();														
							valueCorrespondence2.setConfidence(1d);
							//System.out.println("propagateFeedbackMap() - Positives: Updating correspondence prev confidence="+prevConfidence);
							
							if(prevConfidence<1)
							{
								i.mapFeedback.fbPropToMatch++;
								//System.out.println("ACTUAL MATCH PROPAGATION (Pos) "+valueCorrespondence2.toString());
							}
							
							
							if (q > i.matchFeedback.feedbackAmountCollectedPositives) {
								i.matchFeedback.feedbackAmountCollectedPositives++;
								ArrayList<ValueCorrespondence> newFeedbackCorrespondenceListPositives = new ArrayList<>();
								newFeedbackCorrespondenceListPositives.add(valueCorrespondence2);
								for (int r = 0; r < i.matchFeedback.feedbackCorrespondenceListPositives.size(); r++)
									if (r != q)
										newFeedbackCorrespondenceListPositives.add(i.matchFeedback.feedbackCorrespondenceListPositives.get(r));
								i.matchFeedback.feedbackCorrespondenceListPositives.clear();
								i.matchFeedback.feedbackCorrespondenceListPositives.addAll(newFeedbackCorrespondenceListPositives);
							}
							
						}
					}
					for (int q = 0; q < i.matchFeedback.feedbackCorrespondenceListNegatives.size(); q++) {
						ValueCorrespondence valueCorrespondence2 = i.matchFeedback.feedbackCorrespondenceListNegatives.get(q);
						if (matchUtil.compareValueCorrespondences(
								valueCorrespondence, valueCorrespondence2)) {
							System.out.println("propagateFeedbackMap() - Negatives: Updating correspondence");
							double prevConfidence = valueCorrespondence2.getConfidence();
							valueCorrespondence2.setConfidence(1d);
							if(prevConfidence<1)
							{
								i.mapFeedback.fbPropToMatch++;
							}
							if (q > i.matchFeedback.feedbackCollectedNegatives) {
								i.matchFeedback.feedbackCollectedNegatives++;
								ArrayList<ValueCorrespondence> newFeedbackCorrespondenceListNegatives = new ArrayList<>();
								newFeedbackCorrespondenceListNegatives.add(valueCorrespondence2);
								for (int r = 0; r < i.matchFeedback.feedbackCorrespondenceListNegatives
										.size(); r++)
									if (r != q)
										newFeedbackCorrespondenceListNegatives.add(i.matchFeedback.feedbackCorrespondenceListNegatives.get(r));
								i.matchFeedback.feedbackCorrespondenceListNegatives.clear();
								i.matchFeedback.feedbackCorrespondenceListNegatives.addAll(newFeedbackCorrespondenceListNegatives);
							}
						}
					}
				}
				//System.out.println("MatchFeedback.feedbackCollectedPositives after="+ MatchFeedback.feedbackCorrespondenceListPositives.size());
				/*for (int s = 0; s < MatchFeedback.feedbackCorrespondenceListPositives.size(); s++) {
					if (MatchFeedback.feedbackCorrespondenceListPositives.get(s).getConfidence() == 1)
						System.out.println("Correspondence="+ MatchFeedback.feedbackCorrespondenceListPositives.get(s).toString());
				}*/
			}
		}
		
		if(JaguarConstants.LOGGING)
		{
			LogWriter logWriter = new LogWriter();
			logWriter.writeToFile("Log.txt", "Propagate Feedback(Map)");
		}
       
	}

	// this should propagate to map, but not sure that with
	// updateAnnotations Method
	/**
	 * Propagate ER feedback after updateAnnotationsER.
	 * To take account of the er feedback just applied.
	 * We propagate feedback between records of the same pair.
	 * 
	 * This method will work when there be more than one mapping retrieving results to the same table, 
	 * and that the mappings retrieve the same results, because:
	 * - the spicy mappings use UNIQUE, so the spicy mappings would never retrieve repeated results.
	 * - 
	 * 
	 * @param feedbackType
	 * @param i
	 */
	public void propagateFeedbackER(int feedbackType, Integration i) {
		ArrayList<MappingResultsSet> mapFeedbackSetList = i.mapFeedback.getMappingResultsSetList();// new ArrayList<MapFeedbackSet>(MapFeedback.mapFeedback.mapFeedbackHashtable.values());
		
		ArrayList<DuplicatePair> duplicatePairList = i.erFeedback.duplicatePairList;
		
		for (int k = 0; k < duplicatePairList.size(); k++) {
			DuplicatePair duplicatePair = duplicatePairList.get(k);
			
			if (duplicatePair.isHasFeedback() && duplicatePair.isPropagated() == false) {
				
				Record record1 = duplicatePair.getRecordObject1();//These records do not have MAP feedback, they are from ER.
				Record record2 = duplicatePair.getRecordObject2();
				//similarity ==1 to ensure that the propagation is done on equals, otherwise there is a risk to
				//propagate to mappings that are wrong w.r.t. the ground truth and spoil the experiment adding
				//incorrect mappings because of propagated map feedback to similar records of incorrect mappings.
				if (duplicatePair.getFeedbackValue() && duplicatePair.getSimilarity()==1) {//This only means that the pair is correct.
					
					int prevFbProToMap = i.erFeedback.fbPropToMap;
					if (record2.hasMapFeedback() && !record1.hasMapFeedback())
					{
						if(record2.getFeedbackValue().compareTo("tp")==0)
						{
							propagateFbToRecords(2,"tp",record2,mapFeedbackSetList,i);
							//System.out.println("propagateFbToRecords(2,\"tp\",record2,mapFeedbackSetList,i);");
							record1.setFeedbackValue("tp");
		            		record1.setHasMapFeedback(true);
		            		i.mapFeedback.saveFeedbackToTableFeedbackSet(record1);
						}
						else if(record2.getFeedbackValue().compareTo("fp")==0)
						{
							propagateFbToRecords(2,"fp",record2,mapFeedbackSetList,i);
							//System.out.println("propagateFbToRecords(2,\"fp\",record2,mapFeedbackSetList,i);");
							record1.setFeedbackValue("fp");
		            		record1.setHasMapFeedback(true);
		            		i.mapFeedback.saveFeedbackToTableFeedbackSet(record1);							
						}
					}
					
					if (record1.hasMapFeedback() && !record2.hasMapFeedback())
					{
						if(record1.getFeedbackValue().compareTo("tp")==0)
						{
							propagateFbToRecords(2,"tp",record1,mapFeedbackSetList,i);
							//System.out.println("propagateFbToRecords(2,\"tp\",record1,mapFeedbackSetList,i);");
							record2.setFeedbackValue("tp");
		            		record2.setHasMapFeedback(true);
		            		i.mapFeedback.saveFeedbackToTableFeedbackSet(record2);
						}
						else if(record1.getFeedbackValue().compareTo("fp")==0)
						{
							propagateFbToRecords(2,"fp",record1,mapFeedbackSetList,i);
							//System.out.println("propagateFbToRecords(2,\"fp\",record1,mapFeedbackSetList,i);");
							record2.setFeedbackValue("fp");
		            		record2.setHasMapFeedback(true);
		            		i.mapFeedback.saveFeedbackToTableFeedbackSet(record2);
						}
					}
					
					if(i.erFeedback.fbPropToMap>prevFbProToMap)
					{
						i.erFeedback.fbUniqueRecPropToMap++;
					}
					
				}
				duplicatePair.setPropagated(true);
			}
		}
		//System.out.println("END. Propagate Feedback(ER)");
		if(JaguarConstants.LOGGING)
		{
			LogWriter logWriter = new LogWriter();
			logWriter.writeToFile("Log.txt", "Propagate Feedback(ER)");
		}

				
	}
	
	/**
	 * This propagation is done when we obtain feedback on tuples and its propagated to other tuples of other mappings.
	 * @param feedbackType
	 * @param feedback
	 * @param record
	 * @param mapFeedbackSetList
	 */
	public void propagateFbToRecords(int feedbackType, String feedback, Record record, ArrayList<MappingResultsSet> mapFeedbackSetList, Integration i)
	{
		MappingUtil mappingUtil = new MappingUtil();
		for(int r=0; r<mapFeedbackSetList.size(); r++)
		{
	        MappingResultsSet mappingResultsSet = mapFeedbackSetList.get(r);
	        String tableName = mappingResultsSet.getTableName();
	        String recordTableName = JaguarVariables.globalSchemaTablesComplete.get(record.getEntityNumber());
	        
	        if(tableName.compareTo(recordTableName)==0)
	        {
		        ArrayList<Record> records = mappingResultsSet.getRecords();
		        for(int s=0; s<records.size(); s++)
		        {
		        	Record record2 = records.get(s);
		        	if(mappingUtil.compareRecords(record, record2)==0)
		        	{
		        		if(record2.getFeedbackValue().compareTo("")==0)
		        		{
			        		record2.setFeedbackValue(feedback);
			        		record2.setHasMapFeedback(true);
			        		
			        		mappingResultsSet.setFeedbackObtainedForThisMapPlus1();
			        		String recordTableName2 = JaguarVariables.globalSchemaTablesComplete.get(record2.getEntityNumber());
			        		JaguarVariables.countMapFbckPropagated++;
			        		
			        		/*if(JaguarVariables.countMapFbckPropagated<3000)
			        		{
				        		System.out.println("Propagation. Source Record Table="+record.getEntityNumber()+"  "+record.getRecordConcatenatedText()+" provenance="+record.getProvenance()+" recordTableName="+recordTableName);
				        		System.out.println("Propagation. Destiny Record Table="+record2.getEntityNumber()+"  "+record2.getRecordConcatenatedText()+" provenance="+record2.getProvenance()+" recordTableName2="+recordTableName2);
				        		System.out.println(record.getAttributesValues().toString());
				        		System.out.println(record2.getAttributesValues().toString());
				        		if(recordTableName.compareTo(recordTableName2)!=0)
				        			System.out.println("THEY ARE FROM DIFFERENT TABLES");
				        		
				        		if(record.getProvenance()==record2.getProvenance())
				        			System.out.println("THEY ARE FROM THE SAME MAPPING");
				        		System.out.println("Feedback propagated="+JaguarVariables.countMapFbckPropagated);
				        	}*/
		        			if(feedbackType==2)
		        			{
		        				i.erFeedback.fbPropToMap++;
		        			}
		        		}
		        		else
		        		{
		        			//System.out.println("Record has feedback."+record2.getAttributesValues().toString()+" "+record2.getFeedbackValue());
		        		}
		        	}
		        }
	        }
		}
	}	
}
