package services;

import message.Marshall;
import message.Unmarshall;
import message.OneByteInt;
import java.io.IOException;
import main.Client;
import main.ConsoleLogger;

// Class to handle transferring funds - non-idempotent operation
public class BalanceTransfer extends Service {

	public BalanceTransfer() {
		super(null);
	}

	// Assumption: User always transfers funds in the currency of their account
	public void executeRequest(ConsoleLogger console, Client client) throws IOException {
		ConsoleLogger.println("======================== Balance Transfer ========================");
		String name = console.askForString("Enter your name:");
		int accNum = console.askForInteger("Enter your Account No.:");
		String password = console.askForString("Enter your 6-character password: ");
		int receiver = console.askForInteger("Enter Account No. of Recipient:");
		double amount = console.askForDouble("Enter Amount you wish to transfer:");
		int message_id = client.getMessage_id();

		Marshall packer = new Marshall.Builder()
				.setProperty("ServiceId", new OneByteInt(Client.TRANSFER_BALANCE))
				.setProperty("messageId", message_id)
				.setProperty("Name", name)
				.setProperty("accNum", accNum)
				.setProperty("Password", password)
				.setProperty("receiver", receiver)
				.setProperty("amount", amount)
				.build();
		client.send(packer);

		Unmarshall.UnpackedMsg unmarshaledMsg = receivalProcedure(client, packer, message_id);
		if (checkStatus(unmarshaledMsg)) {
			String reply = unmarshaledMsg.getString(Service.REPLY);
			ConsoleLogger.println(reply);
		} else {
			ConsoleLogger.println("The requested fund transfer has failed.");
		}
	}

	@Override
	public String ServiceName() {
		return "Transfer Funds";
	}
}
