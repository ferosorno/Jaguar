/*#################################
* class:  FeedbackPlansGenerator.java
* author: Fernando Osorno-Gutierrez
* date:   12 Apr 2014
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.FeedbackPlanApplication;
import uk.ac.man.jaguar.model.Episode;
import uk.ac.man.jaguar.model.FeedbackPlan;

public class FeedbackPlansGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		FeedbackPlansGenerator feedbackPlansGenerator = new FeedbackPlansGenerator();	
	
		feedbackPlansGenerator.generateRandomFeedbackPlans2(1, 400);
		
	}
	
	public ArrayList<FeedbackPlan> generateRandomFeedbackPlans(int numberOfPlans, int budget)
	{
		ArrayList<FeedbackPlan> feedbackPlans = new ArrayList<>();
		
		Random rand = new Random();
		Random rand2 = new Random();
		
		int numberOfEpisodes=0;
		
		int low = 1; //inclusive
		
		int high =11;//exclusive 
		int actualNumberOfEpisodes=0;
		double prob;
		FeedbackPlan feedbackPlan;
		double d;	
		int amount=0;
		int actualAmount=0;
		double a;
		int type;
		int prevType;
		int j0;
		int numAttempts=0;
		Episode e;
		
		for(int k=0; k<numberOfPlans; k++)
		{
			numberOfEpisodes = rand.nextInt(high-low) + low;
			prob = (double)(100-numberOfEpisodes)/100;
			
			boolean hasMappingFeedback = false;
			boolean hasSmallBudgetEpisode = false;
			//System.out.println("numberOfPlans="+numberOfPlans);
			//System.out.println("numberOfEpisodes="+numberOfEpisodes);
			//System.out.println("prob="+prob); 
			
			do
			{
				actualNumberOfEpisodes = 0;
				actualAmount=0;
				j0=0;			
				prevType=-1;
				feedbackPlan = new FeedbackPlan();
				hasMappingFeedback = false;
				//System.out.println("Number of episodes="+numberOfEpisodes);
				
				for(int j=1; j<=100; j++)
				{
					d = rand2.nextDouble();
					if(d>prob)
					{
						rand = new Random();
						do
						{
							type = rand.nextInt(3);
						}
						while(type==prevType);
						prevType = type;
						a = (((double)(j-j0)/100)*budget);
						amount = (int)Math.round(a);
						//System.out.println("j="+j);
						//System.out.println("j0="+j0);
						//System.out.println("amount="+amount);
						actualAmount += amount;
						
						if(type==1)
							hasMappingFeedback=true;
						if(amount<2)
							hasSmallBudgetEpisode=true;
						
						e = new Episode(type,amount);
						
						feedbackPlan.addEpisode(e);
						
						actualNumberOfEpisodes++;
						
						//System.out.println("Episode["+type+","+amount+"]");
						//System.out.println("amount="+amount+" q="+j+" q0="+j0);
						
						j0 = j;
					}					
				}
				//System.out.println();
				
				numAttempts++;
				if(numAttempts>1000000)
				{
					System.out.println("Failed to create the feedback plan. Attempts exceeded.");
					System.exit(0);
				}
				//System.gc();
			}while(actualNumberOfEpisodes!=numberOfEpisodes || actualAmount!=budget || hasMappingFeedback==false ||	hasSmallBudgetEpisode==true);//
			//}while(actualNumberOfEpisodes==1 || actualAmount!=budget);
			String randomPlan="";
			for(int j=0;j<feedbackPlan.getFeedbackPlanList().size();j++)
			{
				System.out.print("["+feedbackPlan.getFeedbackPlanList().get(j).type+","+feedbackPlan.getFeedbackPlanList().get(j).amount+"]");
				randomPlan += "["+feedbackPlan.getFeedbackPlanList().get(j).type+","+feedbackPlan.getFeedbackPlanList().get(j).amount+"]";
				if(j<feedbackPlan.getFeedbackPlanList().size()-1)
				{
					System.out.print(" ");//Inter-episode blank space
					randomPlan +=" ";
				}
			}
			LogWriter logWriter = new LogWriter();
			logWriter.writeToFile("randomplans.txt",budget+"|"+randomPlan);
			
			System.out.println();
			feedbackPlans.add(feedbackPlan);
			
			//System.out.println("actualAmount="+actualAmount+" budget="+budget);
		    //System.out.println("actualNumberOfEpisodes="+actualNumberOfEpisodes+" numberOfEpisodes="+numberOfEpisodes);		
		}
		
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		//System.out.println("\tGenerate Random Feedback Plan. Elapsed Time = "+elapsedTime);
        	
        }
		
		return feedbackPlans;
	}
	
	public ArrayList<FeedbackPlan> generateRandomFeedbackPlans2(int numberOfPlans, int budget)
	{
		ArrayList<FeedbackPlan> feedbackPlans = new ArrayList<>();
		
		Random rand = new Random();
		Random rand2 = new Random();
		Random rand3 = new Random();
		int numberOfEpisodes=0;
		
		int low = 1; //inclusive
		int high = 50;//exclusive
		int actualNumberOfEpisodes=0;
		double prob;
		FeedbackPlan feedbackPlan;
		double d;
		int amount=0;
		int actualAmount=0;
		double a;
		int type;
		int prevType;
		int j0;
		int numAttempts=0;
		Episode e;
		
		/** limits types  **/
		int limit0=90;
		int limit1=200;
		int limit2=150;
		
		for(int k=0; k<numberOfPlans; k++)
		{
			numberOfEpisodes = rand.nextInt(high-low) + low;
			prob = (double)(100-numberOfEpisodes)/100;
			
			
			//System.out.println("numberOfPlans="+numberOfPlans);
			//System.out.println("numberOfEpisodes="+numberOfEpisodes);
			//System.out.println("prob="+prob); 
			int numType0=0;
			int numType1=0;
			int numType2=0;
			
			do
			{
				actualNumberOfEpisodes = 0;
				actualAmount=0;
				j0=0;			
				prevType=-1;
				feedbackPlan = new FeedbackPlan();
				numType0=0;
				numType1=0;
				numType2=0;
				//System.out.println("Number of episodes="+numberOfEpisodes);
				
				for(int j=1; j<=100; j++)
				{
					d = rand2.nextDouble();
					
					if(d>prob)	
					{
						rand = new Random();
						do
						{
							type = rand.nextInt(3);
						}
						while(type==prevType);
						
						prevType = type;
						 
						
						
						a = (((double)(j-j0)/100)*budget);
						amount = (int)Math.round(a);
						
						
						actualAmount += amount;
						
						
						if(type==0)
							numType0++;
						
						if(type==1)
							numType1++;
						
						if(type==2)
							numType2++;
						
						
						e = new Episode(type,amount);
						
						feedbackPlan.addEpisode(e);
						
						actualNumberOfEpisodes++;

						
						j0 = j;
					}					
				}
				//System.out.println();
				
				numAttempts++;
				if(numAttempts>1000000)
				{
					System.out.println("Failed to create the feedback plan. Attempts exceeded.");
					System.exit(0);
				}
				
			}while(actualNumberOfEpisodes!=numberOfEpisodes || actualAmount!=budget);
			//}while(actualNumberOfEpisodes==1 || actualAmount!=budget);
			
			
			boolean amountNotDistributedInTypes = true;
			
			
			int countTypes=0;
			if(numType0>0)
				countTypes++;
			if(numType1>0)
				countTypes++;
			if(numType2>0)
				countTypes++;
			
			
			
			
			int amTypes[] = new int[100];//uper limit infinity
			int typesIndex=0;
			double prob2 = (double)(100-countTypes)/100;
			//Distribute amounts to the types.
			boolean limitsOk=true;
			do
			{
				j0=0;//budget consumed
				actualAmount=0;
				typesIndex=0;
				System.out.println("countTypes="+countTypes);
				
				for(int j=1; j<=100; j++)// j = 1-100
				{
					d = rand3.nextDouble();
					
					if(d>prob2)
					{
						rand = new Random();
						a = (((double)(j-j0)/100)*budget);
						amount = (int)Math.round(a);
						actualAmount += amount;
						System.out.println("amount="+amount);
						amTypes[typesIndex] = amount;
						typesIndex++;
						j0 = j;
						
					}					
				}
				
				limitsOk=true;
				if(countTypes==1)
				{
					if(amTypes[0]>limit0)
						limitsOk=false;						
				}
				if(countTypes==2)
				{
					if(amTypes[0]>limit0)
						limitsOk=false;		
					if(amTypes[1]>limit1)
						limitsOk=false;						
				}				
				if(countTypes==3)
				{
					if(amTypes[0]>limit0)
						limitsOk=false;		
					if(amTypes[1]>limit1)
						limitsOk=false;		
					if(amTypes[2]>limit2)
						limitsOk=false;						
				}
				
				if((typesIndex-2)==countTypes)
					amountNotDistributedInTypes=false;
			}
			while(typesIndex!=countTypes || actualAmount!=budget || limitsOk==false);  
			
			//Now I have to distribute the amounts in each type.
			
			int[] amountsOneType = null;
			
			for(int v=0; v<countTypes; v++)
			{
				int currentTypeNum=0;
				if(v==0)
					currentTypeNum=numType0;
				if(v==1)
					currentTypeNum=numType1;
				if(v==2)
					currentTypeNum=numType2;
				
				double prob3 = (double)(100-currentTypeNum)/100;
				//Distribute amounts.
				
				int actualNumEpisodes=0;
				
				amountsOneType = null;
				amountsOneType = new int[100];
				
				do
				{
					j0=0;//budget consumed
					actualAmount=0;
					
					actualNumEpisodes=0;
					
					System.out.println("Type="+v);
					for(int j=1; j<=100; j++)// j = 1-100
					{
						d = rand3.nextDouble();
						
						if(d>prob3)
						{
							rand = new Random();
							
							a = (((double)(j-j0)/100)*amTypes[v]);
							
							amount = (int)Math.round(a);
							
							actualAmount += amount;
							
							System.out.println("amount one type="+amount);
							amountsOneType[actualNumEpisodes] = amount;
							
							actualNumEpisodes++;
							
							j0 = j;
						}
					}
				}
				while(actualNumEpisodes!=currentTypeNum || actualAmount!=amTypes[v]);
				
				int fpIndex=0;
				int findEpisodOfType=0;
				while( fpIndex < feedbackPlan.size())
				{
					if(feedbackPlan.get(fpIndex).getType()==v)
					{
						(feedbackPlan.get(fpIndex)).setAmount(amountsOneType[findEpisodOfType]);
						findEpisodOfType++;
					}
					fpIndex++;
				}
			}
			
			
			for(int j=0;j<feedbackPlan.getFeedbackPlanList().size();j++)
			{
				System.out.println("["+feedbackPlan.getFeedbackPlanList().get(j).type+","+feedbackPlan.getFeedbackPlanList().get(j).amount+"]");
			}
			feedbackPlans.add(feedbackPlan);
		}
		

		
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("\tGenerate Random Feedback Plan. Elapsed Time = "+elapsedTime);
        }
		
		return feedbackPlans;
	}
}
