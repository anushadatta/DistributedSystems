package services;


import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import main.Console;
import message.BytePacker;
import message.OneByteInt;
import socket.Socket;

// Class that handles the callback service. 
// A list of subscribers is kept and a message is sent to each of them when the broadcast method is called 
 
public class CallbackHandlerClass implements Runnable {
	private Socket designatedSocket;
	private static ArrayList<Subscriber> allTheSubscribers;
	
	// designated socket - socked assigned to send and receive messages 
	public CallbackHandlerClass(Socket designatedSocket){
		this.designatedSocket = designatedSocket;
		allTheSubscribers = new ArrayList<>();
		
	}
	
	// To register a subscriber - use address, port no., message ID and monitoring interval (timeout)
	public void registerSubscriber(InetAddress address, int portNumber, int messageId, int timeout){
		Subscriber subscriber = new Subscriber(address, portNumber, messageId, timeout);
		
		// Check if client has already subscribed to the service
		if(checkExisting(address,portNumber,messageId,timeout)){
			allTheSubscribers.add(subscriber); 
			Console.debug("New subscriber added!");
			subscriber.printSubscriberInfo();
		}
		else{
			Console.debug("Client already exists in list of subscribers.");
		}
		
	}
	
	// Checks if subscriber already exists in list of subscribers. 
	public boolean checkExisting(InetAddress address, int portNumber, int messageId, int timeout){
		boolean DoesNotExists = true; 
		for(Subscriber s: allTheSubscribers){
			s.printSubscriberInfo();
			if(s.address.equals(address) && s.portNumber==portNumber && s.messageId == messageId){
				DoesNotExists = false;
				break;
			}
		}
		return DoesNotExists;
	}
	
	// Check which subscriber's interval has terminated
	// A termination message is sent to the user before removal from the list
	public void checkValidity() throws IOException{
		Date now = new GregorianCalendar().getTime();
		ArrayList<Subscriber> temp = new ArrayList<>();
		for (Subscriber s: allTheSubscribers){
			if(now.after(s.expireTime.getTime())){
				Console.debug("Removing subscriber.");
				s.printSubscriberInfo();
				OneByteInt status = new OneByteInt(4);
				sendTerminationMessage(s,status);				// Before removing, a termination message is sent
				temp.add(s);
			}
		}
		for(Subscriber x: temp){
			if(allTheSubscribers.contains(x)){
				allTheSubscribers.remove(x);
			}
		}
	}
	
	// Send a message to subscriber stating that the monitoring interval has ended
	public void sendTerminationMessage(Subscriber s,OneByteInt status) throws IOException{
		Console.debug("Sending termination message");
		String reply = "Auto-monitoring expired. Please subscribe again if you wish to extend the period";
		BytePacker replyMessage = new BytePacker.Builder()
				.setProperty(Service.STATUS, status)
				.setProperty(Service.getMessageId(), s.messageId)
				.setProperty(Service.REPLY, reply)
				.build();
		designatedSocket.send(replyMessage, s.address, s.portNumber);
	}
	
	// Send updates to non-expired subscribers
	public void broadcast(BytePacker msg){
		try {
			checkValidity();
			if(((OneByteInt)msg.getPropToValue().get(Service.getStatus())).getValue()==0){ // If reply status is 0 => then broadcast out. 
				if(!allTheSubscribers.isEmpty()){
					Console.debug("Send updates to subscribers.");
					for (Subscriber s: allTheSubscribers){
						msg.getPropToValue().put(Service.getMessageId(), s.messageId); 
						designatedSocket.send(msg, s.address, s.portNumber);
					}
				}
				
			}
		} catch (IOException e) {
			System.out.println("An error has occurred while broadcasting");
			e.printStackTrace();
		}
		
		
	}
	
	// Check validity of subscribers every 10s on separate thread.
	@Override
	public void run() {
		while(true){
			try {
				checkValidity();
				Thread.sleep(10000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// Store information of clients who have subscribed
	public static class Subscriber{
		private InetAddress address;
		private int portNumber;
		private Calendar expireTime;
		private int messageId; 

		public Subscriber(InetAddress address, int portNumber, int messageId, int timeLimit){
			this.address = address;
			this.portNumber = portNumber;
			this.messageId = messageId;
			
			// Calculate remaining time to remove subscriber
			expireTime = Calendar.getInstance();
			expireTime.add(Calendar.MINUTE, timeLimit);
			Console.debug("Time left for expiration of monitoring: " + expireTime.getTime());
		}
		
		
		public void printSubscriberInfo(){
			Console.debug("Address: " + address.toString() + ", portNumber: " + portNumber + ", messageId: " + messageId + ", expireTime: " + expireTime.getTime());
		}
	}

	

	

}
