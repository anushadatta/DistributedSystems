package services;

import message.Marshall;
import message.Unmarshall;
import message.OneByteInt;
import java.io.IOException;
import main.Client;
import main.ConsoleLogger;

// Class to handle account creation 
public class CreateAccountService extends Service {

	protected final static String NAME = "Name";
	protected final static String PASSWORD = "Password";
	protected final static String CURRENCY = "Currency";
	protected final static String BALANCE = "Balance";

	public CreateAccountService() {
		super(null);
	}

	// Account creation
	@Override
	public void executeRequest(ConsoleLogger console, Client client) throws IOException {
		ConsoleLogger.println("======================== Account creation ========================");
		String name = console.askForString("Enter your Name:");
		String password = console.askForString("Enter your 6-character password: ");
		String currency = console.askForString("Specify the currency type of your account:");
		double init_balance = console.askForDouble("Enter the initial balance of your account:");

		int message_id = client.getMessage_id();

		// Perform marshalling
		Marshall packer = new Marshall.Builder()
				.setProperty(Service.SERVICE_ID, new OneByteInt(Client.CREATE_ACCOUNT))
				.setProperty(Service.MESSAGE_ID, message_id)
				.setProperty(NAME, name)
				.setProperty(PASSWORD, password)
				.setProperty(CURRENCY, currency)
				.setProperty(BALANCE, init_balance)
				.build();
		client.send(packer);

		Unmarshall.UnpackedMsg unmarshaledMsg = receivalProcedure(client, packer, message_id);

		// Reply status 0 - success
		if (checkStatus(unmarshaledMsg)) {

			String reply = unmarshaledMsg.getString(Service.REPLY);
			ConsoleLogger.println(reply);
		} else {
			ConsoleLogger.println("Account creation has failed.");
		}
	}

	@Override
	public String ServiceName() {
		return "Create Account";
	}

}
