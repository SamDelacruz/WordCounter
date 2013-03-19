package com.sdelacruz.network.sender;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class depicting a Sender object. When executed, will take requests to send Objects
 * via OutputStream to a destination, assuming the destination is listening for a connection on
 * a matching port.
 * 
 * The class ensures that no more than one Object is sent to the same destination at any given time,
 * and queues any clashing requests to a current connection.
 * 
 * To use: create instance of Sender, to a given port. Start the thread with .start().
 * To send an Object to a particular InetAddress, call .send(Object o, InetAddress dest).
 * 
 * @author Sam Delacruz
 * @version 18-03-2013
 *
 */
public class Sender extends Thread {
	
	private static final int maxThreads = 10;
	
	//Port to send all requests on
	private int port;

	//Stores a list of active connections, which can be checked to prevent clashes
	private List<InetAddress> activeConnections;
	
	//A list of SendTask objects, queued for later sending
	private List<SendTask> sendQueue;
	
	ExecutorService threadpool = Executors.newFixedThreadPool(maxThreads);
	
	public Sender(int port){
		this.port = port;
		this.activeConnections = new ArrayList<InetAddress>();
		this.sendQueue = new ArrayList<SendTask>();
	}
	
	
	public synchronized void send(Object o, InetAddress dest){
		//Create new SendTask
		SendTask send = new SendTask(o,dest);
		this.getSendQueue().add(send);
	}
	
	@Override
	public void run() {
		
		//Run as long as the Thread is uninterrupted
		while(!isInterrupted()){
			//Check for unsent SendTasks
			if(!this.getSendQueue().isEmpty()){
				
				//Create a new collection of SendTask to hold submitted tasks
				Collection<SendTask> toRemove = new ArrayList<SendTask>();
				//Iterate over all tasks in a current sendqueue
				for(SendTask t : this.cloneSendQueue()){
					//Check that there isn't already a send request for this address
					if(!this.getActiveConnections().contains(t.address)){
						//Add the address to list of active connections
						this.getActiveConnections().add(t.address);
						//Start the task
						this.threadpool.execute(t);
						//Add task to a collection of SendTasks for later removal from sendQueue
						toRemove.add(t);
					}
				}
				//Remove all executed Tasks from the queue
				this.getSendQueue().removeAll(toRemove);
				
			}
		}

	}
	
	/*
	 * BEGIN - Synchronized private methods for accessing fields
	 */
	private synchronized void deactivateConnection(InetAddress address) {

		this.getActiveConnections().remove(address);
		
	}
	
	private synchronized List<InetAddress> getActiveConnections(){
		return this.activeConnections;
	}
	
	private synchronized List<SendTask> getSendQueue(){
		return this.sendQueue;
	}
	
	/*
	 * END - Synchronized private methods
	 */
	
	/*
	 * Method used to return a cloned List of sendqueue items
	 */
	private synchronized List<SendTask> cloneSendQueue(){
		SendTask[] cloneArray = (SendTask[]) getSendQueue().toArray();
		List<SendTask> cloneQueue = new ArrayList<SendTask>();
		
		for(SendTask t : cloneArray){
			cloneQueue.add(t);
		}
		
		return cloneQueue;
		
	}
	
	
	
	
	/**
	 * Inner class depicting each individual send operation.
	 * Given an Object and a destination, an instance of this class will attempt to send the object
	 * @author Sam Delacruz
	 * @version 18-03-2013
	 */
	private class SendTask implements Runnable{

		private InetAddress address = null;
		private int port;
		
		private Socket s = null;
		
		private ObjectOutputStream out = null;
		
		private Serializable object = null;
		
		private SendTask(Object o, InetAddress dest){
			this.address = dest;
			this.port = Sender.this.port;
			//Check that o is a Serializable object before execution of run() begins.
			if(o instanceof Serializable){
				this.object = (Serializable)o;
			}
			
			else
				throw new IllegalArgumentException("Error: attempting to send non-serializable Object!");
		}
		
		@Override
		public void run() {
			
			//Try to connect to receiver
			try {
				this.s = new Socket(this.address,this.port);
			} catch (IOException e) {
				//Error connecting to receiver
				e.printStackTrace();
			}
			
			//Check that connection has been established
			if(s!=null){
				//Try to open OutputStream
				try {
					this.out = new ObjectOutputStream(this.s.getOutputStream());
				} catch (IOException e) {
					//Error opening ObjectOutputStream
					e.printStackTrace();
				}
				
				//Check that outputstream has been setup
				if(out!=null){
					//Try to send the Object via outputstream
					try {
						out.writeObject(this.object);
					} catch (IOException e) {
						//Error sending Object
						e.printStackTrace();
					}
					
					//Close the stream
					try {
						out.close();
						s.close();
					} catch (IOException e) {
						//error closing stream
						e.printStackTrace();
					}
				}
			}
			//Remove this connection from the list of active connections
			Sender.this.deactivateConnection(this.address);
		}
		
	}



}
