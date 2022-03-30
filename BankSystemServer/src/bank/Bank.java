package bank;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import main.ConsoleLogger;

// Class that handles all functions related to the bank account

public class Bank {
	public static HashMap<Integer, Account> AllTheAccounts;

	public Bank() {
		AllTheAccounts = new HashMap<>();
	}

	// Create a new account
	public static int createAccount(String accountUserName, String password, String accountCurrency,
			double accountBalance) {
		// Generate a random 4-digit account number (integer)
		int accNum = -1;
		int min = 1000;
		int max = 9999;
		do {
			accNum = ThreadLocalRandom.current().nextInt(min, max);
		} while (AllTheAccounts.get(accNum) != null);
		Account newAcc = new Account.Builder()
				.setaccountUserName(accountUserName)
				.setaccountPassword(password)
				.setaccountNumber(accNum)
				.setaccountCurrency(accountCurrency)
				.setaccountBalance(accountBalance)
				.build();
		AllTheAccounts.put(accNum, newAcc);
		ConsoleLogger.debug("Account has been successfully created. The Account No. is: " + accNum);
		return accNum;
	}

	// Close bank account
	public static int closeAccount(String accountUserName, int accNum, String password) {
		Account temp = AllTheAccounts.get(accNum);
		if (temp != null) {
			if (!(temp.getaccountUserName().equals(accountUserName))) {
				return -3;
			}
			ConsoleLogger.debug("Account exists");
			if (temp.getaccountPassword().equals(password)) {
				AllTheAccounts.remove(accNum);
				return 1;
			} else {
				ConsoleLogger.debug("Incorrect password");
				return -2;
			}
		} else {
			ConsoleLogger.debug("Account does not exist");
			return -1;
		}
	}

	// Deposit/Withdraw from account
	public static double updateBalance(String accountUserName, int accNum, String password, int choice, double amount,
			String currency) {
		System.out.println("Choice (0 = Withdraw, 1 = Deposit): " + choice); // 0 - Withdraw, 1 - Deposit

		if (AllTheAccounts.get(accNum) == null)
			return -1; // Check if account exists

		if (!(AllTheAccounts.get(accNum).getaccountPassword().equals(password)))
			return -2; // Check if password matches

		if (!(AllTheAccounts.get(accNum).getaccountUserName().equals(accountUserName)))
			return -5; // Check if account holder name matches

		Account user = Bank.AllTheAccounts.get(accNum);
		String userCurrency = user.getaccountCurrency();

		if (!userCurrency.equals(currency)) {
			if (userCurrency.equals("USD")) {
				amount = 0.73 * amount;
			}

			else {
				amount = 1.36 * amount;
			}
		}

		// User wants to deposit money
		if (choice == 1) {
			Account temp = AllTheAccounts.get(accNum);
			temp.setaccountBalance(temp.getaccountBalance() + amount);
		}
		// User wants to withdraw money
		else if (choice == 0) {
			Account temp = AllTheAccounts.get(accNum);
			if (temp.getaccountBalance() > amount) { // Check if account has sufficienct balance
				temp.setaccountBalance(temp.getaccountBalance() - amount);
				ConsoleLogger.debug("Current account balance is " + temp.getaccountBalance());
			} else {
				ConsoleLogger.debug("Alert! Account balance is not enough - " + temp.getaccountBalance());
				return -3;
			}
		} else
			return -4; // Invalid choice

		System.out.println(AllTheAccounts.get(accNum).getaccountBalance());
		return AllTheAccounts.get(accNum).getaccountBalance();
	}

	// Allow user to check their account balance
	public static double checkBalance(int accNum, String password) {
		double balance = 0;
		Account acc = AllTheAccounts.get(accNum);
		if (acc != null) {
			if (acc.getaccountPassword().equals(password)) {
				balance = acc.getaccountBalance();
			} else
				balance = -2; // invalid password
		} else {
			balance = -1; // account does not exist
		}
		return balance;
	}

	// Transfer funds from one account to another
	public static double transferBalance(String accountUserName, int accNum, int receiver, String password,
			double amount) {

		Account senderAcc, receiverAcc;

		double receiverAmt;

		if (AllTheAccounts.get(accNum) == null || AllTheAccounts.get(receiver) == null) { // Check if accounts exist
			return -1;
		}

		senderAcc = AllTheAccounts.get(accNum);
		receiverAcc = AllTheAccounts.get(receiver);

		String senderCurr = senderAcc.getaccountCurrency();
		String receiverCurr = receiverAcc.getaccountCurrency();

		// Perform conversion from USD to SGD and SGD to USD in case the currencies of
		// the account are different
		// Convert sender's amount to receiver's account currency
		if (!senderCurr.equals(receiverCurr)) {
			if (senderCurr.equals("USD")) {
				receiverAmt = amount * 1.36;
			} else {
				receiverAmt = amount * 0.73;
			}

		} else {
			receiverAmt = amount;
		}

		if (!(senderAcc.getaccountUserName().equals(accountUserName)))
			return -4; // Incorrect account holder name
		if (!(senderAcc.getaccountPassword().equals(password))) { // Incorrect password
			return -2;
		}

		if (senderAcc.getaccountBalance() > amount) { // If transfer is possible
			senderAcc.setaccountBalance(senderAcc.getaccountBalance() - amount);
			receiverAcc.setaccountBalance(receiverAcc.getaccountBalance() + receiverAmt);
			System.out.println(AllTheAccounts.get(accNum).getaccountBalance());
			System.out.println(AllTheAccounts.get(receiver).getaccountBalance());
			return senderAcc.getaccountBalance();
		} else {
			return -3; // Insufficient funds
		}

	}

}
