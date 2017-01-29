package uk.ac.man.jaguar.controller.feedback.operators;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.DatabaseAccessInfo;
import uk.ac.man.jaguar.util.DatabaseUtils;

/**
 * This class has to do:
 * 1.- Identify automatically the ground truth of a mapping (or consult it from 
 * somewhere else).
 * 2.- Generate synthetic feedback automatically for a mapping.
 * @author osornogf
 */


public class SyntheticFeedbackGeneratorMap {
    public String getFeedbackForMap(MappingResultsSet mapFeedbackSet)
    {
        String result=null;
        //System.out.println("Provenance="+mapFeedbackSet.getProvenance());
        double groundTruth = getGroundTruth(mapFeedbackSet.getProvenance());
        result = getFeedbackValue(groundTruth, mapFeedbackSet.getProvenance());
        return result;
    }
    
    
    
    /**
     * This method will obtain the correct ground truth feedback, checking for the real
     * value in the ground truth table.
     * 
     * @param record
     * @return
     */
    public String getFeedbackForMap(Record record)
    {
        String feedback="fp";
        DatabaseAccessInfo accessInfo = DatabaseUtils.getDatabaseAccessInfo("Results");
        Connection connection = DatabaseUtils.createDBConnection(accessInfo.getUri(), accessInfo.getUserName(), accessInfo.getPassword());                                
        try
        {
            try (Statement stmt = connection.createStatement()) {
                ConfigurationReader configurationReader = new ConfigurationReader();
                ArrayList<String> tablesNames = configurationReader.getStringsFromFile("GlobalSchemaTables.dat");
                
                
                
                ArrayList<String> attributesNames = record.getAttributesNames();
                
                
                ArrayList<String> attributesValues = record.getAttributesValues();
                
                String attributes="";
                for(int j=0; j<attributesNames.size(); j++)
                {
                    attributes+=attributesNames.get(j)+", ";
                }
                attributes = attributes.substring(0,attributes.length()-2);
                attributes = "id, " + attributes;
                
                String where=" WHERE ";
                for(int k=0; k<attributesNames.size(); k++)
                {
                    if(k<attributesNames.size()-1)
                        where += attributesNames.get(k) + "='" + attributesValues.get(k) + "' AND ";
                    else
                        where += attributesNames.get(k) + " ='" + attributesValues.get(k) +"';";
                }
                
                String sql="SELECT "+attributes+" FROM "+tablesNames.get(0) + "_gt " + where ;
                //System.out.println(sql);
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    String attributesDB = "";
                    while ( rs.next() )
                    {
                        int id = rs.getInt("id");
                        for(int r=0; r<attributesNames.size(); r++)
                        {
                            attributesDB += " " + rs.getString((String)attributesNames.get(r));
                        }
                        feedback="tp";
                    }
                    //System.out.println(attributesDB);
                }
            }
            connection.close();
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle.getMessage());
            sqle.printStackTrace();
            //System.exit(0);
        }
        return feedback;
    }
    String getFeedbackValue(double groundTruth, int provenance)
    {
        String feedbackValue="fp";
        double random =  Math.random();
        if(random < groundTruth)
            feedbackValue = "tp";
        return feedbackValue;
    }
    /**
     * This method will retrieve a fixed synthetic ground truth
     * of mappings' precision.
     *  
     * @param provenance
     * @return
     */
    public double getGroundTruth(int provenance)
    {
        double result = 0.0d;
        if(provenance==0)
            result = 0.0;
        else
        if(provenance==1)
            result = 0.1;        
        else
        if(provenance==2)
            result = 0.2;        
        else
        if(provenance==3)
            result = 0.3;            
        else
        if(provenance==4)
            result = 0.4;        
        else
        if(provenance==5)
            result = 0.5;        
        else
        if(provenance==6)
            result = 0.6;        
        else
        if(provenance==7)
            result = 0.7;        
        else
        if(provenance==8)
            result = 0.8;        
        else
        if(provenance==9)
            result = 0.9;        
        else
            result = 1.0;
        return result;
    }
}
