/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package edu.harvard.hms.dbmi.bd2k.irct.action;


import java.util.Date;
import java.util.Map;

import javax.naming.NamingException;

import edu.harvard.hms.dbmi.bd2k.irct.event.IRCTEventListener;
import edu.harvard.hms.dbmi.bd2k.irct.exception.ResourceInterfaceException;
import edu.harvard.hms.dbmi.bd2k.irct.model.process.IRCTProcess;
import edu.harvard.hms.dbmi.bd2k.irct.model.resource.Resource;
import edu.harvard.hms.dbmi.bd2k.irct.model.resource.implementation.ProcessResourceImplementationInterface;
import edu.harvard.hms.dbmi.bd2k.irct.model.result.Result;
import edu.harvard.hms.dbmi.bd2k.irct.model.result.ResultStatus;
import edu.harvard.hms.dbmi.bd2k.irct.model.security.User;
import edu.harvard.hms.dbmi.bd2k.irct.util.Utilities;

/**
 * Implements the Action interface to run a process on a specific instance
 */
public class ProcessAction implements Action {
	
	private IRCTProcess process ;
	private Resource resource;
	private ActionStatus status;
	private Result result;
	
	private IRCTEventListener irctEventListener;
	
	/**
	 * Run a given process on a resource 
	 * 
	 * @param resource The resource to run the process on
	 * @param process The process to run
	 */
	public void setup(Resource resource, IRCTProcess process) {
		this.resource = resource;
		this.process = process;
		this.irctEventListener = Utilities.getIRCTEventListener();
	}
	
	@Override
	public void updateActionParams(Map<String, Result> updatedParams) {
		for(String key : updatedParams.keySet()) {
			process.getStringValues().put(key, updatedParams.get(key).getId().toString());
		}
	}
	
	@Override
	public void run(User user) {
		irctEventListener.beforeProcess(user, process);
		this.status = ActionStatus.RUNNING;
		try {
			ProcessResourceImplementationInterface processInterface = (ProcessResourceImplementationInterface) resource.getImplementingInterface();
			
			result = ActionUtilities.createResult(processInterface.getProcessDataType(process));
			result.setUser(user);
			
			process.setObjectValues(ActionUtilities.convertResultSetFieldToObject(user, process.getProcessType().getFields(), process.getStringValues()));
			result = processInterface.runProcess(user, process, result);
			
			ActionUtilities.mergeResult(result);
		} catch (Exception e) {
			result.setMessage(e.getMessage());
			this.status = ActionStatus.ERROR;
		}
		irctEventListener.afterProcess(user, process);
	}

	@Override
	public Result getResults(User user) throws ResourceInterfaceException {
		this.result = ((ProcessResourceImplementationInterface)resource.getImplementingInterface()).getResults(user, result);
		try {
			while((this.result.getResultStatus() != ResultStatus.ERROR) && (this.result.getResultStatus() != ResultStatus.COMPLETE)) {
				Thread.sleep(5000);
				this.result = ((ProcessResourceImplementationInterface)resource.getImplementingInterface()).getResults(user, result);
			}
			
			result.getData().close();
		} catch(Exception e) {
			this.result.setResultStatus(ResultStatus.ERROR);
			this.result.setMessage(e.getMessage());
		}
		
		
		result.setEndTime(new Date());
		//Save the query Action
		try {
			ActionUtilities.mergeResult(result);
			this.status = ActionStatus.COMPLETE;
		} catch (NamingException e) {
			result.setMessage(e.getMessage());
			this.status = ActionStatus.ERROR;
		}
		
		return this.result;
	}

	/**
	 * Get the process
	 * 
	 * @return Process
	 */
	public IRCTProcess getProcess() {
		return this.process;
	}

	/**
	 * Sets the process
	 * 
	 * @param process Process
	 */
	public void setProcess(IRCTProcess process) {
		this.process = process;
	}

	@Override
	public ActionStatus getStatus() {
		return status;
	}
}
