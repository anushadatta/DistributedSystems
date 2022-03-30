package main;

import message.Marshall;
import services.Service;
import transmission.NormalTransmission;
import transmission.ReceivingLossTransmission;
import transmission.SendingLossTransmission;
import transmission.Socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

// Client Class

public class Client {

	// Use Service ID to map to service when message is transmitted
	public static final int CREATE_ACCOUNT = 1;
	public static final int CLOSE_ACCOUNT = 2;
	public static final int UPDATE_BALANCE = 3;
	public static final int REGISTER_CALLBACK = 4;
	public static final int CHECK_BALANCE = 5;
	public static final int TRANSFER_BALANCE = 6;
	public static final int BUFFER_SIZE = 2048;

	private Socket mySocket = null;
	private HashMap<Integer, Service> idToServiceMap; // Mapping between serviceID and associated service
	private String serverIpAddress;
	private int serverPortNumber;
	private InetAddress InetIpAddress = null;
	private int message_id;
	private int timeout;
	private byte[] buffer = new byte[BUFFER_SIZE]; // buffer

	public Client(String ipAddress, int portNumber, int timeout) throws UnknownHostException, SocketException {
		this.idToServiceMap = new HashMap<>();
		this.serverIpAddress = ipAddress;
		this.InetIpAddress = InetAddress.getByName(ipAddress);
		this.mySocket = new NormalTransmission(new DatagramSocket());
		this.serverPortNumber = portNumber;
		this.timeout = timeout;
		this.mySocket.setTimeOut(timeout);
		this.message_id = 0;
	}

	public int getMessage_id() {
		return message_id++;
	}

	public void setMessage_id(int message_id) {
		this.message_id = message_id;
	}

	public void send(Marshall packer) throws IOException {
		this.mySocket.send(packer, this.InetIpAddress, this.serverPortNumber);
	}

	public DatagramPacket receive() throws IOException {
		Arrays.fill(buffer, (byte) 0);
		DatagramPacket p = new DatagramPacket(buffer, buffer.length);
		this.mySocket.receive(p);
		return p;
	}

	public void addService(int serviceId, Service service) {
		idToServiceMap.put(serviceId, service);
	}

	public void execute(int id, ConsoleLogger console) throws IOException {
		if (idToServiceMap.containsKey(id)) {
			Service service = this.idToServiceMap.get(id);
			service.executeRequest(console, this);
		}
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void printMenu() {
		for (Integer serviceId : idToServiceMap.keySet()) {
			ConsoleLogger.println(String.format("%d. %s", serviceId, idToServiceMap.get(serviceId).ServiceName()));
		}
	}

	public Socket getmySocket() {
		return this.mySocket;
	}

	public void useReceivingLossSocket(double socketProbability) {
		this.mySocket = new ReceivingLossTransmission(this.mySocket, socketProbability);
	}

	public void useSendingLossSocket(double socketProbability) {
		this.mySocket = new SendingLossTransmission(this.mySocket, socketProbability);
	}

}
