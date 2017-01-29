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

package uk.ac.man.jaguar.controller.feedback.match;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.paths.PathExpression;

import java.util.ArrayList;

import uk.ac.man.jaguar.controller.feedback.AbstractFeedback;
import uk.ac.man.jaguar.util.MatchUtil;

/* MatchFeedback.java (UTF-8)
 *
 * 05-Feb-2014
 * @author Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
 */
public class MatchFeedback extends AbstractFeedback
{
    
    //Aqui voy a guardar el Feedback1 Match
    //public static MatchFeedback matchFeedback = new MatchFeedback();
    
    //The negatives and positives on which we have obtained
    //feedback. 
    //This is to keep count of the feedback obtained so far.
    public int feedbackAmountCollectedPositives;
    public int lastFeedbackCollectedPositives;
    public int feedbackCollectedNegatives;
    public int lastFeedbackCollectedNegatives;
    
    public int feedbackCorrespondenceListPositivesIndex=0;
    
    
    
    
    //These are the initial positives and negatives found.
    public int positivesFound;
    public int negativesFound;    
    
    //These are going to store the initial correspondences and 
    //I will obtain feedback on them, they are STATIC
    //TP+FP
    public ArrayList<ValueCorrespondence> feedbackCorrespondenceListPositives = new ArrayList();
    public boolean positivesIdentified=false;
    //FN + TN
    public ArrayList<ValueCorrespondence> feedbackCorrespondenceListNegatives = new ArrayList();
    public boolean negativesIdentified=false;
    
    public ArrayList<ValueCorrespondence> feedbackCorrespondenceListFeedbackObtained = new ArrayList();
    
    /// And I will try to make these ones DYNAMIC 
    /// and I will use them to replace the list of the integration mapping task.
    //relevant and retrieved
    public ArrayList<ValueCorrespondence> truePositives = new ArrayList();
    public int truePositivesCount;
    //not relevand and retrieved
    public ArrayList<ValueCorrespondence> falsePositives = new ArrayList();
    public int falsePositivesCount;
    //Relevant but not retrieved
    public ArrayList<ValueCorrespondence> falseNegatives = new ArrayList();
    public int falseNegativesCount;
    //not relevant not retrieved
    public ArrayList<ValueCorrespondence> trueNegatives = new ArrayList();
    public int trueNegativesCount;
    
    public ArrayList<ValueCorrespondence> fromArray = null;
    public ArrayList<ValueCorrespondence> toArray = null;

    
    public void reset()
    {
        //matchFeedback = new MatchFeedback();    
        feedbackAmountCollectedPositives = 0;
        lastFeedbackCollectedPositives = 0;
        feedbackCollectedNegatives = 0;
        lastFeedbackCollectedNegatives = 0;        
        positivesFound = 0;
        negativesFound = 0;      	
        
        feedbackCorrespondenceListPositives = new ArrayList();
        positivesIdentified=false;

        feedbackCorrespondenceListNegatives = new ArrayList();
        negativesIdentified=false;
        
        feedbackCorrespondenceListFeedbackObtained = new ArrayList();
        
        ArrayList<ValueCorrespondence> truePositives = new ArrayList();
        truePositivesCount = 0;

        falsePositives = new ArrayList();
        falsePositivesCount = 0;
        falseNegatives = new ArrayList();
        falseNegativesCount = 0;
        trueNegatives = new ArrayList();
        trueNegativesCount = 0;
        
        fromArray = null;
        toArray = null;   
        
        
        feedbackCorrespondenceListPositivesIndex=0;
    }
    
