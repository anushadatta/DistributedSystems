package services;

import message.Marshall;
import message.Unmarshall;
import message.OneByteInt;
import java.io.IOException;
import main.Client;
import main.ConsoleLogger;

// Class to handle depositing and withdrawing funds from your account
public class BalanceUpdate extends Service {

	public BalanceUpdate() {
		super(null);
	}

	public void executeRequest(ConsoleLogger console, Client client) throws IOException {
		ConsoleLogger.println("======================== Balance Update ========================");
		String name = console.askForString("Enter your name:");
		int accNum = console.askForInteger("Enter your Account No.:");
		String password = console.askForString("Enter your 6-character password: ");
		int choice = console.askForInteger("Would you like to withdraw (0) or deposit(1)?");
		String currency = console.askForString("Enter the currency you want to withdraw/deposit in (SGD or USD):");
		double amount = console.askForDouble("Enter the amount you wish to withdraw/deposit:");
		int message_id = client.getMessage_id();

		Marshall packer = new Marshall.Builder()
				.setProperty("ServiceId", new OneByteInt(Client.UPDATE_BALANCE))
				.setProperty("messageId", message_id)
				.setProperty("Name", name)
				.setProperty("accNum", accNum)
				.setProperty("currency", currency)
				.setProperty("Password", password)
				.setProperty("choice", choice)
				.setProperty("amount", amount)
				.build();
		client.send(packer);

		Unmarshall.UnpackedMsg unmarshaledMsg = receivalProcedure(client, packer, message_id);
		if (checkStatus(unmarshaledMsg)) {
			String reply = unmarshaledMsg.getString(Service.REPLY);
			ConsoleLogger.println(reply);
		} else {
			ConsoleLogger.println("Withdraw/Deposit to/from your account has failed.");
		}
	}

	@Override
	public String ServiceName() {
		return "Make Deposit/Withdrawal";
	}
}
