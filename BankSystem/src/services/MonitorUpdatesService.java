package services;

import main.Client;
import main.ConsoleLogger;
import message.Marshall;
import message.Unmarshall;
import message.OneByteInt;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

// Handle registering for callback
public class MonitorUpdatesService extends Service {

	protected final static String TIMEOUT = "timeout";

	public MonitorUpdatesService() {
		super(null);
	}

	@Override
	public void executeRequest(ConsoleLogger console, Client client) throws IOException {
		ConsoleLogger.println("======================== Register to Auto-Monitor ======================== ");
		int timeout = console.askForInteger("Enter your desired monitoring interval (in minutes):");
		int message_id = client.getMessage_id();
		Marshall packer = new Marshall.Builder()
				.setProperty(Service.SERVICE_ID, new OneByteInt(Client.REGISTER_CALLBACK))
				.setProperty(Service.MESSAGE_ID, message_id)
				.setProperty(TIMEOUT, timeout)
				.build();
		client.send(packer);

		// Wait for reply from server that says callback registered, then enter auto
		// monitoring state
		Unmarshall.UnpackedMsg unmarshaledMsg = receivalProcedure(client, packer, message_id);
		if (checkStatus(unmarshaledMsg)) {
			String reply = unmarshaledMsg.getString(Service.REPLY);
			ConsoleLogger.println(reply);

			while (true) {
				client.getmySocket().setTimeOut(0);
				Unmarshall.UnpackedMsg callbackMsg = callbackUpdatesHandler(client, message_id, super.getUnpacker());
				String callbackMsgReply = callbackMsg.getString(Service.REPLY);
				ConsoleLogger.println(callbackMsgReply);
				if (checkStatus(callbackMsg, 4)) {
					client.getmySocket().setTimeOut(client.getTimeout());
					break;
				}
			}
		}

	}

	public Unmarshall.UnpackedMsg callbackUpdatesHandler(Client client, int message_id, Unmarshall unpacker)
			throws IOException {
		while (true) {
			try {
				DatagramPacket reply = client.receive();
				Unmarshall.UnpackedMsg unmarshaledMsg = unpacker.parseByteArray(reply.getData());
				if (checkMsgId(message_id, unmarshaledMsg))
					return unmarshaledMsg;
			} catch (SocketTimeoutException e) {
				ConsoleLogger.debug("Socket timeout exception in callbackUpdates handler");

			}
		}

	}

	@Override
	public String ServiceName() {
		return "Register Callback";
	}

}
