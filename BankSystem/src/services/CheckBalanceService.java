package services;

import java.io.IOException;

import main.Client;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;

// Class to view Account Balance - idempotent operation

public class CheckBalanceService extends Service {
	
	protected final static String ACC_NUMBER = "AccountNumber";
	protected final static String PIN = "Pin";

	public CheckBalanceService() {
		super(null);
	}

	@Override
	public void executeRequest(Console console, Client client) throws IOException {

		Console.println("======================== View Account Balance ========================");
		int accNum = console.askForInteger("Enter your Account No.: ");
	    String pin = console.askForString("Enter your 6-character password:");
		int messageId = client.getMessage_id();
		BytePacker packer = new BytePacker.Builder()
							.setProperty(Service.SERVICE_ID,new OneByteInt(Client.CHECK_BALANCE))
							.setProperty(Service.MESSAGE_ID, messageId)
							.setProperty(ACC_NUMBER, accNum)
							.setProperty(PIN, pin)
							.build();
		client.send(packer);
		ByteUnpacker.UnpackedMsg unpackedMsg = receivalProcedure(client, packer, messageId);
		if(checkStatus(unpackedMsg)){
			String reply = unpackedMsg.getString(Service.REPLY);
			Console.println(reply);
		}
		
		
	}

	@Override
	public String ServiceName() {
		return "Check Balance";
	}	
	
	

}
