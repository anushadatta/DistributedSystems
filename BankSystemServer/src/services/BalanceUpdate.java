package services;

import java.net.InetAddress;
import java.io.IOException;
import bank.Bank;
import bank.Account;
import message.Marshall;
import message.Unmarshall;
import transmission.Socket;
import message.OneByteInt;

// Handles depositing/withdrawl to/from account
public class BalanceUpdate extends Service {
	protected final static String ACCOUNTNUMBER = "accNum";
	protected final static String NAME = "Name";
	protected final static String PASSWORD = "Password";
	protected final static String AMOUNT = "amount";
	protected final static String CURRENCY = "currency";
	protected final static String CHOICE = "choice";

	private CallbackHandler callbackHandler;

	// Handle callback service for clients (the ones that subscribe to the service)
	public BalanceUpdate(CallbackHandler callbackHandler) {
		super(new Unmarshall.Builder()
				.setType(NAME, Unmarshall.TYPE.STRING)
				.setType(ACCOUNTNUMBER, Unmarshall.TYPE.INTEGER)
				.setType(CURRENCY, Unmarshall.TYPE.STRING)
				.setType(PASSWORD, Unmarshall.TYPE.STRING)
				.setType(CHOICE, Unmarshall.TYPE.INTEGER)
				.setType(AMOUNT, Unmarshall.TYPE.DOUBLE)
				.build());
		this.callbackHandler = callbackHandler;
	}

	@Override
	public Marshall handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {
		Unmarshall.UnpackedMsg unmarshaledMsg = this.getUnpacker().parseByteArray(dataFromClient);
		String accHolderName = unmarshaledMsg.getString(NAME);
		int accNum = unmarshaledMsg.getInteger(ACCOUNTNUMBER);
		String currency = unmarshaledMsg.getString(CURRENCY);
		String accountPassword = unmarshaledMsg.getString(PASSWORD);
		int choice = unmarshaledMsg.getInteger(CHOICE);
		double amount = unmarshaledMsg.getDouble(AMOUNT);
		int messageId = unmarshaledMsg.getInteger(super.getMessageId());
		String reply = "";

		double accountBalance = Bank.updateBalance(accHolderName, accNum, accountPassword, choice, amount, currency);
		OneByteInt status = new OneByteInt(0);

		if (accountBalance == -1) {
			reply = "Invalid Account No. Please try again.";
		} else if (accountBalance == -2) {
			reply = "Invalid Password. Please try again.";
		} else if (accountBalance == -3) {
			Account user = Bank.AllTheAccounts.get(accNum);
			String userCurrency = user.getaccountCurrency();

			if (!userCurrency.equals(currency)) {
				if (userCurrency.equals("USD")) {
					amount = 0.73 * amount;
				}

				else {
					amount = 1.36 * amount;
				}
			}
			reply = "You have insufficient funds. Your current balance is: "
					+ Bank.checkBalance(accNum, accountPassword)
					+ userCurrency;
		} else if (accountBalance == -4) {
			reply = "Invalid Choice. Please try again";
		} else if (accountBalance == -5) {
			reply = "Account number does not match name. Please try again.";
		} else {
			Account user = Bank.AllTheAccounts.get(accNum);
			String userCurrency = user.getaccountCurrency();

			if (!userCurrency.equals(currency)) {
				if (userCurrency.equals("USD")) {
					amount = 0.73 * amount;
				}

				else {
					amount = 1.36 * amount;
				}
			}
			String choiceType = "";
			String choiceType2 = "";
			if (choice == 1) {
				choiceType = "Funds Deposited";
				choiceType2 = "Amount Deposited to Account No.";
			} else if (choice == 0) {
				choiceType = "Funds Withdrawn";
				choiceType2 = "Amount Withdrawn from Account No.";
			}
			reply = String.format(
					"===================== %s ===================== \n%s %d: %f %s \nCurrent Account Balance: %f %s\n=====================",
					choiceType, choiceType2, accNum, amount, currency, accountBalance, userCurrency);
			Marshall replyMessageSubscriber = super.generateReply(status, messageId, reply);
			callbackHandler.broadcast(replyMessageSubscriber);
		}

		Marshall replyMessageClient = super.generateReply(status, messageId, reply);
		return replyMessageClient;

	}

	@Override
	public String ServiceName() {
		return "Make Deposit/Withdrawal";
	}

}
