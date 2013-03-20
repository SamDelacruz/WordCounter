package com.sdelacruz.network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class NetworkModel extends Observable{

	private Map<InetAddress, Integer> workers;
	private InetAddress master;
	
	private int masterSendPort;
	private int masterReceivePort;
	private int workerSendPort;
	private int workerReceivePort;
	
	public synchronized Map<InetAddress, Integer> getWorkers(){
		return this.workers;
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
	
	
	
}
