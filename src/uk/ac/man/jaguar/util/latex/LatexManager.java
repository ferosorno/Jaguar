/*#################################
* class:  latexResultsPrinter.java
* author: Fernando Osorno-Gutierrez
* date:   15 May 2014
* #################################
**********************************/

package uk.ac.man.jaguar.util.latex;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import uk.ac.man.jaguar.model.FeedbackPlan;

public class LatexManager {

	StringBuffer stringBuffer;
	public LatexManager()
	{
		stringBuffer = new StringBuffer();
	}
	
	public void printFrameHeader(String frameTitle)
	{
		stringBuffer.append("\\begin{frame}[t] \n");
		stringBuffer.append("\\frametitle{"+frameTitle+"} \n");		
	}
	
	public void printFrameFooter()
	{
		stringBuffer.append("\\end{frame} \n");
	}
	
	public void printTableHeader(int columns)
	{
		stringBuffer.append("\\begin{table}[h] \n");
		stringBuffer.append("\\tiny \n");
		stringBuffer.append("\\begin{tabular}{");	
		int k;
        for(k=0; k<2; k++)
        {
        	stringBuffer.append("|l");
        }		
        for(k=2; k<columns; k++)
        {
        	stringBuffer.append("|c");
        }
        stringBuffer.append("|} \n");
        stringBuffer.append("\\hline \n");
	}
	
	public void printTableFooter(String caption, String label)
	{
		stringBuffer.append("\\end{tabular} \n");
		if(caption!=null)
			if(caption.compareTo("")!=0)
				stringBuffer.append("\\caption["+caption.substring(0, caption.length()<10?caption.length():10)+"]{"+caption+"} \n");
		if(label!=null)
			if(label.compareTo("")!=0)		
					stringBuffer.append("\\label{tab:"+label+"} \n");
		stringBuffer.append("\\end{table} \n");
	}
	
	public void addCustomText(String text)
	{
		stringBuffer.append(text+" \n");
	}
	
	
	public void printLatex2IO()
	{
		System.out.println(stringBuffer.toString());
	}
	
