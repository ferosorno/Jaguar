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

import ec.util.InitializationTools;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import uk.ac.man.jaguar.controller.artefacts.ERManipulation;
import uk.ac.man.jaguar.controller.artefacts.IArtefactManipulation;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.artefacts.MatchManipulation;
import uk.ac.man.jaguar.controller.feedback.er.ERFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.match.MatchFeedback;
import uk.ac.man.jaguar.controller.feedback.operators.FeedbackCollectionBroker;
import uk.ac.man.jaguar.controller.feedback.operators.FeedbackDistribution;
import uk.ac.man.jaguar.controller.operators.IRunEpisode;
import uk.ac.man.jaguar.controller.operators.RunEREpisode;
import uk.ac.man.jaguar.controller.operators.RunMapEpisode;
import uk.ac.man.jaguar.controller.operators.RunMatchEpisode;
import uk.ac.man.jaguar.util.AnnotationsCollector;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.GroundTruth;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicy.persistence.relational.DAORelational;

/**
 * Integration is a facade that is used to control the generation and annotation of matchings,
 mappingsManipulation and pairs of recordsManipulation
 * 
 * @author osornogf
 */
public class Integration {
    
    public IArtefactManipulation matchesManipulation;
    public IArtefactManipulation mappingsManipulation;
    public IArtefactManipulation recordsManipulation;
    
    public IRunEpisode matchEpisode;
    public IRunEpisode mapEpisode;
    public IRunEpisode erEpisode;
    
    public MappingTask mappingTask;
    public FeedbackDistribution feedbackApplication;
    public FeedbackCollectionBroker feedbackCollectionBroker;
    public IDataSourceProxy iDataSourceProxy;
    
    public MatchFeedback matchFeedback;
    public MapFeedback mapFeedback;
    public ERFeedback erFeedback;    
    
    ////GROUND TRUTH
    public GroundTruth groundTruth;
    
    ////ANNOTATIONS
    AnnotationsCollector annotationsCollector;
    
    // ACCUMULATED FEEDBACK CROSS-EPISODES
    public int accumulatedMatchFeedback;
    public int accumulatedMapFeedback;
    public int accumulatedERFeedback;
    
    public int groundTruthSize;
	
	/**
	 * 
	 */
	public Integration()
    {
        //System.out.println("Integration()");
		
        matchesManipulation = new MatchManipulation();
        matchEpisode = new RunMatchEpisode();
        matchFeedback = new MatchFeedback();
        
        mappingsManipulation = new MapManipulation();
        mapEpisode = new RunMapEpisode();
        mapFeedback = new MapFeedback();
        
        recordsManipulation = new ERManipulation();
        erEpisode = new RunEREpisode();
        erFeedback= new ERFeedback();
        
        //For feedback propagation.
        feedbackApplication = new FeedbackDistribution();
        //For feedback collection.
        feedbackCollectionBroker = new FeedbackCollectionBroker();  
        
        /////GROUND TRUTH
        groundTruth = new GroundTruth();
        groundTruthSize=InitializationTools.GROUNDTRUTHSIZE;

        
        // ACCUMULATED FEEDBACK
        accumulatedMatchFeedback = 0;
        accumulatedMapFeedback = 0;
        accumulatedERFeedback = 0;        
        /*
        try
        {
            ConfigurationReader configurationReader = new ConfigurationReader();
            DAOMappingTask daoMappingTask = new DAOMappingTask();
            this.mappingTask = daoMappingTask.loadMappingTask(configurationReader.getMappingTaskPath());
            
        }
        catch(DAOException daoe)
        {
            System.out.println(daoe.getMessage());
            System.exit(0);
        }*/
    }
	
    public void loadMappingTask()
    {
    	if(this.mappingTask==null)
    	{
	        try
	        {
	            ConfigurationReader confR = new ConfigurationReader();
	            DAOMappingTask daoMappingTask = new DAOMappingTask();
	            this.mappingTask = null;
	            this.mappingTask = daoMappingTask.loadMappingTask(confR.getMappingTaskPath());
	        }
	        catch(DAOException daoe)
	        {
	            daoe.printStackTrace();
	            System.out.println(daoe.getMessage());
	            System.exit(0);            
	        }
    	}
    }
    
    
    public void unloadMappingTask()
    {
            this.mappingTask = null;
    }    
    
    
    
   
    
    public void resetIntegration()
    {
    	
        mapFeedback.reset();
        erFeedback.reset();
        matchFeedback.reset();    	
        
        matchesManipulation = null;
        mappingsManipulation = null;
        recordsManipulation = null;
        
        matchEpisode = null;
        mapEpisode = null;
        erEpisode  = null;
        
        mappingTask  = null;
        feedbackApplication = null;
        feedbackCollectionBroker = null;
        iDataSourceProxy = null;
        
        mapFeedback = null;
        erFeedback = null;
        matchFeedback = null;
        
        groundTruth = null;
        
        annotationsCollector = null;
        
        accumulatedMatchFeedback = 0;
        accumulatedMapFeedback = 0;
        accumulatedERFeedback = 0;        
        
    }
}
