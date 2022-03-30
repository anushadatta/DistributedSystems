package main;

import main.History.Client;
import services.Service;
import transmission.Socket;
import message.Marshall;
import message.Unmarshall;

import java.io.IOException;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

// At-Most-Once Invocation Semantics
public class AtMostOnceServer extends Server {

	private History history;

	public AtMostOnceServer(Socket socket) throws SocketException {
		super(socket);
		history = new History();
	}

	// Server to begin listening to incoming requests
	public void start() {
		while (true) {
			try {
				DatagramPacket p = receive(); // datagram packet to receive requests
				if (p.getLength() != 0) {
					byte[] data = p.getData();
					InetAddress clientAddress = p.getAddress();
					int clientPortNumber = p.getPort();
					int serviceRequested = data[0]; // serviceID is first byte in packet
					Service service = null;
					if (idToServiceMap.containsKey(serviceRequested)) {
						service = idToServiceMap.get(serviceRequested);
						Unmarshall.UnpackedMsg unmarshaledMsg = service.getUnpacker().parseByteArray(data);
						int messageId = unmarshaledMsg.getInteger(Service.getMessageId());
						Client client = history.findClient(clientAddress, clientPortNumber);
						Marshall replyToServicedReq = client.searchForDuplicateRequest(messageId);
						if (replyToServicedReq == null) {
							replyToServicedReq = service.handleService(clientAddress, clientPortNumber, data,
									this.mySocket);
							client.addServicedReqToMap(messageId, replyToServicedReq);
						}
						this.mySocket.send(replyToServicedReq, clientAddress, clientPortNumber);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} finally {
				continue;
			}
		}
	}

}
