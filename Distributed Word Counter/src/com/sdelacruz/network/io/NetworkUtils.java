package com.sdelacruz.network.io;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkUtils {

	/**
	 * Method to retrieve current InetAddress of current machine
	 * @return InetAddress of this machine
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static InetAddress getInetAddress() throws SocketException, UnknownHostException {

		//Get the name of operating system
	    String os = System.getProperty("os.name").toLowerCase();

	    //Is this machine running Unix or Linux?
	    if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {  
	    	//Try to get eth0 interface
	        NetworkInterface ni = NetworkInterface.getByName("eth0");
	        //Get the InetAddress(es) - could have IPv4 and/or IPv6 address
	        Enumeration<InetAddress> ias = ni.getInetAddresses();
	        
	        InetAddress iaddress;
	        do {
	        	//Only want IPv4, so iterate until we find it
	            iaddress = ias.nextElement();
	        } while(!(iaddress instanceof Inet4Address));

	        return iaddress;
	    }
	    // This is for Windows and MacOS, which don't encounter the same issues as Linux/Unix with .getLocalHost() returning 127.0.1.1
	    return InetAddress.getLocalHost();  
	}
	
}
