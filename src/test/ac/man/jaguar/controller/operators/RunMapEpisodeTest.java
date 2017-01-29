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

package test.ac.man.jaguar.controller.operators;
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
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
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
import uk.ac.man.jaguar.controller.operators.IRunEpisode;
import uk.ac.man.jaguar.controller.operators.RunMatchEpisode;
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


public class RunMapEpisodeTest implements IRunEpisode{
    
	
	public static void main(String args[])
	{
		ConfigurationReader configurationReader = new ConfigurationReader();  
		JaguarVariables.environments = configurationReader.getEnvironmentsFromFile();
				
        configurationReader.loadEnvironmentConfiguration(0);
        	
        Integration i = new Integration();
        i.loadMappingTask();
        	
		RunMatchEpisodeTest match = new RunMatchEpisodeTest();
		match.runEpisode(i);
		for(int u=0; u<i.matchFeedback.feedbackCorrespondenceListPositives.size();u++)
		{
			
			if(
					
					i.matchFeedback.feedbackCorrespondenceListPositives.get(u).toString().compareTo("[SDatabase5.affects.affectsTuple.different --> GDatabase5.bluet.bluetTuple.different]")==0
					|| i.matchFeedback.feedbackCorrespondenceListPositives.get(u).toString().compareTo("[SDatabase5.attentions.attentionsTuple.different --> GDatabase5.affectt.affecttTuple.different]")==0
					|| i.matchFeedback.feedbackCorrespondenceListPositives.get(u).toString().compareTo("[SDatabase5.brains.brainsTuple.branch --> GDatabase5.societyt.societytTuple.branch]")==0
					|| i.matchFeedback.feedbackCorrespondenceListPositives.get(u).toString().compareTo("[SDatabase5.societys.societysTuple.branch --> GDatabase5.approvalt.approvaltTuple.branch]")==0							
					)
			{
				i.matchFeedback.feedbackCorrespondenceListPositives.remove(u);
			}
			else
				System.out.println(i.matchFeedback.feedbackCorrespondenceListPositives.get(u).toString());
		}
		System.out.println(i.matchFeedback.feedbackCorrespondenceListPositives.size());
		//i.mappingTask.setCandidateCorrespondences(i.matchFeedback.feedbackCorrespondenceListPositives);
		
		RunMapEpisodeTest  map = new RunMapEpisodeTest(); 
		
		map.runEpisode(i);
		
		
		
		
		
		
		
	}
	
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
            rs = stmt.executeQuery("SELECT max(id) FROM "+mappingObject.getTableName()+";");                        
            while (rs.next()) {
                endId = rs.getInt(1);
            }
            mappingObject.setSizeOfResults(endId-startId);
            //System.out.println("Size of results="+(endId-startId)+" -"+startId+" "+endId);
            connection.commit();
            
        }
        catch(ClassNotFoundException | SQLException e)
        {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            e.printStackTrace();
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
    
    public void emptyResultExchangeTable()
    {
        DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
        Connection connection = this.createDBConnection(accessInfo.getUri(), accessInfo.getUserName(), accessInfo.getPassword());
        
        Statement stmt;
           try{
                ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesTakenOnBoard;               
                
                Class.forName("org.postgresql.Driver");
                stmt = connection.createStatement();
                for(int i=0; i<tablesNames.size(); i++)
                {
                	stmt.executeUpdate("DELETE FROM "+tablesNames.get(i)+";");              
                }
                connection.commit();
                stmt.close();
                connection.close();
            }
            catch(ClassNotFoundException | SQLException e)
            {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                e.printStackTrace();
                //System.exit(0);
            }
    }
    
    public void runTGDs(MappingObject mappingObject)
    {
    	Connection connection = null;
    	Statement stmt = null;
    	
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
            	
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                e.printStackTrace();            
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
            	int lastId = rs.getInt(1);
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
