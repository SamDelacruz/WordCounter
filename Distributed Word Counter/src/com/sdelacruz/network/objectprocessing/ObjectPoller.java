package com.sdelacruz.network.objectprocessing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sdelacruz.network.io.Receiver;

/**
 * ObjectPoller polls Objects from a Queue, and submits them to an ObjectProcessor to be processed
 * @author Sam Delacruz
 * @version 20-03-2013
 */
public class ObjectPoller extends Thread {
	
	private ObjectProcessor objProcessor;
	private Receiver receiver;
	
	/**
	 * Creates a new ObjectPoller, given an ObjectProcessor to submit objects to, and a BlockingQueue to poll
	 * @param p An ObjectProcessor to submit objects to
	 * @param q A blockingqueue to poll for new Objects
	 */
	public ObjectPoller(ObjectProcessor p, Receiver r){
		this.objProcessor = p;
		this.receiver = r;
	}
	
	public ObjectProcessor getObjectProcessor(){
		return this.objProcessor;
	}
	
	/**
	 * Method to shutdown an active Pollerthread.
	 */
	public synchronized void shutdown(){
		if(this.isAlive())
		this.interrupt();
	}
	
	@Override
	public void run(){
		while(!isInterrupted()){
			
			this.objProcessor.process(this.receiver.poll());
			
		}
	}
	
}
