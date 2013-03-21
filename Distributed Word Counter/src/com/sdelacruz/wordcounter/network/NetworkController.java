package com.sdelacruz.wordcounter.network;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.sdelacruz.network.INetworkController;
import com.sdelacruz.network.NetworkModel;
import com.sdelacruz.network.io.Receiver;
import com.sdelacruz.network.io.Sender;
import com.sdelacruz.network.objectprocessing.ObjectPoller;
import com.sdelacruz.network.objectprocessing.ObjectProcessor;
import com.sdelacruz.wordcounter.WordCounter;

public class NetworkController implements INetworkController {

	private final int startport = 8502;
	
	private Map<Integer, Sender> senders;
	private Map<Integer, Receiver> receivers;
	private Map<Receiver, ObjectPoller> objectPollers;
	private NetworkModel networkModel;
	private WordCounter wordCounter;
	
	public NetworkController(int maxWorkersPerBranch, int minWordSendUnit, InetAddress master){
		this.senders = new HashMap<Integer,Sender>();
		this.receivers = new HashMap<Integer,Receiver>();
		this.objectPollers = new HashMap<Receiver, ObjectPoller>();
		this.networkModel = new NetworkModel(this.startport, maxWorkersPerBranch, minWordSendUnit, master);
		this.wordCounter = new WordCounter();
	}
	
	@Override
	public Sender addSender(int port) {
		Sender s = new Sender(port);
		this.senders.put(port, s);
		s.start();
		return s;
	}

	@Override
	public Receiver addReceiver(int port) {
		Receiver r = new Receiver(port);
		this.receivers.put(port, r);
		r.start();
		return r;
	}

	@Override
	public Sender getSender(int port) {
		Sender s = null;
		s = this.senders.get(port);
		return s;
	}

	@Override
	public Receiver getReceiver(int port) {
		Receiver r = null;
		r = this.receivers.get(port);
		return r;
	}

	@Override
	public void removeSender(int port) {
		this.senders.get(port).shutdown();
		this.senders.remove(port);

	}

	@Override
	public void removeReceiver(int port) {
		this.receivers.get(port).shutdown();
		removeObjectPoller(this.receivers.get(port));
		this.receivers.remove(port);

	}

	@Override
	public ObjectPoller addObjectPoller(ObjectProcessor p, Receiver r) {
		ObjectPoller op = new ObjectPoller(p,r);
		this.objectPollers.put(r, op);
		return op;
	}

	@Override
	public ObjectPoller getObjectPoller(Receiver r) {
		ObjectPoller op = null;
		op = this.objectPollers.get(r);
		return op;
	}

	@Override
	public void removeObjectPoller(Receiver r) {
		this.objectPollers.get(r).shutdown();
		this.objectPollers.get(r).getObjectProcessor().shutdown();
		this.objectPollers.remove(r);

	}

	@Override
	public void setNetworkModel(NetworkModel m) {
		this.networkModel = m;

	}

	@Override
	public NetworkModel getNetworkModel() {
		return this.networkModel;
	}

	@Override
	public WordCounter getWordCounter() {
		return this.wordCounter;
	}

}
