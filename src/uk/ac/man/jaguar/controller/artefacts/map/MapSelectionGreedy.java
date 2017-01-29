/*#################################
* class:  MapSelectionGreedy.java
* author: Fernando Osorno-Gutierrez
* date:   10 May 2014
* #################################
**********************************/

package uk.ac.man.jaguar.controller.artefacts.map;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.controller.artefacts.MapManipulation;
import uk.ac.man.jaguar.controller.feedback.map.MapFeedback;
import uk.ac.man.jaguar.controller.feedback.map.MappingResultsSet;
import uk.ac.man.jaguar.model.DuplicatePair;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.MappingObject;
import uk.ac.man.jaguar.model.Record;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.MappingUtil;



public class MapSelectionGreedy {

	public int[] C;
	public int[] CO;
	public int[] I;
	public int[] P;
	public int[] S;
	public int[] D;//duplicates
	ArrayList<MappingObject> M = null;
	ArrayList<MappingObject> m = null;
	
	ArrayList<Record> result = null;
	MappingUtil mappingUtil = null;
	
	
	/**
	 * FEEDBACK VERSION
	 * This method implements the Greedy Algorithm.
	 * @param IM: Initial set of all the mappings.
	 * @param mappingResultsList: Variable with the feedback of the mappings' results.
	 * @param k: Proportion threshold.
	 * @return
	 */
	public ArrayList<MappingObject> selectMappings(
			Integration i, ArrayList<MappingObject> IM, ArrayList<MappingResultsSet> mappingResultsList, double k)
	{
		M = null;
		m = null;
		M = new ArrayList<>();	
		m = new ArrayList<MappingObject>();
		
		boolean isEmpty=false;
		double proportionRest = -1000;
		double proportionCorrect = 0;
		int totalRetrieved = 0;
		
		double q=-0.25;
		double kthreshold = 0.06;
		//System.out.println("Print of duplicates:");
		/*for(int j=0; j<i.erFeedback.duplicatePairList.size(); j++)
		{
			DuplicatePair duplicatePair =  i.erFeedback.duplicatePairList.get(j);
			Record record1 = duplicatePair.getRecordObject1();
			Record record2 = duplicatePair.getRecordObject2();
			
			System.out.println("Table="+record1.getEntityNumber()+" Record1="+record1.getAttributesValues().toString()+" Prov="+record1.getProvenance());
			System.out.println("Table="+record2.getEntityNumber()+" Record2="+record2.getAttributesValues().toString()+" Prov="+record2.getProvenance());
			System.out.println();
		}*/
		setUnselected(i);
		
		System.out.println("\t\tSelected mappings Greedy:");
		do{
			C = null;
			I = null;
			P = null;
			S = null;
			D = null;
			
			C = new int[IM.size()];			
			I = new int[IM.size()];
			P = new int[IM.size()];
			S = new int[IM.size()];
			D = new int[IM.size()];
			
			for(int p=0; p<IM.size(); p++)
			{
				C[p] = 0;
				I[p] = 0;
				P[p] = 0;
				S[p] = 0;
				D[p] = 0;
			}
			
			/*
			System.out.println("Mapping, Unretrieved TP, Unretrieved FP");
			System.out.println("P.size="+P.length);*/
			// m is the set of mappings considered so far in the cycle.
			ArrayList<Record> recordsmTP=null;
			ArrayList<Record> recordsMTP=null;
			ArrayList<Record> recordsmFP=null;
			ArrayList<Record> recordsMFP=null;
			
			m = new ArrayList<MappingObject>();
			
			recordsMTP = getCorrectTuplesFeedback(M,mappingResultsList);
			recordsMFP = getIncorrectTuplesFeedback(M,mappingResultsList);
			
			ArrayList<DuplicatePair> pair = i.erFeedback.duplicatePairList;
			
			// This for is used only to fill C, I and P.
			for(int j=0; j<IM.size(); j++)
			{
				m = null;
				m = new ArrayList<MappingObject>();
				m.add(IM.get(j));
				
				/*System.out.println("Adding mapping="+IM.get(j).getProvenance());
				System.out.println("m.size="+m.size());
				System.out.println("M.size="+M.size());
				System.out.println("getCorrectTuples m");*/
				/**
				 * getCorrectTuplesFeedback(m,mappingResultsList) should retrieve tuples that:
				 * - are annotated with feedback and
				 * - are correct and 
				 * - does not participate in any duplicate pair
				 */
				recordsmTP = getCorrectTuplesFeedback(m,mappingResultsList);
				
				//int correctTuplesOthers = tuplesSetDifference(recordsMTP, recordsmTP);
				//counts the tuples of m - M (i.e., the 'new' tuples that m retrieves)
				int correctTuples = tuplesSetDifference(recordsmTP, recordsMTP);/// recordsm = recordsM
				/**
				 * getIncorrectTuplesFeedback(m, mappingResultsList) should retrieve tuples that:
				 * - are annotated with feedback and
				 * - are incorrect or
				 * - participate in any duplicate pair
				 */
				recordsmFP = getIncorrectTuplesFeedback(m, mappingResultsList);// Incorrect Tuples
				
				int incorrectTuples = tuplesSetDifference(recordsmFP,recordsMFP);
				//System.out.println("mapping provenance="+m.get(0).getProvenance());
				/**
				 * getDuplicateRecordsCount should retrieve the duplicate pairs where m participates.
				 */
				int duplicateRecords = 0;//getDuplicateRecordsCount(i,m.get(0));
				for(int w=0; w<recordsmFP.size(); w++)
				{
					Record recw=recordsmFP.get(w);
					if(recw.isDuplicate())
						duplicateRecords++;
				}
				
				/*System.out.println("duplicates count="+duplicateRecords);
				if(m.get(0).getTableNumber()==7)
				{
					for(int x=0; x<pair.size(); x++)
					{
						int prov1 = pair.get(x).getRecordObject1().getProvenance();
						int prov2 = pair.get(x).getRecordObject2().getProvenance();
						
						System.out.println("prov1="+prov1+" id="+pair.get(x).getRecordObject1().getRecordId());
						System.out.println(pair.get(x).getRecordObject1().getAttributesValues());
						System.out.println("prov2="+prov2+" id="+pair.get(x).getRecordObject2().getRecordId());
						System.out.println(pair.get(x).getRecordObject2().getAttributesValues());
						System.out.println();
					}
				}*/
				/*if(IM.get(j).getTableName().compareTo("indicatefrom0tot")==0)
				{
					System.out.println("recordsmFP="+recordsmFP.size()+" recordsMFP="+recordsMFP.size()+" incorrectTuples="+incorrectTuples);
					System.out.println("tableName="+IM.get(j).getTableName()+" prov="+IM.get(j).getProvenance()+", " + correctTuples+", " + incorrectTuples+", "+duplicateRecords);
				}*/
				
				C[j] = correctTuples;		// correct unretrieved
				I[j] = incorrectTuples;		// incorrect unretrieved
				P[j] = correctTuples - incorrectTuples;//- duplicates?
				S[j] = correctTuples + incorrectTuples;
				D[j] = duplicateRecords;
			}
			
			/*System.out.print("Order of IM before:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getTableName()+",");
			System.out.println();
			
			System.out.print("Order of IM before:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getProvenance()+",");
			System.out.println();
			
			System.out.print("Order of P before:");
			for(int j=0; j<P.length; j++)
				System.out.print(P[j]+",");
			System.out.println();
			
			System.out.print("Order of C before:");
			for(int j=0; j<C.length; j++)
				System.out.print(C[j]+",");
			System.out.println();			
			
			System.out.print("Order of I before:");
			for(int j=0; j<I.length; j++)
				System.out.print(I[j]+",");
			System.out.println();
			
			System.out.print("Order of D after:");
			for(int j=0; j<I.length; j++)
				System.out.print(D[j]+",");
			System.out.println();*/
			
			if(IM.size()>0)
			{
				sortSelectionArraysOnI(IM,M);
				sortSelectionArraysOnC(IM,M); //STILL NOT FULLY IMPLEMENTED
				
				/*System.out.print("Order of IM after:");
				for(int j=0; j<IM.size(); j++)
					System.out.print(IM.get(j).getTableName()+",");
				System.out.println();
				
				System.out.print("Order of IM after:");
				for(int j=0; j<IM.size(); j++)
					System.out.print(IM.get(j).getProvenance()+",");
				System.out.println();
				
				System.out.print("Order of P after:");
				for(int j=0; j<P.length; j++)
					System.out.print(P[j]+",");
				System.out.println();
				
				System.out.print("Order of C after:");
				for(int j=0; j<C.length; j++)
					System.out.print(C[j]+",");
				System.out.println();			
				
				System.out.print("Order of I after:");
				for(int j=0; j<I.length; j++)
					System.out.print(I[j]+",");
				System.out.println();
				
				System.out.print("Order of D after:");
				for(int j=0; j<I.length; j++)
					System.out.print(D[j]+",");
				System.out.println();*/
			}
			
			/*
			int correctTuplesNumber=0;
			if(recordsMTP!=null)
				correctTuplesNumber= recordsMTP.size();
			System.out.println("correctTuplesNumber="+correctTuplesNumber);
			if(correctTuplesNumber!=0)
				proportionRetrievedMapping = (double)S[0]/correctTuplesNumber;
			else
				proportionRetrievedMapping = S[0];
			*/
			/*for(int h=0; h<C.length; h++)
			{
				if(C[0]>0)
				{
					System.out.println("Mapping found:"+IM.get(h).getTableName());
				}
			}*/
			
			//Proportion of Rest and
			//Proportion of Correct
			/*
			System.out.println("Size of tuples of Tp M="+recordsMTP.size());
			System.out.println("Size of tuples of Fp M="+recordsMFP.size());
			System.out.println("Size of tuples of Tp m="+recordsmTP.size());
			System.out.println("Size of tuples of Fp m="+recordsmFP.size());			
			*/
			
			if(totalRetrieved>0)
			{
				proportionCorrect = (double) C[0]/totalRetrieved;
				proportionRest = (double) P[0]/totalRetrieved;
			}
			else
			{
				proportionCorrect = (double) C[0];
				proportionRest = (double) P[0];
			}
			
			//System.out.println("\t\tco="+C[0]+"\t in="+I[0]+"\t du=" + D[0]+"\t  totalRetrieved="+totalRetrieved+"\t "+IM.get(0).getTableName()+ "\t\t\t  proportionCorrect="+proportionCorrect+" proportionRest="+proportionRest+ " prov="+IM.get(0).getProvenance());
			
			if(proportionRest >= q && proportionCorrect > kthreshold)//original database5 k=0.06
			//if(C[0]>0)
			{
				setSelected(IM.get(0).getProvenance(),i);
				MappingResultsSet mappingResultsSet = getMappingsResultsSet(mappingResultsList,IM.get(0).getProvenance());
				
				if(mappingResultsSet!=null)
				{
					ArrayList<Record> mappingResults = mappingResultsSet.getRecords();
					int tp=0;int fp=0;
					for(int w=0; w<mappingResults.size();w++)
					{
						if(mappingResults.get(w).hasMapFeedback())
						{
							if(mappingResults.get(w).getFeedbackValue().compareTo("tp")==0)
								tp++;
							if(mappingResults.get(w).getFeedbackValue().compareTo("tn")==0)
								fp++;
						}
					}
					//System.out.println("feedback tp="+tp+" fp="+fp);
				}
				m = null;
				m = new ArrayList<MappingObject>();
				MappingObject removed = new MappingObject(IM.get(0));
				M.add(removed);
				//System.out.println("proportionRetrieved="+proportionRetrieved);
				//System.out.println("totalRetrieved="+totalRetrieved);
				
				totalRetrieved = totalRetrieved + C[0];
				System.out.println("\t\tco="+C[0]+"\t in="+I[0]+"\t du=" + D[0]+"\t  totalRetrieved="+totalRetrieved+"\t "+IM.get(0).getTableName()+ "\t\t\t  proportionCorrect="+proportionCorrect+" proportionRest="+proportionRest+ " prov="+IM.get(0).getProvenance());
				IM.remove(0);
			}
			else
			{
				IM.remove(0);
				//Discard mapping 0
				//System.out.println("proportionRetrieved <= 0 ..." + proportionRetrieved);
			}
			//System.out.println("After remove IM.size()="+IM.size());
			if(IM.size()==0)
			{
				isEmpty = true;
				//System.out.println("finally IM isEmpty="+isEmpty);
			}
			//System.out.println("before while condition proportionRetrieved="+proportionRetrieved);
		}while(proportionRest >= q && isEmpty==false);/// why I don't have the condition of k here???
		return M;
	}
	
