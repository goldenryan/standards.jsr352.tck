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

import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.api.chunk.listener.AbstractItemProcessListener;
import javax.batch.api.chunk.listener.AbstractItemWriteListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

public class OnErrorArtifacts {
	
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
	    @Inject 
	    JobContext jobCtx;
		
	    @Override
	    public void onWriteError(List<Object> items, Exception e) throws Exception {
	    	jobCtx.setExitStatus(items.toString());
	    }
		
	}
	
	@javax.inject.Named("OnErrorItemProcessListener")
	public static class OnErrorItemProcessListener extends AbstractItemProcessListener {
	    @Inject 
	    JobContext jobCtx;
		
		@Override
		public void onProcessError(Object item, Exception ex) throws Exception {
	    	jobCtx.setExitStatus(item.toString());
		}
		
	}
}