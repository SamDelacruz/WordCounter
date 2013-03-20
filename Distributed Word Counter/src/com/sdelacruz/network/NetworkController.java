package com.sdelacruz.network;

import com.sdelacruz.network.io.Receiver;
import com.sdelacruz.network.io.Sender;
import com.sdelacruz.network.objectprocessing.ObjectPoller;
import com.sdelacruz.network.objectprocessing.ObjectProcessor;

public interface NetworkController{

	public abstract Sender addSender(int port);
	public abstract Receiver addReceiver(int port);
	public abstract Sender getSender(int port);
	public abstract Receiver getReceiver(int port);
	public abstract void removeSender(int port);
	public abstract void removeReceiver(int port);

	public abstract ObjectPoller addObjectPoller(ObjectProcessor p, Receiver r);
	public abstract ObjectPoller getObjectPoller(Receiver r);
	public abstract void removeObjectPoller(Receiver r);

	public abstract void addNetworkModel(NetworkModel m);
	public abstract NetworkModel getNetworkModel();
	
}
