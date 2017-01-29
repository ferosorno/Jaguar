/*#################################
* class:  MapSelectionGreedyTest.java
* author: Fernando Osorno-Gutierrez
* date:   5 May 2015
* #################################
**********************************/

package test.ac.man.jaguar.controller.artefacts.map;

import java.util.ArrayList;

import uk.ac.man.jaguar.controller.artefacts.map.MapSelectionGreedy;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;

public class MapSelectionGreedyTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MapSelectionGreedy testClass = new MapSelectionGreedy();
		
		double k = 0.03;
		Integration i = new Integration();
		ArrayList<MappingObject> IM = new ArrayList<>();
		ArrayList<MappingResultsSet> mappingResultsList = new ArrayList<>();
		ArrayList<MappingObject> result =  testClass.selectMappings( i, IM,mappingResultsList,  k);
		
		
		//Integration i, ArrayList<MappingObject> IM, ArrayList<MappingResultsSet> mappingResultsList, double k)
		
		
		
		
	}

}
