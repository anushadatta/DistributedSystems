package transmission;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import message.Marshall;
import java.io.IOException;

// Implements methods of socket class
public class WrapperSocket implements Socket {
	private final Socket socket;

	public WrapperSocket(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	@Override
	public void send(Marshall msg, InetAddress address, int port) throws IOException {

		this.socket.send(msg, address, port);

	}

	@Override
	public void receive(DatagramPacket p) throws IOException {
		this.socket.receive(p);
	}

	@Override
	public void setTimeOut(int timeout) throws SocketException {
		this.socket.setTimeOut(timeout);

	}

	@Override
	public void close() {
		this.socket.close();

	}
}
