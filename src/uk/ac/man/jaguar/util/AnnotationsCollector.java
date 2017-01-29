
package uk.ac.man.jaguar.util;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.model.FeedbackPlan;
import uk.ac.man.jaguar.util.latex.LatexManager;

/* AnnotationsCollector.java (UTF-8)
 *
 * 04-Mar-2014
 * @author Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
 */
public class AnnotationsCollector {

    public double matchPrecision[];
    public int matchTP[];
    public int matchFP[]; 
    public int matchTN[];
    
    public double mapFeedbackPrecision[];
    public double mapFeedbackRecall[];
    public double mapFeedbackFMeasure[];
    
    public double mapGroundTruthPrecision[];
    public int mapTP[];
    public int mapFP[];
    public int mapTN[];
    public int mapFN[];
    public int duplicatedRecords[];
    public int participatingFeedbackMappings[];
    public int participatingGroundTruthMappings[];
    public int fbPropToMatch[];
    
    public double erPrecision[];
    public int erTP[];
    public int erFP[];
    public int fbPropToMap[];
    public int fbUniqueRecPropToMap[];
    
    int annotationsCounter;
    
    
    
    
    public double averagePrecision[];
    
    public AnnotationsCollector(int annotationsNumber)
    {
        matchPrecision = new double[annotationsNumber];
        matchTP = new int[annotationsNumber];
        matchFP = new int[annotationsNumber];
        matchTN = new int[annotationsNumber];
        
        mapFeedbackPrecision = new double[annotationsNumber];
        mapFeedbackRecall = new double[annotationsNumber];
        mapFeedbackFMeasure = new double[annotationsNumber];
        
        mapGroundTruthPrecision = new double[annotationsNumber];
        participatingFeedbackMappings = new int[annotationsNumber];   
        participatingGroundTruthMappings = new int[annotationsNumber];
        mapTP = new int[annotationsNumber];
        mapFP = new int[annotationsNumber];
        mapTN = new int[annotationsNumber];
        mapFN = new int[annotationsNumber];
        duplicatedRecords = new int[annotationsNumber];
        fbPropToMatch = new int[annotationsNumber];
        
        erPrecision = new double[annotationsNumber];
        erTP = new int[annotationsNumber];
        erFP = new int[annotationsNumber];
        fbPropToMap = new int[annotationsNumber];
        fbUniqueRecPropToMap = new int[annotationsNumber];
        annotationsCounter=1;
        
        averagePrecision = new double[annotationsNumber];
    }
    
    
    public void collectAnnotations(
    		//match
    		int tpMatch, int fpMatch, int tnMatch,
    		//map
    		int tpMap,int fpMap,int tnMap,int fnMap,int duplicatedRecords,int participatingFeedbackMappings,int participatingGroundTruthMappings,double mapGroundTruthPrecision,int fbPropToMatch,
    		//er
    		int tpER, int fpER,int fbPropToMap,int fbUniqueRecPropToMap
    		)
    {
    	//match
        matchTP[annotationsCounter]=tpMatch;
        matchFP[annotationsCounter]=fpMatch;
        matchTN[annotationsCounter]=tnMatch;
        if(tpMatch+fpMatch!=0)
            matchPrecision[annotationsCounter]=(double)tpMatch/(tpMatch+fpMatch);
        else
            matchPrecision[annotationsCounter]=0;
        //map
        mapTP[annotationsCounter]=tpMap;
        mapFP[annotationsCounter]=fpMap;
        mapTN[annotationsCounter]=tnMap;
        mapFN[annotationsCounter]=fnMap;
        this.duplicatedRecords[annotationsCounter]=duplicatedRecords;
        
        this.mapGroundTruthPrecision[annotationsCounter] = mapGroundTruthPrecision;
        this.participatingFeedbackMappings[annotationsCounter] = participatingFeedbackMappings;
        this.participatingGroundTruthMappings[annotationsCounter] = participatingGroundTruthMappings;            
        if(tpMap+fpMap+duplicatedRecords>0)
            mapFeedbackPrecision[annotationsCounter]=(double)tpMap/(tpMap+fpMap+duplicatedRecords);
        else
            mapFeedbackPrecision[annotationsCounter]=0;        
        this.fbPropToMatch[annotationsCounter]=fbPropToMatch;        
        if(tpMap+fnMap>0)        
        	mapFeedbackRecall[annotationsCounter] = (double)tpMap/(tpMap+fnMap+duplicatedRecords);
        else
        	mapFeedbackRecall[annotationsCounter] = 0;        
        if(mapFeedbackPrecision[annotationsCounter]+mapFeedbackRecall[annotationsCounter]>0)
        	mapFeedbackFMeasure[annotationsCounter] = (double)2*(
        			(mapFeedbackPrecision[annotationsCounter]*mapFeedbackRecall[annotationsCounter])
        			/(mapFeedbackPrecision[annotationsCounter]+mapFeedbackRecall[annotationsCounter])
        			);     
        else
        	mapFeedbackFMeasure[annotationsCounter] = 0;       
        
        //er
        erTP[annotationsCounter]=tpER;
        erFP[annotationsCounter]=fpER;
        if(tpER+fpER>0)
            erPrecision[annotationsCounter]=(double)tpER/(tpER+fpER);
        else
            erPrecision[annotationsCounter]=0;
        this.fbPropToMap[annotationsCounter] = fbPropToMap;
        this.fbUniqueRecPropToMap[annotationsCounter] = fbUniqueRecPropToMap;
        
        //
        
        annotationsCounter++;
    }
    
