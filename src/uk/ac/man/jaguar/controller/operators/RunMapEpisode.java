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

package uk.ac.man.jaguar.controller.operators;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Transformation;
import it.unibas.spicy.attributematch.strategies.IMatchAttributes;
import it.unibas.spicy.attributematch.strategies.MatchAttributes1to1Coma;
import it.unibas.spicy.attributematch.strategies.MatchAttributes1to1StructuralAnalysis;
import it.unibas.spicy.findmappings.strategies.FindMappingsMatchMapCheck;
import it.unibas.spicy.findmappings.strategies.FindMappingsMatchMapCheckLoop;
import it.unibas.spicy.findmappings.strategies.IFindBestMappingsStrategy;
import it.unibas.spicy.findmappings.strategies.computequality.ComputeQualityAverageFeatureSimilarity;
import it.unibas.spicy.findmappings.strategies.computequality.ComputeQualityMatchAverage;
import it.unibas.spicy.findmappings.strategies.computequality.ComputeQualityMatchDistance;
import it.unibas.spicy.findmappings.strategies.computequality.ComputeQualityMatchHarmonicMean;
import it.unibas.spicy.findmappings.strategies.computequality.ComputeQualityStructuralAnalysis;
import it.unibas.spicy.findmappings.strategies.computequality.IComputeQuality;
import it.unibas.spicy.findmappings.strategies.generatecandidates.GenerateCandidates1to1FixedSizeCaching;
import it.unibas.spicy.findmappings.strategies.generatecandidates.GenerateCandidates1to1PowersetCaching;
import it.unibas.spicy.findmappings.strategies.generatecandidates.IGenerateCandidateMappingTaskStrategy;
import it.unibas.spicy.findmappings.strategies.stopsearch.IStopSearchStrategy;
import it.unibas.spicy.findmappings.strategies.stopsearch.StopNonEmptyCandidatesSimilarityThreshold;
import it.unibas.spicy.findmappings.strategies.stopsearch.StopSizeAndSimilarity;
import it.unibas.spicy.model.algebra.query.operators.sql.GenerateSQL;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.structuralanalysis.circuits.strategies.BuildCircuitSSDensityReduced;
import it.unibas.spicy.structuralanalysis.circuits.strategies.BuildCircuitSSDensityReducedWithCaching;
import it.unibas.spicy.structuralanalysis.circuits.strategies.BuildCircuitSSReduced;
import it.unibas.spicy.structuralanalysis.circuits.strategies.BuildCircuitSSReducedWithCaching;
import it.unibas.spicy.structuralanalysis.circuits.strategies.FindNodesUndersampled;
import it.unibas.spicy.structuralanalysis.circuits.strategies.FindNodesUndersampledConstraints;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IFindNodesToExclude;
import it.unibas.spicy.structuralanalysis.compare.operators.CompareFeatures;
import it.unibas.spicy.structuralanalysis.compare.strategies.AggregateAsHarmonicMean;
import it.unibas.spicy.structuralanalysis.compare.strategies.AggregateAsWeightedAverage;
import it.unibas.spicy.structuralanalysis.compare.strategies.IAggregateSimilarityFeatures;
import it.unibas.spicy.structuralanalysis.sampling.operators.SampleInstances;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateConsistencyPolynomial;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateStandardCharCategoryDistribution;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateStandardLengthDistribution;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateStressAverageLength;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateCharCategoryDistributionStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateConsistencyStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateLengthDistributionStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateStressStrategy;
import it.unibas.spicy.structuralanalysis.strategies.IPerformStructuralAnalysis;
import it.unibas.spicy.structuralanalysis.strategies.PerformLocalStructuralAnalysis;
import it.unibas.spicy.structuralanalysis.strategies.PerformStructuralAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;





import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.artefacts.MatchManipulation;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.controller.operators.map.MapResult;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.DatabaseAccessInfo;
import uk.ac.man.jaguar.util.DatabaseUtils;
import uk.ac.man.jaguar.util.ExitTrappedException;
import uk.ac.man.jaguar.util.LogWriter;
import uk.ac.man.jaguar.util.MappingUtil;

/**
 *
 * @author osornogf
 */
public class RunMapEpisode implements IRunEpisode{
    
    /**
     *  Tasks:
     * - Read the existing match feedback.
     * - Generate the mappings with the existing feedback.
     * - Run the mappings.
     * - If there are existing mappings, then we should keep their results and the feedback
     *   that we have of the results.
     *   
     * @param i
     */
    @Override
    public void runEpisode(Integration i)
    {
    	if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tRun Map Episode Start. Elapsed Time = "+elapsedTime);
        }
    	
    	MappingUtil mappingUtil = new MappingUtil();
        if(JaguarConstants.SYSTEMOUT)
        	System.out.println("Running MapEpisode.runMap()");
        
