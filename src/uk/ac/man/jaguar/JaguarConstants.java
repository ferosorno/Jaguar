/*#################################
* class:  CrowdFeedbackControllerConstants.java
* author: Fernando Osorno-Gutierrez
* date:   4 Sep 2014
* #################################
**********************************/

package uk.ac.man.jaguar;

public class JaguarConstants {
	
	public static final boolean SYSTEMEXIT = false;
	
	/******** INPUT/OUTPUT *******/
	
	public static final boolean ELAPSEDTIMEOUT = true;
	public static final boolean SYSTEMOUT = false;
	public static final boolean FILEOUTOUT = false;
	public static final boolean LOGGING = false;
	
	/// Mode 1 = every 3 
	public static final boolean UPDATE_ANNOTATIONS_MODE_1 = false;
	// Mode 2 = every 1
	public static final boolean UPDATE_ANNOTATIONS_MODE_2 = !UPDATE_ANNOTATIONS_MODE_1;
	
	public static final String CONFIGURATION_PATH = "conf\\";
	public static final String FILEOUT_PATH = "out\\";
	public static final String APPLICATION_PROPERTIES_FILENAME = "application-standard.properties";
	public static final String ENVIRONMENT_PROPERTIES_FILENAME = "environment.properties";
	public static final String SOURCESLOCATION_FILE = "config.xml";
	
	/********MATCHING*******/
	
	public static final boolean CUSTOM_MATCHFEEDBACK_ORDER = true;
	
	/********MAPPINGS*******/
	
	public static final double K_GREEDY = 0.03;
	
	//to print to a file
	public static final boolean QUALITY_RESULTS = true;
	//Filename of the quality results
	public static final String QUALITY_RESULTS_FILENAME="qualityresults.dat";
	//Filename of the latex file
	public static final boolean LATEX_RESULTS = true;
	//Latex results file name
	public static final String LATEX_RESULTS_FILENAME="latexresults.dat";
	
	/*********ER*************/
	//similarity feedback threshold 
	public static final double K_ENTITYRESOLUTION = 0.5;//0.42;
	
}
