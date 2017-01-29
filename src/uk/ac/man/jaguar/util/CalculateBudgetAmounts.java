/*#################################
* class:  CalculateBudgetAmounts.java
* author: Fernando Osorno-Gutierrez
* date:   13 Jun 2015
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import ec.util.InitializationTools;

public class CalculateBudgetAmounts {
		
	public CalculateBudgetAmounts(){}
	static int matches = InitializationTools.ENVIRONMENTMATCHES;
	static int results = InitializationTools.RESULTSSIZE;
	static int pairs = InitializationTools.ENVIRONMENTPAIRSOFRECORDS;
	
	/*static int matches = 3325;
	static int results = 4620;
	static int pairs = 397;*/
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*80
		int matches = 3162;
		int results = 1218;
		int pairs = 146;
		*/
		//70
		/*int matches = 3302;
		int results = 909;
		int pairs = 59;*/

		for(int i=1;i<=10;i++)
		{
			double b = (0.1*i)*((matches*3)+(results*3)+(pairs*3));
			System.out.print(Math.round(b)+",");
		}	
	
	}
	
	public int[] getBudget()
	{
		int[] result=new int[10];
		
		for(int i=1;i<=10;i++)
		{
			double b = (0.1*i)*((matches*3)+(results*3)+(pairs*3));
			//System.out.print(Math.round(b)+",");
			result[i-1] = (int)Math.round(b);
		}	
		
		return result;
	}

}
