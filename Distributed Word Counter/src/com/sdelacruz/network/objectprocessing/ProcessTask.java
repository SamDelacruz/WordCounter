package com.sdelacruz.network.objectprocessing;

/**
 * Abstract class depicting a ProcessTask object.
 * A ProcessTask is a task executed by an ObjectProcessor. Classes inheriting from ProcessTask must
 * implement the method process(Object o), in a way appropriate to the instance type.
 * 
 * It is advisable that a companion class implementing ProcessTaskFactory is created for each implementation
 * of ProcessTask.
 * @author Sam Delacruz
 * @version 20-03-2013
 *
 */
public abstract class ProcessTask implements Runnable {
	
	protected Object processObject;
	protected ObjectProcessor objectProcessor;
	
	protected ProcessTask(Object o, ObjectProcessor p){
		this.processObject = o;
		this.objectProcessor = p;
	}

	@Override
	public void run() {
		
		process(this.processObject);

	}
	
	protected abstract void process(Object o);

}
