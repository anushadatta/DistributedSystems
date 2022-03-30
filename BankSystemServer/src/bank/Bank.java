package bank;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import main.Console;

// Class that handles all functions related to the bank account

public class Bank {
	public static HashMap<Integer, Account> AllTheAccounts;
	public Bank(){
		AllTheAccounts = new HashMap<>();
	}
	
	// Create a new account
	public static int createAccount(String accOwner, String pin, String accCurrency, double accBalance){
		// Generate a random 4-digit account number (integer)
		int accNum = -1;
		int min = 1000;
		int max = 9999;
		do{
			accNum = ThreadLocalRandom.current().nextInt(min, max);
		}while(AllTheAccounts.get(accNum)!=null);
		Account newAcc = new Account.Builder()
						.setAccOwner(accOwner)
						.setAccPin(pin)
						.setAccNumber(accNum)
						.setAccCurrency(accCurrency)
						.setAccBalance(accBalance)
						.build();
		AllTheAccounts.put(accNum, newAcc);
		Console.debug("Account has been successfully created. The Account No. is: " + accNum);
		return accNum;
	}
	
	// Allow user to check their account balance
	public static double checkBalance(int accNum, String pin){
		double balance = 0;
		Account acc = AllTheAccounts.get(accNum);
		if(acc!=null){
			if(acc.getAccPin().equals(pin)){
				balance = acc.getAccBalance();
			}
			else balance = -2; 					// invalid password
		}
		else{
			balance = -1; 						// account does not exist
		}
		return balance;
	}
	
  // Deposit/Withdraw from account
	public static double updateBalance(String accOwner, int accNum, String pin, int choice, double amount, String currency){
		System.out.println("Choice (0 = Withdraw, 1 = Deposit): " + choice);									// 0 - Withdraw, 1 - Deposit
	  
    if(AllTheAccounts.get(accNum)==null)return -1;							                          // Check if account exists

		if(!(AllTheAccounts.get(accNum).getAccPin().equals(pin))) return -2;	                // Check if password matches 
		
    if (!(AllTheAccounts.get(accNum).getAccOwner().equals(accOwner))) return -5;          // Check if account holder name matches
		
    Account user = Bank.AllTheAccounts.get(accNum);
		String userCurrency = user.getAccCurrency();

		if (!userCurrency.equals(currency)) {
			if(userCurrency.equals("USD")) {
				amount = 0.73 * amount;
			}

			else {
				amount = 1.36 * amount;
			}
		}
		
		// User wants to deposit money
		if(choice == 1){
			Account temp = AllTheAccounts.get(accNum);
			temp.setAccBalance(temp.getAccBalance() + amount);		
		}
		// User wants to withdraw money
		else if(choice==0){
			Account temp = AllTheAccounts.get(accNum);
			if(temp.getAccBalance() > amount){									// Check if account has sufficienct balance
				temp.setAccBalance(temp.getAccBalance() - amount);
				Console.debug("Current account balance is " + temp.getAccBalance());
			}		
			else {
				Console.debug("Alert! Account balance is not enough - " + temp.getAccBalance());
				return -3;
			}
		}
		else return -4; //	Invalid choice	
		
		System.out.println(AllTheAccounts.get(accNum).getAccBalance());		
		return AllTheAccounts.get(accNum).getAccBalance();
	}
	
	// Close bank account
	public static int closeAccount(String accOwner, int accNum, String pin){
		Account temp = AllTheAccounts.get(accNum);
		if(temp!=null){
			if (!(temp.getAccOwner().equals(accOwner))) {
				return -3;
			}
			Console.debug("Account exists");
			if(temp.getAccPin().equals(pin)){
				AllTheAccounts.remove(accNum);
				return 1;
			}
			else{
				Console.debug("Incorrect password");
				return -2;
			}
		}
		else{
			Console.debug("Account does not exist");
			return -1;
		}
	}

	// Transfer funds from one account to another
	public static double transferBalance(String accOwner, int accNum, int receiver, String pin, double amount){
		
		Account senderAcc, receiverAcc;

		double receiverAmt;

		if(AllTheAccounts.get(accNum) == null || AllTheAccounts.get(receiver) == null) {       // Check if accounts exist
			return -1;
		}
		
		senderAcc = AllTheAccounts.get(accNum);
		receiverAcc = AllTheAccounts.get(receiver);

		String senderCurr = senderAcc.getAccCurrency();
		String receiverCurr = receiverAcc.getAccCurrency();

    // Perform conversion from USD to SGD and SGD to USD in case the currencies of the account are different
		// Convert sender's amount to receiver's account currency
		if (!senderCurr.equals(receiverCurr)) {
			if (senderCurr == "USD") {
				receiverAmt = amount * 1.36; 
			}
			else {
				receiverAmt = amount * 0.73;
			}

		}
		else {
			receiverAmt = amount;
		}
		
		if (!(senderAcc.getAccOwner().equals(accOwner))) return -4;         // Incorrect account holder name
		if (!(senderAcc.getAccPin().equals(pin))) {                         // Incorrect password
			return -2; 
		}
		
		
		if(senderAcc.getAccBalance() > amount) {				// If transfer is possible 
			senderAcc.setAccBalance(senderAcc.getAccBalance() - amount);
			receiverAcc.setAccBalance(receiverAcc.getAccBalance() + receiverAmt);
			System.out.println(AllTheAccounts.get(accNum).getAccBalance());
			System.out.println(AllTheAccounts.get(receiver).getAccBalance());
			return senderAcc.getAccBalance();
		}
		else{
			return -3; 											// Insufficient funds
		}
				
		
	}
	
}
