package com.upnp.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.upnp.core.Discover;

public class UdpClient extends Thread{
	
	private InetAddress ip;
	
	private String message;
	
	
	public UdpClient(InetAddress ip,String message){
		this.ip = ip;
		this.message = message;
	}
	
	private DatagramSocket socket = null;
	
	public DatagramSocket getConnect(){
		try {
			socket = new DatagramSocket(new InetSocketAddress(ip, 0));
		} catch (SocketException e) {			
			e.printStackTrace();
		}
		return socket;
	}
	
	public void sendSearchMessage(){
		
		socket = getConnect();
		
		byte[] buffer = message.getBytes();		
		try {
			if(socket != null){				
				DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
				packet.setAddress(InetAddress.getByName(Discover.IP));
				packet.setPort(Discover.PORT);
				socket.send(packet);
				socket.setSoTimeout(Discover.timeout);
				boolean isWaiting = true;
				while(isWaiting){
					DatagramPacket recieve = new DatagramPacket(new byte[1536], 1536);				
					try {
						socket.receive(recieve);
						byte[] recieveData = recieve.getData();
						String response = new String(recieveData);
						System.out.println(response);
					} catch (SocketTimeoutException e) {
						isWaiting = false;
					}
				}
			}		
		} catch (IOException e) {			
			e.printStackTrace();
		}finally{
			socket.close();
		}
		
		
	}
	
	@Override
	public void run() {
		sendSearchMessage();
	}

}
