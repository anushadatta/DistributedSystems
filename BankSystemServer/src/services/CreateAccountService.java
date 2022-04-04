package services;

import java.io.IOException;
import java.net.InetAddress;
import bank.Bank;
import message.Marshall;
import message.Unmarshall;
import transmission.Socket;
import message.OneByteInt;

// Handle account creation
public class CreateAccountService extends Service {
	protected final static String NAME = "Name";
	protected final static String PASSWORD = "Password";
	protected final static String CURRENCY = "Currency";
	protected final static String BALANCE = "Balance";
	private CallbackHandler callbackHandler;

	// Handle callback service for clients (the ones that subscribe to the service)
	public CreateAccountService(CallbackHandler callbackHandler) {
		super(new Unmarshall.Builder()
				.setType(NAME, Unmarshall.TYPE.STRING)
				.setType(PASSWORD, Unmarshall.TYPE.STRING)
				.setType(CURRENCY, Unmarshall.TYPE.STRING)
				.setType(BALANCE, Unmarshall.TYPE.DOUBLE)
				.build());
		this.callbackHandler = callbackHandler;
	}

	@Override
	public Marshall handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {

		Unmarshall.UnpackedMsg unmarshaledMsg = this.getUnpacker().parseByteArray(dataFromClient);
		String accHolderName = unmarshaledMsg.getString(NAME);
		String accountPassword = unmarshaledMsg.getString(PASSWORD);
		String accountCurrency = unmarshaledMsg.getString(CURRENCY);
		double accountBalance = unmarshaledMsg.getDouble(BALANCE);
		int messageId = unmarshaledMsg.getInteger(super.getMessageId());
		int accNum = Bank.createAccount(accHolderName, accountPassword, accountCurrency, accountBalance);

		OneByteInt status = new OneByteInt(0);
		String reply = String.format(
				"================== Successful Account Creation ================== \n Account Holder Name: %s \n Account No.: %d \n Currency: %s \n Balance: %f \n ================================================================",
				accHolderName, accNum, accountCurrency, accountBalance);
		Marshall replyMessageClient = super.generateReply(status, messageId, reply);

		String toSubscribers = String.format("%s created an account. Account number: %d, Account Balance: %.2f %s",
				accHolderName, accNum, accountBalance, accountCurrency);
		Marshall replyMessageSubscribers = super.generateReply(status, messageId, toSubscribers);
		callbackHandler.broadcast(replyMessageSubscribers);
		return replyMessageClient;

	}

	@Override
	public String ServiceName() {
		return "Create Account";
	}
}