	public void printLatex2File()
	{
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results.txt", true)));
		    out.println(stringBuffer.toString());
		    out.close();
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		}
	}	
	
	

	public void makeLatexTableSingleSpace(			
			FeedbackPlan feedbackPlan,
			//match
			double[] matchPrecision,
			int[] matchTP,
			int[] matchFP,
			int[] matchTN,
			int[] mapTP,
			int[] mapFP,
			int[] mapTN,
			int[] mapFN,
			int[] duplicatedRecords,
			//map
			int[] participatingFeedbackMappings,
			double[] mapFeedbackPrecision,
			double[] mapFeedbackRecall,
			double[] mapFeedbackFMeasure,
			int[] participatingGroundTruthMappings,
			double[] mapGroundTruthPrecision,
			//er
			int[] fbPropToMatch,
			int[] erTP,
			int[] erFP,
			double[] erPrecision,
			int[] fbPropToMap,
			int[] fbUniqueRecPropToMap
			)
	{
		int columns = feedbackPlan.getFeedbackPlanList().size()+4;
		
		printTableHeader(columns);
	
		int annotationsCounter = columns;

        stringBuffer.append("& &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(k);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        /////////////// STARTS MATCH \\\\\\\\\\\\\\\\
        stringBuffer.append("Match & TP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(matchTP[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& FP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(matchFP[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& TN &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(matchTN[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(matchPrecision[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        ///////////  ENDS MATCH \\\\\\\\\\\\\\		
	        
        /////////////// STARTS MAP \\\\\\\\\\\\\\\\
        stringBuffer.append("Map & TP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapTP[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& FP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapFP[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& DU &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(duplicatedRecords[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& TN &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapTN[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");   
        
        stringBuffer.append("& FN &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapFN[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");         
        
        stringBuffer.append("& Mappings Used &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(participatingFeedbackMappings[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");          

        stringBuffer.append("& Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackPrecision[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& Recall &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackRecall[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        
        stringBuffer.append("& FMeasure &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackFMeasure[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        
        
        stringBuffer.append("& Existing Mappings &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(participatingGroundTruthMappings[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");                       
        stringBuffer.append("& Fbk Prop To Match &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(fbPropToMatch[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");          
        ///////////  ENDS MAP \\\\\\\\\\\\\\        
        

        
        /////////////// STARTS ER  \\\\\\\\\\\\\\\\
        stringBuffer.append("ER & TP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(erTP[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& FP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(erFP[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        stringBuffer.append("& Precision &");
        for(int k=0; k<annotationsCounter; k++)
        { 
        	String s = (new Double(erPrecision[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");          
        stringBuffer.append("& Fbk Prop To Map &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(fbPropToMap[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");                   
        stringBuffer.append("& Fbk Prop To Map &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(fbUniqueRecPropToMap[k]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");          
        ///////////  ENDS ER  \\\\\\\\\\\\\\
        
        String caption ="Feedback Plan: \\{";
        for(int k=0;k<feedbackPlan.getFeedbackPlanList().size(); k++)
        {
        	String episode="<";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==0)
        		episode = episode + "Match";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==1)
        		episode = episode + "Map";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==2)
        		episode = episode + "ER";        	
        	episode = episode + "," + feedbackPlan.getFeedbackPlanList().get(k).getAmount();
        	episode = episode + ">";        	
        	caption = caption + episode;
        	if(k<feedbackPlan.getFeedbackPlanList().size()-1)
        		caption = caption + ",";
        }
        caption = caption + "\\}";
        
        
	    printTableFooter(caption, "table"+System.currentTimeMillis());    
		//System.out.println(stringBuffer);	
	}
	
	public void printLatexTable(
			
			FeedbackPlan feedbackPlan,
			double[] matchPrecision,
			int[] matchTP,
			int[] matchFP,
			int[] matchTN,
			int[] mapTP,
			int[] mapFP,
			int[] mapTN,
			int[] mapFN,
			int[] duplicatedRecords,
			int[] participatingFeedbackMappings,
			double[] mapFeedbackPrecision,
			double[] mapFeedbackRecall,
			double[] mapFeedbackFMeasure,
			int[] participatingGroundTruthMappings,
			double[] mapGroundTruthPrecision,
			int[] fbPropToMatch,
			int[] erTP,
			int[] erFP,
			double[] erPrecision,
			int[] fbPropToMap,
			int[] fbUniqueRecPropToMap
			)
	{
		int columns = feedbackPlan.getFeedbackPlanList().size()+4;
		
		printTableHeader(columns);
		int arrPos[] = new int[feedbackPlan.getFeedbackPlanList().size()+2];		
		arrPos[0] = 3;// Bootstrapping
		int i = 0;		
		for(i=0; i<feedbackPlan.getFeedbackPlanList().size(); i++){
			int addPos = 0;
			if(feedbackPlan.getFeedbackPlanList().get(i).getType()==0)
				addPos = 3;
			if(feedbackPlan.getFeedbackPlanList().get(i).getType()==1)
				addPos = 4;			
			if(feedbackPlan.getFeedbackPlanList().get(i).getType()==2)
				addPos = 5;									
			arrPos[i+1]=arrPos[i] + addPos;
		}
		arrPos[i+1]=arrPos[i] + 3;  //Position of the final Run Integration Workflow.		
		int annotationsCounter = arrPos.length;

        stringBuffer.append("& &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(k);            
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        /////////////// STARTS MATCH \\\\\\\\\\\\\\\\
        stringBuffer.append("Match & TP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(matchTP[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& FP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(matchFP[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& TN &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(matchTN[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(matchPrecision[arrPos[k]]).toString());
        	if(s.length()>4)
        		s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        ///////////  ENDS MATCH \\\\\\\\\\\\\\		
	        
        /////////////// STARTS MAP \\\\\\\\\\\\\\\\
        stringBuffer.append("Map & TP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapTP[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& FP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapFP[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& DU &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(duplicatedRecords[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& TN &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapTN[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");   
        
        stringBuffer.append("& FN &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(mapFN[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");         
        
        stringBuffer.append("& Mappings Used &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(participatingFeedbackMappings[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");          

        stringBuffer.append("& Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackPrecision[arrPos[k]]).toString());
        	//if(s.length()>4)
        	//	s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        
        stringBuffer.append("& Recall &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackRecall[arrPos[k]]).toString());
        	if(s.length()>4)
        		s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        
        stringBuffer.append("& FMeasure &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackFMeasure[arrPos[k]]).toString());
        	if(s.length()>4)
        		s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        
        
        stringBuffer.append("& Existing Mappings &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(participatingGroundTruthMappings[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");                       
//        stringBuffer.append("& Fbk Prop To Match &");
//        for(int k=0; k<annotationsCounter; k++)
//        {
//            stringBuffer.append(fbPropToMatch[arrPos[k]]);
//            if(k<annotationsCounter-1)
//            	stringBuffer.append("&");            
//        }
//        stringBuffer.append("\\\\ \\hline \n");          
        ///////////  ENDS MAP \\\\\\\\\\\\\\        
        

        
        /////////////// STARTS ER  \\\\\\\\\\\\\\\\
        stringBuffer.append("ER & TP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(erTP[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        stringBuffer.append("& FP &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(erFP[arrPos[k]]);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        stringBuffer.append("& Precision &");
        for(int k=0; k<annotationsCounter; k++)
        { 
        	String s = (new Double(erPrecision[arrPos[k]]).toString());
        	if(s.length()>4)
        		s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");          
//        stringBuffer.append("& Fbk Prop To Map &");
//        for(int k=0; k<annotationsCounter; k++)
//        {
//            stringBuffer.append(fbPropToMap[arrPos[k]]);
//            if(k<annotationsCounter-1)
//            	stringBuffer.append("&");            
//        }
//        stringBuffer.append("\\\\ \\hline \n");                   
//        stringBuffer.append("& Fbk Prop To Map &");
//        for(int k=0; k<annotationsCounter; k++)
//        {
//            stringBuffer.append(fbUniqueRecPropToMap[arrPos[k]]);
//            if(k<annotationsCounter-1)
//            	stringBuffer.append("&");            
//        }
//        stringBuffer.append("\\\\ \\hline \n");          
        ///////////  ENDS ER  \\\\\\\\\\\\\\
        
        String caption ="Feedback Plan: \\{";
        for(int k=0;k<feedbackPlan.getFeedbackPlanList().size(); k++)
        {
        	String episode="<";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==0)
        		episode = episode + "Match";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==1)
        		episode = episode + "Map";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==2)
        		episode = episode + "ER";        	
        	episode = episode + "," + feedbackPlan.getFeedbackPlanList().get(k).getAmount();
        	episode = episode + ">";        	
        	caption = caption + episode;
        	if(k<feedbackPlan.getFeedbackPlanList().size()-1)
        		caption = caption + ",";
        }
        caption = caption + "\\}";
        
        
	    printTableFooter(caption, "table"+System.currentTimeMillis());    
		//System.out.println(stringBuffer);	
	}
	
	public void printShortLatexTable(
			FeedbackPlan feedbackPlan,
			double[] matchPrecision,
			double[] mapFeedbackPrecision,
			double[] erPrecision
			)
	{
		int columns = feedbackPlan.getFeedbackPlanList().size()+3;
		
		printTableHeader(columns);
		int arrPos[] = new int[feedbackPlan.getFeedbackPlanList().size()+2];
		
		arrPos[0] = 3;// Bootstrapping
		int i = 0;		
		for(i=0; i<feedbackPlan.getFeedbackPlanList().size(); i++){
			int addPos = 0;
			if(feedbackPlan.getFeedbackPlanList().get(i).getType()==0)
				addPos = 3;
			if(feedbackPlan.getFeedbackPlanList().get(i).getType()==1)
				addPos = 4;			
			if(feedbackPlan.getFeedbackPlanList().get(i).getType()==2)
				addPos = 5;									
			arrPos[i+1]=arrPos[i] + addPos;
		}
		arrPos[i+1]=arrPos[i] + 3;  //Position of the final Run Integration Workflow.		
		int annotationsCounter = arrPos.length;

		
        stringBuffer.append(" &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(k);            
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        /////////////// STARTS MATCH \\\\\\\\\\\\\\\\
        stringBuffer.append("Match Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(matchPrecision[arrPos[k]]).toString());
        	if(s.length()>4)
        		s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        ///////////  ENDS MATCH \\\\\\\\\\\\\\		
	        
        /////////////// STARTS MAP \\\\\\\\\\\\\\\\
        stringBuffer.append("Map Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackPrecision[arrPos[k]]).toString());
        	if(s.length()>4)
        		s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        ///////////  ENDS MAP \\\\\\\\\\\\\\
        
        /////////////// STARTS ER  \\\\\\\\\\\\\\\\    
        stringBuffer.append("ER Precision &");
        for(int k=0; k<annotationsCounter; k++)
        { 
        	String s = (new Double(erPrecision[arrPos[k]]).toString());
        	if(s.length()>4)
        		s = s.substring(0,4);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        ///////////  ENDS ER  \\\\\\\\\\\\\\
        
        String caption ="Feedback Plan: \\{";
        for(int k=0;k<feedbackPlan.getFeedbackPlanList().size(); k++)
        {
        	String episode="<";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==0)
        		episode = episode + "Match";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==1)
        		episode = episode + "Map";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==2)
        		episode = episode + "ER";
        	episode = episode + "," + feedbackPlan.getFeedbackPlanList().get(k).getAmount();
        	episode = episode + ">";
        	caption = caption + episode;
        	if(k<feedbackPlan.getFeedbackPlanList().size()-1)
        		caption = caption + ",";
        }
        caption = caption + "\\}";        
	    printTableFooter(caption, "table"+System.currentTimeMillis());    
		//System.out.println(stringBuffer);	
	}
	

	public void makeShortLatexTableSingleSpace(
			FeedbackPlan feedbackPlan,
			double[] matchPrecision,
			double[] mapFeedbackPrecision,
			double[] erPrecision
			)
	{
		int columns = feedbackPlan.getFeedbackPlanList().size()+3;
		
		printTableHeader(columns);
				
		int annotationsCounter = columns;

		
        stringBuffer.append(" &");
        for(int k=0; k<annotationsCounter; k++)
        {
            stringBuffer.append(k);            
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        /////////////// STARTS MATCH \\\\\\\\\\\\\\\\
        stringBuffer.append("Match Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(matchPrecision[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");        
        ///////////  ENDS MATCH \\\\\\\\\\\\\\		
	        
        /////////////// STARTS MAP \\\\\\\\\\\\\\\\
        stringBuffer.append("Map Precision &");
        for(int k=0; k<annotationsCounter; k++)
        {
        	String s = (new Double(mapFeedbackPrecision[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        ///////////  ENDS MAP \\\\\\\\\\\\\\
        
        /////////////// STARTS ER  \\\\\\\\\\\\\\\\    
        stringBuffer.append("ER Precision &");
        for(int k=0; k<annotationsCounter; k++)
        { 
        	String s = (new Double(erPrecision[k]).toString());
        	if(s.length()>6)
        		s = s.substring(0,6);
            stringBuffer.append(s);
            if(k<annotationsCounter-1)
            	stringBuffer.append("&");            
        }
        stringBuffer.append("\\\\ \\hline \n");
        ///////////  ENDS ER  \\\\\\\\\\\\\\
        
        String caption ="Feedback Plan: \\{";
        for(int k=0;k<feedbackPlan.getFeedbackPlanList().size(); k++)
        {
        	String episode="<";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==0)
        		episode = episode + "Match";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==1)
        		episode = episode + "Map";
        	if(feedbackPlan.getFeedbackPlanList().get(k).getType()==2)
        		episode = episode + "ER";
        	episode = episode + "," + feedbackPlan.getFeedbackPlanList().get(k).getAmount();
        	episode = episode + ">";
        	caption = caption + episode;
        	if(k<feedbackPlan.getFeedbackPlanList().size()-1)
        		caption = caption + ",";
        }
        caption = caption + "\\}";        
	    printTableFooter(caption, "table"+System.currentTimeMillis());    
		//System.out.println(stringBuffer);	
	}
}
