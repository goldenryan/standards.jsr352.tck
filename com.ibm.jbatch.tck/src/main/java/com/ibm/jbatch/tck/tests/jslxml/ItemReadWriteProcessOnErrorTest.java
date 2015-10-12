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

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.partition.AbstractPartitionAnalyzer;
import javax.batch.api.partition.PartitionCollector;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ibm.jbatch.tck.artifacts.specialized.ParallelContextPropagationArtifacts;
import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import static com.ibm.jbatch.tck.utils.AssertionUtils.assertWithMessage;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ItemReadWriteProcessOnErrorTest {
	private static JobOperatorBridge jobOp = null;
	private static int sleepTime = 20000;
	
	@BeforeMethod
	@BeforeClass
	public static void setup() throws Exception {
		jobOp = new JobOperatorBridge();
	}



    /*
     * @testName: testOnWriteErrorItems
     * 
     * @assertion:
     * 
     * @test_Strategy: 
     */
	@Test
	@org.junit.Test
	public void testOnWriteErrorItems() throws Exception {
        Properties jobParams = new Properties();
		
		JobExecution je = jobOp.startJobAndWaitForResult("partitionCtxPropagation", null);
		Thread.sleep(sleepTime);
		
		// Check job COMPLETED since some validation is crammed into the execution.
		assertEquals("Test successful completion", "COMPLETED", je.getBatchStatus().toString());

		// Get the correct exec id and instance id
		long theExecId = je.getExecutionId();
		long theInstanceId = jobOp.getJobInstance(theExecId).getInstanceId();


        Reporter.log("Create job parameters for execution #2:<p>");
        jobParams = new Properties();
        Reporter.log("process.fail.immediate=true<p>");
        
        jobParams.put("process.fail.immediate", "true");


        Reporter.log("Invoke startJobAndWaitForResult for execution #2<p>");
        JobExecution execution2 = jobOp.startJobAndWaitForResult("chunkReadWriteProcessTest", jobParams);
        Reporter.log("execution #2 JobExecution getBatchStatus()=" + je.getBatchStatus() + "<p>");
        Reporter.log("execution #2 JobExecution getExitStatus()=" + je.getExitStatus() + "<p>");
        /*assertWithMessage("Testing execution #2 for the PROCESS LISTENER", BatchStatus.FAILED, execution2.getBatchStatus());
        assertWithMessage("Testing execution #2 for the PROCESS LISTENER", "123456789",
                execution2.getExitStatus());*/
		
	    Reporter.log("Create job parameters for execution #3:<p>");
	    jobParams = new Properties();
	    Reporter.log("write.fail.immediate=true<p>");
	    
	    jobParams.put("write.fail.immediate", "true");
	
	
	    Reporter.log("Invoke startJobAndWaitForResult for execution #3<p>");
	    JobExecution execution3 = jobOp.startJobAndWaitForResult("chunkReadWriteProcessTest", jobParams);
	    Reporter.log("execution #3 JobExecution getBatchStatus()=" + je.getBatchStatus() + "<p>");
	    Reporter.log("execution #3 JobExecution getExitStatus()=" + je.getExitStatus() + "<p>");
	    assertWithMessage("Testing execution #3 for the WRITE LISTENER", BatchStatus.FAILED, execution3.getBatchStatus());
	    assertWithMessage("Testing execution #3 for the WRITE LISTENER", "[5, 4, 3, 2, 1]",
	            execution3.getExitStatus());
	}

    /*
     * @testName: testOnProccessErrorItems
     * 
     * @assertion:
     * 
     * @test_Strategy: 
     */
	/*@Test
	@org.junit.Test
	public void testOnProccessErrorItems() throws Exception {
        Properties jobParams = new Properties();
		
		JobExecution je = jobOp.startJobAndWaitForResult("partitionCtxPropagation", null);
		Thread.sleep(sleepTime);
		
		// Check job COMPLETED since some validation is crammed into the execution.
		assertEquals("Test successful completion", "COMPLETED", je.getBatchStatus().toString());

		// Get the correct exec id and instance id
		long theExecId = je.getExecutionId();
		long theInstanceId = jobOp.getJobInstance(theExecId).getInstanceId();
		
	    Reporter.log("Create job parameters for execution #3:<p>");
	    jobParams = new Properties();
	    Reporter.log("write.fail.immediate=true<p>");
	    
	    jobParams.put("write.fail.immediate", "true");
	
	
	    Reporter.log("Invoke startJobAndWaitForResult for execution #3<p>");
	    JobExecution execution3 = jobOp.startJobAndWaitForResult("testListenersOnError", jobParams);
	    Reporter.log("execution #3 JobExecution getBatchStatus()=" + je.getBatchStatus() + "<p>");
	    Reporter.log("execution #3 JobExecution getExitStatus()=" + je.getExitStatus() + "<p>");
	    assertWithMessage("Testing execution #3 for the WRITE LISTENER", BatchStatus.FAILED, execution3.getBatchStatus());
	    assertWithMessage("Testing execution #3 for the WRITE LISTENER", "123456789",
	            execution3.getExitStatus());
	}*/
}
