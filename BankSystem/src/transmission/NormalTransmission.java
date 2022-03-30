package transmission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import main.ConsoleLogger;
import message.Marshall;

// Normal socket 
public class NormalTransmission implements Socket {

	private DatagramSocket socket;

	public NormalTransmission(DatagramSocket socket) {
		this.socket = socket;
	}

	@Override
	public void send(Marshall msg, InetAddress address, int port) throws IOException {
		ConsoleLogger.debug("Sending Message...");
		byte[] message = msg.getByteArray();
		DatagramPacket p = new DatagramPacket(message, message.length, address, port);
		send(p);
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

	public void send(DatagramPacket p) throws IOException {
		this.socket.send(p);
	}

	@Override
	public void receive(DatagramPacket p) throws IOException {
		this.socket.receive(p);
		ConsoleLogger.debug("Receiving Message from Server...");
		return;
	}

	@Override
	public void close() {
		ConsoleLogger.debug("Closing Socket...");
		this.socket.close();
		return;
	}

}
