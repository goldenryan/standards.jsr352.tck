/*
 * Copyright 2014 International Business Machines Corp.
 * 
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.jbatch.tck.tests.jslxml;

import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;

import org.junit.Before;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ibm.jbatch.tck.artifacts.chunkartifacts.InventoryReader;
import com.ibm.jbatch.tck.utils.JobOperatorBridge;

public class PartitionRerunTests {
	private static final String CLASSNAME = InventoryReader.class.getName();
	private final static Logger logger = Logger.getLogger(CLASSNAME);
	static JobOperatorBridge jobOp = null;

	private static void handleException(String methodName, Exception e) throws Exception {
		Reporter.log("Caught exception: " + e.getMessage()+"<p>");
		Reporter.log(methodName + " failed<p>");
		throw e;
	}

	public void setup(String[] args, Properties props) throws Exception {

		String METHOD = "setup";

		try {
			jobOp = new JobOperatorBridge();
		} catch (Exception e) {
			handleException(METHOD, e);
		}
	}

	/* cleanup */
	public void  cleanup()	{		
		jobOp = null;
	}

	@BeforeTest
	@Before
	public void beforeTest() throws ClassNotFoundException {
		jobOp = new JobOperatorBridge(); 
	}

	@AfterTest
	public void afterTest() {
		jobOp = null;
	}
	
	/*
	 * @testName: testRerunPartitionAndBatchlet
	 * @assertion: The first two runs don't complete successfully and the last run completes successfully.  Also,
	 * the first run only has two partitions pass out of 3, the second run has one partition pass out of the one
	 * that should be reran, and the third rerun should rerun all three partitions in the first step and pass.
	 * @test_Strategy: The test fails automatically in the first run in one partition and passes in the other two while
	 * running through step1.  The second run ensures that the one partition that failed in step1 is the only one to be run 
	 * again, taking precedence over the allow-start-if-complete="true" variable and then automatically fails in step2.
	 * Since all previous failed partitions have rerun and passed the allow-start-if-complete="true" variable has precedence and
	 * all three partitions in step1 are reran.
	 */
	@Test
	@org.junit.Test
	public void testRerunPartitionAndBatchlet() throws Exception {
		Properties origParams = new Properties();
		origParams.setProperty("force.failure", "true");
		origParams.setProperty("force.failure2", "false");

		JobExecution je = jobOp.startJobAndWaitForResult("partitionRerun", origParams);
		long execId = je.getExecutionId();
		assertEquals("Didn't fail as expected", BatchStatus.FAILED, je.getBatchStatus());
		logger.fine("FINAL JOB EXIT STATUS: " + je.getExitStatus());
		assertEquals("Two partitions were supposed to pass, but more or less did", 2, je.getExitStatus().split(",").length);
		
		//Now run again, since we failed in one partition on the first run this run should have only that one partition rerun
		Properties restartParams = new Properties();
		restartParams.setProperty("force.failure", "false");
		restartParams.setProperty("force.failure2", "true");
		JobExecution restartje = jobOp.restartJobAndWaitForResult(execId, restartParams);
		long restartExecId = restartje.getExecutionId();
		assertEquals("Didn't fail as expected", BatchStatus.FAILED, jobOp.getJobExecution(restartExecId).getBatchStatus());
		logger.fine("FINAL JOB EXIT STATUS: " + restartje.getExitStatus());
		assertEquals("One partition was supposed to run, but more or less did", 1, restartje.getExitStatus().split(",").length);

		//Now a third time where we rerun from a fail in step to and expect allow-start-if-complete='true' variable to take over
		//since the failed partitions already reran.
		Properties restartParams2 = new Properties();
		restartParams2.setProperty("force.failure", "false");
		restartParams2.setProperty("force.failure2", "false");
		JobExecution restartje2 = jobOp.restartJobAndWaitForResult(restartExecId, restartParams2);
		long restartExecId2 = restartje2.getExecutionId();
		assertEquals("Didn't complete successfully", BatchStatus.COMPLETED, jobOp.getJobExecution(restartExecId2).getBatchStatus());
		logger.fine("FINAL JOB EXIT STATUS: " + restartje2.getExitStatus());
		assertEquals("Three partitions were supposed to run, but more or less did", 3, restartje2.getExitStatus().split(",").length);
	}

}
