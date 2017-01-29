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

package uk.ac.man.jaguar.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;

/**
 *
 * @author osornogf
 */
public class ConfigurationReader {
	
	public static String environment="";
	ArrayList<String> globalSchemaAttributes = null;
    public void ConfigurationReader(){
    	
    }
    
    
    
    public String getMappingTaskPath(){
        String path=null;
        SAXBuilder builder = new SAXBuilder();
	 File xmlFile = new File(
			 JaguarConstants.CONFIGURATION_PATH + "//" + 
			 JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +
			 JaguarConstants.SOURCESLOCATION_FILE
			 );
         try{
		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();
		List list = rootNode.getChildren("mappingtask");
                Element node = (Element) list.get(0);
                path= node.getChildText("path");
         }
         catch(IOException e)
         {
             e.printStackTrace();
         }
         catch(JDOMException e)
         {
             e.printStackTrace();
         }
        return path;
    }
    
    public List getDatabaseInfo(String databaseName)
    {
		List databaseInfo=null;
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(JaguarConstants.CONFIGURATION_PATH + "//" + 
				 JaguarVariables.environments.get(JaguarVariables.currentEnv) + "\\" +
				 JaguarConstants.SOURCESLOCATION_FILE);
		try{
		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();
		databaseInfo = rootNode.getChildren(databaseName);
         }
		 catch(IOException e)
		 {
		     e.printStackTrace();
		 }
		 catch(JDOMException e)
		 {
		     e.printStackTrace();
		 }
		return databaseInfo;
    }
    
