package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import main.Console;
import message.BytePacker;

// Normal socket with no packet loss
public class NormalSocket implements Socket {
	
	private DatagramSocket socket;
	
	public NormalSocket(DatagramSocket socket){
		this.socket = socket;
	}
	
	@Override
	public void send(BytePacker msg, InetAddress address, int port) throws IOException {
		Console.debug("Address: "+ address + ", Port No.: " + port);
		byte[] message = msg.getByteArray();
		DatagramPacket p = new DatagramPacket(message, message.length,address, port);
		send(p);
		return;
	}

	@Override
	public void receive (DatagramPacket p) throws IOException {
		this.socket.receive(p);
		return;
	}

	@Override
	public void close() {
		this.socket.close();
		return;
	}

	@Override
	public void setTimeOut(int timeout) throws SocketException {
		this.socket.setSoTimeout(timeout);
		return;
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
