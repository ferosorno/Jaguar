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

package uk.ac.man.jaguar.controller.operators.map;

import java.util.ArrayList;

/**
 *
 * @author osornogf
 */
public class MapResult {
    
    int provenance;//provenance
    
    ArrayList<String> attributesNames;
    
    ArrayList<ArrayList<String>> results;

    public MapResult() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int getProvenance() {
        return provenance;
    }

    public void setProvenance(int provenance) {
        this.provenance = provenance;
    }

    public ArrayList<String> getAttributesNames() {
        return attributesNames;
    }

    public void setAttributesNames(ArrayList<String> attributesNames) {
        this.attributesNames = attributesNames;
    }

    public ArrayList<ArrayList<String>> getResults() {
        return results;
    }

    public void setResults(ArrayList<ArrayList<String>> results) {
        this.results = results;
    }

    
    
    public MapResult(int provenance, ArrayList<String> attributesNames, ArrayList<ArrayList<String>> results)
    { 
        this.provenance = provenance;
        this.attributesNames = attributesNames;
        this.results = results;
    }          
}
