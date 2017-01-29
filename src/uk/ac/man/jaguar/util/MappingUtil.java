/*#################################
* class:  MappingUtil.java
* author: Fernando Osorno-Gutierrez
* date:   12 May 2014
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.model.Record;

public class MappingUtil {
	
	public int getEntityNumber(String tableName)
	{
		int result=-1;
		for(int i=0; i<JaguarVariables.globalSchemaTablesComplete.size(); i++)
		{
			String table = JaguarVariables.globalSchemaTablesComplete.get(i);
			if(table.compareTo(tableName)==0)
			{
				result = i;
			}
		}
		return result;
	}
    /**
     * Method used in the Greedy algorithm and in the Propagation of Map-Feedback to other records.
     * Compare based on:
     * - attributes' values and
     * - GS table number.
     * 
     * @param recordA
     * @param recordB
     * @return -1 if recordA != recordB, 0 if recordA == recordB 
     */
	public int compareRecords(Record recordA, Record recordB)
	{
		ArrayList<String> attributesValuesA = recordA.getAttributesValues();
		ArrayList<String> attributesValuesB = recordB.getAttributesValues();
		
		if(attributesValuesA.size() != attributesValuesB.size())
			return -1;
		
		int numberOfAttributes = attributesValuesA.size();
		int equalAttributes = 0;
		if(recordA.getEntityNumber() == recordB.getEntityNumber())
		{
				for(int a=0; a<numberOfAttributes; a++)
				{
					String attributeValueA = attributesValuesA.get(a);
					String attributeValueB = attributesValuesB.get(a);	
					if(attributeValueA.compareTo(attributeValueB)==0)
					{
						equalAttributes++;
					}
				}
		}
		else
			return -1;
		
		if(equalAttributes == numberOfAttributes)
			return 0;		
		else
			return -1;
	}
	
	
	
	/**
	 * 
	 * @param recordA
	 * @param recordB
	 * @return
	 */
	public int compareRecords2(Record recordA, Record recordB)
	{
		int result = -2;
		boolean equalAttributesValues = true;
		
		ArrayList<String> attributesValuesA = recordA.getAttributesValues();
		
		ArrayList<String> attributesValuesB = recordB.getAttributesValues();
		
		int numberOfAttributes = attributesValuesA.size();
		
		int equalAttributes = 0;
		
		if(recordA.getEntityNumber()==recordB.getEntityNumber())
		{

			if(attributesValuesA.size()!= attributesValuesB.size())
			{
				System.out.println("They are different size:");
				System.out.println(attributesValuesA.toString());
				System.out.println(attributesValuesB.toString());				
			}
			else	
			{
				for(int a=0; a<numberOfAttributes; a++)
				{
					String attributeValueA = attributesValuesA.get(a);
					String attributeValueB = attributesValuesB.get(a);	
					
					if(attributeValueA.compareTo(attributeValueB)==0)
					{
						equalAttributes++;
					}
				}					
			}
		}
		
		if(equalAttributes!=numberOfAttributes)
			equalAttributesValues = false;		
		
		if(equalAttributesValues==true)
			result = 0;
		
		return result;
	}
	
	public String getGroundTruthValue(ArrayList<String> names, ArrayList<String> values, String entity)
	{
		boolean isInGroundTruth=false;
		String result="fp";
		DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");		
		Connection connection = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try {
			Class.forName("org.postgresql.Driver");

			connection = DriverManager.getConnection(accessInfo.getUri(),accessInfo.getUserName(),accessInfo.getPassword());
			
			connection.setAutoCommit(false);
			stmt2 = connection.createStatement();
			
			ArrayList<String> attributesNames = names;
			
            String attributes = attributesNames.get(0);
            
            for(int j=1; j<attributesNames.size(); j++)
            {
                attributes += ", " + attributesNames.get(j);
            }
            
            attributes = "id, " + attributes;
            
            String where=" WHERE ";
            
            for(int q=0; q<attributesNames.size(); q++)
            {
                if(q<attributesNames.size()-1)
                    where += attributesNames.get(q) + "='" + values.get(q) + "' AND ";
                else
                    where += attributesNames.get(q) + " = '" + values.get(q) +"';";
            }
            
            String sql2="SELECT " + attributes + " FROM " + entity + "_gt " + where;
            
			//System.out.println("GT sql2="+sql2);
			
            rs2 = stmt2.executeQuery(sql2);
			
			while (rs2.next()) {
				isInGroundTruth=true;
				//System.out.println("IS IN GROUND TRUTH!!");
			}
			
			connection.commit();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			//System.exit(0);
		}
		finally {
			    if (rs2 != null) {
			        try {
			            rs2.close();
			        } catch (SQLException e) { /* ignored */}
			    }
			    if (stmt2 != null) {
			        try {
			            stmt2.close();
			        } catch (SQLException e) { /* ignored */}
			    }
			    if (connection != null) {
			        try {
			            connection.close();
			        } catch (SQLException e) { /* ignored */}
			    }
			}
		
		
		
		if(isInGroundTruth)
			result="tp";
		
		return result;
	}
	public int getTableNumber(String tableName)
	{
		for(int i=0;i<JaguarVariables.globalSchemaTablesComplete.size();i++)
		{
			if(JaguarVariables.globalSchemaTablesComplete.get(i).compareTo(tableName)==0)
				return i;
		}
		return -1;
	}
}
