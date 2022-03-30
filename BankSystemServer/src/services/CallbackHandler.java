package services;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.net.InetAddress;
import java.util.ArrayList;
import java.io.IOException;
import main.ConsoleLogger;
import message.Marshall;
import message.OneByteInt;
import transmission.Socket;

// Class that handles the callback service. 
// A list of subscribers is kept and a message is sent to each of them when the broadcast method is called 

public class CallbackHandler implements Runnable {
	private Socket mySocket;
	private static ArrayList<Subscriber> allSubscribers;

	// designated socket - socked assigned to send and receive messages
	public CallbackHandler(Socket mySocket) {
		this.mySocket = mySocket;
		allSubscribers = new ArrayList<>();

	}

	// Check validity of subscribers every 10s on separate thread.
	@Override
	public void run() {
		while (true) {
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

	// To register a subscriber - use address, port no., message ID and monitoring
	// interval (timeout)
	public void registerSubscriber(InetAddress address, int portNumber, int messageId, int timeout) {
		Subscriber subscriber = new Subscriber(address, portNumber, messageId, timeout);

		// Check if client has already subscribed to the service
		if (checkExisting(address, portNumber, messageId, timeout)) {
			allSubscribers.add(subscriber);
			ConsoleLogger.debug("New subscriber added!");
			subscriber.printSubscriberInfo();
		} else {
			ConsoleLogger.debug("Client already exists in list of subscribers.");
		}

	}

	// Checks if subscriber already exists in list of subscribers.
	public boolean checkExisting(InetAddress address, int portNumber, int messageId, int timeout) {
		boolean DoesNotExists = true;
		for (Subscriber s : allSubscribers) {
			s.printSubscriberInfo();
			if (s.address.equals(address) && s.portNumber == portNumber && s.messageId == messageId) {
				DoesNotExists = false;
				break;
			}
		}
		return DoesNotExists;
	}

	// Store information of clients who have subscribed
	public static class Subscriber {
		private InetAddress address;
		private int portNumber;
		private Calendar expireTime;
		private int messageId;

		public Subscriber(InetAddress address, int portNumber, int messageId, int timeLimit) {
			this.address = address;
			this.portNumber = portNumber;
			this.messageId = messageId;

			// Calculate remaining time to remove subscriber
			expireTime = Calendar.getInstance();
			expireTime.add(Calendar.MINUTE, timeLimit);
			ConsoleLogger.debug("Time left for expiration of monitoring: " + expireTime.getTime());
		}

		public void printSubscriberInfo() {
			ConsoleLogger.debug("Address: " + address.toString() + ", portNumber: " + portNumber + ", messageId: "
					+ messageId + ", expireTime: " + expireTime.getTime());
		}
	}

	// Send updates to non-expired subscribers
	public void broadcast(Marshall msg) {
		try {
			checkValidity();
			if (((OneByteInt) msg.getPropToValue().get(Service.getStatus())).getValue() == 0) { // If reply status is 0
																								// => then broadcast
																								// out.
				if (!allSubscribers.isEmpty()) {
					ConsoleLogger.debug("Send updates to subscribers.");
					for (Subscriber s : allSubscribers) {
						msg.getPropToValue().put(Service.getMessageId(), s.messageId);
						mySocket.send(msg, s.address, s.portNumber);
					}
				}

			}
		} catch (IOException e) {
			System.out.println("An error has occurred while broadcasting");
			e.printStackTrace();
		}

	}

	// Check which subscriber's interval has terminated
	// A termination message is sent to the user before removal from the list
	public void checkValidity() throws IOException {
		Date now = new GregorianCalendar().getTime();
		ArrayList<Subscriber> temp = new ArrayList<>();
		for (Subscriber s : allSubscribers) {
			if (now.after(s.expireTime.getTime())) {
				ConsoleLogger.debug("Removing subscriber.");
				s.printSubscriberInfo();
				OneByteInt status = new OneByteInt(4);
				sendTerminationMessage(s, status); // Before removing, a termination message is sent
				temp.add(s);
			}
		}
		for (Subscriber x : temp) {
			if (allSubscribers.contains(x)) {
				allSubscribers.remove(x);
			}
		}
	}

	// Send a message to subscriber stating that the monitoring interval has ended
	public void sendTerminationMessage(Subscriber s, OneByteInt status) throws IOException {
		ConsoleLogger.debug("Sending termination message");
		String reply = "Auto-monitoring expired. Please subscribe again if you wish to extend the period";
		Marshall replyMessage = new Marshall.Builder()
				.setProperty(Service.STATUS, status)
				.setProperty(Service.getMessageId(), s.messageId)
				.setProperty(Service.REPLY, reply)
				.build();
		mySocket.send(replyMessage, s.address, s.portNumber);
	}

}
