package com.sdelacruz.wordcounter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WordCounter extends Thread {

	private BlockingQueue<WordCounterTask> taskQueue;
	private BlockingQueue<Map<String,Integer>> completedCounts;
	private ExecutorService threadpool;
	private final int maxThreads = 10;
	
	public WordCounter(){
		this.taskQueue = new LinkedBlockingQueue<WordCounterTask>();
		this.completedCounts = new LinkedBlockingQueue<Map<String,Integer>>();
		this.threadpool = Executors.newFixedThreadPool(this.maxThreads);
	}
	
	/**
	 * Submit a String[] of words for counting
	 * @param words Words to be Counted
	 */
	public void countWords(String[] words){
		if(words!=null&&words.length>0){
			try {
				this.taskQueue.put(new WordCounterTask(words));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method to shutdown an active WordCounter thread.
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
	
	/**
	 * Poll for completed WordCount maps
	 * @return Completed WordCount
	 */
	public Map<String,Integer> pollCompletedCounts(){
		
		Map<String,Integer> toReturn = null;
		
		 try {
			toReturn = this.completedCounts.poll(500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return toReturn;
		
	}
	
	public void run() {
		
		//Run as long as the Thread is uninterrupted
		while(!isInterrupted()){
		
				WordCounterTask task = null;
				
				try {
					//Read from the queue
					task = this.taskQueue.poll(500,TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					//Interrupted while waiting for new send task
					e.printStackTrace();
				}
				
				if(task!=null){
					//Start the task
					this.threadpool.execute(task);
				}
			
		}

	}
	
	private class WordCounterTask implements Runnable{
		
		private String[] words;
		
		private WordCounterTask(String[] words){
			this.words = words;
		}

		@Override
		public void run() {
			
			if(words!=null&&words.length>0){
				Map<String,Integer> wordcount = new HashMap<String,Integer>();
				
				for(String word : this.words){
					Integer count = wordcount.get(word);
					if(count!=null){
						count += 1;
					}
					
					else{
						wordcount.put(word, 1);
					}
				}
				
				try {
					completedCounts.put(wordcount);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			
			
		}
		
	}
	
}
