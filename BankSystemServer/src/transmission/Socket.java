package transmission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import message.Marshall;

// Abstract Socket class for child classes to extend
public interface Socket {
	void send(Marshall msg, InetAddress address, int port) throws IOException;

	void receive(DatagramPacket p) throws IOException;

	void close();

	void setTimeOut(int timeout) throws SocketException;
}
