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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.model.MappingObject;

/**
 *
 * @author osornogf
 */
public class DatabaseUtils {
    
    public static Connection createDBConnection(DatabaseAccessInfo databaseAccessInfo)
    {
        return DatabaseUtils.createDBConnection(databaseAccessInfo.getUri(), 
                databaseAccessInfo.getUserName(), databaseAccessInfo.getPassword());
        
    }    
    public static Connection createDBConnection(String uri, String user, String password)
    {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
 
			e.printStackTrace();
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(uri,user,password);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
                return connection;
	}
    
    public static DatabaseAccessInfo getDatabaseAccessInfo(String databaseName)
    {
            ConfigurationReader configurationReader = new ConfigurationReader();
            @SuppressWarnings("unchecked")
			List<Element> databaseInfo = (List<Element>)configurationReader.getDatabaseInfo(databaseName);
            Element node = (Element) databaseInfo.get(0);
            String uri = node.getChildText("uri");
            String username = node.getChildText("username");
            String password = node.getChildText("password"); 
            DatabaseAccessInfo databaseAccessInfo = new DatabaseAccessInfo(uri, username, password);
            return databaseAccessInfo;
    }
    
    public void emptyGSTables()
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
}