    public ArrayList<String> getStringsFromFile(String fileRoute)
    {
        ArrayList<String> result = new ArrayList<>();
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(fileRoute));
            while ((sCurrentLine = br.readLine()) != null) {
                    result.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                    if (br != null)br.close();
            } catch (IOException ex) {
                    ex.printStackTrace();
            }
        }
        return result;
    }    
    
    public static double getMatchingConfidenceThreshold()
    {
        double threshold = 0.0d;
	BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader("src\\conf\\"+environment+"\\matchThreshold.dat"));
            while ((sCurrentLine = br.readLine()) != null) {
                    if(sCurrentLine.startsWith(""))
                    {
                        String thresholdStr = sCurrentLine.substring(sCurrentLine.indexOf('=')+1);
                        threshold = (new Double(thresholdStr)).doubleValue();
                    }
            }
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
            try {
                    if (br != null)br.close();
            } catch (IOException ex) {
                    ex.printStackTrace();
            }
        }
        return threshold;
    }

    public void loadEnvironmentConfiguration(int environment)
    {
    	JaguarVariables.currentEnv = environment;
    	
    	globalSchemaAttributes = null;
    	globalSchemaAttributes = new ArrayList<>();    	

    	globalSchemaAttributes = getStringsFromFile(JaguarConstants.CONFIGURATION_PATH + "\\" +
    			JaguarVariables.environments.get(environment) + "\\" +
    			"globalschema-attributes.dat");
    	JaguarVariables.globalSchemaTablesAttribsComplete = parseGlobalSchemaTablesAndAttributes(0, globalSchemaAttributes);    	
    	JaguarVariables.globalSchemaTablesAttribsTakenOnBoard = parseGlobalSchemaTablesAndAttributes(1, globalSchemaAttributes);    	
    	JaguarVariables.globalSchemaTablesComplete = parseGlobalSchemaTables(0, globalSchemaAttributes);
    	JaguarVariables.globalSchemaTablesTakenOnBoard = parseGlobalSchemaTables(1, globalSchemaAttributes);
    	
    	parseEnvironmentProperties(environment);
    }
    /**
     * This method will read the tables and attributes of the Global Schema and store them in System Variables.
     * @param tag
     * @param lines
     * @return
     */
    private Hashtable<String,ArrayList<String>> parseGlobalSchemaTablesAndAttributes(int tag, ArrayList<String> lines)
    {
    	Hashtable<String,ArrayList<String>> result = new Hashtable<>();
    	for(int i=0; i<lines.size(); i++)
    	{
    		String line = lines.get(i);
    		if(!line.startsWith("#"))
    		{
    			StringTokenizer st = new StringTokenizer(line,",");
    			String tableName = st.nextToken();
    			String tableTag = st.nextToken();
    			int tableBeTaken = (new Integer(tableTag)).intValue();
    			if(tableBeTaken >= tag)
    			{
	    			ArrayList<String> attributes = new ArrayList<>();
	    			while(st.hasMoreTokens())
	    			{
	    				String attribute = st.nextToken();
	    				String attributeTag = st.nextToken();
	    				int attributeBeTaken = (new Integer(attributeTag)).intValue();
	    				if(attributeBeTaken >= tag)
	    				{
		    				attributes.add(attribute);	    					
	    				}
	    			}
	    			result.put(tableName, attributes);
    			}
    			
    		}
    	}
    	return result;
    }
    
    private ArrayList<String> parseGlobalSchemaTables(int tag, ArrayList<String> lines)
    {
    	ArrayList<String> result = new ArrayList<>();
    	for(int i=0; i<lines.size(); i++)
    	{
    		String line = lines.get(i);
    		if(!line.startsWith("#"))
    		{
    			StringTokenizer st = new StringTokenizer(line,",");
    			String tableName = st.nextToken();
    			String tableTag = st.nextToken();
    			int tableBeTaken = (new Integer(tableTag)).intValue();
    			if(tableBeTaken >= tag)
    			{
	    			result.add(tableName);
    			}
    		}
    	}
    	return result;
    }    
    
    private void parseEnvironmentProperties(int environment)
    {
    	
    	ArrayList<String> lines = this.getStringsFromFile(
    			JaguarConstants.CONFIGURATION_PATH + "\\" +
    		    JaguarVariables.environments.get(environment) + "\\" +
    			JaguarConstants.ENVIRONMENT_PROPERTIES_FILENAME);
    	
    	for(int i=0; i<lines.size(); i++)
    	{
    		if(!lines.get(i).startsWith("#"))
    		{
    			
    			if(lines.get(i).startsWith("MATCH_THRESHOLD"))
    			{
    				int beginIndex = lines.get(i).indexOf('=') + 1;
    				int endIndex = lines.get(i).length();
    				JaguarVariables.matchThreshold = (new Double(lines.get(i).substring(beginIndex, endIndex))).doubleValue();
    			}
    			
    			
    		}
    	}
    }
    
    private void parseApplicationProperties()
    {
    	
    	ArrayList<String> lines = this.getStringsFromFile(
    			JaguarConstants.CONFIGURATION_PATH + "\\" +
    			JaguarConstants.APPLICATION_PROPERTIES_FILENAME);
    
    }
    
    
    public ArrayList<String> getEnvironmentsFromFile()
    {
    	
    	ArrayList<String> lines = this.getStringsFromFile(
    			JaguarConstants.CONFIGURATION_PATH + "\\" +
    			JaguarConstants.APPLICATION_PROPERTIES_FILENAME);
    
    	ArrayList<String> environments = new ArrayList<String>();
    	
    	for(int i=0; i<lines.size(); i++)
    	{
    		if(!lines.get(i).startsWith("#"))
    		{
    			
    			if(lines.get(i).startsWith("ENVIRONMENTS"))
    			{
    				int beginIndex = (lines.get(i)).indexOf('=') + 1;
    				int endIndex = (lines.get(i)).length();
    				String subString = (lines.get(i)).substring(beginIndex, endIndex);

    				StringTokenizer st = new StringTokenizer(subString,",");
    				while(st.hasMoreTokens())
    				{
    					environments.add((st.nextToken()).trim());    				
    				}
    				
    			}
    			
    		}
    	}
    	return environments;
    }
    
    
    
    
    
    
}
