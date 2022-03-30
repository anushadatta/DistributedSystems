package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import message.BytePacker;

// Abstract Socket class for child classes to extend
public interface Socket {
	void send(BytePacker msg ,InetAddress address, int port) throws IOException;
	void receive(DatagramPacket p) throws IOException;
	void close();
	void setTimeOut(int timeout) throws SocketException;
}