	/**
	 * METHOD 2. USING PROPORTIONS OF C AND P.
	 * This method implements the Greedy Algorithm.
	 * @param IM: Initial set of all the mappings.
	 * @param mappingResultsList: Variable with the feedback of the mappings' results.
	 * @param k: Proportion threshold.
	 * @return
	 */
	public ArrayList<MappingObject> selectMappings2(
			ArrayList<MappingObject> IM, ArrayList<MappingResultsSet> mappingResultsList, double k)
	{
		M = null;
		m = null;
		M = new ArrayList<>();	
		m = new ArrayList<MappingObject>();
		
		double proportionRetrieved = 0.0d;
		double proportionRetrievedMapping = 0.0d;
		
		boolean isEmpty=false;
		double proportionRest = -1000;
		double proportionCorrect = 0;
		int totalRetrieved = 0;
		
		do{
			C = null;
			CO = null;
			I = null;
			P = null;
			S = null;
			C = new int[IM.size()];
			CO = new int[IM.size()];
			I = new int[IM.size()];
			P = new int[IM.size()];
			S = new int[IM.size()];
			
			for(int i=0; i<IM.size(); i++)
			{
				C[i] = 0;
				I[i] = 0;
				P[i] = 0;
				S[i] = 0;
			}
			
			/*
			System.out.println("Mapping, Unretrieved TP, Unretrieved FP");
			System.out.println("P.size="+P.length);*/
			// m is the set of mappings considered so far in the cycle.
			ArrayList<Record> recordsmTP=null;
			ArrayList<Record> recordsMTP=null;
			ArrayList<Record> recordsmFP=null;
			ArrayList<Record> recordsMFP=null;
			m = new ArrayList<MappingObject>();
			
			recordsMTP = getCorrectTuples(M,mappingResultsList);
			recordsMFP = getIncorrectTuples(M, mappingResultsList);
			// This for is used only to fill C, I and P.
			for(int j=0; j<IM.size(); j++)
			{
				m=null;
				m = new ArrayList<MappingObject>();
				m.add(IM.get(j));
				
				/*System.out.println("Adding mapping="+IM.get(j).getProvenance());
				System.out.println("m.size="+m.size());
				System.out.println("M.size="+M.size());
				System.out.println("getCorrectTuples m");*/
				
				recordsmTP = getCorrectTuples(m,mappingResultsList);
				
				//int correctTuplesOthers = tuplesSetDifference(recordsMTP, recordsmTP);
				
				int correctTuples = tuplesSetDifference(recordsmTP, recordsMTP);/// recordsm = recordsM
				
				recordsmFP = getIncorrectTuples(m, mappingResultsList);
				
				int incorrectTuples = tuplesSetDifference(recordsmFP,recordsMFP);
				
				/*if(IM.get(j).getTableName().compareTo("indicatefrom5000tot")==0)
				{
					System.out.println("recordsmFP="+recordsmFP.size()+" recordsMFP="+recordsMFP.size()+" incorrectTuples="+incorrectTuples);
					System.out.println("tableName="+IM.get(j).getTableName()+" prov="+IM.get(j).getProvenance()+", " + correctTuples+", " + incorrectTuples);
				}*/
				
				C[j] = correctTuples;		// correct unretrieved
			
				I[j] = incorrectTuples;		// incorrect unretrieved
				
				P[j] = correctTuples - incorrectTuples;
				S[j] = correctTuples + incorrectTuples;
			}
			
			/*System.out.print("Order of IM before:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getProvenance()+",");
			System.out.println();
			System.out.print("Order of P before:");
			for(int j=0; j<P.length; j++)
				System.out.print(P[j]+",");
			System.out.println();*/
			sortSelectionArraysOnI(IM,M);
			sortSelectionArraysOnC(IM,M); 
			/*
			
			System.out.print("Order of IM after:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getProvenance()+",");
			System.out.println();
			
			System.out.print("Order of IM after:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getTableName()+",");
			System.out.println();
			
			System.out.print("Order of P after:");
			for(int j=0; j<P.length; j++)
				System.out.print(P[j]+",");
			System.out.println();
			
			System.out.print("Order of C after:");
			for(int j=0; j<C.length; j++)
				System.out.print(C[j]+",");
			System.out.println();			
			
			System.out.print("Order of I after:");
			for(int j=0; j<I.length; j++)
				System.out.print(I[j]+",");
			System.out.println();
			*/
			
			/*
			int correctTuplesNumber=0;
			if(recordsMTP!=null)			
				correctTuplesNumber= recordsMTP.size();
			
			System.out.println("correctTuplesNumber="+correctTuplesNumber);
			
			if(correctTuplesNumber!=0)
				proportionRetrievedMapping = (double)S[0]/correctTuplesNumber;
			else
				proportionRetrievedMapping = S[0];
			*/
			
			/*for(int h=0; h<C.length; h++)
			{
				if(C[0]>0)
				{
					System.out.println("Mapping found:"+IM.get(h).getTableName());
				}
				
			}*/
			
			
			
			//Proportion of Rest and
			//Proportion of Correct
			/*
			System.out.println("Size of tuples of Tp M="+recordsMTP.size());
			System.out.println("Size of tuples of Fp M="+recordsMFP.size());
			
			System.out.println("Size of tuples of Tp m="+recordsmTP.size());
			System.out.println("Size of tuples of Fp m="+recordsmFP.size());			
			*/
			
			
			if(totalRetrieved>0)
			{
				proportionCorrect = (double) C[0]/totalRetrieved;
				proportionRest = (double) P[0]/totalRetrieved;
			}
			else 
			{
				proportionCorrect = (double) C[0];
				proportionRest = (double) P[0];
			}
			
			if(proportionRest >= 0.0 && proportionCorrect > 0.06)
			{
				System.out.println("Selecting one mapping in Greedy. table="+
				IM.get(0).getTableName()+" corr="+C[0]+" inc="+I[0]+ " CO[0]="+CO[0]+"  totalRetrieved="+totalRetrieved+" prov="+IM.get(0).getProvenance()+"  proportionCorrect="+proportionCorrect+" proportionRest="+proportionRest);
				
				
				
				MappingResultsSet mappingResultsSet = getMappingsResultsSet(mappingResultsList,IM.get(0).getProvenance());
				
				if(mappingResultsSet!=null)
				{
					ArrayList<Record> mappingResults = mappingResultsSet.getRecords();
					int tp=0;int fp=0;
					for(int w=0; w<mappingResults.size();w++)
					{
						if(mappingResults.get(w).hasMapFeedback())
						{
							if(mappingResults.get(w).getFeedbackValue().compareTo("tp")==0)
								tp++;
							if(mappingResults.get(w).getFeedbackValue().compareTo("tn")==0)
								fp++;
						}
					}
					System.out.println("feedback tp="+tp+" fp="+fp);
				}
				m = null;
				m = new ArrayList<MappingObject>();
				MappingObject removed = new MappingObject(IM.get(0));
				
				M.add(removed);
				//System.out.println("proportionRetrieved="+proportionRetrieved);
				//System.out.println("totalRetrieved="+totalRetrieved);
				
				
				IM.remove(0);
				
				totalRetrieved = totalRetrieved + C[0];
			}
			else
			{
				IM.remove(0);
				//Discard mapping 0
				//System.out.println("proportionRetrieved <= 0 ..." + proportionRetrieved);
			}
			//System.out.println("After remove IM.size()="+IM.size());
			if(IM.size()==0)
			{
				isEmpty = true;
				//System.out.println("finally IM isEmpty="+isEmpty);
			}
			//System.out.println("before while condition proportionRetrieved="+proportionRetrieved);
			
		}while(proportionRest > 0.00 && isEmpty==false);

		return M;
	}
	/**
	 * This method implements the Greedy Algorithm.
	 * @param IM: Initial set of all the mappings.
	 * @param mappingResultsList: Variable with the feedback of the mappings' results.
	 * @param k: Proportion threshold.
	 * @return
	 */
	public ArrayList<MappingObject> selectMappings1(
			ArrayList<MappingObject> IM, ArrayList<MappingResultsSet> mappingResultsList, double k)
	{
		M = null;
		m = null;
		M = new ArrayList<>();	
		m = new ArrayList<MappingObject>();
		
		double proportionRetrieved=0.0d;
		double proportionRetrievedMapping=0.0d;
		
		boolean isEmpty=false;
		double proportionRest = -1000;
		
		int totalRetrieved = 0;
		
		
		do{
			C = null;
			I = null;
			P = null;
			S = null;
			C = new int[IM.size()];
			I = new int[IM.size()];
			P = new int[IM.size()];
			S = new int[IM.size()];
			
			for(int i=0; i<IM.size(); i++)
			{
				C[i] = 0;
				I[i] = 0;
				P[i] = 0;
				S[i] = 0;
			}
			
			/*
			System.out.println("Mapping, Unretrieved TP, Unretrieved FP");
			System.out.println("P.size="+P.length);*/
			// m is the set of mappings considered so far in the cycle.
			ArrayList<Record> recordsmTP=null;
			ArrayList<Record> recordsMTP=null;
			ArrayList<Record> recordsmFP=null;
			ArrayList<Record> recordsMFP=null;
			m = new ArrayList<MappingObject>();
			
			
			recordsMTP = getCorrectTuples(M,mappingResultsList);
			recordsMFP = getIncorrectTuples(M, mappingResultsList);
			// This for is used only to fill C, I and P.
			for(int j=0; j<IM.size(); j++)
			{
				m=null;
				m = new ArrayList<MappingObject>();
				m.add(IM.get(j));
				
				/*System.out.println("Adding mapping="+IM.get(j).getProvenance());
				System.out.println("m.size="+m.size());
				System.out.println("M.size="+M.size());
				System.out.println("getCorrectTuples m");*/
				
				recordsmTP = getCorrectTuples(m,mappingResultsList);
				
				int correctTuples = tuplesSetDifference(recordsmTP, recordsMTP);/// recordsm = recordsM
				
				recordsmFP = getIncorrectTuples(m, mappingResultsList);
				
				int incorrectTuples = tuplesSetDifference(recordsmFP,recordsMFP);
				
				/*if(IM.get(j).getTableName().compareTo("twistt")==0)
				{
					System.out.println("recordsmFP="+recordsmFP.size()+" recordsMFP="+recordsMFP.size()+" incorrectTuples="+incorrectTuples);
					
				}*/
				
				//System.out.println("tableName="+IM.get(j).getTableName()+" prov="+IM.get(j).getProvenance()+", " + correctTuples+", " + incorrectTuples);				
				
				C[j] = correctTuples;		// correct unretrieved
				
				I[j] = incorrectTuples;		// incorrect unretrieved
				
				P[j] = correctTuples - incorrectTuples;
				S[j] = correctTuples + incorrectTuples;
			}
			
			/*System.out.print("Order of IM before:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getProvenance()+",");
			System.out.println();
			System.out.print("Order of P before:");
			for(int j=0; j<P.length; j++)
				System.out.print(P[j]+",");
			System.out.println();*/
			sortSelectionArraysOnI(IM,M);
			sortSelectionArraysOnC(IM,M); //STILL NOT FULL IMPLEMENTED
			
			/*
			System.out.print("Order of IM after:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getProvenance()+",");
			System.out.println();
			
			System.out.print("Order of IM after:");
			for(int j=0; j<IM.size(); j++)
				System.out.print(IM.get(j).getTableName()+",");
			System.out.println();
			
			System.out.print("Order of P after:");
			for(int j=0; j<P.length; j++)
				System.out.print(P[j]+",");
			System.out.println();
			
			System.out.print("Order of C after:");
			for(int j=0; j<C.length; j++)
				System.out.print(C[j]+",");
			System.out.println();			
			
			System.out.print("Order of I after:");
			for(int j=0; j<I.length; j++)
				System.out.print(I[j]+",");
			System.out.println();
			*/
			
			/*
			int correctTuplesNumber=0;
			if(recordsMTP!=null)			
				correctTuplesNumber= recordsMTP.size();
			
			System.out.println("correctTuplesNumber="+correctTuplesNumber);
			
			if(correctTuplesNumber!=0)
				proportionRetrievedMapping = (double)S[0]/correctTuplesNumber;
			else
				proportionRetrievedMapping = S[0];
			*/
			
			
			//Proportion of Rest and
			//Proportion of Correct
			/*
			System.out.println("Size of tuples of Tp M="+recordsMTP.size());
			System.out.println("Size of tuples of Fp M="+recordsMFP.size());
			
			System.out.println("Size of tuples of Tp m="+recordsmTP.size());
			System.out.println("Size of tuples of Fp m="+recordsmFP.size());			
			*/
			proportionRest = (double) P[0]/132;
			
			double proportionCorrect = (double) C[0]/132;
			
			if(proportionRest > -0.1 && proportionCorrect > 0.05)
			{
				System.out.println("Selecting one mapping in Greedy. table="+
				IM.get(0).getTableName()+" corr="+C[0]+" inc="+I[0]+" prov="+IM.get(0).getProvenance());
				
				MappingResultsSet mappingResultsSet = getMappingsResultsSet(mappingResultsList,IM.get(0).getProvenance());
				
				if(mappingResultsSet!=null)
				{
					ArrayList<Record> mappingResults = mappingResultsSet.getRecords();
					int tp=0;int fp=0;
					for(int w=0; w<mappingResults.size();w++)
					{
						if(mappingResults.get(w).hasMapFeedback())
						{
							if(mappingResults.get(w).getFeedbackValue().compareTo("tp")==0)
								tp++;
							if(mappingResults.get(w).getFeedbackValue().compareTo("tn")==0)
								fp++;
						}
					}
					System.out.println("feedback tp="+tp+" fp="+fp);
				}
				m = null;
				m = new ArrayList<MappingObject>();
				MappingObject removed = new MappingObject(IM.get(0));
				
				M.add(removed);
				//System.out.println("proportionRetrieved="+proportionRetrieved);
				//System.out.println("totalRetrieved="+totalRetrieved);
				
				
				IM.remove(0);
				
				totalRetrieved = totalRetrieved + C[0];
			}
			else
			{
				IM.remove(0);
				//Discard mapping 0
				//System.out.println("proportionRetrieved <= 0 ..." + proportionRetrieved);
			}
			//System.out.println("After remove IM.size()="+IM.size());
			if(IM.size()==0)
			{
				isEmpty = true;
				//System.out.println("finally IM isEmpty="+isEmpty);
			}
			//System.out.println("before while condition proportionRetrieved="+proportionRetrieved);
			
		}while(proportionRest>=-0.1 && isEmpty==false);

		return M;
	}
	public MappingResultsSet getMappingsResultsSet(ArrayList<MappingResultsSet> mappingResultsList, int provenance)
	{
		MappingResultsSet m=null;
		for(int j=0; j<mappingResultsList.size(); j++)
		{
			if(mappingResultsList.get(j).getProvenance() == provenance)
				m =mappingResultsList.get(j); 
		}
		return m;
	}

	
	
