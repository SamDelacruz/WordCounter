package com.sdelacruz.network.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	//A queue of SendTask objects, queued for later sending
	private LinkedBlockingQueue<SendTask> sendQueue;
	
	private ExecutorService threadpool = Executors.newFixedThreadPool(maxThreads);
	
	/**
	 * Constructs a Sender on on a specified port
	 * @param port Port to send requests to
	 */
	public Sender(int port){
		this.port = port;
		this.activeConnections = new ArrayList<InetAddress>();
		this.sendQueue = new LinkedBlockingQueue<SendTask>();
	}
	
	/**
	 * Method to send a Serializable Object o over network to InetAddress destination
	 * Queues the Object for sending, Sender.run() will try to clear queue
	 * @param o Object to be sent. Must be serializable
	 * @param dest Destination address for Object to be sent to
	 */
	public void send(Object o, InetAddress dest){
		//Create new SendTask
		SendTask send = new SendTask(o,dest);
		try {
			this.sendQueue.put(send);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to shutdown an active Sender thread.
	 * Waits for any tasks currently executing to finish before stopping
	 */
	public synchronized void shutdown(){
		//initiate shutdown of threadpool
		this.threadpool.shutdown();
		//wait for all connections to be closed
		while(!this.threadpool.isTerminated());
		//Interrupt the Sender thread once threadpool is terminated
		if(this.isAlive())
		this.interrupt();

	}
	
	@Override
	public void run() {
		
		//Run as long as the Thread is uninterrupted
		while(!isInterrupted()){
			
			//Peek at the sendQueue to see if anything is there
			if(this.sendQueue.peek()!=null){
				
				SendTask send = null;
				
				try {
					//Read from the queue
					send = this.sendQueue.take();
				} catch (InterruptedException e) {
					//Interrupted while waiting for new send task
					e.printStackTrace();
				}
				
				if(send!=null){
					//Check that we aren't already connected to host address
					if(!this.getActiveConnections().contains(send.address)){
						//Add the address to list of active connections
						this.getActiveConnections().add(send.address);
						//Start the task
						this.threadpool.execute(send);
					}
					//Otherwise, requeue the task for later sending
					else{
						try {
							this.sendQueue.put(send);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
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
	
	
	/*
	 * END - Synchronized private methods
	 */
	
	
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
