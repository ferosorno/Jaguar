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

package uk.ac.man.jaguar.controller;

import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.model.Episode;
import uk.ac.man.jaguar.model.FeedbackPlan;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Step;
import uk.ac.man.jaguar.util.AnnotationsCollector;
import uk.ac.man.jaguar.util.DatabaseUtils;

/**
 *
 * @author osornogf
 */
public class FeedbackPlanApplication {
	
	boolean isLastRunIntegrationWorkflow;
	static String data;
	static ArrayList<Step> workflow = null;
	
    WorkflowRunner workflowRunner;
    public static AnnotationsCollector annotationsCollector = null;

    
    public FeedbackPlanApplication()
    {
    	workflowRunner = null;
        workflowRunner = new WorkflowRunner();
        annotationsCollector = null;
        annotationsCollector =  new AnnotationsCollector(200);
        
    }
    
    public static void runFeedbackPlanApplication(FeedbackPlan feedbackPlan)
    {
        /*
        * Create the bootstrapping workflow, has three steps.
        */
       
        workflow = new ArrayList<Step>();
        
        Step s1 = new Step();
        s1.type=0;//match
        workflow.add(s1);
        Step s2 = new Step();
        s2.type=1;//map
        workflow.add(s2);
        Step s3 = new Step();
        s3.type=2;//er
        workflow.add(s3);
        
        Integration i = new Integration();
        
        FeedbackPlanApplication feedbackPlanApplication = new FeedbackPlanApplication();
        
        feedbackPlanApplication.applyFeedbackPlan(feedbackPlan, workflow, i);
        
        if(JaguarConstants.LATEX_RESULTS)
        	annotationsCollector.sendToLatexTablesSingleSpace(feedbackPlan);
        if(JaguarConstants.QUALITY_RESULTS)
        	annotationsCollector.sendAnnotationsToTxtFile(feedbackPlan);
        
        /*
         * The following code is used to "clean" the Dataspace by setting the variables to null
         * and clearing the memory calling the java garbage collector.
         */
        
        DatabaseUtils dbu = new DatabaseUtils();
        dbu.emptyGSTables();
        MapManipulation mappingsManipulation = (MapManipulation)i.mappingsManipulation;        
        for(int k=0; k<mappingsManipulation.mappingsList.size(); k++)
        {
        	MappingObject mappingObject = mappingsManipulation.mappingsList.get(k);
        	dbu.dropTGDs(mappingObject);
        }
        i.resetIntegration();
        i = null;
        dbu = null;
        System.gc();
    }
    
    public void applyFeedbackPlan(FeedbackPlan feedbackPlan, ArrayList<Step> workflow, Integration i)
    {
        int feedbackPlanSize = feedbackPlan.size();
        
        isLastRunIntegrationWorkflow = false;
        
        //Bootstrapping.
        //this workflow has three steps for bootstrapping.
        //it is an starting workflow.
        workflowRunner.runIntegrationWorkflow(workflow, i);
        //This collect annotations
        //doesn't have feedback. It only
        //calculate the initial Precision, Recall and 
        //FMeasure from the bootstrapping results.
        // Because there is not feedback yet, it 
        // may collect only ground truth values or nothing.
        collectAnnotations(i);
        
        for(int k=0; k<feedbackPlanSize; k++)
        {
            Episode episode = feedbackPlan.get(k);
            applyFeedbackEpisode(episode, workflow, i);
            //This collect annotations in theory
            //already calculates annotations after
            //feedback has been collected.
            collectAnnotations(i);
            System.gc();
        }
        //last run integration workflow.
        
        workflowRunner.runIntegrationWorkflow(workflow, i);
        //Last collect Annotations.
        isLastRunIntegrationWorkflow = true;
        collectAnnotations(i);
        
    }
    /**
     * We create the workflow until the current step and receive as argument which step has to be the end,
     * we create a new integration workflow (limited workflow) which is a portion of w.
     * @param episode
     * @param workflow
     * @param i 
     */
    public void applyFeedbackEpisode(Episode episode, ArrayList<Step> workflow, Integration i)
    {
        ArrayList<Step> limitedWorkflow = new ArrayList<>();
        if(episode.type==0){
            limitedWorkflow.add(workflow.get(0));
        }
        if(episode.type==1){
            limitedWorkflow.add(workflow.get(0));
            limitedWorkflow.add(workflow.get(1));
        }
        if(episode.type==2){
            limitedWorkflow.add(workflow.get(0));
            limitedWorkflow.add(workflow.get(1));
            limitedWorkflow.add(workflow.get(2));
        }
        
        workflowRunner.runIntegrationWorkflow(limitedWorkflow, i);
        
        i.feedbackCollectionBroker.collectFeedback(episode,i);
        i.feedbackApplication.applyFeedback(episode.type, i);// Apply on the current step (updateAnnotations())
        i.feedbackApplication.propagateFeedback(episode.type, i);// propagate to other steps
        
        //added refresh integration
        workflowRunner.runIntegrationWorkflow(limitedWorkflow, i);
        
        System.gc();
    }
    
    public void collectAnnotations(Integration i)
    {
        
        annotationsCollector.collectAnnotations(
        		//match
				i.matchFeedback.truePositivesCount,
				i.matchFeedback.falsePositivesCount,
				i.matchFeedback.trueNegativesCount,
				//map
				i.mapFeedback.truePositivesGT,
				i.mapFeedback.falsePositivesGT,
				i.mapFeedback.trueNegativesGT,
				i.mapFeedback.falseNegativesGT,
				i.mapFeedback.duplicatesGT,
        		i.mapFeedback.numMapForAveragePrecision,
        		i.mapFeedback.totalNumMap,
        		i.mapFeedback.gtAveragePrecision,
        		i.mapFeedback.fbPropToMatch,
        		//er
        		i.erFeedback.truePositives,
        		i.erFeedback.falsePositives,
        		i.erFeedback.fbPropToMap,
        		i.erFeedback.fbUniqueRecPropToMap
        		);
        
        data = i.mapFeedback.truePositivesGT+"|"+i.mapFeedback.falsePositivesGT+"|"+i.mapFeedback.duplicatesGT;

        
    }
    
    public static String getData(int feedbackPlanNumber)
    {
    	return feedbackPlanNumber+"|"+data;
    }
    
    
    
}
