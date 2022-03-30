package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

import message.Marshall;
import services.CallbackHandler;
import services.Service;
import transmission.NormalTransmission;
import transmission.ReceivingLossTransmission;
import transmission.SendingLossTransmission;
import transmission.Socket;

// At-Least Once Semantics - Server
public class Server {
	protected HashMap<Integer, Service> idToServiceMap;
	protected Socket mySocket;
	protected int portNumber;
	protected String ipAddress;
	protected final int bufferSize = 2048;
	protected byte[] buffer;

	public Server(Socket socket) throws SocketException {
		this.idToServiceMap = new HashMap<>();
		this.mySocket = socket;
		this.buffer = new byte[bufferSize];

	}

	// Add service
	public void addServiceToServer(int id, Service service) {
		if (!this.idToServiceMap.containsKey(id)) {
			this.idToServiceMap.put(id, service);
			System.out.println("Service Added!");
		} else {
			System.out.printf("There is no existing service using service id %d, please use a different id.\n", id);
		}
	}

	// Listen for incoming messages
	public DatagramPacket receive() throws IOException {
		Arrays.fill(buffer, (byte) 0); // empty buffer
		DatagramPacket p = new DatagramPacket(buffer, buffer.length);
		// System.out.println("Waiting for request...");
		this.mySocket.receive(p);

		return p;
	}

	// Start listening for requests
	@SuppressWarnings("finally")
	public void start() {
		while (true) {
			try {
				DatagramPacket p = receive();
				if (p.getLength() != 0) {
					byte[] data = p.getData();
					InetAddress clientAddress = p.getAddress();
					int clientPortNumber = p.getPort();
					int serviceRequested = data[0]; // Service ID is the first byte
					Service service = null;
					if (idToServiceMap.containsKey(serviceRequested)) {
						service = idToServiceMap.get(serviceRequested);
						System.out.println("Service Requested: " + service.ServiceName());
						Marshall replyToRequest = service.handleService(clientAddress, clientPortNumber, data,
								this.mySocket);
						this.mySocket.send(replyToRequest, clientAddress, clientPortNumber);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();

			} catch (NullPointerException e) {
				ConsoleLogger.debug("Received corrupted data");
				e.printStackTrace();
			} finally {
				continue;
			}
		}
	}

	// Simulate packet loss when receiving
	public void useReceivingLossSocket(double socketProbability) {
		this.mySocket = new ReceivingLossTransmission(this.mySocket, socketProbability);
	}

	// Simulate packet loss when sending
	public void useSendingLossSocket(double socketProbability) {
		this.mySocket = new SendingLossTransmission(this.mySocket, socketProbability);
	}

}
