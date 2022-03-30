package services;

import java.io.IOException;

import main.Client;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;

// Class to handle transferring funds - non-idempotent operation
public class BalanceTransfer extends Service {

	public BalanceTransfer() {
		super(null);
	}

	// Assumption: User always transfers funds in the currency of their account
	public void executeRequest(Console console, Client client) throws IOException{
		Console.println("======================== Balance Transfer ========================");
		String name = console.askForString("Enter your name:");
		int accNum = console.askForInteger("Enter your Account No.:");
		String pin = console.askForString("Enter your 6-character password: ");
		int receiver = console.askForInteger("Enter Account No. of Recipient:");
		double amount = console.askForDouble("Enter Amount you wish to transfer:");
		int message_id = client.getMessage_id();	
		
		BytePacker packer = new BytePacker.Builder()
								.setProperty("ServiceId", new OneByteInt(Client.TRANSFER_BALANCE))
								.setProperty("messageId", message_id)
								.setProperty("Name", name)
								.setProperty("accNum", accNum)
								.setProperty("Pin", pin)
								.setProperty("receiver", receiver)
								.setProperty("amount", amount)
								.build();
		client.send(packer);
		
		ByteUnpacker.UnpackedMsg unpackedMsg = receivalProcedure(client, packer, message_id);
		if(checkStatus(unpackedMsg)){
			String reply = unpackedMsg.getString(Service.REPLY);
			Console.println(reply);	
		}
		else{
			Console.println("The requested fund transfer has failed.");
		}
	}
	
	@Override
	public String ServiceName() {
		return "Transfer Funds";
	}
}
