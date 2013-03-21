package com.sdelacruz.wordcounter.network;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import com.sdelacruz.network.io.NetworkUtils;
import com.sdelacruz.network.objectprocessing.ObjectProcessor;
import com.sdelacruz.network.objectprocessing.ProcessTask;

public class MasterProcessTask extends ProcessTask {

	protected MasterProcessTask(Object o, ObjectProcessor p) {
		super(o, p);
	}

	@Override
	protected void process(Object o) {

		//If an InetAddress received from Master
		if(o instanceof InetAddress){
			InetAddress receivedAddress = (InetAddress)o;
			//Check whether the received InetAddress is same as one stored as Master
			if(receivedAddress.equals(
					this.objectProcessor.getNetworkController().getNetworkModel().getMasterInet()
					)){
				
				try {
					//Send the Master this machine's InetAddress to confirm connection
					this.sendToMaster(NetworkUtils.getInetAddress());
					
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			//Address is different to stored Master address, because Master has enough workers
			else {
				//Set the stored master inet to the one received
				this.objectProcessor.getNetworkController().getNetworkModel().setMasterInet(receivedAddress);
				//Increment ports, so we can communicate with new master
				this.objectProcessor.getNetworkController().getNetworkModel().incrementPorts();
				try {
					//Send InetAddress to new master, see if we can become a worker for it.
					this.sendToMaster(NetworkUtils.getInetAddress());
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		} 
		//Received some words to count
		else if(o instanceof String[]){
			String[] words = (String[])o;
			List<InetAddress> idlers = this.objectProcessor.getNetworkController().getNetworkModel().getIdleWorkers();
			if(idlers.size()>0){
				Deque<String[]> toSend = splitWordsByWorkerCount(words, idlers);
				if(toSend.size()>idlers.size())
					this.objectProcessor.getNetworkController().getWordCounter().countWords(toSend.removeLast());
				sendWordsToWorkers(toSend, idlers);
			}
		}
		
		
		

	}
	
	private void sendWordsToWorkers(Queue<String[]> words, List<InetAddress> workers){
		for(InetAddress i : workers){
			this.objectProcessor.getNetworkController().getSender(
					this.objectProcessor.getNetworkController().getNetworkModel().getWorkerSendPort())
					.send(words.poll(), i);
		}
	}
	
	private Deque<String[]> splitWordsByWorkerCount(String[] words, List<InetAddress> workers){

		Deque<String[]> wordsplit = new LinkedBlockingDeque<String[]>();
		int start = 0;
		for(InetAddress i : workers){
			int nWords = this.objectProcessor.getNetworkController().getNetworkModel().getWorkerCount(i)
					* this.objectProcessor.getNetworkController().getNetworkModel().getMinWordSendUnit();
			String[] wordsunit = new String[nWords];
			int n;
			for(n = start;n<start+nWords&&n<words.length;n++){
				wordsunit[n] = words[n];
			}
			wordsplit.add(wordsunit);
			start = n;
		}
		int nWords = this.objectProcessor.getNetworkController().getNetworkModel().getMinWordSendUnit();
		String[] wordsplitEnd = new String[nWords];
		for(int n = start;n<words.length;n++){
			wordsplitEnd[n] = words[n];
		}
		wordsplit.add(wordsplitEnd);
		
		return wordsplit;
	}
	
	
	//Method to send an Object to this Node's master
	private void sendToMaster(Object o){
		this.objectProcessor.getNetworkController().getSender(
				this.objectProcessor.getNetworkController().getNetworkModel().getMasterSendPort()
				).send(o, this.objectProcessor.getNetworkController().getNetworkModel().getMasterInet());
	}

}
