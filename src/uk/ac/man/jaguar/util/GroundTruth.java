/*#################################
* class:  GroundTruth.java
* author: Fernando Osorno-Gutierrez
* date:   24 Jun 2014
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.model.Record;

public class GroundTruth {

	public ArrayList<Record> groundTruth;
	
	public ArrayList<Record> getGroundTruth() {
		return groundTruth;
	}

	public void setGroundTruth(ArrayList<Record> groundTruth) {
		this.groundTruth = groundTruth;
	}

	public GroundTruth()
	{
		groundTruth = new ArrayList<>();
		readGroundTruth();
	}
	
	public boolean isInGroundTruth(Record recordA)
	{
		boolean result = false;
		MappingUtil mUtil = new MappingUtil();
		for(int i=0; i<groundTruth.size(); i++)
		{
			Record recordB = groundTruth.get(i);
			int r = mUtil.compareRecords(recordA, recordB);
			if(r==0)
			{
				result = true;
				break;
			}
			
		}
		return result;
	}
	
	public void readGroundTruth()
	{
		ArrayList<ArrayList<String>> rows = new ArrayList<>();
		
		try {

			DatabaseAccessInfo accessInfo = DatabaseUtils
					.getDatabaseAccessInfo("Results");
			Connection connection = DatabaseUtils.createDBConnection(
					accessInfo.getUri(), accessInfo.getUserName(),
					accessInfo.getPassword());
			
			Class.forName("org.postgresql.Driver");
			connection.setAutoCommit(false);
			Statement stmt2 = connection.createStatement();
			ConfigurationReader configurationReader = new ConfigurationReader();
			
			ArrayList<String> tablesNames = JaguarVariables.globalSchemaTablesComplete;
			
			for(int i=0; i<tablesNames.size(); i++)
			{
				ArrayList<String> attributesNames = (ArrayList<String>) JaguarVariables.globalSchemaTablesAttribsTakenOnBoard.get(tablesNames.get(i)); 
				
	            String attributes = "";
	            
	            attributes = attributesNames.get(0);
	            
	            for(int j=1; j<attributesNames.size(); j++)
	            {
	                attributes+= (", " + attributesNames.get(j));
	            }
	            
	            attributes = "id, " + attributes;
	            
	            String sql2 = "SELECT "+attributes+" FROM "+tablesNames.get(i) + "_gt;"  ;		
	            
	            ResultSet rs = stmt2.executeQuery(sql2);
				
				while (rs.next()) {
	                int id = rs.getInt("id");
	                ArrayList<String> attributeValuesNoProvenance = new ArrayList<>();
	                
	                for(int r=0; r<attributesNames.size(); r++)
	                {
	                    String attribute = rs.getString((String)attributesNames.get(r));
	                    if(attribute!=null)
	                        attributeValuesNoProvenance.add(attribute.trim());
	                    else
	                        attributeValuesNoProvenance.add("");
	                }
	                
	                Record record = new Record();
	                record.setRecordId(""+id);
	                record.setAttributesValues(attributeValuesNoProvenance);
	                groundTruth.add(record);
				}
			}

			
			JaguarVariables.groundTruthSize = groundTruth.size();
			
			
			
			
			
			
			
			
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			//System.exit(0);
		}		
	}
	
	
	
	
}
