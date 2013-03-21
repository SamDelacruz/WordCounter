package com.sdelacruz.wordcounter.network;

import com.sdelacruz.network.objectprocessing.ObjectProcessor;
import com.sdelacruz.network.objectprocessing.ProcessTask;
import com.sdelacruz.network.objectprocessing.ProcessTaskFactory;

public class MasterProcessTaskFactory implements ProcessTaskFactory {

	@Override
	public ProcessTask newTask(Object o, ObjectProcessor c) {
		return new MasterProcessTask(o,c);
	}

}