	public void sortSelectionArraysOnP(ArrayList<MappingObject> IM, ArrayList<MappingObject> M)
	{
		
		for(int x=0; x<IM.size();x++)
		{
			for(int y=0; y<IM.size()-1; y++)
			{
				if(P[y]<P[y+1])
				{
					Collections.swap(IM, y+1, y);
					
					int tempC = C[y];
					C[y] = C[y+1];
					C[y+1] = tempC;

					int tempI = I[y];
					I[y] = I[y+1];
					I[y+1] = tempI;
					
					int tempP = P[y];
					P[y] = P[y+1];
					P[y+1] = tempP;		
					
					int tempS = S[y];
					S[y] = S[y+1];
					S[y+1] = tempS;	
					
				}

			}
		}
		
	}
	

	public void sortSelectionArraysOnC(ArrayList<MappingObject> IM, ArrayList<MappingObject> M)
	{

		for(int x=0; x<IM.size();x++)
		{
			for(int y=0; y<IM.size()-1; y++)
			{
				if(C[y]<C[y+1])
				{
					Collections.swap(IM, y+1, y);
					
					int tempC = C[y];
					C[y] = C[y+1];
					C[y+1] = tempC;
					
					int tempI = I[y];
					I[y] = I[y+1];
					I[y+1] = tempI;
					
					int tempP = P[y];
					P[y] = P[y+1];
					P[y+1] = tempP;					
					
					int tempS = S[y];
					S[y] = S[y+1];
					S[y+1] = tempS;	
					
				}

			}
		}

	}
	public void sortSelectionArraysOnI(ArrayList<MappingObject> IM, ArrayList<MappingObject> M)
	{

		for(int x=0; x<IM.size();x++)
		{
			for(int y=0; y<IM.size()-1; y++)
			{
				if(I[y]>I[y+1])
				{
					Collections.swap(IM, y+1, y);
					int tempC = C[y];
					C[y] = C[y+1];
					C[y+1] = tempC;
					


					int tempI = I[y];
					I[y] = I[y+1];
					I[y+1] = tempI;
					
					int tempP = P[y];
					P[y] = P[y+1];
					P[y+1] = tempP;			
					
					int tempS = S[y];
					S[y] = S[y+1];
					S[y+1] = tempS;	
					
				}

			}
		}
	}	
	/**
	 * 
	 * @param minuend
	 * @param subtrahend
	 * @return
	 */
	public int tuplesSetDifference(ArrayList<Record> minuend, ArrayList<Record> subtrahend)
	{
		int result =0;
		MappingUtil mappingUtil = new MappingUtil();
		for(int m=0; m<minuend.size(); m++)
		{
			Record recordMinuend = minuend.get(m);
			boolean recordExists=false;
			for(int n=0; n<subtrahend.size(); n++)
			{
				Record recordSubtrahend = subtrahend.get(n);
			
				if(mappingUtil.compareRecords(recordMinuend, recordSubtrahend)==0)
				{
					recordExists = true;
				}
			}
			if(recordExists == false)
			{
				result++;
			}
		}
		
		return result;
	}
	/*
	 * Count the duplicate records where m participate.
	 */
	public int getDuplicateRecordsCount(Integration i, MappingObject m)
	{
		int count=0;
		ArrayList<DuplicatePair> duplicatePairList = i.erFeedback.duplicatePairList;
		//System.out.println("duplicatePairList.size="+duplicatePairList.size());
		
		for(int j=0; j<duplicatePairList.size(); j++)
		{
			DuplicatePair duplicatePair = duplicatePairList.get(j);
			Record record1 = duplicatePair.getRecordObject1();
			
			/*int tableNumber1 = (duplicatePair.getRecordObject1()).getEntityNumber();
			String recordId1 = (duplicatePair.getRecordObject1()).getRecordId();
			*/
			Record record2 = duplicatePair.getRecordObject2();
			int record2Provenance = record2.getProvenance();
			
			/*int tableNumber2 = (duplicatePair.getRecordObject2()).getEntityNumber();
			String recordId2 = (duplicatePair.getRecordObject2()).getRecordId();
			*/
			
			if(record1.getProvenance() == m.getProvenance() && record1.isDuplicateAndThisRecordCounts())
			{
				//System.out.println("Counting duplicate record1:"+record1.getAttributesValues().toString());
				count++;
			}
			if(record2.getProvenance() == m.getProvenance() && record1.isDuplicateAndThisRecordCounts())
			{
				//System.out.println("Counting duplicate record2:"+record2.getAttributesValues().toString());
				count++;
			}
			/*
			if(m.getTableNumber()==7)
				System.out.println(record1Provenance+"-"+tableNumber1+ ","+record2Provenance+"-"+tableNumber2);
			*/
		}
		return count;
	}
	/**
	 * Returns an array with the records/tuples that:
	 *  are annotated correct and
	 *  are retrieved by "mappings" and
	 *  are NOT retrieved by ANY mapping of the mappings in "mappingResultsSetList" and
	 *  does not participate in any duplicate pair.
	 * 
	 * @param mappings is a set of mappings.
	 * @param mappingResultsSetList is a set of MappingsResultsSet objects that contains the results of the mappings' results.
	 * @return a list of records.
	 */
	public ArrayList<Record> getCorrectTuplesFeedback(ArrayList<MappingObject> mappings,
			ArrayList<MappingResultsSet> mappingResultsSetList)
	{
		ArrayList<Record> result =new ArrayList<>();
		MappingUtil mappingUtil = new MappingUtil();
		
		for(int n=0; n<mappings.size(); n++)  // for all mappings
		{
			if((mappings.get(n)).isActive()) // if mapping n is active
			{
				String mappingTableName = (mappings.get(n)).getTableName();
				int mappingProvenance = (mappings.get(n)).getProvenance();
				
				/*System.out.println("Active mapping="+provenance);
				System.out.println("mapping table="+(mappings.get(n)).getTableName());*/
				
				for(int m=0; m<mappingResultsSetList.size(); m++)  // For all mappings' results (one object per mapping).
				{
					MappingResultsSet mappingResultSet = mappingResultsSetList.get(m);					
					String resultMappingTableName = mappingResultSet.getTableName();			
					int resultMappingProvenance = mappingResultSet.getProvenance();
					
					if(mappingTableName.compareTo(resultMappingTableName)==0 && (mappingProvenance == resultMappingProvenance)) // We identify the mapping and its feedback-object
					{
						/*System.out.println("Same provenance mapping="+provenance);*/
						ArrayList<Record> mappingResults = mappingResultSet.getRecords();
						/**
						 * Now I have the records of the mapping. I will check if they are correct.
						 */
						for(int y=0; y<mappingResults.size(); y++)
						{
							Record resultRecord = null;
							resultRecord = mappingResults.get(y);
							boolean isPossible=false;
							
							//Is not duplicate.
							if(!resultRecord.isDuplicate){
								isPossible=true;
							}else if(resultRecord.hasERFeedback /*If it doesn't have feedback, it's duplicated and it's not possible.*/
									&&(!resultRecord.erFeedback) /*Feedback says is not.*/
									&& resultRecord.duplicateAndThisRecordCounts){
								isPossible=true;
							}
							
							if(resultRecord.hasMapFeedback() && isPossible)//changed for feedback change
							//if(isPossible)
							{
								//if((resultRecord.getFeedbackValue()).compareTo("tp")==0)//changed for feedbackchange
								if((resultRecord.getGroundTruthValue()).compareTo("tp")==0)//changed for feedbackchange
								{
									/**
									 * I have found that the record is correct. Now I have to check if it exists or not, i.e., if it
									 * has been retrieved before or not.
									 */
									boolean recordExists=false;
									
									for(int p=0; p<result.size(); p++)
									{
										Record record = result.get(p);									
										if(mappingUtil.compareRecords(resultRecord, record)==0)
										{
											recordExists = true;
										}
									}
									
									if((recordExists == false))
									{
										result.add(resultRecord);
										//if(resultRecord.getEntityNumber()==8 || resultRecord.getEntityNumber()==7)
										//	System.out.println(resultRecord.getAttributesValues().toString());		
									}
								}
							}
						}
					}
				}			
			}
		}
		
		/*System.out.println("The correct tuples are:");
		for(int j=0; j<result.size(); j++)
		{
			System.out.println(result.get(j).getAttributesValues().toString()+" provenance="+result.get(j).getProvenance());
		}*/
		
		return result;
	}
	
