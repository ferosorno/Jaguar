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

package uk.ac.man.jaguar.controller.artefacts.er;

import java.util.ArrayList;

import uk.ac.man.jaguar.model.Record;

/**
 *
 * @author osornogf
 */
public class PairOfRecords {
    public ArrayList<Record> recordsArray1;
    public ArrayList<Record> recordsArray2;
    
    public ArrayList<String> record1;
    public ArrayList<String> record2;
    
    public double similarity;
    
    public double getSimilarity() {
        return similarity;
    }
    
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
    public PairOfRecords() {
        this.recordsArray1 = new ArrayList<>();
        this.recordsArray2 = new ArrayList<>();
        this.record1=new ArrayList<>();
        this.record2=new ArrayList<>();
        similarity = 0.0d;
    }
    
    
    
}
