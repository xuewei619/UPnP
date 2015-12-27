package com.upnp.core;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.bitlet.weupnp.GatewayDevice;

import com.upnp.udp.UdpClient;

public class Discover {
	
	public static final String IP = "239.255.255.250";
	
	public static final int PORT = 1900;
	
	public static final int timeout = 3000;
	
	private static final String[] DEFAULT_SEARCH_TYPES =
		{
	            "urn:schemas-upnp-org:device:InternetGatewayDevice:1",
	            "urn:schemas-upnp-org:service:WANIPConnection:1",
	            "urn:schemas-upnp-org:service:WANPPPConnection:1"
		};
	 
	 private Map<InetAddress, GatewayDevice> test;
	
	public void discover(){
		
		for(int i = 0;i < DEFAULT_SEARCH_TYPES.length;i++){
			
			String searchMessage = "M-SEARCH * HTTP/1.1\r\n" +
	                "HOST: " + IP + ":" + PORT + "\r\n" +
	                "ST: \"" + DEFAULT_SEARCH_TYPES[i] + "\"\r\n" +
	                "MAN: \"ssdp:discover\"\r\n" +
	                "MX: 2\r\n" +   
	                "\r\n";	
			
			
			List<InetAddress> addresses = getIPv4Address();
			for(InetAddress address : addresses){
				UdpClient client = new UdpClient(address, searchMessage);
				client.start();
			}
			return;
//			if(){
//				
//			}
		}		
	}
	
	
	private List<InetAddress> getIPv4Address(){
		Enumeration<NetworkInterface> list = null;
		List<InetAddress> addressList = new ArrayList<InetAddress>();
		
		
		try {
			list = NetworkInterface.getNetworkInterfaces();

			if(list == null){
				return addressList;
			}
			while(list.hasMoreElements()){
				NetworkInterface net = list.nextElement();
				
				//System.out.println(net.getDisplayName());
				if(net.isLoopback() || net.isPointToPoint() || net.isVirtual() || !net.isUp()){
					continue;
				}
				Enumeration<InetAddress> addresses = net.getInetAddresses();				
				
				while(addresses.hasMoreElements()){
					InetAddress address = addresses.nextElement();
					
					if(address == null){
						continue;
					}
					
					if(!Inet4Address.class.isInstance(address)){
						continue;
					}			
					//System.out.println(address.getHostName());
					addressList.add(address);
				}
			}
		} catch (SocketException e) {			
			e.printStackTrace();
		}finally {
			//System.out.println(addressList.toString());
		}
		return addressList;
	}
	
	
	public Gateway parseResponse(byte[] recieveData){
		
		String responseStr = new String(recieveData);
		String[] strLines = responseStr.split("\r\n");
		for(String str : strLines){
			if(str.indexOf("HTTP/1.") > -1 || str.indexOf("NOTIFY *") > -1){
				continue;
			}
		}
		return null;
	}

}
