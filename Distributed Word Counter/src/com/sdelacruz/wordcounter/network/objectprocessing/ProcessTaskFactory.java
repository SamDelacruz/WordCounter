package com.sdelacruz.wordcounter.network.objectprocessing;

public interface ProcessTaskFactory {

	ProcessTask newTask(Object o, ObjectProcessor c);
	
}
