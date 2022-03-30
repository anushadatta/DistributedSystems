package services;

import message.Marshall;
import message.Unmarshall;
import message.OneByteInt;
import java.io.IOException;
import main.Client;
import main.ConsoleLogger;

// Class to view Account Balance - idempotent operation

public class CheckBalanceService extends Service {

	protected final static String ACC_NUMBER = "AccountNumber";
	protected final static String PASSWORD = "Password";

	public CheckBalanceService() {
		super(null);
	}

	@Override
	public void executeRequest(ConsoleLogger console, Client client) throws IOException {

		ConsoleLogger.println("======================== View Account Balance ========================");
		int accNum = console.askForInteger("Enter your Account No.: ");
		String password = console.askForString("Enter your 6-character password:");
		int messageId = client.getMessage_id();
		Marshall packer = new Marshall.Builder()
				.setProperty(Service.SERVICE_ID, new OneByteInt(Client.CHECK_BALANCE))
				.setProperty(Service.MESSAGE_ID, messageId)
				.setProperty(ACC_NUMBER, accNum)
				.setProperty(PASSWORD, password)
				.build();
		client.send(packer);
		Unmarshall.UnpackedMsg unmarshaledMsg = receivalProcedure(client, packer, messageId);
		if (checkStatus(unmarshaledMsg)) {
			String reply = unmarshaledMsg.getString(Service.REPLY);
			ConsoleLogger.println(reply);
		}

	}

	@Override
	public String ServiceName() {
		return "Check Balance";
	}

}