        i.loadMappingTask();
        //i.mappingTask.clearCandidateCorrespondences();
        //i.mappingTask.clearCorrespondences();
        //System.out.println("Size of new matches="+((MatchManipulation)i.matchesManipulation).getMatchesList().size());
        if(i.mappingTask.getCandidateCorrespondences().size()==0)
        {
        	//Here, I have to read the CandidateCorrespondences from "i.matchesManipulation"
        	//and update the CandidateCorrespondences from the i.mappingTask.
        	i.mappingTask.setCandidateCorrespondences(((MatchManipulation)i.matchesManipulation).getMatchesList());
        }
        /*else
        {
        	System.out.println("i.mappingTask.getCandidateCorrespondences() is not 0");
        }*/
        /**
         * In this method, we are going to take the variables in the MappingTask object (which are
         * the correspondences, if any) and try to create the mappingsManipulation. It is very probable that we
         * don't get any mappingsManipulation. WE TAKE THE CURRENT ANNOTATIONS OF THE CORRESPONDENCES.
         */
        
    	//  STRATEGY TO CREATE A SAMPLER
        IGenerateLengthDistributionStrategy standardLengthDistribution = new GenerateStandardLengthDistribution();
        
        IGenerateStressStrategy stressAverageLength= new GenerateStressAverageLength(standardLengthDistribution);
        
        IGenerateCharCategoryDistributionStrategy charCategoryDistributionGenerator = new GenerateStandardCharCategoryDistribution();
        
        IGenerateConsistencyStrategy consistencyPolinomial = 
        		new GenerateConsistencyPolynomial(charCategoryDistributionGenerator);
        
        SampleInstances sampleInstances = new SampleInstances(stressAverageLength, consistencyPolinomial);
        
        // STRATEGY TO EXCLUDE NODE 
        IFindNodesToExclude excluder = new FindNodesUndersampled(30);
        //IFindNodesToExclude excluder = new FindNodesUndersampledConstraints(30);
        
        // STRATEGY TO BUILD A CIRCUIT
        // This compares the rules with instances        
        
        double multiplyingFactorForElements = 1.0d;
        double multiplyingFactorForStatisticsElements = 1.0d;
        double levelResistance = 1.0d;
        double externalResistance = 1.0d;
        
        //This creates good mappings. is for matching but i am not mathcing.
        IBuildCircuitStrategy circuitBuilder = new BuildCircuitSSReduced(excluder,
		multiplyingFactorForElements,
		multiplyingFactorForStatisticsElements,
		levelResistance,
		externalResistance);
        /*IBuildCircuitStrategy circuitBuilder = new BuildCircuitSSReducedWithCaching(excluder,
		multiplyingFactorForElements,
		multiplyingFactorForStatisticsElements,
		levelResistance,
		externalResistance);*/ 
        /*IBuildCircuitStrategy circuitBuilder = new BuildCircuitSSDensityReduced(excluder,
		multiplyingFactorForElements,
		multiplyingFactorForStatisticsElements,
		levelResistance,
		externalResistance);*/        
        /*IBuildCircuitStrategy circuitBuilder = new BuildCircuitSSDensityReducedWithCaching(excluder,
        		multiplyingFactorForElements,
        		multiplyingFactorForStatisticsElements,
        		levelResistance,
        		externalResistance);*/
        
        //STRATEGY TO AGGREGATE SIMILARITY FEATURES, I am not matching
        //This also can have other parameters as signature - strategy to aggregate similarity features.
        IAggregateSimilarityFeatures aggregationStrategy = new AggregateAsHarmonicMean();
        //IAggregateSimilarityFeatures aggregationStrategy = new AggregateAsWeightedAverage();
        
        // COMPARATOR, i am not matching
        Double similarityThresholdComparator = new Double(0.0d);
        CompareFeatures comparator = new CompareFeatures(aggregationStrategy, similarityThresholdComparator);
        
        // STRATEGY TO MATCH, but I am not using this match so is not relevant
        double similarityThreshold = 0.0d;
        int minSampleSize = 30;
        IMatchAttributes attributeMatcher = new MatchAttributes1to1StructuralAnalysis(sampleInstances, circuitBuilder, comparator,
        similarityThreshold, minSampleSize);//argument = minimum sample size        
        /*IMatchAttributes attributeMatcher = new MatchAttributes1to1Coma(minSampleSize);*/      
        
        //STRATEGY TO MAKE STRUCTURAL ANALYSIS
        //IPerformStructuralAnalysis analyzer = new PerformLocalStructuralAnalysis(sampleInstances,circuitBuilder,comparator);
        IPerformStructuralAnalysis analyzer = new PerformStructuralAnalysis(sampleInstances,circuitBuilder,comparator);
        
        // STRATEGY TO COMPUTE QUALITY
        //IComputeQuality computeQualityStructuralAnalysis = new ComputeQualityStructuralAnalysis(analyzer);
        // IComputeQuality computeQualityAverageFeatureSimilarity = new ComputeQualityAverageFeatureSimilarity();
        IComputeQuality computeQualityMatchAverage = new ComputeQualityMatchAverage();
        //IComputeQuality computeQualityMatchHarmonicMean = new ComputeQualityMatchHarmonicMean();
        //IComputeQuality computeQualityMatchDistance = new ComputeQualityMatchDistance();
        
        // STRATEGY TO STOP SEARCH
        IStopSearchStrategy stopSearchStrategy = new StopNonEmptyCandidatesSimilarityThreshold();  
        //IStopSearchStrategy stopSearchStrategy = new StopSizeAndSimilarity();//does not work
        
        double qualityThreshold = 0.0d;// 0 produces more mappings, nh// 0 reduces the mappings search space
        
