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

package uk.ac.man.jaguar.controller;

/* WorkflowRunner.java (UTF-8)
 *
 * 30-Jan-2014
 * @author Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
 */
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTask;

import java.util.ArrayList;

import uk.ac.man.jaguar.JaguarConstants;
import uk.ac.man.jaguar.JaguarVariables;
import uk.ac.man.jaguar.model.Integration;
import uk.ac.man.jaguar.model.Step;
import uk.ac.man.jaguar.util.ConfigurationReader;
import uk.ac.man.jaguar.util.LogWriter;

/**
 *
 * @author osornogf
 */
public class WorkflowRunner {///this mostly would be IntegrationWorkflow or something.
   
    


    
    /**
     * This method runs whenever we want to generate the integration 
     * artefacts and take on board the stored feedback.
     * @param workflow
     * @param i 
     */
    public void runIntegrationWorkflow(ArrayList<Step> workflow, Integration i)
    {
    	if(JaguarConstants.LOGGING)
    	{
    		LogWriter logWriter = new LogWriter();
    		logWriter.writeToFile("Log.txt","Run Integration Workflow");            
    	}
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("Run Integration Workflow. Start. Elapsed Time = "+elapsedTime);        	
        }
    	
        for(int k=0; k<workflow.size(); k++)
        {
            Step s = workflow.get(k);
            run(s, i);// the generation is here. run episode
            i.feedbackApplication.applyFeedback(s.type, i);//Update annotations functions
        }
        
        if(JaguarConstants.ELAPSEDTIMEOUT)
        {
    		long currentTime   = System.currentTimeMillis();
    		long elapsedTime = (currentTime - JaguarVariables.initialTime)/1000;
    		System.out.println("Run Integration Workflow. End. Elapsed Time = "+elapsedTime);        	
        }
    }

    /**
     * 
     * @param s
     * @param i
     */
    public void run(Step s, Integration i)
    {
        switch(s.type)
        {
            case 0:
                i.matchEpisode.runEpisode(i);//Basically generates matchesManipulation
                break;
            case 1:
                i.mapEpisode.runEpisode(i);//Basically generates mappingsManipulation
                break;
            case 2:
                i.erEpisode.runEpisode(i);//Basically generates pairs of recordsManipulation
                break;
        }
    }
}
