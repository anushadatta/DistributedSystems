package transmission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import main.ConsoleLogger;
import message.Marshall;

// Normal socket with no packet loss
public class NormalTransmission implements Socket {

	private DatagramSocket socket;

	public NormalTransmission(DatagramSocket socket) {
		this.socket = socket;
	}

	@Override
	public void send(Marshall msg, InetAddress address, int port) throws IOException {
		ConsoleLogger.debug("Address: " + address + ", Port No.: " + port);
		byte[] message = msg.getByteArray();
		DatagramPacket p = new DatagramPacket(message, message.length, address, port);
		send(p);
		return;
	}

	@Override
	public void receive(DatagramPacket p) throws IOException {
		this.socket.receive(p);
		return;
	}

	@Override
	public void setTimeOut(int timeout) throws SocketException {
		this.socket.setSoTimeout(timeout);
		return;
	}

	@Override
	public void close() {
		this.socket.close();
		return;
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

}
