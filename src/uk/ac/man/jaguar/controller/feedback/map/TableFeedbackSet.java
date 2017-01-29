/*#################################
* class:  TableFeedbackSet.java
* author: Fernando Osorno-Gutierrez
* date:   22 Sep 2014
* #################################
**********************************/

package uk.ac.man.jaguar.controller.feedback.map;

import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.model.Record;

public class TableFeedbackSet {
    
	
	public boolean hasRecords=false;
	
	public int relationsNumber;
	
	public String provenance;
    String tableName = null;
    
    public ArrayList<Record> records = null;
    public ArrayList<String> attributesNames = null;
    
	public TableFeedbackSet()
    {
        records = new ArrayList<>();
    }
	
	public ArrayList<Record> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public ArrayList<String> getAttributesNames() {
		return attributesNames;
	}

	public void setAttributesNames(ArrayList<String> attributesNames) {
		this.attributesNames = attributesNames;
	}

	public int getEntityNumber()
	{
		int result = -1;
		for(int i=0; i<JaguarVariables.globalSchemaTablesAttribsComplete.size();i++)
		{
			String table = JaguarVariables.globalSchemaTablesComplete.get(i);
			if(tableName.compareTo(table)==0)
				result = i;
		}
		return result;
	}
    
}
