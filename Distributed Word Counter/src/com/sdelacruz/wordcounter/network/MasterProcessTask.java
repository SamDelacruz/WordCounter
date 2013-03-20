package com.sdelacruz.wordcounter.network;

import java.net.InetAddress;

import com.sdelacruz.network.objectprocessing.ObjectProcessor;
import com.sdelacruz.network.objectprocessing.ProcessTask;

public class MasterProcessTask extends ProcessTask {

	protected MasterProcessTask(Object o, ObjectProcessor p) {
		super(o, p);
	}

	@Override
	protected void process(Object o) {

		if(o instanceof InetAddress){
			InetAddress receivedAddress = (InetAddress)o;
		}

	}

}
