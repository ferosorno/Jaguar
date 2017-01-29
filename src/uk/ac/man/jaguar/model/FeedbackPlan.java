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

package uk.ac.man.jaguar.model;

import java.util.ArrayList;

/**
 *
 * @author osornogf
 */
public class FeedbackPlan {
	
	public ArrayList<Episode> feedbackPlanList;
	
    public FeedbackPlan()
    {
        feedbackPlanList = new ArrayList<Episode>();
    }
    
    public ArrayList<Episode> getFeedbackPlanList() {
		return feedbackPlanList;
	}
    
	public void setFeedbackPlanList(ArrayList<Episode> feedbackPlanList) {
		this.feedbackPlanList = feedbackPlanList;
	}
	
	public void addEpisode(Episode episode)
    {
        feedbackPlanList.add(episode);
    }
	
	public int size()
	{
		return feedbackPlanList.size();
	}
	
	public Episode get(int pos)
	{
		return feedbackPlanList.get(pos);
	}
	public String toString()
	{
		String result="";
		for(int i=0; i<feedbackPlanList.size(); i++)
		{
			result+="["+feedbackPlanList.get(i).getType()+","+feedbackPlanList.get(i).getAmount()+"] ";
		}
		return result.trim();
	}
}