    // For Mode = every 3
    public void updateMatchAnnotations(int tp, int fp, int tn)
    {
    	System.out.println("in AnnotationsCollector.updateMatchAnnotations");
        matchTP[annotationsCounter]=tp;
        matchFP[annotationsCounter]=fp;
        matchTN[annotationsCounter]=tn;
        if(tp+fp!=0)
            matchPrecision[annotationsCounter]=(double)tp/(tp+fp);
        else
            matchPrecision[annotationsCounter]=0;
        
        mapTP[annotationsCounter] = mapTP[annotationsCounter-1];
        mapFP[annotationsCounter] = mapFP[annotationsCounter-1];
        mapTN[annotationsCounter] = mapTN[annotationsCounter-1];
        mapFN[annotationsCounter] = mapFN[annotationsCounter-1];
        this.duplicatedRecords[annotationsCounter] = this.duplicatedRecords[annotationsCounter-1];
        
        mapFeedbackPrecision[annotationsCounter] = mapFeedbackPrecision[annotationsCounter-1];        
        mapGroundTruthPrecision[annotationsCounter] = mapGroundTruthPrecision[annotationsCounter-1];
        mapFeedbackRecall[annotationsCounter] = mapFeedbackRecall[annotationsCounter-1];
        mapFeedbackFMeasure[annotationsCounter] = mapFeedbackFMeasure[annotationsCounter-1];       
        
        participatingFeedbackMappings[annotationsCounter] = participatingFeedbackMappings[annotationsCounter-1];
        participatingGroundTruthMappings[annotationsCounter] = participatingGroundTruthMappings[annotationsCounter-1];   
        fbPropToMatch[annotationsCounter] = fbPropToMatch[annotationsCounter-1];
        
        erTP[annotationsCounter] = erTP[annotationsCounter-1];
        erFP[annotationsCounter] = erFP[annotationsCounter-1];
        erPrecision[annotationsCounter] = erPrecision[annotationsCounter-1];        
        fbPropToMap[annotationsCounter] = fbPropToMap[annotationsCounter-1];
        fbUniqueRecPropToMap[annotationsCounter] = fbUniqueRecPropToMap[annotationsCounter-1];
        
        annotationsCounter++;
    }
    
