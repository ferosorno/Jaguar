package uk.ac.man.jaguar.controller.feedback;

/**
 * Abstract class to represent feedback.
 * 
 * @author Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
 */


public abstract class AbstractFeedback {
    public int feedbackType;

    public int getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(int feedbackType) {
        this.feedbackType = feedbackType;
    }
    
    public int getFeedbackAmount() {
        return feedbackAmount;
    }

    public void setFeedbackAmount(int feedbackAmount) {
        this.feedbackAmount = feedbackAmount;
    }
    public int feedbackAmount;    
}