	/**
	 * 
	 * @param mappings
	 * @param mapFeedbackArrayList
	 * @return
	 */
	public ArrayList<Record> getIncorrectTuplesFeedback(ArrayList<MappingObject> mappings, ArrayList<MappingResultsSet> mapFeedbackArrayList)
	{
		result = null;
		mappingUtil = null;
		result = new ArrayList<>();
		mappingUtil = new MappingUtil();
		
		for(int n=0; n<mappings.size(); n++) // for all mappings
		{
			
			String mappingTableName = (mappings.get(n)).getTableName();
			int provenance = (mappings.get(n)).getProvenance();
			
			if((mappings.get(n)).isActive())
			{
				// for all the mappings' results
				for(int m=0; m<mapFeedbackArrayList.size(); m++) 
				{
					MappingResultsSet mapFeedbackSet = mapFeedbackArrayList.get(m);
					String feedbackTableName = mapFeedbackSet.getTableName();
					int resultProvenance = mapFeedbackSet.getProvenance();
					if(mappingTableName.compareTo(feedbackTableName)==0 && provenance == resultProvenance)
					{			
						ArrayList<Record> mappingResults = mapFeedbackSet.getRecords();
						for(int y=0; y<mappingResults.size(); y++)
						{
							Record resultRecord = null;
							resultRecord = mappingResults.get(y);
							String value ="";
							
							/**Here only pass eval duplicates.**/
							boolean isPossible=false;
							/**To filter duplicated with feedback (if no feedback, it enters)*/
							
							//Is not duplicate.
							if(!resultRecord.isDuplicate){
								isPossible=true;
							}else //Found duplicate, but feedback indicated it is not.	
								if(resultRecord.hasERFeedback/*Has feedback.*/   
									&& resultRecord.erFeedback/*Annotated true.*/
									&& resultRecord.duplicateAndThisRecordCounts/*Counts.*/){
								isPossible=true;
							}
							else
							//If is not pair, then it doesn't matter if it counts or not.
								if(resultRecord.hasERFeedback/*Has feedback.*/
									&&(!resultRecord.erFeedback)/*Annotated false.*/){
								isPossible=true;
							}
							
							if(resultRecord.hasMapFeedback() && isPossible)//changed for feedback change
							//if(isPossible)
							{
								//value = resultRecord.getFeedbackValue();//changed for feedback change
								value = resultRecord.getGroundTruthValue();								
								if(value.compareTo("fp")==0)
								{
									/**
									 * I have found that the record is fp. Now I have to check if it exists or not, i.e., if it
									 * has been retrieved before or not.
									 */
									boolean recordExists = false;
									for(int p=0; p<result.size(); p++)
									{
										Record record = result.get(p);
										if(mappingUtil.compareRecords(resultRecord, record)==0)
										{
											recordExists = true;
										}
									}
									if(recordExists == false)
									{
										result.add(resultRecord);
										//if(resultRecord.getEntityNumber()==8 || resultRecord.getEntityNumber()==7)
										//	System.out.println(resultRecord.getAttributesValues().toString());								
									}
								}
							}
						}						
					}
				}
			}
		}
		return result;
	}	
	