    // for mode = every 3
    public void updateMapAnnotations(
    		int tp, 
    		int fp, 
    		int tn,
    		int fn,
    		int duplicatedRecords,
    		int participatingFeedbackMappings, 
    		int participatingGroundTruthMappings,
    		double mapGroundTruthPrecision,
    		int fbPropToMatch)
    {
    	System.out.println("in AnnotationsCollector.updateMapAnnotations");
    	
    	this.mapTP[annotationsCounter]=tp;
        this.mapFP[annotationsCounter]=fp;
        this.mapTN[annotationsCounter]=tn;
        this.mapFN[annotationsCounter]=fn;
        this.duplicatedRecords[annotationsCounter]=duplicatedRecords;
        
        this.mapGroundTruthPrecision[annotationsCounter] = mapGroundTruthPrecision;
        this.participatingFeedbackMappings[annotationsCounter] = participatingFeedbackMappings;
        this.participatingGroundTruthMappings[annotationsCounter] = participatingGroundTruthMappings;            
        if(tp+fp>0)
            mapFeedbackPrecision[annotationsCounter]=(double)tp/(tp+fp+duplicatedRecords);
        else
            mapFeedbackPrecision[annotationsCounter]=0;        
        this.fbPropToMatch[annotationsCounter]=fbPropToMatch;        
        if(tp+fn>0)        
        	mapFeedbackRecall[annotationsCounter] = (double)tp/(tp+fn+duplicatedRecords);
        else
        	mapFeedbackRecall[annotationsCounter] = 0;        
        if(mapFeedbackPrecision[annotationsCounter]+mapFeedbackRecall[annotationsCounter]>0)
        	mapFeedbackFMeasure[annotationsCounter] = (double)2*(
        			(mapFeedbackPrecision[annotationsCounter]*mapFeedbackRecall[annotationsCounter])
        			/(mapFeedbackPrecision[annotationsCounter]+mapFeedbackRecall[annotationsCounter])
        			);     
        else
        	mapFeedbackFMeasure[annotationsCounter] = 0;
        
        matchTP[annotationsCounter] = matchTP[annotationsCounter-1];
        matchFP[annotationsCounter] = matchFP[annotationsCounter-1];
        matchTN[annotationsCounter] = matchTN[annotationsCounter-1];
        matchPrecision[annotationsCounter] = matchPrecision[annotationsCounter-1];
        
        erTP[annotationsCounter] = erTP[annotationsCounter-1];
        erFP[annotationsCounter] = erFP[annotationsCounter-1];
        erPrecision[annotationsCounter] = erPrecision[annotationsCounter-1];  
        fbPropToMap[annotationsCounter] = fbPropToMap[annotationsCounter-1]; 
        fbUniqueRecPropToMap[annotationsCounter] = fbUniqueRecPropToMap[annotationsCounter-1];        

        annotationsCounter++;
    }
    // for mode = every 3
    public void updateERAnnotations(int tp, int fp,int fbPropToMap,int fbUniqueRecPropToMap)
    {
    	System.out.println("in AnnotationsCollector.updateERAnnotations");
    	
        erTP[annotationsCounter]=tp;
        erFP[annotationsCounter]=fp;
        if(tp+fp>0)
            erPrecision[annotationsCounter]=(double)tp/(tp+fp);
        else
            erPrecision[annotationsCounter]=0;
        this.fbPropToMap[annotationsCounter] = fbPropToMap;
        this.fbUniqueRecPropToMap[annotationsCounter] = fbUniqueRecPropToMap;
        
        matchTP[annotationsCounter] = matchTP[annotationsCounter-1];
        matchFP[annotationsCounter] = matchFP[annotationsCounter-1];
        matchTN[annotationsCounter] = matchTN[annotationsCounter-1];
        matchPrecision[annotationsCounter] = matchPrecision[annotationsCounter-1];
        
        mapTP[annotationsCounter] = mapTP[annotationsCounter-1];
        mapFP[annotationsCounter] = mapFP[annotationsCounter-1];
        mapTN[annotationsCounter] = mapTN[annotationsCounter-1];
        mapFN[annotationsCounter] = mapFN[annotationsCounter-1];
        this.duplicatedRecords[annotationsCounter] = this.duplicatedRecords[annotationsCounter-1];
        
        mapFeedbackPrecision[annotationsCounter] = mapFeedbackPrecision[annotationsCounter-1];
        mapFeedbackRecall[annotationsCounter] = mapFeedbackRecall[annotationsCounter-1];
        mapFeedbackFMeasure[annotationsCounter] = mapFeedbackFMeasure[annotationsCounter-1];
        mapGroundTruthPrecision[annotationsCounter] = mapGroundTruthPrecision[annotationsCounter-1];
        participatingFeedbackMappings[annotationsCounter] = participatingFeedbackMappings[annotationsCounter-1];
        participatingGroundTruthMappings[annotationsCounter] = participatingGroundTruthMappings[annotationsCounter-1];
        fbPropToMatch[annotationsCounter]=fbPropToMatch[annotationsCounter-1];
        
        annotationsCounter++;
    }
    