        // STRATEGY TO GENERATE CANDIDATE MAPPING TASKS - this produces more mappings
        //IGenerateCandidateMappingTaskStrategy candidatesGenerator = new GenerateCandidates1to1PowersetCaching();
        //this produces less mappings
        IGenerateCandidateMappingTaskStrategy candidatesGenerator = new GenerateCandidates1to1FixedSizeCaching();        
        
        // STRATEGY TO FIND MAPPINGS
        //IFindBestMappingsStrategy findMappingsMMCheck1 = new FindMappingsMatchMapCheckLoop(attributeMatcher,  
        //IFindBestMappingsStrategy findMappingsMMCheck1 = new FindMappingsMatchMapCheck(attributeMatcher,              
        //candidatesGenerator, computeQualityStructuralAnalysis, stopSearchStrategy, qualityThreshold);
        
        IFindBestMappingsStrategy findMappingsMMCheck2 = new FindMappingsMatchMapCheck(attributeMatcher,
        candidatesGenerator, computeQualityMatchAverage, stopSearchStrategy, qualityThreshold); 
        
        //IFindBestMappingsStrategy findMappingsMMCheck3 = new FindMappingsMatchMapCheck(attributeMatcher,        
        //candidatesGenerator, computeQualityMatchHarmonicMean, stopSearchStrategy, qualityThreshold);            
        
        //IFindBestMappingsStrategy findMappingsMMCheck4 = new FindMappingsMatchMapCheck(attributeMatcher,        
        //candidatesGenerator, computeQualityMatchDistance, stopSearchStrategy, qualityThreshold);/**/        
        
        ////System.out.println("Checking if has candidate correspondences=");
        ////System.out.println(i.mappingTask.getCandidateCorrespondences().isEmpty());
        
        // FIND MAPPINGS
        ArrayList<AnnotatedMappingTask> annotatedMappingTasks = new ArrayList<AnnotatedMappingTask>();
        
		//for(int s=0; s<1; s++)
		//{
			//long seed = System.nanoTime();
			//Collections.shuffle((i.mappingTask.getCandidateCorrespondences()), new Random(seed));
	        //annotatedMappingTasks.addAll(findMappingsMMCheck1.findBestMappings(i.mappingTask));
	        annotatedMappingTasks.addAll(findMappingsMMCheck2.findBestMappings(i.mappingTask));
	        //annotatedMappingTasks.addAll(findMappingsMMCheck3.findBestMappings(i.mappingTask));
	        //annotatedMappingTasks.addAll(findMappingsMMCheck4.findBestMappings(i.mappingTask));
		//}
        
        ArrayList<JoinCondition> joinConditionsSourceList = (ArrayList)i.mappingTask.getSourceProxy().getJoinConditions();
        
        ArrayList<JoinCondition> joinConditionsTargetList = (ArrayList)i.mappingTask.getTargetProxy().getJoinConditions();
        
        //Here, so far we go well. But we still have to finish the mapping generation.
        //if(JaguarConstants.SYSTEMOUT)
        	//System.out.println("Number of Mapping Tasks="+annotatedMappingTasks.size());
        //IDataSourceProxy proxy=i.annotatedMappingTask.getMappingData().getSolution();
        
        //annotatedMappingTasks.get(0).getMappingTask()
        
        //if(JaguarConstants.SYSTEMOUT)
        	//System.out.println("size of annotatedMappingTasks.size()="+annotatedMappingTasks.size());//I can modify this threshold in the generation of mappings.
        
        MapManipulation mappingsManipulation = (MapManipulation)i.mappingsManipulation;
        
        //mappingsManipulation.setActiveFalseAllExistingMappings(); //I don't have to deactivate because there is nothing to deactivate.
        if(mappingsManipulation.mappingsList ==null)
        	mappingsManipulation.mappingsList = new ArrayList<MappingObject>();
        
        //mappingsManipulation.mappingsList = null;//I start new mappings everytime.
        for(int r=0;r<mappingsManipulation.mappingsList.size();r++)
        {
        	mappingsManipulation.mappingsList.get(r).setActive(false);
        }
        
