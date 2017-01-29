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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.FeedbackPlanApplication;
import uk.ac.man.jaguar.controller.feedback.match.MatchFeedback;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.util.AnnotationsCollector;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.LogWriter;
import uk.ac.man.jaguar.util.MatchUtil;

/**
 * 
 * @author osornogf
 */
public class MatchManipulation implements IArtefactManipulation {

	
	
	
	public ArrayList<ValueCorrespondence> matchesList;
	
	



	/**
	 * In this method, we are going to update the annotations (match precision
	 * and the feedback value of each correspondence) of the matches of the
	 * Integration. We should not modify the CandidateCorrespondences used to
	 * generate the mappings. We only calculate the Precision of the Match step.
	 * (In theory, in the RunEpisodeMatch the CandidateCorrespondences are modified.
	 * @param feedbackAmount
	 */
	@SuppressWarnings("unused")
	@Override
	public void updateAnnotations(Integration i, int feedbackType) {
		if(JaguarConstants.ELAPSEDTIMEOUT)
		{
			long currentTime   = System.currentTimeMillis();
			long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
			System.out.println("\tApplyFeedback.updateAnnotations(Match) Start. Elapsed Time = "+elapsedTime);
		}
		
		MatchUtil matchUtil = new MatchUtil();
		ConfigurationReader configReader = new ConfigurationReader();
		ArrayList<String> matchGroundTruth = configReader
				.getStringsFromFile(
						JaguarConstants.CONFIGURATION_PATH + "\\" + 
					    JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +
						"match-groundtruth.dat");
		
		
		int matchTruePositives;
		int matchFalsePositives;
		matchTruePositives = i.matchFeedback.truePositivesCount;		
		matchFalsePositives = (i.mappingTask.getCandidateCorrespondences().size()) - (i.matchFeedback.truePositivesCount);		
		
		i.matchFeedback.falsePositivesCount = matchFalsePositives;
		
		double matchPrecision=0d;
		double matchRecall=0d;
		double matchFMeasur=0d;
		
		/***** Calculate Precision, Recall and F-Measure  *****/
		
		if ((i.matchFeedback.truePositivesCount + i.matchFeedback.falsePositivesCount) != 0) {
			matchPrecision = (double) i.matchFeedback.truePositivesCount
					/ (i.matchFeedback.truePositivesCount + i.matchFeedback.falsePositivesCount);
		} else {
			matchPrecision = 0d;
		}

		


		if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
		{
			AnnotationsCollector annotationsCollector = FeedbackPlanApplication.annotationsCollector;
			annotationsCollector.updateMatchAnnotations(
					matchTruePositives,
					matchFalsePositives,
					i.matchFeedback.trueNegativesCount);
		}		
				
		//System.out.println("matchPrecision=" + matchPrecision);
		//System.out.println("matchRecall=" + matchRecall);
		//System.out.println("matchFMeasure=" + matchFMeasure);
		
		if(JaguarConstants.LOGGING)
		{
			LogWriter logWriter = new LogWriter();
			logWriter.writeToFile("Log.txt","ApplyFeedback.updateAnnotations(Match)");			
		}
		if(JaguarConstants.ELAPSEDTIMEOUT)
		{
			long currentTime   = System.currentTimeMillis();
			long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
			System.out.println("\tApplyFeedback.updateAnnotations(Match) End. Elapsed Time = "+elapsedTime);			
		}
	}
	//@SuppressWarnings("unused")
	//@Override
	public void updateAnnotations2(Integration i, int feedbackType) {
		
		
		MatchUtil matchUtil = new MatchUtil();
		ConfigurationReader configReader = new ConfigurationReader();
		ArrayList<String> matchGroundTruth = configReader
				.getStringsFromFile(
						JaguarConstants.CONFIGURATION_PATH + "\\" + 
					    JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +
						"match-groundtruth.dat");
		
		System.out.println("i.matchFeedback.lastFeedbackCollectedPositives="+i.matchFeedback.lastFeedbackCollectedPositives);
		System.out.println("i.matchFeedback.feedbackCollectedPositives="+i.matchFeedback.feedbackAmountCollectedPositives);
		
		int matchTruePositives;
		int matchFalsePositives;
		/**
		 * A range to collect feedback.
		 * 
		 */
		for (int p = i.matchFeedback.lastFeedbackCollectedPositives; p < i.matchFeedback.feedbackAmountCollectedPositives; p++) {
			//feedbackCorrespondenceListPositives only has Correspondences with feedback.
			//ValueCorrespondence valueCorrespondence = i.matchFeedback.feedbackCorrespondenceListPositives.get(p);
			ValueCorrespondence valueCorrespondence = (i.mappingTask.getCandidateCorrespondences()).get(p);
			
			String sourceTableName = valueCorrespondence.getSourcePaths().get(0).getPathSteps().get(1);			
			String sourceAttributeName = valueCorrespondence.getSourcePaths().get(0).getLastStep();			
			
			String targetTableName = valueCorrespondence.getTargetPath().getPathSteps().get(1);			
			String targetAttributeName = valueCorrespondence.getTargetPath().getLastStep();
			
			
			// This means that it was annotated as Incorrect by user feedback.
			if (valueCorrespondence.getConfidence() == 0) {
				// Is annotated as Incorrect and is in the ground truth.
				if (matchUtil.isInMatchGroundTruth(	matchGroundTruth,
													sourceTableName, 
													sourceAttributeName, 
													targetTableName,
													targetAttributeName, 1)) {
					
					//System.out.println("Updating feedback 1");
					
					//i.matchFeedback.moveFrom("tp","tn",valueCorrespondence,i.matchFeedback);
					i.matchFeedback.truePositivesCount--;
					//i.matchFeedback.trueNegativesCount++;
				} else// Is annotated as Incorrect and is in not the ground
						// truth
				{
					// System.out.println("Updating feedback 2");
					//i.matchFeedback.moveFrom("fp", "tn", valueCorrespondence,i.matchFeedback);
					i.matchFeedback.falsePositivesCount--;
					//i.matchFeedback.trueNegativesCount++;
				}
			} else // This means that it was annotated Correct by user feedback and confidence = 1
			{
				// Is annotated as Correct and is in the ground truth.
				if (matchUtil.isInMatchGroundTruth(matchGroundTruth,
						sourceTableName, sourceAttributeName, targetTableName,
						targetAttributeName, 1)) {
					
					//System.out.println("Updating feedback 3");
					//i.matchFeedback.updateConfidenceIn("tp", valueCorrespondence, 1d, i.matchFeedback);
					
				} else {
					// Is annotated as correct and is not in the ground truth.
					//System.out.println("Updating feedback 4");
					//i.matchFeedback.updateConfidenceIn("fp", valueCorrespondence,1d,i.matchFeedback);
				}
			}
		}
		/**
		 * i.matchFeedback.lastFeedbackCollectedPositives: 	is used as an index to keep the place where we are
		 * i.matchFeedback.feedbackCollectedPositives: 		is the amount of feedback collected so far.
		 */
		i.matchFeedback.lastFeedbackCollectedPositives = i.matchFeedback.feedbackAmountCollectedPositives;
		
		System.out.println("i.matchFeedback.lastFeedbackCollectedPositives="+i.matchFeedback.lastFeedbackCollectedPositives);
		System.out.println("i.matchFeedback.feedbackCollectedPositives="+i.matchFeedback.feedbackAmountCollectedPositives);
		
		for (int p = i.matchFeedback.lastFeedbackCollectedNegatives; p < i.matchFeedback.feedbackCollectedNegatives; p++) {
			
			ValueCorrespondence valueCorrespondence = i.matchFeedback.feedbackCorrespondenceListNegatives.get(p);
			
			if (valueCorrespondence.getConfidence() == 0) {
				
				i.matchFeedback.updateConfidenceIn("tn", valueCorrespondence, 0d, i.matchFeedback);
				
			} else {
				
				//i.matchFeedback.moveFrom("tn", "tp", valueCorrespondence,i.matchFeedback);
				//i.matchFeedback.updateConfidenceIn("tp", valueCorrespondence, 1d, i.matchFeedback);
				i.matchFeedback.trueNegativesCount--;
				i.matchFeedback.truePositivesCount++;
			}
		}
		
		i.matchFeedback.lastFeedbackCollectedNegatives = i.matchFeedback.feedbackCollectedNegatives;
		
		// Update the correspondences with the existing feedback
		//ArrayList<ValueCorrespondence> newCandidateCorrespondences = new ArrayList<>();
		
		/*for (int o = 0; o < i.matchFeedback.truePositives.size(); o++) {
			
			ValueCorrespondence valueCorrespondence = new ValueCorrespondence(
			i.matchFeedback.truePositives.get(o));
			
			newCandidateCorrespondences.add(valueCorrespondence);
			
		}

		for (int o = 0; o < i.matchFeedback.falsePositives.size(); o++) {
			
			ValueCorrespondence valueCorrespondence = new ValueCorrespondence(
			i.matchFeedback.falsePositives.get(o));
			
			if(valueCorrespondence.getConfidence()!=0)
				newCandidateCorrespondences.add(valueCorrespondence);
			
		}*/

		/*
		for (int o = 0; o < i.matchFeedback.trueNegatives.size(); o++) {
			ValueCorrespondence valueCorrespondence = new ValueCorrespondence(
					i.matchFeedback.trueNegatives.get(o));
			newCandidateCorrespondences.add(valueCorrespondence);
		}
		*/
		
		
		//if(JaguarConstants.SYSTEMOUT)
		//	System.out.println("Size of new correspondences ="+newCandidateCorrespondences.size());
		
		if(JaguarConstants.SYSTEMOUT)
		{
			/*for(int y=0; y<newCandidateCorrespondences.size(); y++)
			{
				System.out.println(newCandidateCorrespondences.get(y).toString());
			}*/
		}
		
		
		double matchPrecision;
		double matchRecall;
		double matchFMeasure;
		
		/***** Calculate Precision, Recall and F-Measure  *****/
		
		if ((i.matchFeedback.truePositivesCount + i.matchFeedback.falsePositivesCount) != 0) {
			matchPrecision = (double) i.matchFeedback.truePositivesCount
					/ (i.matchFeedback.truePositivesCount + i.matchFeedback.falsePositivesCount);
		} else {
			matchPrecision = 0d;
		}

		if ((i.matchFeedback.truePositivesCount + i.matchFeedback.falseNegativesCount) != 0) {
			matchRecall = (double) i.matchFeedback.truePositivesCount
					/ (i.matchFeedback.truePositivesCount + i.matchFeedback.falseNegativesCount);
		} else {
			matchRecall = 0d;
		}

		if ((matchPrecision + matchRecall) != 0) {
			matchFMeasure = (double) 2 * (matchPrecision * matchRecall)
					/ (matchPrecision + matchRecall);
		} else {
			matchFMeasure = 0d;
		}

		/*
		 * for(int w=0; w<i.mappingTask.getCandidateCorrespondences().size();
		 * w++) {
		 * System.out.println("Mapping Task Updated confidence="+i.mappingTask
		 * .getCandidateCorrespondences().get(w).getConfidence()); }
		 */
		
		
		/*if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
		{
			AnnotationsCollector annotationsCollector = FeedbackPlanApplication.annotationsCollector;
			annotationsCollector.updateMatchAnnotations(
					i.matchFeedback.truePositivesCount,
					i.matchFeedback.falsePositivesCount,
					i.matchFeedback.trueNegativesCount);
		}*/
		
		matchTruePositives = i.matchFeedback.truePositivesCount;		
		matchFalsePositives = (i.mappingTask.getCandidateCorrespondences().size()) - (i.matchFeedback.truePositivesCount);
		if(JaguarConstants.UPDATE_ANNOTATIONS_MODE_1==true)
		{
			AnnotationsCollector annotationsCollector = FeedbackPlanApplication.annotationsCollector;
			annotationsCollector.updateMatchAnnotations(
					matchTruePositives,
					matchFalsePositives,
					i.matchFeedback.trueNegativesCount);
		}		
				
		//System.out.println("matchPrecision=" + matchPrecision);
		//System.out.println("matchRecall=" + matchRecall);
		//System.out.println("matchFMeasure=" + matchFMeasure);
		
		if(JaguarConstants.LOGGING)
		{
			LogWriter logWriter = new LogWriter();
			logWriter.writeToFile("Log.txt","ApplyFeedback.updateAnnotations(Match)");			
		}
		if(JaguarConstants.ELAPSEDTIMEOUT)
		{
			long currentTime   = System.currentTimeMillis();
			long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
			System.out.println("\tApplyFeedback.updateAnnotations(Match). Elapsed Time = "+elapsedTime);			
		}
	}
	public ArrayList<ValueCorrespondence> getMatchesList() {
		return matchesList;
	}


	public void setMatchesList(ArrayList<ValueCorrespondence> matchesList) {
		//this.matchesList = null;
		if(this.matchesList==null)
			this.matchesList = matchesList;
	}
	@Override
	public void updateAnnotationsBootstrapping(Integration i, int feedbackType) {
		// TODO Auto-generated method stub
		
	}
}
