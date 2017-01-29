/*#################################
* class:  CrowdFeedbackControllerVariables.java
* author: Fernando Osorno-Gutierrez
* date:   4 Sep 2014
* #################################
**********************************/

package uk.ac.man.jaguar;

import java.util.ArrayList;
import java.util.Hashtable;
/**
 * The variables in this class will change for different Environments.
 * 
 * @author osornogf
 *
 */
public class JaguarVariables {
	
public static long initialTime;
public static boolean matchHasRan=false;
	
public static ArrayList<String> environments;
public static int currentEnv;
public static int groundTruthSize;
public static double matchThreshold;

public static ArrayList<String> globalSchemaTablesComplete;
public static ArrayList<String> globalSchemaTablesTakenOnBoard;
public static Hashtable<String,ArrayList<String>> globalSchemaTablesAttribsComplete;
public static Hashtable<String,ArrayList<String>> globalSchemaTablesAttribsTakenOnBoard;



/*****UTILS****/

public static int count=0;

public static int countMapAnnotations=0;
public static int countMapFbckPropagated=0;
/*****RUNNING PARAMETERS****/
public static int budgetAmounts = 1;
public static int feedbackPlansNumber = 1;
}