        int tableMappingsCounter = 0;
        int tableExistMappingsCounter = 0;
        int activeMappings=0;
        //Be careful as generation may be done more than once
        //if I do not control this, then I can not make more than
        //one mapping generation.
        for(int k=0; k<annotatedMappingTasks.size(); k++)
        {
            AnnotatedMappingTask annotatedMappingTask = annotatedMappingTasks.get(k);
            
            MappingTask mappingTask = annotatedMappingTask.getMappingTask();
            if(JaguarConstants.SYSTEMOUT) 
            	System.out.println("NUMBER OF JOIN CONDITIONS="+mappingTask.getSourceProxy().getJoinConditions());
            
            for(int q=0; q<joinConditionsSourceList.size(); q++)
            {
                mappingTask.getSourceProxy().addJoinCondition(joinConditionsSourceList.get(q));
                if(JaguarConstants.SYSTEMOUT)
                	System.out.println("JOIN CONDITION=*"+joinConditionsSourceList.get(q).toString()+"*");
            }
            
            for(int q=0; q<joinConditionsTargetList.size(); q++)
            {
                mappingTask.getTargetProxy().addJoinCondition(joinConditionsTargetList.get(q));
                if(JaguarConstants.SYSTEMOUT)
                	System.out.println("JOIN CONDITION TARGET=*"+joinConditionsTargetList.get(q).toString()+"*");
            }
            
            GenerateSQL generateSQLObject = new GenerateSQL();
            
            String sql = generateSQLObject.generateSQL(mappingTask);//SQL of all the mapping task.
            
            if(JaguarConstants.SYSTEMOUT)
            {
            	System.out.println("GENERATED SQL = *"+sql+"*ENDSQL");
            	System.out.println("ValueCorrespondences="+annotatedMappingTask.getMappingTask().getValueCorrespondences().toString());
            	System.out.println("CandidateCorrespondences="+annotatedMappingTask.getMappingTask().getCandidateCorrespondences().toString());
            }
            try
            {
	            /**
	             * In createMappingObject(String sql) we parse the mapping sql to obtain the
	             * statements to be executed on the database.
	             * Here, we separate the mapping task into separate mappings per table.
	             */
	            ArrayList<MappingObject> mappingObjects = mappingsManipulation.createMappingObjects(sql, mappingTask);
            	
                //System.out.println("mappingObjects.size()="+mappingObjects.size());
	            //System.out.println("Number of Mappings Objects="+mappingObjects.size());
	            for(int l=0; l<mappingObjects.size(); l++)  
	            {
	            	MappingObject mappingObject = mappingObjects.get(l);
	            	/**
	            	 * I have to identify if the mapping exists. If the mapping does not
	            	 * exist then I have to add it and it will be active in this episode.
	            	 * If the mapping does exist then I have to activate the existing mapping, but
	            	 * it has to be run also. 
	            	 * The NEW mapping has to take the provenance of the existing mapping.
	            	 * Also I have to deactivate those mappings that are not produced here.
	            	 */
	            	/**
	            	 * singleMappingExists2(mappingObject) checks if there is another mapping with
	            	 * the same SS attributes and the same table number.
	            	 */
	            	/*if(mappingObject.getTableName().compareTo("testt")==0 || mappingObject.getTableName().compareTo("societyt")==0)
	            		System.out.println("Found "+mappingObject.getTableName());
	            	*/
	            	boolean mappingExists=true;
	            	
	            	if(!mappingsManipulation.singleMappingExistsSSAttributes(mappingObject))
	            	{
	            		mappingExists = false;
	            	}else 
	            	if(mappingObject.isHasEXCEPT() && mappingObject.getSSAttributes().size()>0)
	            	if(!mappingsManipulation.singleMappingExistsFORules(mappingObject))
	            	{
	            		mappingExists = false;
	            		if(mappingExists)
	            			System.out.println("Existing with FORule mappingExists="+mappingExists);
	            	}
		            
	            	if(!mappingExists)
	            	{
		            	//I have a new mapping:
		            	tableMappingsCounter++;
		            	//Add it and it will be active in this episode.
		            	mappingObject.setActive(true);
		                mappingsManipulation.addMappingObject(mappingObject);
		               
		                if(JaguarConstants.FILEOUTOUT)
		                {
			                try{
			                    try (PrintWriter writer = new PrintWriter(JaguarConstants.FILEOUT_PATH+"\\mappings\\"+mappingObject.getProvenance()+"_"+k+"_"+l+"_"+System.currentTimeMillis()+""+".txt", "UTF-8")) 
			                    {
			                       //writer.println(sql);
			                    	writer.println(mappingObject.getTgds().toString());
			                    	for(int e=0;e<mappingObject.getResultOfExchange().size();e++)
			                    		writer.println(mappingObject.getResultOfExchangeAt(e).toString());
			                    	writer.println(mappingObject.getSSAttributes().toString());
			                        /*for(int j=0; j<mappingTask.getValueCorrespondences().size(); j++)
			                        {
			                        	writer.println(mappingTask.getValueCorrespondences().get(j).toString());
			                        }
			                        for(int j=0; j<annotatedMappingTask.getTransformations().size(); j++)
			                        {
			                        	writer.println(annotatedMappingTask.getTransformations().get(j).toString());
			                        }*/
			                    	writer.flush();
			                    	writer.close();
			                    }
			                }
			                catch(FileNotFoundException | UnsupportedEncodingException ex)
			                {
			                    System.out.println(ex.getMessage());
			                    ex.printStackTrace();
			                    if(JaguarConstants.SYSTEMEXIT)
			                    	System.exit(0);
			                }
		                }

		            }
		            else
		            {
		            	tableExistMappingsCounter++;
		            	//System.out.println("The mappings already exists!!");
		            	mappingsManipulation.setActiveTrueToExistingMapping(mappingObject);
		            	//if(JaguarConstants.SYSTEMOUT)
		            		//System.out.println("The mappings already exists!!");
		                if(JaguarConstants.FILEOUTOUT )
		                {
			                try{
			                    try (PrintWriter writer = new PrintWriter(JaguarConstants.FILEOUT_PATH+"\\mappings\\"+"existing_"+mappingObject.getProvenance()+"_"+k+"_"+l+"_"+System.currentTimeMillis()+""+".txt", "UTF-8")) 
			                    {
			                       //writer.println(sql);
			                    	writer.println(mappingObject.getTgds().toString());
			                    	for(int e=0;e<mappingObject.getResultOfExchange().size();e++)
			                    		writer.println(mappingObject.getResultOfExchangeAt(e).toString());
			                    	writer.println(mappingObject.getSSAttributes().toString());
			                        /*for(int j=0; j<mappingTask.getValueCorrespondences().size(); j++)
			                        {
			                        	writer.println(mappingTask.getValueCorrespondences().get(j).toString());
			                        }
			                        for(int j=0; j<annotatedMappingTask.getTransformations().size(); j++)
			                        {
			                        	writer.println(annotatedMappingTask.getTransformations().get(j).toString());
			                        }*/
			                    	writer.flush();
			                    	writer.close();
			                    }
			                }
			                catch(FileNotFoundException | UnsupportedEncodingException ex)
			                {
			                    System.out.println(ex.getMessage());
			                    ex.printStackTrace();
			                    if(JaguarConstants.SYSTEMEXIT)
			                    	System.exit(0);
			                }
		                }
		            }
	            }
	            
            }
            catch(NullPointerException e)
            {
            	e.printStackTrace();
            }
        }
        //System.out.println("Size of mappings found="+mappingsManipulation.mappingsList.size());
        //System.out.println("Number of new table mappings: "+tableMappingsCounter);
        //System.out.println("Number of exist table mappings: "+tableExistMappingsCounter);
        for(int r=0;r<mappingsManipulation.mappingsList.size();r++)
        {
        	if(mappingsManipulation.mappingsList.get(r).isActive())
        	{
        		activeMappings++;
        		//System.out.println("Active prov="+mappingsManipulation.mappingsList.get(r).getProvenance()+" table="+mappingsManipulation.mappingsList.get(r).getTableNumber());
        	}
        }
        //System.out.println("Active mappings="+activeMappings);
        //System.out.println("All mappings="+mappingsManipulation.mappingsList.size());
        
