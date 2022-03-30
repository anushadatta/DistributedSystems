package services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

import main.Client;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;

// Handle registering for callback
public class RegisterCallbackService extends Service{
	
	protected final static String TIMEOUT = "timeout";
	public RegisterCallbackService(){
		super(null);
	}

	@Override
	public void executeRequest(Console console, Client client) throws IOException {
		Console.println("======================== Register to Auto-Monitor ======================== ");
		int timeout = console.askForInteger("Enter your desired monitoring interval (in minutes):");
		int message_id = client.getMessage_id();	
		BytePacker packer = new BytePacker.Builder()
								.setProperty(Service.SERVICE_ID, new OneByteInt(Client.REGISTER_CALLBACK))
								.setProperty(Service.MESSAGE_ID, message_id)
								.setProperty(TIMEOUT, timeout)
								.build();
		client.send(packer);
		
		// Wait for reply from server that says callback registered, then enter auto monitoring state
		ByteUnpacker.UnpackedMsg unpackedMsg = receivalProcedure(client, packer, message_id);
		if(checkStatus(unpackedMsg)){
			String reply = unpackedMsg.getString(Service.REPLY);
			Console.println(reply);

			while(true){
				client.getDesignatedSocket().setTimeOut(0);
				ByteUnpacker.UnpackedMsg callbackMsg = callbackUpdatesHandler(client, message_id, super.getUnpacker());
				String callbackMsgReply = callbackMsg.getString(Service.REPLY);
				Console.println(callbackMsgReply);
				if(checkStatus(callbackMsg,4)){
					client.getDesignatedSocket().setTimeOut(client.getTimeout());
					break;
				}
			}
		}
	
	}
	public ByteUnpacker.UnpackedMsg callbackUpdatesHandler(Client client, int message_id, ByteUnpacker unpacker) throws IOException{
		while(true){
			try{
				DatagramPacket reply = client.receive();
				ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(reply.getData());
				if(checkMsgId(message_id,unpackedMsg)) return unpackedMsg;
			}catch (SocketTimeoutException e){
				Console.debug("Socket timeout exception in callbackUpdates handler");
				
			}
		}
	
	}
	
	@Override
	public String ServiceName() {
		return "Register Callback";
	}
	
	
	

}
