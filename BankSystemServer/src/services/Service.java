package services;

import java.io.IOException;
import java.net.InetAddress;

import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import socket.Socket;

// Abstract class for service to be extended by child classes
public abstract class Service {
	
	/*
	 * STATUS:
	 * 0 - success
	 * 1 - Fail
	 * 2 - Auto monitoring update
	 * 4 - Auto monitoring expired
	 * */
	private ByteUnpacker unpacker;
	protected static final String SERVICE_ID = "serviceId";
	private static final String MESSAGE_ID = "messageId";
	protected static final String STATUS = "status";
    protected static final String REPLY = "reply";
    
	public Service(ByteUnpacker unpacker){
		this.setUnpacker(new ByteUnpacker.Builder()
						.setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
						.setType(getMessageId(), ByteUnpacker.TYPE.INTEGER)
						.build()
						.defineComponents(unpacker));
	}
	
	public BytePacker generateReply(OneByteInt status, int messageId, String reply){
		BytePacker replyMessage = new BytePacker.Builder()
							.setProperty(STATUS, status)
							.setProperty(getMessageId(), messageId)
							.setProperty(REPLY, reply)
							.build();
		return replyMessage;
	}
	public static String getStatus(){
		return STATUS;
	}
	
	public abstract BytePacker handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient, Socket socket) throws IOException, NullPointerException;
	public abstract String ServiceName();

	public ByteUnpacker getUnpacker() {
		return unpacker;
	}

	public void setUnpacker(ByteUnpacker unpacker) {
		this.unpacker = unpacker;
	}

	public static String getMessageId() {
		return MESSAGE_ID;
	}
}