        /**
         * Then here I have to identify if there are repeated mappings, new mappings or 
         * are missing mappings.
         * For repeated     mappings:   do nothing
         * For new          mappings:   run them
         * for missing      mappings:   do nothing
         * for returning    mappings:   identify their structure/data
         */
        /*###############################################################################################*/
        /*######### AFTER CREATING THE MAPPINGS, WE HAVE TO RUN THEM ####################################
          ######### AND CREATE THE SETS OF RESULTS IN A DATA STRUCTURE ##################################*/
        /******  TO DO We should have a method to calculate a sample  *********/
        // I have to erase all the previous results.
        
        emptyGlobalSchema();//This method call makes the work to double check that the GS is empty.
        
        // RUN MAPPINGS, It should run only the active mappings.
        runMappings(mappingsManipulation.mappingsList);
        
        int mappingsNonEmpty = 0;
        int sizeOfRes=0;
        for(int k=0; k<mappingsManipulation.mappingsList.size(); k++)
        {
        	MappingObject mappingObject = (MappingObject)mappingsManipulation.mappingsList.get(k);
        	if(mappingObject.isActive())
        	{
            	mappingsManipulation.estimateGroundTruthPrecision(mappingObject);
            	if(JaguarConstants.SYSTEMOUT)
            		System.out.println("Running mapping " + k);
        	}
        	if(mappingObject.sizeOfResults>0)
        	{
        		mappingsNonEmpty++;
        		sizeOfRes +=mappingObject.sizeOfResults;
        	}
        	//System.out.println(mappingObject.getTableName()+"\t "+mappingObject.getProvenance()+" "+mappingObject.sizeOfResults );
        }
        
