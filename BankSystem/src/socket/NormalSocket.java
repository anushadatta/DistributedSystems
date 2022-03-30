package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import main.Console;
import message.BytePacker;

// Normal socket 
public class NormalSocket implements Socket {
	
	private DatagramSocket socket;
	
	public NormalSocket(DatagramSocket socket){
		this.socket = socket;
	}
	
	@Override
	public void send(BytePacker msg, InetAddress address, int port) throws IOException {
		Console.debug("Sending Message...");
		byte[] message = msg.getByteArray();
		DatagramPacket p = new DatagramPacket(message, message.length,address, port);
		send(p);
		return;
	}

	@Override
	public void receive (DatagramPacket p) throws IOException {
		this.socket.receive(p);
		Console.debug("Receiving Message from Server...");
		return;
	}

	@Override
	public void close() {
		Console.debug("Closing Socket...");
		this.socket.close();
		return;
	}

	@Override
	public void setTimeOut(int timeout) throws SocketException {
		this.socket.setSoTimeout(timeout);
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}
	
	public void send(DatagramPacket p) throws IOException{
		this.socket.send(p);
	}
	
	
}