    public void sendToLatexTables(FeedbackPlan feedbackPlan)
    {
        LatexManager latexResultsPrinter = new LatexManager();     
        //LONG TABLE
        latexResultsPrinter.printFrameHeader("Long Results");
    	latexResultsPrinter.printLatexTable(
    			feedbackPlan,
    			matchPrecision,
    			matchTP,
    			matchFP,
    			matchTN,
    			mapTP,
    			mapFP,
    			mapTN,
    			mapFN,
    			duplicatedRecords,
    			participatingFeedbackMappings,
    			mapFeedbackPrecision,
    		    mapFeedbackRecall,
    		    mapFeedbackFMeasure,
    			participatingGroundTruthMappings,
    			mapGroundTruthPrecision,
    			fbPropToMatch,
    			erTP,
    			erFP,
    			erPrecision,
    			fbPropToMap,
    			fbUniqueRecPropToMap    			
    			);      
        latexResultsPrinter.printFrameFooter();
        latexResultsPrinter.addCustomText("\n");
        
        //SHORT TABLE
        latexResultsPrinter.printFrameHeader("Short Results");
    	latexResultsPrinter.printShortLatexTable(
    			feedbackPlan,
    			matchPrecision,
    			mapFeedbackPrecision,
    			erPrecision
    			);     
        latexResultsPrinter.printFrameFooter();
        
        latexResultsPrinter.printLatex2IO();
        latexResultsPrinter.printLatex2File();
    }
    
    public void sendToLatexTablesSingleSpace(FeedbackPlan feedbackPlan)
    {
        LatexManager latexResultsPrinter = new LatexManager();
        //LONG TABLE
        latexResultsPrinter.printFrameHeader("Long Results");
    	latexResultsPrinter.makeLatexTableSingleSpace(
    			feedbackPlan,
    			matchPrecision,
    			matchTP,
    			matchFP,
    			matchTN,
    			mapTP,
    			mapFP,
    			mapTN,
    			mapFN,
    			duplicatedRecords,
    			participatingFeedbackMappings,
    			mapFeedbackPrecision,
    		    mapFeedbackRecall,
    		    mapFeedbackFMeasure,
    			participatingGroundTruthMappings,
    			mapGroundTruthPrecision,
    			fbPropToMatch,
    			erTP,
    			erFP,
    			erPrecision,
    			fbPropToMap,
    			fbUniqueRecPropToMap
    			);
        latexResultsPrinter.printFrameFooter();
        latexResultsPrinter.addCustomText("\n");
        
        //SHORT TABLE
        latexResultsPrinter.printFrameHeader("Short Results");
    	latexResultsPrinter.makeShortLatexTableSingleSpace(
    			feedbackPlan,
    			matchPrecision,
    			mapFeedbackPrecision,
    			erPrecision
    			);
        latexResultsPrinter.printFrameFooter();
        
        latexResultsPrinter.printLatex2IO();
        latexResultsPrinter.printLatex2File();
        
    }    
    @SuppressWarnings("unused")
	public void sendAnnotationsToTxtFile(FeedbackPlan feedbackPlan)
    {
    	ResultsFilesManager resultsFilesManager = new ResultsFilesManager();
    	
    	//Pipes file
    	//if(JaguarConstants.PREC_REC_FMEAS_PIPESFILE == true)
    	resultsFilesManager.writePrecisionRecallFMeasureToPipesFile(
    			JaguarConstants.QUALITY_RESULTS_FILENAME,
    			mapFeedbackPrecision,
    			mapFeedbackRecall, 
    			mapFeedbackFMeasure,
    			feedbackPlan,
    			annotationsCounter);
    }
    
}
