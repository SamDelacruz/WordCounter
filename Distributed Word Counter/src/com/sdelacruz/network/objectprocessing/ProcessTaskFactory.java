package com.sdelacruz.network.objectprocessing;

/**
 * Interface defining a ProcessTaskFactory. ProcessTaskFactory implementations should be used to
 * create new instances of ProcessTask objects.
 * @author Sam Delacruz
 * @version 20-03-2013
 *
 */
public interface ProcessTaskFactory {

	/**
	 * Returns a new ProcessTask
	 * @param o Object to be processed
	 * @param c The ObjectProcessor to be associated with
	 * @return A new ProcessTask
	 */
	ProcessTask newTask(Object o, ObjectProcessor c);
	
}
