package services;

import java.io.IOException;

import main.Client;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;

// Class to handle closure of account

public class CloseAccountService extends Service {

	protected final static String NAME = "Name";
	protected final static String ACCNUM = "accNum";
	protected final static String PIN = "Pin";

	public CloseAccountService(){
		super(null);
	}
	
	@Override
	public void executeRequest(Console console, Client client) throws IOException {
		Console.println("======================== Account Closure ======================== ");
		String name = console.askForString("Enter your Name:");
		int accNum = console.askForInteger("Enter your Account No.:");
	    String pin = console.askForString("Enter your 6-character password: ");
		int message_id = client.getMessage_id();	

		BytePacker packer = new BytePacker.Builder()
								.setProperty(Service.SERVICE_ID, new OneByteInt(Client.CLOSE_ACCOUNT))
								.setProperty(Service.MESSAGE_ID, message_id)
								.setProperty(NAME, name)
								.setProperty(ACCNUM, accNum)
								.setProperty(PIN, pin)
								.build();
		client.send(packer);
		
		ByteUnpacker.UnpackedMsg unpackedMsg = receivalProcedure(client, packer, message_id);
		
		// if status == 0, checkStatus returns true
		if(checkStatus(unpackedMsg)){ 
			String reply = unpackedMsg.getString(Service.REPLY);
			Console.println(reply);
		}
		else{
			Console.println("Your request to close your account has failed.");
		}
	}
	
	@Override
	public String ServiceName() {
		return "Close Account";
	}
}
