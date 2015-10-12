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
package com.ibm.jbatch.tck.artifacts.specialized;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.batch.api.partition.AbstractPartitionAnalyzer;
import javax.batch.api.partition.PartitionCollector;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.api.chunk.listener.AbstractItemReadListener;
import javax.batch.api.chunk.listener.AbstractItemWriteListener;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ibm.jbatch.tck.artifacts.chunktypes.ReadRecord;
import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OnErrorArtifacts {

public static String GOOD_EXIT_STATUS = "VERY GOOD INVOCATION";
	
	@javax.inject.Named("OnErrorItemReader")
	public static class OnErrorItemReader extends AbstractItemReader  {
		ArrayList<Integer> Items = new ArrayList<Integer>();
		@Inject JobContext jobCtx;
		@Inject StepContext stepCtx;

		@Override
		public void open(Serializable checkpoint) throws Exception {
			for(int i=0;i<5;i++)
				Items.add(i);
		}
		
		@Override
		public Object readItem() throws Exception {
			try{
				Integer res = Items.get(Items.size()-1);
				Items.remove(Items.size()-1);
				return res;
			}catch(Exception e){
				throw e;
			}
		}
	}
	
	@javax.inject.Named("OnErrorItemWriteListener")
	public static class OnErrorItemWriteListener extends AbstractItemWriteListener {
		
		/*private final static String sourceClass = OnErrorItemWriteListener.class.getName();
		private final static Logger logger = Logger.getLogger(sourceClass);*/

		int beforecounter = 1;
		int aftercounter = 1;
		
	    @Inject 
	    JobContext jobCtx; 
		
	    @Inject    
	    @BatchProperty(name="app.listenertest")
	    String applistenerTest;
		
		@Override
		public void beforeWrite(List<Object> items) throws Exception {
		}
		
		@Override
		public void afterWrite(List<Object> items) throws Exception {
		}
		
	    @Override
	    public void onWriteError(List<Object> items, Exception e) throws Exception {
	    	jobCtx.setExitStatus(items.toString());
	    }
		
	}
}