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

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.mapping.FORule;

import java.util.ArrayList;

/**
 *
 * @author osornogf
 */
public class MappingObject {

	public int numberOfRelationsTarget;
	public int numberOfRelationsSource;
	public int sizeOfResults;
	public int tableNumber;

	public ArrayList<String> tgds;
    public ArrayList<String> workingTables;
    public ArrayList<String> resultOfExchange;
    
    public double precision;
    public double recall;
    public double fmeasure;
    
    public String ruleName;
	public String selectString;
	public String tableName;
	public ArrayList<String> attributes;
    public int provenance;
    public String integrationProvenance;
    public boolean mappingUsedForPrecision = false;
    public boolean feedbackHasBeenPropagated = false;
    public boolean mappingWasRun = false;
    public boolean active = false;
    
    public boolean selected=false;
    
    public double groundTruthPrecision;
    public int gtTruePositives;
    
    public FORule forule;
    
    ArrayList<String> SSAttributes;
    ArrayList<String> GSAttributes;
    public boolean noNullsAttributes;
    
    public boolean hasEXCEPT;
    
    public MappingObject(MappingObject mappingObject)
    {
    	this.tgds=mappingObject.tgds;
    	this.workingTables=mappingObject.workingTables;
    	this.precision = mappingObject.precision;
    	this.recall = mappingObject.recall;
    	
    	this.attributes = mappingObject.attributes;
    	this.provenance = mappingObject.provenance;
    	this.groundTruthPrecision = mappingObject.groundTruthPrecision;
    	this.gtTruePositives = mappingObject.gtTruePositives;
    	this.gtFalsePositives = mappingObject.gtFalsePositives;
    	
    	this.setTableName(mappingObject.getTableName());
    	this.setForule(mappingObject.getForule());
    }
    
    
    
	public int getSizeOfResults() {
		return sizeOfResults;
	}



	public void setSizeOfResults(int sizeOfResults) {
		this.sizeOfResults = sizeOfResults;
	}



	public boolean isActive() {
		return active;
	}



	public void setActive(boolean active) {
		this.active = active;
	}



	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}    
    public String getTableName() {
		return tableName;
	}

	public FORule getForule() {
		return forule;
	}
	public void setForule(FORule forule) {
		this.forule = forule;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
    public int getNumberOfRelationsTarget() {
		return numberOfRelationsTarget;
	}

	public void setNumberOfRelationsTarget(int numberOfRelationsTarget) {
		this.numberOfRelationsTarget = numberOfRelationsTarget;
	}

	public int getNumberOfRelationsSource() {
		return numberOfRelationsSource;
	}

	public void setNumberOfRelationsSource(int numberOfRelationsSource) {
		this.numberOfRelationsSource = numberOfRelationsSource;
	}

	public double getFmeasure() {
		return fmeasure;
	}


	public void setFmeasure(double fmeasure) {
		this.fmeasure = fmeasure;
	}    
    public double getRecall() {
		return recall;
	}


	public void setRecall(double recall) {
		this.recall = recall;
	}


	public int getGtTruePositives() {
		return gtTruePositives;
	}

	public void setGtTruePositives(int gtTruePositives) {
		this.gtTruePositives = gtTruePositives;
	}

	public int getGtFalsePositives() {
		return gtFalsePositives;
	}

	public void setGtFalsePositives(int gtFalsePositives) {
		this.gtFalsePositives = gtFalsePositives;
	}
	public int gtFalsePositives;
    
    
    public MappingObject() {
        this.tgds = new ArrayList<>();
        this.resultOfExchange = new ArrayList<>();
        this.workingTables = new ArrayList<>();
        this.setPrecision(0d);
        this.valueCorrespondences = new ArrayList<>();
        this.SSAttributes = new ArrayList<>();
        this.GSAttributes = new ArrayList<>();
        
        hasEXCEPT = false;
    }

    /**
	 * @return the groundTruthPrecision
	 */
	public double getGroundTruthPrecision() {
		return groundTruthPrecision;
	}

	/**
	 * @param groundTruthPrecision the groundTruthPrecision to set
	 */
	public void setGroundTruthPrecision(double groundTruthPrecision) {
		this.groundTruthPrecision = groundTruthPrecision;
	}

	public ArrayList getTgds() {
        return tgds;
    }

    public void setTgds(ArrayList tgds) {
        this.tgds = tgds;
    }

    public boolean isMappingUsedForPrecision() {
        return mappingUsedForPrecision;
    }

    public void setMappingUsedForPrecision(boolean mappingUsedForPrecision) {
        this.mappingUsedForPrecision = mappingUsedForPrecision;
    }

    public boolean isMappingWasRun() {
        return mappingWasRun;
    }

    public void setMappingWasRun(boolean mappingWasRun) {
        this.mappingWasRun = mappingWasRun;
    }
    public ArrayList<ValueCorrespondence> valueCorrespondences;

    public ArrayList<ValueCorrespondence> getValueCorrespondences() {
        return valueCorrespondences;
    }

    public void setValueCorrespondences(ArrayList<ValueCorrespondence> valueCorrespondences) {

        if (valueCorrespondences != null) {
            this.valueCorrespondences = new ArrayList<>();
            for (int k = 0; k < valueCorrespondences.size(); k++) {
                ValueCorrespondence valueCorrespondence = new ValueCorrespondence(valueCorrespondences.get(k));
                this.valueCorrespondences.add(valueCorrespondence);
            }
        }
    }

    public int getProvenance() {
        return provenance;
    }

    public void setProvenance(int provenance) {
        this.provenance = provenance;
    }

    public String getSelectString() {
        return selectString;
    }

    public void setSelectString(String selectString) {
        this.selectString = selectString;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    public String getTGD(int index) {
        return (String) this.tgds.get(index);
    }

    public void setResultOfExchange(String resultOfExchange) {
        this.resultOfExchange.add(resultOfExchange);
    }

    public String getResultOfExchangeAt(int index) {
        return this.resultOfExchange.get(index);
    }
    public ArrayList getResultOfExchange() {
        return this.resultOfExchange;
    }
    public boolean feedbackHasBeenPropagated() {
        return this.feedbackHasBeenPropagated;
    }
	
    public int getTableNumber() {
		return tableNumber;
	}



	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}



	public ArrayList<String> getSSAttributes() {
		return SSAttributes;
	}



	public void setSSAttributes(ArrayList<String> sSAttributes) {
		SSAttributes = sSAttributes;
	}



	public ArrayList<String> getGSAttributes() {
		return GSAttributes;
	}



	public void setGSAttributes(ArrayList<String> gSAttributes) {
		GSAttributes = gSAttributes;
	}



	public boolean isNoNullsAttributes() {
		return noNullsAttributes;
	}



	public void setNoNullsAttributes(boolean noNullsAttributes) {
		this.noNullsAttributes = noNullsAttributes;
	}



	public boolean isHasEXCEPT() {
		return hasEXCEPT;
	}



	public void setHasEXCEPT(boolean hasEXCEPT) {
		this.hasEXCEPT = hasEXCEPT;
	}
	
	
}
