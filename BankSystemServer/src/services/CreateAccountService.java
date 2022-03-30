package services;

import java.io.IOException;
import java.net.InetAddress;

import bank.Bank;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import socket.Socket;

// Handle account creation
public class CreateAccountService extends Service {
	protected final static String NAME = "Name";
	protected final static String PIN = "Pin";
	protected final static String CURRENCY = "Currency";
	protected final static String BALANCE = "Balance";
	private CallbackHandlerClass callbackHandler;

	// Handle callback service for clients (the ones that subscribe to the service)
	public CreateAccountService(CallbackHandlerClass callbackHandler){
		super(new ByteUnpacker.Builder()
						.setType(NAME, ByteUnpacker.TYPE.STRING)
						.setType(PIN, ByteUnpacker.TYPE.STRING)
						.setType(CURRENCY, ByteUnpacker.TYPE.STRING)
						.setType(BALANCE, ByteUnpacker.TYPE.DOUBLE)
						.build());
		this.callbackHandler = callbackHandler;			
	}
	
	@Override
	public BytePacker handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient, Socket socket) {

		ByteUnpacker.UnpackedMsg unpackedMsg = this.getUnpacker().parseByteArray(dataFromClient);
		String accHolderName = unpackedMsg.getString(NAME);
		String accPin = unpackedMsg.getString(PIN);
		String accCurrency = unpackedMsg.getString(CURRENCY);
		double accBalance = unpackedMsg.getDouble(BALANCE);
		int messageId = unpackedMsg.getInteger(super.getMessageId());
		int accNum = Bank.createAccount(accHolderName, accPin, accCurrency, accBalance);
		
		OneByteInt status = new OneByteInt(0); 
		String reply = String.format("Successful Account Creation ================== \n Account Holder Name: %s \n Account No.: %d \n Currency: %s \n Balance: %f \n ===================== ",accHolderName, accNum, accCurrency, accBalance);
		BytePacker replyMessageClient = super.generateReply(status, messageId, reply);
		
		String toSubscribers = String.format("%s created an account. Account number: %d, Account Balance: %.2f %s", accHolderName, accNum, accBalance, accCurrency);
		BytePacker replyMessageSubscribers = super.generateReply(status, messageId, toSubscribers);
		callbackHandler.broadcast(replyMessageSubscribers);
		return replyMessageClient;
		
		
	}

	@Override
	public String ServiceName() {
		return "Create Account";
	}
}
