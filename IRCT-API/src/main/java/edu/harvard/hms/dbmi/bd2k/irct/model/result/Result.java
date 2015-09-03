/*
 *  This file is part of Inter-Resource Communication Tool (IRCT).
 *
 *  IRCT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  IRCT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with IRCT.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hms.dbmi.bd2k.irct.model.result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.SequenceGenerator;
import edu.harvard.hms.dbmi.bd2k.irct.action.Executable;
import edu.harvard.hms.dbmi.bd2k.irct.util.converter.ResultSetConverter;

/**
 * The result class is created for each execution that is run on the IRCT
 * (Query, Process, etc...). It provides a way of the end user to rerun
 * processes, and retrieve the results.
 * 
 * @author Jeremy R. Easton-Marks
 *
 */
@Entity
public class Result {
	@Id
	@GeneratedValue(generator="resultSequencer")
	@SequenceGenerator(name = "resultSequencer", sequenceName="resSeq")
	private Long id;

	// TODO: REMOVE TRANSIENT
	@Transient
	private Executable executable;

	@Temporal(TemporalType.TIMESTAMP)
	private Date runTime;
	@Enumerated(EnumType.STRING)
	private ResultStatus resultStatus;

	@Convert(converter = ResultSetConverter.class)
	private ResultSet implementingResultSet;
	private String resultSetLocation;

	/**
	 * Returns a JSONObject representation of the object. This returns only the
	 * attributes associated with this object and not their representation.
	 * 
	 * This is equivalent of toJson(1);
	 * 
	 * @return JSON Representation
	 */
	public JsonObject toJson() {
		return toJson(1);
	}

	/**
	 * Returns a JSONObject representation of the object. This returns only the
	 * attributes associated with this object and not their representation.
	 * 
	 * 
	 * @param depth
	 *            Depth to travel
	 * @return JSON Representation
	 */
	public JsonObject toJson(int depth) {
		depth--;
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		jsonBuilder.add("id", this.id);
		jsonBuilder.add("status", this.resultStatus.toString());
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		jsonBuilder.add("runTime", formatter.format(new Date()));

		return jsonBuilder.build();
	}

	// -------------------------------------------------------------------------
	// SETTERS AND GETTERS
	// -------------------------------------------------------------------------

	/**
	 * Returns the id of the result
	 * 
	 * @return Id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id of the result
	 * 
	 * @param id
	 *            Id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the executable used for the result
	 * 
	 * @return Executable
	 */
	public Executable getExecutable() {
		return executable;
	}

	/**
	 * Sets the executable used for the result
	 * 
	 * @param executable
	 *            Executable
	 */
	public void setExecutable(Executable executable) {
		this.executable = executable;
	}

	/**
	 * Returns the time the result was created
	 * 
	 * @return Run time
	 */
	public Date getRunTime() {
		return runTime;
	}

	/**
	 * Sets the time the result was created
	 * 
	 * @param runTime
	 *            Run time
	 */
	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}

	/**
	 * Gets the result status
	 * 
	 * @return Result status
	 */
	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	/**
	 * Sets the result status
	 * 
	 * @param resultStatus
	 *            Result status
	 */
	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	/**
	 * Returns an instantiation of a class that implements the result status
	 * 
	 * @return Result Set Object
	 */
	public ResultSet getImplementingResultSet() {
		return implementingResultSet;
	}

	/**
	 * Sets the class that is used to implement the result status
	 * 
	 * @param implementingResultSet Implementing Result Set
	 */
	public void setImplementingResultSet(ResultSet implementingResultSet) {
		this.implementingResultSet = implementingResultSet;
	}

	/**
	 * Returns the location of the result set
	 * 
	 * @return Result Set
	 */
	public String getResultSetLocation() {
		return resultSetLocation;
	}

	/**
	 * Sets the result location of the result set
	 * 
	 * @param resultSetLocation
	 *            Result Set
	 */
	public void setResultSetLocation(String resultSetLocation) {
		this.resultSetLocation = resultSetLocation;
	}
}
