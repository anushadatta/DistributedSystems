package services;

import java.io.IOException;
import java.net.InetAddress;
import bank.Bank;
import message.Marshall;
import message.Unmarshall;
import transmission.Socket;
import message.OneByteInt;
import bank.Account;

// Class to handle transfer of funds

public class BalanceTransfer extends Service {
	protected final static String ACCOUNTNUMBER = "accNum";
	protected final static String NAME = "Name";
	protected final static String PASSWORD = "Password";
	protected final static String CURRENCY = "currency";
	protected final static String AMOUNT = "amount";
	protected final static String RECEIVER = "receiver";
	private CallbackHandler callbackHandler;

	// Handle callback service for clients (the ones that subscribe to the service)
	public BalanceTransfer(CallbackHandler callbackHandler) {
		super(new Unmarshall.Builder()
				.setType(NAME, Unmarshall.TYPE.STRING)
				.setType(ACCOUNTNUMBER, Unmarshall.TYPE.INTEGER)
				.setType(PASSWORD, Unmarshall.TYPE.STRING)
				.setType(RECEIVER, Unmarshall.TYPE.INTEGER)
				.setType(AMOUNT, Unmarshall.TYPE.DOUBLE)
				.build());
		this.callbackHandler = callbackHandler;
	}

	@Override
	public Marshall handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {
		String reply = "";
		Unmarshall.UnpackedMsg unmarshaledMsg = this.getUnpacker().parseByteArray(dataFromClient);
		int messageId = unmarshaledMsg.getInteger(super.getMessageId());
		int accNum = unmarshaledMsg.getInteger(ACCOUNTNUMBER);
		String accHolderName = unmarshaledMsg.getString(NAME);
		String accountPassword = unmarshaledMsg.getString(PASSWORD);
		int receiver = unmarshaledMsg.getInteger(RECEIVER);
		double amount = unmarshaledMsg.getDouble(AMOUNT);

		double accountBalance = Bank.transferBalance(accHolderName, accNum, receiver, accountPassword, amount);
		OneByteInt status = new OneByteInt(0);
		if (accountBalance == -1) {
			reply = "Invalid Account No. Please try again.";
		} else if (accountBalance == -2) {
			reply = "Invalid Password. Please try again.";
		} else if (accountBalance == -3) {
			Account sender = Bank.AllTheAccounts.get(accNum);
			String senderCurrency = sender.getaccountCurrency();
			reply = "You have insufficient funds. Your current balance is: "
					+ Bank.checkBalance(accNum, accountPassword)
					+ senderCurrency;
		} else if (accountBalance == -4) {
			reply = "Account number does not match the name. Please try again.";
		} else {
			Account sender = Bank.AllTheAccounts.get(accNum);
			String senderCurrency = sender.getaccountCurrency();
			reply = String.format(
					"===================== Successful Transfer of Funds =====================\nFrom: %d\nTo: %d\nAmount:%.2f\nCurrency:%s\nBalance: %.2f %s\n ================================================================",
					accNum, receiver, amount, senderCurrency, accountBalance, senderCurrency);
			String replyToSubscribers = String.format("%.2f %s transferred from Account No.: %d to Account No.: %d",
					amount, senderCurrency, accNum, receiver);
			Marshall replyMessageToSubcribers = super.generateReply(status, messageId, replyToSubscribers);
			callbackHandler.broadcast(replyMessageToSubcribers);

		}
		Marshall replyMessage = super.generateReply(status, messageId, reply);
		return replyMessage;

	}

	public String ServiceName() {
		return "Transfer funds";
	}
}
