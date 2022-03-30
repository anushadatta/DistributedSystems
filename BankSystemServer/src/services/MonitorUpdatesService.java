package services;

import java.net.InetAddress;
import message.Marshall;
import message.Unmarshall;
import message.OneByteInt;
import services.CallbackHandler.Subscriber;
import transmission.Socket;

// Handle subscriber requests
public class MonitorUpdatesService extends Service {

	private CallbackHandler callbackHandler;
	protected final static String TIMEOUT = "timeout";

	public MonitorUpdatesService(CallbackHandler callbackHandler) {
		super(new Unmarshall.Builder()
				.setType(TIMEOUT, Unmarshall.TYPE.INTEGER)
				.build());
		this.callbackHandler = callbackHandler;

	}

	@Override
	public Marshall handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {
		Unmarshall.UnpackedMsg unmarshaledMsg = this.getUnpacker().parseByteArray(dataFromClient);
		int messageId = unmarshaledMsg.getInteger(Service.getMessageId());
		int timeout = unmarshaledMsg.getInteger(TIMEOUT);
		callbackHandler.registerSubscriber(clientAddress, clientPortNumber, messageId, timeout);
		OneByteInt status = new OneByteInt(0);
		String reply = "Auto-monitoring registered. Waiting for updates...";
		Marshall replyMessage = super.generateReply(status, messageId, reply);
		return replyMessage;

	}

	@Override
	public String ServiceName() {
		return "Register Callback";
	}

}
