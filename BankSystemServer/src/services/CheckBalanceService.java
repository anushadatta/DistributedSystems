package services;

import java.net.InetAddress;
import bank.Bank;
import bank.Account;
import message.Marshall;
import message.Unmarshall;
import transmission.Socket;
import message.OneByteInt;

// class to view account balance
public class CheckBalanceService extends Service {
	protected final static String ACC_NUMBER = "AccountNumber";
	protected final static String PASSWORD = "Password";
	private CallbackHandler callbackHandler;

	// Handle callback service for clients (the ones that subscribe to the service)
	public CheckBalanceService(CallbackHandler callbackHandler) {
		super(new Unmarshall.Builder()
				.setType(ACC_NUMBER, Unmarshall.TYPE.INTEGER)
				.setType(PASSWORD, Unmarshall.TYPE.STRING)
				.build());
		this.callbackHandler = callbackHandler;

	}

	@Override
	public Marshall handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {
		Unmarshall.UnpackedMsg unmarshaledMsg = this.getUnpacker().parseByteArray(dataFromClient);
		int messageId = unmarshaledMsg.getInteger(getMessageId());
		int accNum = unmarshaledMsg.getInteger(ACC_NUMBER);
		String password = unmarshaledMsg.getString(PASSWORD);
		double balance = Bank.checkBalance(accNum, password);
		String reply = "";
		OneByteInt status = new OneByteInt(0);

		Account user = Bank.AllTheAccounts.get(accNum);
		String currency = user.getaccountCurrency();

		if (balance == -1) {
			reply = "Invalid Account No. Please try again.";
		} else if (balance == -2) {
			reply = "Invalid Password. Please try again";
		} else {
			reply = String.format(
					"=====================\nAccount No.: %d\nCurrent Account Balance: %.2f %s\n=====================",
					accNum, balance, currency);
			Marshall replyMessageSubscriber = super.generateReply(status, messageId, reply);
			callbackHandler.broadcast(replyMessageSubscriber);
		}
		Marshall replyMessageClient = super.generateReply(status, messageId, reply);

		return replyMessageClient;
	}

	@Override
	public String ServiceName() {
		return "Check Balance";
	}

}
