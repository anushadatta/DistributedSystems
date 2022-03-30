package main;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import message.BytePacker;


// At-most once semantics - store history of clients
public class History {
	private ArrayList<Client> clientList;
	public static final int HISTORY_RECORD_SIZE = 10;

	public History(){
		clientList = new ArrayList<>();
	}
	
	// Checks if client exists in list, else a new client is created and inserted into list
	public Client findClient(InetAddress address, int port){
		for(Client c : clientList){
			if(c.address.equals(address) && c.portNumber==port){
				return c;
			}
		}
		Client newClient = new Client(address, port);
		clientList.add(newClient);
		return newClient;
	}
	
	// Record of each client that has sent a request to the server earlier 
	public class Client{
		private InetAddress address;
		private int portNumber;
		private HashMap<Integer, BytePacker> messageIdToReplyMap;
		private int[] historyRecord;
		private int count;
		public Client(InetAddress address, int portNumber){
			this.address = address;
			this.portNumber = portNumber;
			this.messageIdToReplyMap = new HashMap<>();
			historyRecord = new int[HISTORY_RECORD_SIZE]; 
			count = 0;
			Arrays.fill(historyRecord, -1);
			
		}
		
		// Searches if messageID exists in client hashmap
		public BytePacker searchForDuplicateRequest(int messageId){
			BytePacker reply = this.messageIdToReplyMap.get(messageId);
			if(reply!=null){
				Console.debug("Request already serviced. Resending reply");
			}
			return reply;
		}
		
		// After request is serviced, adds messageID and reply to hashmap 
		public void addServicedReqToMap(int messageId, BytePacker replyToServicedReq) {
			if(historyRecord[count] !=-1){
				messageIdToReplyMap.remove(historyRecord[count]);
			}
			this.messageIdToReplyMap.put(messageId, replyToServicedReq);
			historyRecord[count] = messageId; 
			count = (count + 1) % HISTORY_RECORD_SIZE;
			
		}
	}


	
}
