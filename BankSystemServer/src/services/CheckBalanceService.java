package services;

import java.net.InetAddress;

import bank.Bank;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import socket.Socket;

import bank.Account;

// class to view account balance
public class CheckBalanceService extends Service {
	protected final static String ACC_NUMBER = "AccountNumber";
	protected final static String PIN = "Pin";
	private CallbackHandlerClass callbackHandler;
	
	// Handle callback service for clients (the ones that subscribe to the service)
	public CheckBalanceService(CallbackHandlerClass callbackHandler) {
		super(new ByteUnpacker.Builder()
				.setType(ACC_NUMBER,ByteUnpacker.TYPE.INTEGER)
				.setType(PIN, ByteUnpacker.TYPE.STRING)
				.build());
		this.callbackHandler = callbackHandler;
		
	}

	@Override
	public BytePacker handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {
		ByteUnpacker.UnpackedMsg unpackedMsg = this.getUnpacker().parseByteArray(dataFromClient);
		int accNum = unpackedMsg.getInteger(ACC_NUMBER);
		String pin = unpackedMsg.getString(PIN);
		int messageId = unpackedMsg.getInteger(getMessageId());
		double balance = Bank.checkBalance(accNum,pin);
		String reply = "";
		OneByteInt status = new OneByteInt(0);
		
		Account user = Bank.AllTheAccounts.get(accNum);
		String currency = user.getAccCurrency();
		
		if(balance == -1){
			reply = "Invalid Account No. Please try again.";
		}
		else if(balance == -2){
			reply = "Invalid Password. Please try again";
		}
		else{
			reply = String.format("=====================\nAccount No.: %d\nCurrent Account Balance: %.2f %s\n=====================",accNum,  balance, currency);
			BytePacker replyMessageSubscriber = super.generateReply(status, messageId, reply);
			callbackHandler.broadcast(replyMessageSubscriber);
		}
		BytePacker replyMessageClient = super.generateReply(status, messageId, reply);
		
		return replyMessageClient;
	}

	@Override
	public String ServiceName() {
		return "Check Balance";
	}
	
}
