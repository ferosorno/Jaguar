/*#################################
* class:  WhiskerPlotter.java
* author: Fernando Osorno-Gutierrez
* date:   11 Jun 2014
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class WhiskerPlotter {

	
	public void generateGNUPlotWhiskerPlotData()
	{
		StringBuffer str = new StringBuffer();
		
		String time = System.currentTimeMillis()+"";
		
        try{
	        try (PrintWriter writer = new PrintWriter("stat_"+time+".txt", "UTF-8")) {
	            writer.println();
	            writer.println(str.toString());
	            
	        }
	    }
	    catch(FileNotFoundException | UnsupportedEncodingException ex)
	    {
	        System.out.println(ex.getMessage());
	        ex.printStackTrace();
	        //System.exit(0);
	    }		

		
		
		
	}
	
	public void generateGNUPlotWhiskerPlot()
	{
		StringBuffer str = new StringBuffer();
		
		str.append("set yrange [ 0.00000 : 1.0000 ] noreverse nowriteback");
		str.append("set xrange [ 0.00000 : 6.0000 ] noreverse nowriteback");
		
		str.append("set title \"candlesticks with open boxes\"");
		str.append("set boxwidth 0.2 absolute");
		
		str.append("plot 'stat.dat' using 1:3:2:6:5:xticlabels(7) with candlesticks whiskerbars 1, '' using 1:4:4:4:4 with candlesticks");
		
        try{
	        try (PrintWriter writer = new PrintWriter("plot"+System.currentTimeMillis()+""+".txt", "UTF-8")) {
	            writer.println();
	            writer.println();
	            writer.println(str.toString());
	            
	        }
	    }
	    catch(FileNotFoundException | UnsupportedEncodingException ex)
	    {
	        System.out.println(ex.getMessage());
	        ex.printStackTrace();
	        //System.exit(0);
	    }
		
	}
	
}
