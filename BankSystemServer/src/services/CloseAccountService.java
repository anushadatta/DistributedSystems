package services;

import java.io.IOException;
import java.net.InetAddress;
import bank.Bank;
import main.ConsoleLogger;
import message.Marshall;
import message.Unmarshall;
import transmission.Socket;
import message.OneByteInt;

// Handle account closure
public class CloseAccountService extends Service {
	protected final static String ACCOUNTNUMBER = "accNum";
	protected final static String NAME = "Name";
	protected final static String PASSWORD = "Password";
	private CallbackHandler callbackHandler;

	// Handle callback service for clients (the ones that subscribe to the service)
	public CloseAccountService(CallbackHandler callbackHandler) {
		super(new Unmarshall.Builder()
				.setType(NAME, Unmarshall.TYPE.STRING)
				.setType(ACCOUNTNUMBER, Unmarshall.TYPE.INTEGER)
				.setType(PASSWORD, Unmarshall.TYPE.STRING)
				.build());
		this.callbackHandler = callbackHandler;
	}

	@Override
	public Marshall handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {
		String reply = "";
		Unmarshall.UnpackedMsg unmarshaledMsg = this.getUnpacker().parseByteArray(dataFromClient);
		String accHolderName = unmarshaledMsg.getString(NAME);
		int accNum = unmarshaledMsg.getInteger(ACCOUNTNUMBER);
		String accountPassword = unmarshaledMsg.getString(PASSWORD);
		ConsoleLogger.debug("Account No.: " + accNum);
		int messageId = unmarshaledMsg.getInteger(super.getMessageId());
		int ret = Bank.closeAccount(accHolderName, accNum, accountPassword);
		OneByteInt status = new OneByteInt(0);
		if (ret == 1) {
			reply = String.format("=====================\nAccount %d successfully deleted\n=====================",
					accNum);
			Marshall replyMessageSubscribers = super.generateReply(status, messageId, reply);
			callbackHandler.broadcast(replyMessageSubscribers);
		} else if (ret == -2) {
			reply = String.format("Invalid Password. Please try again");
		} else if (ret == -1) {
			reply = String.format("Invalid Account No. Please try again.");
		} else if (ret == -3) {
			reply = String.format("This account number is not under your name. Please try again");
		}

		Marshall replyMessageClient = super.generateReply(status, messageId, reply);

		return replyMessageClient;
	}

	@Override
	public String ServiceName() {
		return "Close Account";
	}
}
