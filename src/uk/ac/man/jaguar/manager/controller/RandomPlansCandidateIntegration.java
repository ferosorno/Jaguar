/*#################################
* class:  RandomPlansCandidateIntegration.java
* author: Fernando Osorno-Gutierrez
* date:   7 Jul 2014
* #################################
**********************************/

package uk.ac.man.jaguar.manager.controller;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import uk.ac.man.jaguar.model.Episode;
import uk.ac.man.jaguar.model.FeedbackPlan;
import uk.ac.man.jaguar.util.FeedbackPlansGenerator;
import ec.app.feedbackplanapplication.CandidateIntegration;
import ec.app.feedbackplanapplication.Environment;
import ec.util.InitializationTools;
import ec.vector.MyGene;

public class RandomPlansCandidateIntegration {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int environmentMatches = InitializationTools.ENVIRONMENTMATCHES;
		int environmentMappings = InitializationTools.ENVIRONMENTMAPPINGS;
		int environmentPairs = InitializationTools.ENVIRONMENTPAIRSOFRECORDS;
		double initialFitnessMatch = InitializationTools.localFitnessMatch;
		double initialFitnessMap = InitializationTools.localFitnessMap;
		double initialFitnessER = InitializationTools.localFitnessER;
		
		int resultsSize = InitializationTools.RESULTSSIZE;
		
		Environment environment = new Environment(environmentMatches,
				environmentMappings, environmentPairs,resultsSize, initialFitnessMatch, initialFitnessMap, initialFitnessER);
		
		FeedbackPlansGenerator feedbackPlansGenerator = new FeedbackPlansGenerator();
		ArrayList<FeedbackPlan> feedbackPlans = new ArrayList<>();
		
		
		RandomPlansCandidateIntegration randomPlansCandidateIntegration = new RandomPlansCandidateIntegration();
        //Save mapping to a file
        try{
            try (PrintWriter writer = new PrintWriter("RandomFeedbackPlansModel.dat", "UTF-8")) {
            	
				int setSize=100;
				int budget = 315;
				for(int x=0; x<5; x++)
				{
					//System.out.println("Starting set.");
					
					feedbackPlans = feedbackPlansGenerator.generateRandomFeedbackPlans(setSize, budget);
				
					for(int y=0; y<setSize; y++)
					{
						//System.out.println("Starting Feedback Plan.");
						MyGene myGenome[] = randomPlansCandidateIntegration.getGenome(  (FeedbackPlan)feedbackPlans.get(y)   );
						for(int z=0; z<myGenome.length; z++)
						{
							writer.print("["+myGenome[z].getFeedbackType()+","+myGenome[z].getFeedbackAmount()+"]");
							if(z<myGenome.length-1)
								writer.print(",");
						}
						CandidateIntegration candidateIntegration = new CandidateIntegration(environment);
						double fitness = randomPlansCandidateIntegration.evaluateFitness(myGenome, candidateIntegration, environment);
						if(fitness==0)
							fitness=0.49;
						writer.println("|"+fitness);
					}
					writer.println();
					
					budget += 315;
					
				}
		
            }
        }
        catch(FileNotFoundException | UnsupportedEncodingException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(0);
        }
        
		
		
	}
	public MyGene[] getGenome(FeedbackPlan feedbackPlan)
	{
		MyGene[] genome = new MyGene[feedbackPlan.size()];
			
		for(int x=0; x<feedbackPlan.size(); x++)
		{
			MyGene myGene = new MyGene();
			Episode episode = feedbackPlan.get(x);
			myGene.setFeedbackType(episode.getType());
			myGene.setFeedbackAmount(episode.getAmount());
			genome[x] = myGene;
		}
		
		
		
		return genome;
	}
	
	public double evaluateFitness(MyGene[] genome, CandidateIntegration candidateIntegration, Environment environment)
	{
		for (int i = 0; i < genome.length; i++) {
			candidateIntegration.updateFitness((MyGene) genome[i], environment);
		}
		return candidateIntegration.getFitness();		
	}
	
	
	
	public static void main2(String[] args)
	{
		int environmentMatches = InitializationTools.ENVIRONMENTMATCHES;
		int environmentMappings = InitializationTools.ENVIRONMENTMAPPINGS;
		int environmentPairs = InitializationTools.ENVIRONMENTPAIRSOFRECORDS;
		double initialFitnessMatch = InitializationTools.localFitnessMatch;
		double initialFitnessMap = InitializationTools.localFitnessMap;
		double initialFitnessER = InitializationTools.localFitnessER;
		
		int resultsSize = InitializationTools.RESULTSSIZE;
		
		Environment environment = new Environment(environmentMatches,
				environmentMappings, environmentPairs, resultsSize, initialFitnessMatch, initialFitnessMap, initialFitnessER);		
		CandidateIntegration candInt = new CandidateIntegration(environment);
		
		MyGene[] genome = new MyGene[2];
		
		genome[0] = new MyGene(1,100);
		genome[1] = new MyGene(0,100);
		
		for (int i = 0; i < genome.length; i++) {
			candInt.updateFitness((MyGene) genome[i], environment);
		}
		
		//System.out.println(candInt.getFitness());
	}


}
