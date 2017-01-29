
package uk.ac.man.jaguar.util;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.paths.PathExpression;

import java.util.ArrayList;

import uk.ac.man.jaguar.controller.feedback.match.MatchFeedback;
import uk.ac.man.jaguar.model.Integration;

/* MatchUtil.java (UTF-8)
 *
 * 17-Feb-2014
 * @author Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
 */
public class MatchUtil {
  public void resetMatches(Integration i)
    {
        i.mappingTask.clearCandidateCorrespondences();
        i.mappingTask.clearCorrespondences();

        i.matchFeedback.lastFeedbackCollectedNegatives=0;
        i.matchFeedback.lastFeedbackCollectedPositives=0;
        
        i.matchFeedback.truePositives.clear();
        i.matchFeedback.truePositives = null;
        i.matchFeedback.truePositives = new ArrayList<>();
        
        i.matchFeedback.falseNegatives.clear();
        i.matchFeedback.falseNegatives = null;
        i.matchFeedback.falseNegatives = new ArrayList<>();
        
        i.matchFeedback.falsePositives.clear();
        i.matchFeedback.falsePositives = null;
        i.matchFeedback.falsePositives = new ArrayList<>();
        
        i.matchFeedback.trueNegatives.clear();
        i.matchFeedback.trueNegatives = null;
        i.matchFeedback.trueNegatives = new ArrayList();
        
        
        
        i.matchFeedback.truePositivesCount=0;
        i.matchFeedback.falseNegativesCount=0;
        i.matchFeedback.falsePositivesCount=0;
        i.matchFeedback.trueNegativesCount=0;
        
        
    }

  
    public boolean isInMatchGroundTruth(ArrayList<String> matchGroundTruth,String sourceTable, String sourceAttribute, String targetTable, String targetAttribute, int value)
    {
        boolean result=false;
        String match=sourceTable+"."+sourceAttribute+","+targetTable+"."+targetAttribute+","+value;
        
        for(int k=0;k<matchGroundTruth.size();k++)
        {
        	
            if(match.compareTo(matchGroundTruth.get(k))==0)
            {
                result=true;
                k = matchGroundTruth.size();
            }
        }        
        return result;
    }
    /**
     * We compare two ArrayList<ValueCorrespondence>, regardless of the confidences.
     * We ask if all the correspondences in list1 exist in list2.
     * @param list1
     * @param list2
     * @return 
     */
    public boolean correspondencesListsCompare(ArrayList<ValueCorrespondence> list1, ArrayList<ValueCorrespondence> list2)
    {
        boolean result = true;
        for(int x=0; x<list1.size(); x++)
        {
            ValueCorrespondence valueCorrespondence1 = list1.get(x);
            boolean correspondenceExists = false;
            for(int y=0; y<list2.size(); y++)
            {
                ValueCorrespondence valueCorrespondence2 = list2.get(y);
                
                if(compareValueCorrespondences(valueCorrespondence1,valueCorrespondence2))
                {
                    correspondenceExists = true;
                }
            }
            if(!correspondenceExists)
            {
                result = false;
            }
        }
        
        return result;
    }

   public boolean compareValueCorrespondences(ValueCorrespondence valueCorrespondence1, ValueCorrespondence valueCorrespondence2)
    {
        boolean result = false;
        String sourceAttribute1 = valueCorrespondence1.getSourcePaths().get(0).getLastStep();
        String targeAttribute1 = valueCorrespondence1.getTargetPath().getLastStep();
        
        //obtain the source table name
        ArrayList<PathExpression> sourcePaths1 =(ArrayList) valueCorrespondence1.getSourcePaths();
        PathExpression sourcePathExpression1=sourcePaths1.get(0);//because I have only 1 source
        ArrayList<String> sourcePathSteps1 = (ArrayList)sourcePathExpression1.getPathSteps();
        String sourceTableName1 = sourcePathSteps1.get(1);        
        
        //obtain the target table name
        PathExpression targetPathExpression1 = valueCorrespondence1.getTargetPath();            
        ArrayList<String> targetPathSteps1 = (ArrayList) targetPathExpression1.getPathSteps();
        String targetTableName1 = targetPathSteps1.get(1);
        
        String sourceAttribute2 = valueCorrespondence2.getSourcePaths().get(0).getLastStep();
        String targetAttribute2 = valueCorrespondence2.getTargetPath().getLastStep();
        
        //obtain the source table name
        ArrayList<PathExpression> sourcePaths2 =(ArrayList) valueCorrespondence2.getSourcePaths();
        PathExpression sourcePathExpression2=sourcePaths2.get(0);//because I have only 1 source
        ArrayList<String> sourcePathSteps2 = (ArrayList)sourcePathExpression2.getPathSteps();
        String sourceTableName2 = sourcePathSteps2.get(1);        
        
        //obtain the target table name
        PathExpression targetPathExpression2 = valueCorrespondence2.getTargetPath();            
        ArrayList<String> targetPathSteps2 = (ArrayList) targetPathExpression2.getPathSteps();
        String targetTableName2 = targetPathSteps2.get(1);        
        
        if(sourceAttribute1.compareTo(sourceAttribute2)==0 &&
                targeAttribute1.compareTo(targetAttribute2)==0 &&
                sourceTableName1.compareTo(sourceTableName2)==0 &&
                targetTableName1.compareTo(targetTableName2)==0)
        {
            result = true;
        }
        return result;
    }    
}
