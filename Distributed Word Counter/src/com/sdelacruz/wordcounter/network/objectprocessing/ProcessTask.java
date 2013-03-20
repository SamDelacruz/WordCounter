package com.sdelacruz.wordcounter.network.objectprocessing;

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
