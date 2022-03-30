package services;

import java.net.InetAddress;

import message.Marshall;
import message.Unmarshall;
import transmission.Socket;
import message.OneByteInt;
import java.io.IOException;

// Abstract class for service to be extended by child classes
public abstract class Service {

	/*
	 * STATUS:
	 * 0 - success
	 * 1 - Fail
	 * 2 - Auto monitoring update
	 * 4 - Auto monitoring expired
	 */
	private Unmarshall unpacker;
	protected static final String SERVICE_ID = "serviceId";
	private static final String MESSAGE_ID = "messageId";
	protected static final String STATUS = "status";
	protected static final String REPLY = "reply";

	public Service(Unmarshall unpacker) {
		this.setUnpacker(new Unmarshall.Builder()
				.setType(SERVICE_ID, Unmarshall.TYPE.ONE_BYTE_INT)
				.setType(getMessageId(), Unmarshall.TYPE.INTEGER)
				.build()
				.defineComponents(unpacker));
	}

	public Marshall generateReply(OneByteInt status, int messageId, String reply) {
		Marshall replyMessage = new Marshall.Builder()
				.setProperty(STATUS, status)
				.setProperty(getMessageId(), messageId)
				.setProperty(REPLY, reply)
				.build();
		return replyMessage;
	}

	public abstract Marshall handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) throws IOException, NullPointerException;

	public abstract String ServiceName();

	public static String getStatus() {
		return STATUS;
	}

	public static String getMessageId() {
		return MESSAGE_ID;
	}

	public Unmarshall getUnpacker() {
		return unpacker;
	}

	public void setUnpacker(Unmarshall unpacker) {
		this.unpacker = unpacker;
	}
}
