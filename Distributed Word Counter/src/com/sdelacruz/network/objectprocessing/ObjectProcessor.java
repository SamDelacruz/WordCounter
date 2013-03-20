package com.sdelacruz.network.objectprocessing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sdelacruz.network.NetworkController;


/**
 * Class depicting an ObjectProcessor thread.
 * Queue new Objects to be processed with process(Object o)
 * The Objects will be executed by an instance of an implementation of ProcessTask
 * The type of ProcessTask must be defined by the ProcessTaskFactory passed as an argument to the ObjectProcessor constructor
 * @author Sam Delacruz
 * @version 20-03-2013
 *
 */
public class ObjectProcessor extends Thread{
	private int maxThreads = 10;
	private ProcessTaskFactory pTaskFactory;
	private NetworkController networkController;
	private ExecutorService threadpool;
	
	private BlockingQueue<ProcessTask> taskQueue;
	

	public ObjectProcessor(ProcessTaskFactory f, NetworkController c){
		this.pTaskFactory = f;
		this.networkController = c;
		this.taskQueue = new LinkedBlockingQueue<ProcessTask>();
		this.threadpool = Executors.newFixedThreadPool(this.maxThreads);
	}
	
	/**
	 * Public method to return the NetworkController of this ObjectProcessor
	 * @return NetworkController this ObjectProcessor's NetworkController
	 */
	public NetworkController getNetworkController(){
		return this.networkController;
	}
	
	/**
	 * Method to submit an Object to be processed
	 * @param o Object to be Processed
	 */
	public void process(Object o){
		
		if(o!=null){
			ProcessTask task = this.pTaskFactory.newTask(o, this);
			
			try {
				this.taskQueue.put(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
public void run() {
		
		//Run as long as the Thread is uninterrupted
		while(!isInterrupted()){
		
				ProcessTask task = null;
				
				try {
					//Read from the queue
					task = this.taskQueue.poll(500,TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					//Interrupted while waiting for new send task
					e.printStackTrace();
				}
				
				if(task!=null){
					//Start the task
					this.threadpool.execute(task);
				}
			
		}

	}
	
}
