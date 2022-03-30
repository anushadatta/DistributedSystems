package services;

import java.io.IOException;
import java.net.InetAddress;

import bank.Bank;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import socket.Socket;
import bank.Account;

// Handles depositing/withdrawl to/from account

public class BalanceUpdate extends Service {
	protected final static String NAME = "Name";
	protected final static String ACCNUM = "accNum";
	protected final static String PIN = "Pin";
	protected final static String CHOICE = "choice";
	protected final static String AMOUNT = "amount";
	protected final static String CURRENCY = "currency";

	private CallbackHandlerClass callbackHandler;
	
	// Handle callback service for clients (the ones that subscribe to the service)
	public BalanceUpdate(CallbackHandlerClass callbackHandler){
		super(new ByteUnpacker.Builder()
						.setType(NAME, ByteUnpacker.TYPE.STRING)
						.setType(ACCNUM, ByteUnpacker.TYPE.INTEGER)
						.setType(CURRENCY, ByteUnpacker.TYPE.STRING)
						.setType(PIN, ByteUnpacker.TYPE.STRING)
						.setType(CHOICE, ByteUnpacker.TYPE.INTEGER)
						.setType(AMOUNT, ByteUnpacker.TYPE.DOUBLE)
						.build());	
		this.callbackHandler = callbackHandler;
	}
	
	
	@Override
	public BytePacker handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient, Socket socket) {
		ByteUnpacker.UnpackedMsg unpackedMsg = this.getUnpacker().parseByteArray(dataFromClient);
		String accHolderName = unpackedMsg.getString(NAME);
		int accNum = unpackedMsg.getInteger(ACCNUM);
		String currency = unpackedMsg.getString(CURRENCY);
		String accPin = unpackedMsg.getString(PIN);
		int choice = unpackedMsg.getInteger(CHOICE);
		double amount = unpackedMsg.getDouble(AMOUNT);
		int messageId = unpackedMsg.getInteger(super.getMessageId());
		String reply = "";

		double accBalance = Bank.updateBalance(accHolderName,accNum, accPin, choice, amount, currency);
		OneByteInt status = new OneByteInt(0);
		
		if(accBalance==-1){
			reply = "Invalid Account No. Please try again.";
		}
		else if(accBalance ==-2){
			reply = "Invalid Password. Please try again.";
		}
		else if(accBalance==-3){
			Account user = Bank.AllTheAccounts.get(accNum);
			String userCurrency = user.getAccCurrency();

			if (!userCurrency.equals(currency)) {
				if(userCurrency.equals("USD")) {
					amount = 0.73 * amount;
				}

				else {
					amount = 1.36 * amount;
				}
			}
			reply = "You have insufficient funds. Your current balance is: " + Bank.checkBalance(accNum, accPin) + userCurrency;
		}
		else if(accBalance==-4){
			reply = "Invalid Choice. Please try again";
		}
		else if(accBalance==-5) {
			reply = "Account number does not match name. Please try again.";
		}
		else{
			Account user = Bank.AllTheAccounts.get(accNum);
			String userCurrency = user.getAccCurrency();

			if (!userCurrency.equals(currency)) {
				if(userCurrency.equals("USD")) {
					amount = 0.73 * amount;
				}

				else {
					amount = 1.36 * amount;
				}
			}
			String choiceType = "";
			String choiceType2 = "";
			if(choice == 1){
				choiceType = "Deposit Funds";
				choiceType2 = "Amount Deposited to Account No.";
			} 
			else if(choice==0){
				choiceType = "Withdraw Funds";
				choiceType2 = "Amount Withdrawn from Account No.";
			}
			reply = String.format("=====================\n%s \n%s %d: %f \nCurrent Account Balance: %f %s\n=====================" ,choiceType, choiceType2, accNum, amount, accBalance, userCurrency);
			BytePacker replyMessageSubscriber = super.generateReply(status, messageId, reply);
			callbackHandler.broadcast(replyMessageSubscriber);
		}
		
		BytePacker replyMessageClient = super.generateReply(status, messageId, reply);
		return replyMessageClient;
		
	}

	@Override
	public String ServiceName() {
		return "Make Deposit/Withdrawal";
	}
	
	
}
