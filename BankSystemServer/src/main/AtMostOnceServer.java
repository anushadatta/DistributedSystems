package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import main.History.Client;
import message.BytePacker;
import message.ByteUnpacker;
import services.Service;
import socket.Socket;

// At-Most-Once Invocation Semantics
public class AtMostOnceServer extends Server {
	
	private History history;
	
	public AtMostOnceServer(Socket socket) throws SocketException {
		super(socket);
		history = new History();
	}
	
	// Server to begin listening to incoming requests
	public void start(){
		while(true){
			try{
				DatagramPacket p = receive(); 						// datagram packet to receive requests 
				if(p.getLength()!=0){
					byte[] data = p.getData();
					InetAddress clientAddress = p.getAddress();
					int clientPortNumber = p.getPort();
					int serviceRequested = data[0];					// serviceID is first byte in packet
					Service service = null;
					if(idToServiceMap.containsKey(serviceRequested)){
						service = idToServiceMap.get(serviceRequested);
						ByteUnpacker.UnpackedMsg unpackedMsg = service.getUnpacker().parseByteArray(data);
						int messageId = unpackedMsg.getInteger(Service.getMessageId());
						Client client = history.findClient(clientAddress, clientPortNumber);
						BytePacker replyToServicedReq = client.searchForDuplicateRequest(messageId);
						if(replyToServicedReq == null){
							replyToServicedReq = service.handleService(clientAddress, clientPortNumber, data, this.designatedSocket);
							client.addServicedReqToMap(messageId, replyToServicedReq);
						}
						this.designatedSocket.send(replyToServicedReq, clientAddress, clientPortNumber);
					}	
				}
			}catch(IOException e){
				e.printStackTrace();
			}catch(NullPointerException e){
				e.printStackTrace();
			}finally{
				continue;
			}
		}
	}
	

	

}
