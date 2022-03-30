package services;

import java.io.IOException;
import java.net.InetAddress;

import bank.Bank;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import socket.Socket;

import bank.Account;

// Class to handle transfer of funds

public class BalanceTransfer extends Service {
	protected final static String NAME = "Name";
	protected final static String ACCNUM = "accNum";
	protected final static String PIN = "Pin";
	protected final static String RECEIVER = "receiver";
	protected final static String AMOUNT = "amount";
	protected final static String CURRENCY = "currency";
	private CallbackHandlerClass callbackHandler;
	
	// Handle callback service for clients (the ones that subscribe to the service)
	public BalanceTransfer(CallbackHandlerClass callbackHandler){
		super(new ByteUnpacker.Builder()
						.setType(NAME, ByteUnpacker.TYPE.STRING)
						.setType(ACCNUM, ByteUnpacker.TYPE.INTEGER)
						.setType(PIN, ByteUnpacker.TYPE.STRING)
						.setType(RECEIVER, ByteUnpacker.TYPE.INTEGER)
						.setType(AMOUNT, ByteUnpacker.TYPE.DOUBLE)
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
		int receiver = unpackedMsg.getInteger(RECEIVER);
		double amount = unpackedMsg.getDouble(AMOUNT);
		int messageId = unpackedMsg.getInteger(super.getMessageId());
		
		double accBalance = Bank.transferBalance(accHolderName, accNum, receiver, accPin, amount);
		OneByteInt status = new OneByteInt(0);
		if(accBalance==-1){
			reply = "Invalid Account No. Please try again.";
		}
		else if (accBalance == -2){
			reply = "Invalid Password. Please try again.";
		}
		else if(accBalance==-3){
			reply = "You have insufficient funds. Your current balance is: " + Bank.checkBalance(accNum, accPin) + senderCurrency;
		}
		else if(accBalance==-4) {
			reply = "Account number does not match the name. Please try again.";
		}
		else{
			Account sender = Bank.AllTheAccounts.get(accNum);
			String senderCurrency = sender.getAccCurrency();
			reply = String.format("=====================\n Successful Transfer of Funds\nFrom: %d\nTo: %d\nAmount:%.2f\nCurrency:%s\nBalance: %.2f\n=====================", accNum,receiver,amount,senderCurrency,accBalance);
			String replyToSubscribers = String.format("%.2f %s transferred from Account No.: %d to Account No.: %d", amount, senderCurrency, accNum, receiver);
			BytePacker replyMessageToSubcribers = super.generateReply(status, messageId, replyToSubscribers);
			callbackHandler.broadcast(replyMessageToSubcribers);
			
		}
		BytePacker replyMessage = super.generateReply(status, messageId, reply);
		return replyMessage;
		
	}
	public String ServiceName(){
		return "Transfer funds";
	}
}