        System.out.println("Non empty map="+mappingsNonEmpty);
        System.out.println("Size of res="+sizeOfRes);
        //Create the data structure to store the results, on which we will obtain later the feedback
        //This structure should be used to obtain the feedback in this episode, but the feedback
        //should be stored in another data different structure, because only the feedback should remain
        //not all the results.
        //if(mappingResults == null)//if it is null, the mapping list is empty yet
        //	mappingResults = new ArrayList<>();
        /**
         * In the MapResult data structure I am storing the data of the mappings results.
         */
        i.mapFeedback.deletePreviousResults();
        int sizeOfResultsAll=0;
        int mappingsWithResults=0;
        for(int k=0; k<mappingsManipulation.mappingsList.size(); k++)
        {
            MappingObject mappingObject = (MappingObject) mappingsManipulation.mappingsList.get(k);
            
            if(mappingObject.isActive())
            {
	            int mappingProvenance = mappingObject.getProvenance();
	            ///// FOR MAPPINGS' RESULTS. I can keep this structure. But this object has to be temporal.
	            /// This object should be erased after the episode.
	            //this no more because it will always be a new run
	            //MappingResultsSet mapingResultsSet = i.mapFeedback.getMapFeedbackSet(mappingProvenance);
	            
	            MappingResultsSet mapingResultsSet = null;
	            
                mapingResultsSet = new MappingResultsSet();
                
                mapingResultsSet.setProvenance(mappingProvenance); //Only once because it is ONE mapping
                mapingResultsSet.setTableName(mappingObject.getTableName());
                if(JaguarConstants.SYSTEMOUT)
                	System.out.println("Reading feedback records for the mapping="+mappingProvenance);
                
                DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
                
                Connection connection = null;
                ResultSet rs = null;
                try
                {
                    connection = DriverManager.getConnection(accessInfo.getUri(),accessInfo.getUserName(),accessInfo.getPassword());
                    try (Statement stmt = connection.createStatement()) {
                    	
                    	//Create the sql statement.
                        
                        //ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesTakenOnBoard;
                        
                        String tableName = mappingObject.getTableName();
                        int entityNumber = mappingUtil.getEntityNumber(tableName);
                        //This no more because now the mappings is only for one table
                        
                    	ArrayList<String> attributesNames = JaguarVariables.globalSchemaTablesAttribsTakenOnBoard.get(tableName); 
                    	
                    	if(JaguarConstants.SYSTEMOUT)
                    		System.out.println("Records for Feedback for Table="+tableName);
                    	
                        String attributes = attributesNames.get(0);
                        for(int j=0; j<attributesNames.size(); j++)
                        {
                            attributes += ", " + attributesNames.get(j);
                        }
                        
                        attributes = "id, "+attributes;
                        
                        String sql="select "+attributes+" from "+tableName+" where provenance=\'"+
                                String.valueOf(mappingProvenance)+"\'  order by id;";
                        
                        //Read the results of each mapping for feedback.
                        if(JaguarConstants.SYSTEMOUT)
                        	System.out.println("Running for Records sql="+sql);
                        
                    	rs = stmt.executeQuery(sql);
                        while (rs.next())
                        {
                        	//System.out.println("The mapping generated records.");
                            int id = rs.getInt("id");
                            ArrayList<String> attributeValues = new ArrayList<>();
                            
                            for(int r=0; r<attributesNames.size(); r++)
                            {
                                String attribute = rs.getString((String)attributesNames.get(r));
                                if(attribute!=null)
                                    attributeValues.add(attribute.trim());
                                else
                                    attributeValues.add("");
                            }
                            
                            Record record = new Record();
                            record.setRecordId(String.valueOf(id));
                            record.setProvenance(mappingProvenance);
                            record.setAttributesValues(attributeValues);
                            //System.out.println("Record id="+String.valueOf(id));
                            //System.out.println(attributeValues.toString());
                            record.setEntityNumber(mappingUtil.getEntityNumber(tableName));
                            
                            String gtValue = mappingUtil.getGroundTruthValue(
                            		attributesNames, 
                            		attributeValues,
                            		tableName);
                            
                            //This is the individual GT precision of the mapping.
                            if(JaguarConstants.SYSTEMOUT)
                            	System.out.println("gtValue="+gtValue);
                            //Map flags.
                            record.setGroundTruthValue(gtValue);
                            record.setHasMapFeedback(false);
                            i.mapFeedback.setFeedbackIfExists(record);
                            //ER flags.
                            record.setHasERFeedback(false);//This is new, don't know if there is ER feedback for this.
                            record.setDuplicate(false);
                            record.setDuplicateAndThisRecordCounts(true);//Initially, all the records count.
                            //Add to structure.
                            mapingResultsSet.addRecord(record);
                        }
                        if(mapingResultsSet.getRecords().size()>0)
                        {
                        	//System.out.println("Records count in RunMapEpisode="+mapingResultsSet.getRecords().size());
                        	sizeOfResultsAll +=mapingResultsSet.getRecords().size();
                        	mappingsWithResults++;
                        }
                        mapingResultsSet.setAttributesNames(attributesNames, entityNumber);
                    }
                    connection.commit();
                }
                catch(SQLException sqle)
                {
                    System.out.println(sqle.getMessage());
                    sqle.printStackTrace();
                    if(JaguarConstants.SYSTEMEXIT)
                    	System.exit(0);
                }
                finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) { /* ignored */}
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) { /* ignored */}
                    }
                }
                mapingResultsSet.sortRecords();
                
                i.mapFeedback.addMappingResults(mapingResultsSet);
	        }
        }
        //System.out.println("Mappings with res="+mappingsWithResults);
        //System.out.println("Size all results =  "+sizeOfResultsAll);
        //i.mappingTask.clearCandidateCorrespondences();
        //i.mappingTask.clearCorrespondences();
        //i.unloadMappingTask();
        
        //Optionally we can empty the results here.
        emptyGlobalSchema();
        if(JaguarConstants.LOGGING)
        {
            LogWriter logWriter = new LogWriter();
            logWriter.writeToFile("Log.txt","Run Map Episode");
        }
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tRun Map Episode End. Elapsed Time = "+elapsedTime);
        }
    }
    
    /**
     * 
     * @param mapping
     * @param connection
     * @return
     */
    public MapResult selectResults(MappingObject mapping, Connection connection)
    {
        MapResult mapResult = new MapResult();
        ArrayList<ArrayList<String>> resultsTable = new ArrayList<>();
        ArrayList<String> attributesNames = new ArrayList<>();
        
        ArrayList<String> attributes=mapping.getAttributes();        
        String selectQuery = mapping.getSelectString();
        try
        {
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery( selectQuery )) {
                ResultSetMetaData rsmd = rs.getMetaData();
                
                for(int k=1; k<rsmd.getColumnCount()+1; k++)
                {
                    String nameAttribute = rsmd.getColumnName(k);
                    attributesNames.add(nameAttribute);
                }
                while ( rs.next() ) {
                    ArrayList<String> rowList = new ArrayList<>();
                    
                    for(int k=0; k<rsmd.getColumnCount(); k++)
                    {
                        String attribute = rs.getString((String)attributes.get(k));
                        rowList.add(attribute.trim());
                    }
                    resultsTable.add(rowList);
                }
                mapResult.setResults(resultsTable);
                mapResult.setAttributesNames(attributesNames);
                mapResult.setProvenance(mapping.provenance);
                stmt.close();
                rs.close();
            }
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle.getMessage());     
            sqle.printStackTrace();
            if(JaguarConstants.SYSTEMEXIT)
            	System.exit(0);
        }
        return mapResult;
    }
    public void runMapping(MappingObject mapping)
    {
    	try{    		
    		//System.out.println("runningMapping()_"+mapping.getProvenance());
	        readLastIdInTable(mapping.getTableName(), mapping);	        
	        runTGDs(mapping);
	        runResultOfExchange(mapping);
	        dropTGDs(mapping);	        
	        setProvenanceToJustInsertedResults(mapping.getTableName(), mapping);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    
    public void runMappings(ArrayList<MappingObject> mappingsList)
    {
    	
    	int numberOfMappings = mappingsList.size();
        for(int k=0; k<numberOfMappings; k++)
        {
        	if(JaguarConstants.SYSTEMOUT)
        		System.out.println("Running mapping No="+k);
        	
            MappingObject mappingObject = (MappingObject)mappingsList.get(k);
            
            if(!mappingObject.isMappingWasRun())
            //if(mappingObject.isActiveTrue())
            {
                runMapping(mappingObject);
                //System.out.println("Running mapping No="+k);
                //mappingObject.setMappingWasRun(true);
            }
            
        }
               
    }
    
    
    
    
    /**
     * Metodo muletilla para simular la creacion de mapeos.
     * @param number
     * @return 
     */
    String readMappingScript(int number)
    {
        String result=null;
         try {
             BufferedReader br = new BufferedReader(new FileReader("mapping"+number+".sql"));
             StringBuilder sb = new StringBuilder();
             String line = br.readLine();
             while (line != null) {
                 sb.append(line);
                 sb.append('\n');
                 line = br.readLine();
             }
             result = sb.toString();
             br.close();
         }catch(IOException ioe)
         {
             System.out.println(ioe.getMessage());
             ioe.printStackTrace();
             if(JaguarConstants.SYSTEMEXIT)
            	 System.exit(0);
         }
        return result;
    }
    
    
    /**
     * 
     * @param mappingsList
     * @param connection 
     */
    public void runResultExchange(ArrayList<MappingObject> mappingsList, Connection connection)
    {
        Statement stmt;
            
            try{
                Class.forName("org.postgresql.Driver");
                stmt = connection.createStatement();
                    MappingObject mappingObject = (MappingObject)mappingsList.get(0);

                    for(int index =0; index<mappingObject.getResultOfExchange().size(); index++)
                        stmt.executeUpdate(mappingObject.getResultOfExchangeAt(index));
                    
                stmt.close();
                connection.close();
            }
            catch(ClassNotFoundException | SQLException e)
            {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                e.printStackTrace();
                if(JaguarConstants.SYSTEMEXIT)
                	System.exit(0);
            }
    }
    
    /**
     * 
     * @param mappingObject
     * @param connection 
     */
    public void runResultOfExchange(MappingObject mappingObject)
    {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
        	Class.forName("org.postgresql.Driver");
            DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
            connection = DriverManager.getConnection(accessInfo.getUri(),accessInfo.getUserName(),accessInfo.getPassword());
            
            //System.out.println("in runResultExchange(...)="+mappingObject.getResultOfExchange());
            
            int startId = -1;
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT max(id) FROM "+mappingObject.getTableName()+";" );
            
            
            while (rs.next()) {
                startId = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            
            stmt = connection.createStatement();
            for(int index =0; index<mappingObject.getResultOfExchange().size(); index++)
            {
                stmt.executeUpdate(mappingObject.getResultOfExchangeAt(index));
            }
            connection.commit();
            stmt.close();
            
            int endId = -1;
            stmt = connection.createStatement();
            
            int sizeOfResults=0;
            
            if(startId==0)//if it is the first time, startId=0.
            {
            	rs = stmt.executeQuery("SELECT * FROM "+mappingObject.getTableName()+";"); 
            	while(rs.next()){
            		sizeOfResults++;
                }
            }
            else
            {
            	rs = stmt.executeQuery("SELECT max(id) FROM "+mappingObject.getTableName()+";"); 
            	while (rs.next())
            	{
            		endId = rs.getInt(1);
            	}
            	sizeOfResults=endId-startId;
            }
            
            mappingObject.setSizeOfResults(sizeOfResults);
            
            //System.out.println("Size of results="+(endId-startId)+" -"+startId+" "+endId);
            //System.out.println("Size of results="+sizeOfResults);
            connection.commit();
            
        }
        catch(ClassNotFoundException | SQLException e)
        {
            //System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            //e.printStackTrace();
        	/*
        	 * An Exception is thrown because the source attributes are duplicated, as Spicy
        	 * does not checks all the duplications to save execution effort.
        	 */
            if(JaguarConstants.SYSTEMEXIT)
            	System.exit(0);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) { /* ignored */}
            }        	
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    public void dropTGDs(MappingObject mappingObject)
    {
        DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
        Connection connection = null;
        Statement stmt = null;
            try{
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(accessInfo.getUri(),accessInfo.getUserName(),accessInfo.getPassword());
                
                stmt = connection.createStatement();
                for(int k=0; k<mappingObject.tgds.size(); k++)
                {
                    String tgd = (String)mappingObject.tgds.get(k);
                    int start = tgd.indexOf("table");
                    int end = tgd.indexOf(" AS ");
                    String tgdName = tgd.substring(start+6, end);
                    stmt.executeUpdate("drop table  if exists "+tgdName+";");
                }
                connection.commit();
            }
            catch(ClassNotFoundException | SQLException e)
            {
                System.err.println( e.getClass().getName()+": "+ e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) { /* ignored */}
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) { /* ignored */}
                }
            }
    }


    
    public void runTGDs(MappingObject mappingObject)
    {
    	Connection connection = null;
    	Statement stmt = null;
    	//System.out.println("Running mapping="+mappingObject.getProvenance());
            try{

                DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
                Class.forName("org.postgresql.Driver");
                
                connection = DriverManager.getConnection(accessInfo.getUri(), accessInfo.getUserName(), accessInfo.getPassword());
                String tgd=""; 
                stmt = connection.createStatement();                
                for(int k=mappingObject.tgds.size()-1; k>=0; k--)
                {
                    tgd = mappingObject.getTGD(k);    
                    
                    //System.out.println("tgd="+tgd);
                    stmt.executeUpdate(tgd);
                }
                connection.commit();
            }
            catch(ClassNotFoundException | SQLException e)
            {
            	
                //System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                //e.printStackTrace();
            	//System.err.println("Duplicate Source Attribute Exception trown. Mapping="+mappingObject.getProvenance());
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) { /* ignored */}
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) { /* ignored */}
                }
            }
            
    }
    
    public Connection createDBConnection(String uri, String user, String password)
    {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
                    ////System.out.println(e.getMessage());
                    System.exit(0);			
		}
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(uri,user,password);
		} catch (SQLException e) {
			////System.out.println("Connection Failed! Check output console");
            ////System.out.println(e.getMessage());
            e.printStackTrace();
			
		}
                return connection;
	}
    /**
     * Our global schema has to include an "id" attribute which is used to identify the last row inserted in 
     * the table.
     * @param table
     * @param mappingObject
     * @param connection
     */
    public void readLastIdInTable(String table, MappingObject mappingObject)
    {
        
        DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
        	Class.forName("org.postgresql.Driver");
        	connection = DriverManager.getConnection(accessInfo.getUri(),accessInfo.getUserName(),accessInfo.getPassword());
    		stmt = connection.createStatement();
    		rs = stmt.executeQuery("select max(id) from "+table+";");        	
    		
            while(rs.next()) {
            	int lastId = rs.getInt(1);//If there are not records, then it returns 0.
            	//System.out.println("in reading Lastid ="+lastId);
            	MapManipulation.lastIdinTable[(new Integer(mappingObject.getProvenance())).intValue()] = lastId;
            }
            stmt.close();
            connection.close();
        }
        catch(ClassNotFoundException | SQLException e)
        {
            System.err.println(e.getClass().getName()+": "+ e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

    }
    
    
    public void setProvenanceToJustInsertedResults(String table, MappingObject mappingObject)
    {
    	Connection connection = null;
    	Statement stmt = null;
    	DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
            try{     
            	Class.forName("org.postgresql.Driver");
                connection = this.createDBConnection(accessInfo.getUri(), accessInfo.getUserName(), accessInfo.getPassword());                	    	            	                
                int lastId = MapManipulation.lastIdinTable[new Integer(mappingObject.getProvenance()).intValue()];                
                //System.out.println("in setting Lastid ="+lastId+" prov="+mappingObject.getProvenance());                
                String sql = "UPDATE "+table+" set provenance = "+mappingObject.getProvenance()+" where id>"+lastId;
                stmt = connection.createStatement();
                stmt.executeUpdate(sql);
                connection.commit();                
            }
            catch(ClassNotFoundException | SQLException e)
            {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                e.printStackTrace();
                System.exit(0);
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) { /* ignored */}
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) { /* ignored */}
                }
            }
    }
    /**
     * Deletes the data from the Global Schema.
     * 
     */
    public void emptyGlobalSchema()
    {
    	Connection connection = null;
    	Statement stmt = null;
    	DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
            try{     
            	Class.forName("org.postgresql.Driver");
                connection = this.createDBConnection(accessInfo.getUri(), accessInfo.getUserName(), accessInfo.getPassword());                
                stmt = connection.createStatement();
                
                ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesTakenOnBoard;
                for(int j=0; j<tablesNames.size(); j++)
                {
                	String sql = "DELETE FROM "+tablesNames.get(j)+";";
                	stmt.executeUpdate(sql);
                	connection.commit();
                }
            }
            catch(ClassNotFoundException | SQLException e)
            {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                e.printStackTrace();
                System.exit(0);
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) { /* ignored */}
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) { /* ignored */}
                }
            }    	
    }
}
