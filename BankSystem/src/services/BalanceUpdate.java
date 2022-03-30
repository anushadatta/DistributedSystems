package services;

import java.io.IOException;

import main.Client;
import main.Console;
import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;

// Class to handle depositing and withdrawing funds from your account
public class BalanceUpdate extends Service {
		
	public BalanceUpdate() {
		super(null);
	}

	public void executeRequest(Console console, Client client) throws IOException{
		Console.println("======================== Balance Update ========================");
		String name = console.askForString("Enter your name:");
		int accNum = console.askForInteger("Enter your Account No.:");
		String pin = console.askForString("Enter your 6-character password: ");
		int choice = console.askForInteger("Would you like to withdraw (0) or deposit(1)?"); 
		String currency = console.askForString("Enter the currency you want to withdraw/deposit in (SGD or USD):");
		double amount = console.askForDouble("Enter the amount you wish to withdraw/deposit:");
		int message_id = client.getMessage_id();	
		
		BytePacker packer = new BytePacker.Builder()
								.setProperty("ServiceId", new OneByteInt(Client.UPDATE_BALANCE))
								.setProperty("messageId", message_id)
								.setProperty("Name", name)
								.setProperty("accNum", accNum)
								.setProperty("currency", currency)
								.setProperty("Pin", pin)
								.setProperty("choice", choice)
								.setProperty("amount", amount)
								.build();
		client.send(packer);
		
		ByteUnpacker.UnpackedMsg unpackedMsg = receivalProcedure(client, packer, message_id);
		if(checkStatus(unpackedMsg)){
			String reply = unpackedMsg.getString(Service.REPLY);
			Console.println(reply);	
		}
		else{
			Console.println("Withdraw/Deposit to/from your account has failed.");
		}
	}
	@Override
	public String ServiceName() {
		return "Make Deposit/Withdrawal";
	}
}
