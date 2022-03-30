package services;

import java.io.IOException;

import main.Client;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;

// Class to handle account creation 

public class CreateAccountService extends Service {

	protected final static String NAME = "Name";
	protected final static String PIN = "Pin";
	protected final static String CURRENCY = "Currency";
	protected final static String BALANCE = "Balance";
	
	public CreateAccountService(){
		super(null);
	}
	
	// Account creation
	@Override
	public void executeRequest(Console console, Client client) throws IOException {
		Console.println("======================== Account creation ========================");
		String name = console.askForString("Enter your Name:");
		String pin = console.askForString("Enter your 6-character password: ");
		String currency = console.askForString("Specify the currency type of your account:");
		double init_balance = console.askForDouble("Enter the initial balance of your account:");

		int message_id = client.getMessage_id();	
		
		// Perform marshalling
		BytePacker packer = new BytePacker.Builder()
								.setProperty(Service.SERVICE_ID, new OneByteInt(Client.CREATE_ACCOUNT))
								.setProperty(Service.MESSAGE_ID, message_id)
								.setProperty(NAME, name)
								.setProperty(PIN, pin)
								.setProperty(CURRENCY,currency)
								.setProperty(BALANCE, init_balance)
								.build();
		client.send(packer);
		
		ByteUnpacker.UnpackedMsg unpackedMsg = receivalProcedure(client, packer, message_id);
		
		// Reply status 0 - success
		if(checkStatus(unpackedMsg)){ 
			
			String reply = unpackedMsg.getString(Service.REPLY);
			Console.println(reply);
		}
		else{
			Console.println("Account creation has failed.");
		}
	}
	
	@Override
	public String ServiceName() {
		return "Create Account";
	}

}
