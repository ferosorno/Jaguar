/*#################################
* class:  Main.java
* author: Fernando Osorno-Gutierrez
* date:   9 Sep 2014
* #################################
**********************************/

package test.ac.man.jaguar.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.util.ConfigurationReader;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	ConfigurationReader configurationReader = new ConfigurationReader();    	
    	
    	configurationReader.loadEnvironmentConfiguration(0);
    	
		MapManipulation mp = new MapManipulation();
		Main main = new Main();
		String s = main.getSql();
		ArrayList<MappingObject> mo = mp.createMappingObjects(s,null);
		
		System.out.println("Maps num="+mo.size());
		MappingObject test = mo.get(0);
		
		System.out.println(test.getResultOfExchangeAt(0));
		System.out.println("tableName="+test.getTableName());
		System.out.println("Tgd="+test.getTgds().size());
		for(int x=0; x<test.getTgds().size(); x++)
		{
			System.out.println(test.getTgds().get(x));
		}
		
		
	}
	
public String getSql()
{
	String everything="";
	 try(BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        everything = sb.toString();
	    }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return everything;
}
}
