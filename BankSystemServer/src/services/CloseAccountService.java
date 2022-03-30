package services;

import java.io.IOException;
import java.net.InetAddress;

import bank.Bank;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import socket.Socket;

// Handle account closure
public class CloseAccountService extends Service {
	protected final static String NAME = "Name";
	protected final static String PIN = "Pin";
	protected final static String ACCNUM = "accNum";
	private CallbackHandlerClass callbackHandler;

	// Handle callback service for clients (the ones that subscribe to the service)
	public CloseAccountService(CallbackHandlerClass callbackHandler){
		super(new ByteUnpacker.Builder()
						.setType(NAME, ByteUnpacker.TYPE.STRING)
						.setType(ACCNUM, ByteUnpacker.TYPE.INTEGER)
						.setType(PIN, ByteUnpacker.TYPE.STRING)
						.build());
		this.callbackHandler = callbackHandler;						
	}
	
	@Override
	public BytePacker handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient, Socket socket) {
		String reply = "";
		ByteUnpacker.UnpackedMsg unpackedMsg = this.getUnpacker().parseByteArray(dataFromClient);
		String accHolderName = unpackedMsg.getString(NAME);
		int accNum = unpackedMsg.getInteger(ACCNUM);
		String accPin = unpackedMsg.getString(PIN);
		Console.debug("Account No.: " + accNum);
		int messageId = unpackedMsg.getInteger(super.getMessageId());
		int ret = Bank.closeAccount(accHolderName,accNum ,accPin);
		OneByteInt status = new OneByteInt(0); 
		if (ret == 1){
			reply = String.format("=====================\nAccount %d successfully deleted\n=====================", accNum);
			BytePacker replyMessageSubscribers = super.generateReply(status, messageId, reply);
			callbackHandler.broadcast(replyMessageSubscribers);
		}
		else if(ret==-1){
			 reply = String.format("Invalid Account No. Please try again.");
		}
		else if(ret == -2){
			reply = String.format("Invalid Password. Please try again");
		}
		else if (ret == -3) {
			reply = String.format("This account number is not under your name. Please try again");
		}
		
		BytePacker replyMessageClient = super.generateReply(status, messageId, reply);
		
		return replyMessageClient;
	}
	
	
	@Override
	public String ServiceName() {
		return "Close Account";
	}
}
