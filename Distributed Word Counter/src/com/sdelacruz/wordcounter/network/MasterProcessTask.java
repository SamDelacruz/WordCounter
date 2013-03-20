package com.sdelacruz.wordcounter.network;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
		

	}
	
	//Method to send an Object to this Node's master
	private void sendToMaster(Object o){
		this.objectProcessor.getNetworkController().getSender(
				this.objectProcessor.getNetworkController().getNetworkModel().getMasterSendPort()
				).send(o, this.objectProcessor.getNetworkController().getNetworkModel().getMasterInet());
	}

}
