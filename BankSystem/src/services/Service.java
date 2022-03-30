package services;

import main.Client;
import main.ConsoleLogger;
import message.Marshall;
import message.Unmarshall;
import transmission.NormalTransmission;
import transmission.Socket;
import transmission.WrapperSocket;
import message.OneByteInt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

// Abstract class for child classes to inherit from
public abstract class Service {

	private final Unmarshall unpacker;
	protected static final String SERVICE_ID = "serviceId";
	protected static final String MESSAGE_ID = "messageId";
	protected static final String STATUS = "status";
	protected static final String REPLY = "reply";

	protected Service(Unmarshall unpacker) {
		this.unpacker = new Unmarshall.Builder()
				.setType(STATUS, Unmarshall.TYPE.ONE_BYTE_INT)
				.setType(MESSAGE_ID, Unmarshall.TYPE.INTEGER)
				.setType(REPLY, Unmarshall.TYPE.STRING)
				.build()
				.defineComponents(unpacker);
	}

	// Check message ID
	public final boolean checkMsgId(Integer message_id, Unmarshall.UnpackedMsg unmarshaledMsg) {
		Integer return_message_id = unmarshaledMsg.getInteger(MESSAGE_ID);
		ConsoleLogger.debug("return_message_id: " + return_message_id);
		ConsoleLogger.debug("message_id: " + message_id);
		if (return_message_id != null) {
			return message_id == return_message_id;
		}
		return false;
	}

	// Check status in reply message
	public final boolean checkStatus(Unmarshall.UnpackedMsg unmarshaledMsg) {
		OneByteInt status = unmarshaledMsg.getOneByteInt(STATUS);
		ConsoleLogger.debug("Status: " + status.getValue());
		if (status.getValue() == 0)
			return true; // 0 means no error? okay.
		return false;
	}

	// Reply and request message ID must match
	public final Unmarshall.UnpackedMsg receivalProcedure(Client client, Marshall packer, int message_id)
			throws IOException {
		while (true) {
			try {
				DatagramPacket reply = client.receive();
				Unmarshall.UnpackedMsg unmarshaledMsg = this.getUnpacker().parseByteArray(reply.getData());
				if (checkMsgId(message_id, unmarshaledMsg)) {
					return unmarshaledMsg;
				}
			} catch (SocketTimeoutException e) {
				ConsoleLogger.debug("Socket timeout.");
				client.send(packer);
			}
		}
	}

	public Unmarshall getUnpacker() {
		return unpacker;
	}

	public final boolean checkStatus(Unmarshall.UnpackedMsg unmarshaledMsg, int replyStatus) {
		OneByteInt status = unmarshaledMsg.getOneByteInt(STATUS);
		if (status.getValue() == replyStatus)
			return true;
		return false;
	}

	public abstract String ServiceName();

	public abstract void executeRequest(ConsoleLogger console, Client client) throws IOException;

}
