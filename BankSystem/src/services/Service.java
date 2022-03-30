package services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import main.Client;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import socket.NormalSocket;
import socket.Socket;
import socket.WrapperSocket;

// Abstract class for child classes to inherit from
public abstract class Service {
	
	private final ByteUnpacker unpacker;
	protected static final String STATUS = "status";
	protected static final String SERVICE_ID = "serviceId";
    protected static final String MESSAGE_ID = "messageId";
    protected static final String REPLY = "reply";

	protected Service(ByteUnpacker unpacker){
		this.unpacker = new ByteUnpacker.Builder()
						.setType(STATUS, ByteUnpacker.TYPE.ONE_BYTE_INT)
						.setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
						.setType(REPLY, ByteUnpacker.TYPE.STRING)
						.build()
						.defineComponents(unpacker);
	}
	
	// Reply and request message ID must match
	public final ByteUnpacker.UnpackedMsg receivalProcedure(Client client, BytePacker packer, int message_id ) throws IOException{
		while(true){
			try{
				DatagramPacket reply = client.receive();
				ByteUnpacker.UnpackedMsg unpackedMsg = this.getUnpacker().parseByteArray(reply.getData());
				if(checkMsgId(message_id,unpackedMsg)){
					return unpackedMsg;
				}		
			}catch (SocketTimeoutException e){
				Console.debug("Socket timeout.");
				client.send(packer);
			}
		}
	}
	
	// Check message ID
	public final boolean checkMsgId(Integer message_id, ByteUnpacker.UnpackedMsg unpackedMsg){
		Integer return_message_id = unpackedMsg.getInteger(MESSAGE_ID);
		Console.debug("return_message_id: " + return_message_id);
		Console.debug("message_id: " + message_id);
		if(return_message_id != null){
			return message_id == return_message_id;
		}
		return false;
	}

	// Check status in reply message
	public final boolean checkStatus(ByteUnpacker.UnpackedMsg unpackedMsg){
		OneByteInt status = unpackedMsg.getOneByteInt(STATUS);
		Console.debug("Status: " + status.getValue());
		if(status.getValue()==0)return true; //0 means no error? okay. 
		return false;
	}
	
	public final boolean checkStatus(ByteUnpacker.UnpackedMsg unpackedMsg, int replyStatus){
		OneByteInt status = unpackedMsg.getOneByteInt(STATUS);
		if(status.getValue()==replyStatus)return true; 
		return false;
	}
	
	
	public ByteUnpacker getUnpacker() {
		return unpacker;
	}
	
	public abstract void executeRequest(Console console, Client client) throws IOException;
	public abstract String ServiceName(); 
	
}
