package com.sdelacruz.network;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class NetworkModel extends Observable{

	private Map<InetAddress, Integer> workers;
	private InetAddress master;
	private boolean root = false;
	private int masterSendPort;
	private int masterReceivePort;
	private int workerSendPort;
	private int workerReceivePort;
	
	public NetworkModel(int startport, InetAddress master){
		this.masterSendPort = startport;
		this.masterReceivePort = startport + 1;
		this.workerSendPort = startport + 2;
		this.workerReceivePort = startport + 3;
		this.workers = new HashMap<InetAddress, Integer>();
		this.master = master;
		if(this.master==null)
			this.root = true;
			
	}
	
	public boolean isRoot(){
		return this.root;
	}
	
	public synchronized Map<InetAddress, Integer> getWorkers(){
		return this.workers;
	}
	
	public synchronized InetAddress getMasterInet(){
		return this.master;
	}
	
	public synchronized void setMasterInet(InetAddress i){
		this.master = i;
	}
	
	public void addWorker(InetAddress i){
		this.getWorkers().put(i, 0);
	}
	
	
	public synchronized void incrementPorts(){
		int newMS = this.masterReceivePort + 2;
		int newMR = this.masterSendPort + 2;
		int newWS = this.workerReceivePort + 2;
		int newWR = this.workerSendPort + 2;
		
		this.masterSendPort = newMS;
		this.masterReceivePort = newMR;
		this.workerSendPort = newWS;
		this.workerReceivePort = newWR;
		
		notifyObservers("new_ports");
		setChanged();
	}
	
	public synchronized void decrementPorts(){
		int newMS = this.masterReceivePort - 2;
		int newMR = this.masterSendPort - 2;
		int newWS = this.workerReceivePort - 2;
		int newWR = this.workerSendPort - 2;
		
		this.masterSendPort = newMS;
		this.masterReceivePort = newMR;
		this.workerSendPort = newWS;
		this.workerReceivePort = newWR;
		
		notifyObservers("new_ports");
		setChanged();
	}
	
	public synchronized int getMasterSendPort(){
		return this.masterSendPort;
	}
	
	public synchronized int getMasterReceivePort(){
		return this.masterReceivePort;
	}
	
	public synchronized int getWorkerSendPort(){
		return this.workerSendPort;
	}
	
	public synchronized int getWorkerReceivePort(){
		return this.workerReceivePort;
	}
	
	
	
}