    public void moveFrom(String from, String to, ValueCorrespondence valueCorrespondence, MatchFeedback matchFeedback)
    {
        MatchUtil matchUtil = new MatchUtil();
        if(from.compareTo("tp")==0)
            fromArray=matchFeedback.truePositives;
        if(from.compareTo("fp")==0)
            fromArray=matchFeedback.falsePositives;        
        if(from.compareTo("tn")==0)
            fromArray=matchFeedback.trueNegatives;        
        if(from.compareTo("fn")==0)
            fromArray=matchFeedback.falseNegatives;        
        if(from.compareTo("p")==0)
            fromArray=matchFeedback.feedbackCorrespondenceListPositives;        
        if(from.compareTo("n")==0)
            fromArray=matchFeedback.feedbackCorrespondenceListNegatives;        
        
        if(to.compareTo("tp")==0)
            toArray=matchFeedback.truePositives;
        if(to.compareTo("fp")==0)
            toArray=matchFeedback.falsePositives;        
        if(to.compareTo("tn")==0)
            toArray=matchFeedback.trueNegatives;        
        if(to.compareTo("fn")==0)
            toArray=matchFeedback.falseNegatives;        
        if(to.compareTo("p")==0)
            toArray=matchFeedback.feedbackCorrespondenceListPositives;        
        if(to.compareTo("n")==0)
            toArray=matchFeedback.feedbackCorrespondenceListNegatives;             

        boolean found=false;
        //search for the value correspondence in the true positives
        for(int k=0; k< fromArray.size(); k++)
        {
            ValueCorrespondence valueCorrespondenceK = fromArray.get(k);
            if(matchUtil.compareValueCorrespondences(valueCorrespondence,valueCorrespondenceK))
            {
                found=true;
                ValueCorrespondence newValueCorrespondence = new ValueCorrespondence(valueCorrespondenceK);
                newValueCorrespondence.setConfidence(valueCorrespondence.getConfidence());
                toArray.add(newValueCorrespondence);
                
                fromArray.remove(k);
                
                //break
                k=fromArray.size();
            }

        }
        if(found==false)
            System.out.println("Didnt find the match"+valueCorrespondence.toString());
    }
    
    public void copyFrom(String from, String to, ValueCorrespondence valueCorrespondence,MatchFeedback matchFeedback)
    {
        MatchUtil matchUtil = new MatchUtil();
        if(from.compareTo("tp")==0)
            fromArray=matchFeedback.truePositives;
        if(from.compareTo("fp")==0)
            fromArray=matchFeedback.falsePositives;        
        if(from.compareTo("tn")==0)
            fromArray=matchFeedback.trueNegatives;        
        if(from.compareTo("fn")==0)
            fromArray=matchFeedback.falseNegatives;        
        if(from.compareTo("p")==0)
            fromArray=matchFeedback.feedbackCorrespondenceListPositives;        
        if(from.compareTo("n")==0)
            fromArray=matchFeedback.feedbackCorrespondenceListNegatives;        
        

        if(to.compareTo("tp")==0)
            toArray=matchFeedback.truePositives;
        if(to.compareTo("fp")==0)
            toArray=matchFeedback.falsePositives;        
        if(to.compareTo("tn")==0)
            toArray=matchFeedback.trueNegatives;        
        if(to.compareTo("fn")==0)
            toArray=matchFeedback.falseNegatives;        
        if(to.compareTo("p")==0)
            toArray=matchFeedback.feedbackCorrespondenceListPositives;        
        if(to.compareTo("n")==0)
            toArray=matchFeedback.feedbackCorrespondenceListNegatives;             

        //search for the value correspondence in the true positives
        for(int k=0; k< fromArray.size(); k++)
        {
            ValueCorrespondence valueCorrespondenceK=fromArray.get(k);
            if(matchUtil.compareValueCorrespondences(valueCorrespondence,valueCorrespondenceK))
            {
                ValueCorrespondence newValueCorrespondence = new ValueCorrespondence(valueCorrespondenceK);
                newValueCorrespondence.setConfidence(valueCorrespondence.getConfidence());
                toArray.add(newValueCorrespondence);
                
                //break
                k=fromArray.size();
            }
        }
    }    
    
    public void updateConfidenceIn(String from,ValueCorrespondence valueCorrespondence, double confidence,MatchFeedback matchFeedback)
    {
        MatchUtil matchUtil = new MatchUtil();
        if(from.compareTo("tp")==0)
            fromArray=matchFeedback.truePositives;
        if(from.compareTo("fp")==0)
            fromArray=matchFeedback.falsePositives;        
        if(from.compareTo("tn")==0)
            fromArray=matchFeedback.trueNegatives;        
        if(from.compareTo("fn")==0)
            fromArray=matchFeedback.falseNegatives;        
        if(from.compareTo("p")==0)
            fromArray=matchFeedback.feedbackCorrespondenceListPositives;        
        if(from.compareTo("n")==0)
            fromArray=matchFeedback.feedbackCorrespondenceListNegatives;       
        //search for the value correspondence in the true positives
        for(int k=0; k< fromArray.size(); k++)
        {
            ValueCorrespondence valueCorrespondenceK=fromArray.get(k);
            if(matchUtil.compareValueCorrespondences(valueCorrespondence,valueCorrespondenceK))
            {
                
                valueCorrespondenceK.setConfidence(confidence);
                fromArray.set(k, valueCorrespondenceK);
                //break
                k=fromArray.size();
            }
        }        
    }
    
    

    
    
 
}