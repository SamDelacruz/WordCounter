package com.sdelacruz.wordcounter.network.objectprocessing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ObjectPoller polls Objects from a Queue, and submits them to an ObjectProcessor to be processed
 * @author Sam Delacruz
 * @version 20-03-2013
 */
public class ObjectPoller extends Thread {
	
	private ObjectProcessor objProcessor;
	private BlockingQueue<Object> objectQueue;
	
	public ObjectPoller(ObjectProcessor p, BlockingQueue<Object> q){
		this.objProcessor = p;
		this.objectQueue = q;
	}
	
	public void run(){
		while(!isInterrupted()){
			
			try {
				this.objProcessor.process(this.objectQueue.poll(500,TimeUnit.MILLISECONDS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