	/**
	 * Returns an array with the records/tuples that are correct and that are retrieved by "mappings"
	 * BUT are NOT retrieved by ANY mapping of the mappings in "mappingResultsSetList".
	 *  
	 * @param mappings is a set of mappings.
	 * @param mappingResultsSetList is a set of MappingsResultsSet objects that contains the results of the mappings' results.
	 * @return a list of records.
	 */
	public ArrayList<Record> getCorrectTuples(ArrayList<MappingObject> mappings,
			ArrayList<MappingResultsSet> mappingResultsSetList)
	{
		ArrayList<Record> result =new ArrayList<>();
		MappingUtil mappingUtil = new MappingUtil();
		
		for(int n=0; n<mappings.size(); n++)  // for all mappings 
		{
			if((mappings.get(n)).isActive()) // if mapping n is active
			{				
				String mappingTableName = (mappings.get(n)).getTableName();
				int provenance = (mappings.get(n)).getProvenance();
				
				/*System.out.println("Active mapping="+provenance);
				System.out.println("mapping table="+(mappings.get(n)).getTableName());*/
				
				
				
				for(int m=0; m<mappingResultsSetList.size(); m++)  // For all mappings' results (one object per mapping).
				{
					MappingResultsSet mappingResultSet = mappingResultsSetList.get(m);					
					String resultTableName = mappingResultSet.getTableName();			
					int resultProvenance = mappingResultSet.getProvenance();
					
					if(mappingTableName.compareTo(resultTableName)==0 && (provenance == resultProvenance)) // We identify the mapping and its feedback-object
					{
						/*System.out.println("Same provenance mapping="+provenance);*/
						ArrayList<Record> mappingResults = mappingResultSet.getRecords();
						/**
						 * Now I have the records of the mapping. I will check if they are correct.
						 */
						for(int y=0; y<mappingResults.size(); y++)
						{
							Record resultRecord = null;
							
							resultRecord = mappingResults.get(y);
							
							if((resultRecord.getGroundTruthValue()).compareTo("tp")==0)
							{
								/**
								 * I have found that the record is correct. Now I have to check if it exists or not, i.e., if it
								 * has been retrieved before or not.
								 */
								boolean recordExists=false;
								
								for(int p=0; p<result.size(); p++)
								{
									Record record = result.get(p);									
									if(mappingUtil.compareRecords(resultRecord, record)==0)
									{
										recordExists = true;
									}

								}
								
								if((recordExists == false))
								{								
									result.add(resultRecord);
								}
							}
							
						}
					
					}
				}			
			}
			
		}
		
		/*System.out.println("The correct tuples are:");
		
		for(int j=0; j<result.size(); j++)
		{
			System.out.println(result.get(j).getAttributesValues().toString()+" provenance="+result.get(j).getProvenance());
		}*/
		 
		
		return result;
	}
	
	
	/**
	 * 
	 * @param mappings
	 * @param mapFeedbackArrayList
	 * @return
	 */
	public ArrayList<Record> getIncorrectTuples(ArrayList<MappingObject> mappings, ArrayList<MappingResultsSet> mapFeedbackArrayList)
	{
		result = null;
		mappingUtil = null;
		result = new ArrayList<>();
		mappingUtil = new MappingUtil();
		
		for(int n=0; n<mappings.size(); n++) // for all mappings
		{
			
			String mappingTableName = (mappings.get(n)).getTableName();
			int provenance = (mappings.get(n)).getProvenance();
			
			if((mappings.get(n)).isActive())
			{
				// for all the mappings' results
				for(int m=0; m<mapFeedbackArrayList.size(); m++) 
				{
					MappingResultsSet mapFeedbackSet = mapFeedbackArrayList.get(m);
					
					String feedbackTableName = mapFeedbackSet.getTableName();
					
					int resultProvenance = mapFeedbackSet.getProvenance();
					
					if(mappingTableName.compareTo(feedbackTableName)==0 && provenance == resultProvenance)
					{			
						ArrayList<Record> mappingResults = mapFeedbackSet.getRecords();
						
						for(int y=0; y<mappingResults.size(); y++)
						{
							Record resultRecord = null;
							resultRecord = mappingResults.get(y);
							
							String value ="";
							if(resultRecord.hasMapFeedback())
								value = resultRecord.getFeedbackValue();								
							else
								value = resultRecord.getGroundTruthValue();
							
							if(value.compareTo("fp")==0)
							{
								/**
								 * I have found that the record is fp. Now I have to check if it exists or not, i.e., if it
								 * has been retrieved before or not.
								 */
								boolean recordExists = false;
								for(int p=0; p<result.size(); p++)
								{
									Record record = result.get(p);
									if(mappingUtil.compareRecords(resultRecord, record)==0)
									{
										recordExists = true;
									}
								}
								
								if(recordExists == false)
								{
									result.add(resultRecord);
								}
							}
							
						}
						
					}
				}
			}
		}	
		
		return result;
	}	


public void setSelected(int prov,Integration i)
		{
		MapManipulation m = (MapManipulation)i.mappingsManipulation;
			for(int j = 0; j<m.mappingsList.size(); j++)
			{
				if(m.mappingsList.get(j).getProvenance()==prov)
				{
					m.mappingsList.get(j).selected=true;
				}
			}
		}

public void setUnselected(Integration i)
{
MapManipulation m = (MapManipulation)i.mappingsManipulation;
	for(int j = 0; j<m.mappingsList.size(); j++)
	{
		
			m.mappingsList.get(j).selected=false;
		
	}
}
}

