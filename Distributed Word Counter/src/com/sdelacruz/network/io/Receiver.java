package com.sdelacruz.network.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class depicting a Receiver object. When executed, will take requests to receive Objects
 * via InputStream from a Sender, and store them in a Queue which can be polled to retrieve them externally
 * a matching port.
 * 
 * The class ensures that no more than one Object is sent to the same destination at any given time,
 * and queues any clashing requests to a current connection.
 * 
 * To use: create instance of Sender, to a given port. Start the thread with .start().
 * To send an Object to a particular InetAddress, call .send(Object o, InetAddress dest).
 * 
 * @author Sam Delacruz
 * @version 19-03-2013
 *
 */
public class Receiver extends Thread {
	
	private final int maxThreads = 10;
	private ServerSocket listen;
	private int port;

	private LinkedBlockingQueue<Object> receivedObjects;
	
	private ExecutorService threadpool = Executors.newFixedThreadPool(maxThreads);
	
	/**
	 * Constructs a Receiver on on a specified port
	 * @param port Port to listen for new requests on
	 */
	public Receiver(int port){
		this.port = port;
		try {
			listen = new ServerSocket(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns an Object from a queue of received objects
	 * @return a received Object
	 */
	public Object take(){
		Object o = null;
		try {
			o = receivedObjects.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	/**
	 * Method to shutdown an active Receiver thread.
	 * Waits for any tasks currently executing to finish before stopping
	 */
	public synchronized void shutdown(){
		//initiate shutdown of threadpool
		this.threadpool.shutdown();
		//wait for all connections to be closed
		while(!this.threadpool.isTerminated());
		//Interrupt the Receiver thread once threadpool is terminated
		if(this.isAlive())
		this.interrupt();
		
	}
	
	public void run(){
		
		while(!isInterrupted()){
			try {
				//Listen for a new connection
				Socket s = listen.accept();
				this.threadpool.execute(new ReceiveTask(s));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Inner class depicting each individual receive operation.
	 * Given a Socket, an instance of this class will attempt to read an object from its inputstream
	 * @author Sam Delacruz
	 * @version 18-03-2013
	 */
	private class ReceiveTask implements Runnable{
		
		private Socket s;
		private ObjectInputStream input = null;

		private ReceiveTask(Socket s){
			this.s = s;
		}
		
		@Override
		public void run() {
			
			//Try to initialize inputstream
			try {
				this.input = new ObjectInputStream(s.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Try to read Serialized Object from stream
			try {
				Object o = input.readObject();
				//Try to add read object to queue of received objects held by Receiver
				try {
					Receiver.this.receivedObjects.put(o);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				input.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
}
