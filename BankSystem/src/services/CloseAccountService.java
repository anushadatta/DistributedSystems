package services;

import message.Marshall;
import message.Unmarshall;
import message.OneByteInt;
import java.io.IOException;
import main.Client;
import main.ConsoleLogger;

// Class to handle closure of account

public class CloseAccountService extends Service {

	protected final static String NAME = "Name";
	protected final static String ACCOUNTNUMBER = "accNum";
	protected final static String PASSWORD = "Password";

	public CloseAccountService() {
		super(null);
	}

	@Override
	public void executeRequest(ConsoleLogger console, Client client) throws IOException {
		ConsoleLogger.println("======================== Account Closure ======================== ");
		String name = console.askForString("Enter your Name:");
		int accNum = console.askForInteger("Enter your Account No.:");
		String password = console.askForString("Enter your 6-character password: ");
		int message_id = client.getMessage_id();

		Marshall packer = new Marshall.Builder()
				.setProperty(Service.SERVICE_ID, new OneByteInt(Client.CLOSE_ACCOUNT))
				.setProperty(Service.MESSAGE_ID, message_id)
				.setProperty(NAME, name)
				.setProperty(ACCOUNTNUMBER, accNum)
				.setProperty(PASSWORD, password)
				.build();
		client.send(packer);

		Unmarshall.UnpackedMsg unmarshaledMsg = receivalProcedure(client, packer, message_id);

		// if status == 0, checkStatus returns true
		if (checkStatus(unmarshaledMsg)) {
			String reply = unmarshaledMsg.getString(Service.REPLY);
			ConsoleLogger.println(reply);
		} else {
			ConsoleLogger.println("Your request to close your account has failed.");
		}
	}

	@Override
	public String ServiceName() {
		return "Close Account";
	}
}
