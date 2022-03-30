package bank;

// Class that handles user's account

public class Account {
	private int accountNumber;
	private String accountUserName;
	private String accountPassword;
	private String accountCurrency;
	private double accountBalance;

	public Account() {
	}

	public Account(int accountNumber, String accountUserName, String accountPassword, String accountCurrency,
			double accountBalance) {
		this.accountNumber = accountNumber;
		this.accountUserName = accountUserName;
		this.accountPassword = accountPassword;
		this.accountCurrency = accountCurrency;
		this.accountBalance = accountBalance;
	}

	public int getaccountNumber() {
		return accountNumber;
	}

	public String getaccountUserName() {
		return accountUserName;
	}

	public String getaccountCurrency() {
		return accountCurrency;
	}

	public double getaccountBalance() {
		return accountBalance;
	}

	public String getaccountPassword() {
		return accountPassword;
	}

	public void setaccountUserName(String accountUserName) {
		this.accountUserName = accountUserName;
	}

	public void setaccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	public void setaccountBalance(double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public void setaccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setaccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}

	public static class Builder {
		private Account account;

		public Builder() {
			account = new Account();
		}

		public Builder setaccountNumber(int accountNumber) {
			account.setaccountNumber(accountNumber);
			return this;
		}

		public Builder setaccountUserName(String accountUserName) {
			account.setaccountUserName(accountUserName);
			return this;
		}

		public Builder setaccountPassword(String password) {
			account.setaccountPassword(password);
			return this;
		}

		public Builder setaccountCurrency(String accountCurrency) {
			account.setaccountCurrency(accountCurrency);
			return this;
		}

		public Builder setaccountBalance(double accountBalance) {
			account.setaccountBalance(accountBalance);
			return this;
		}

		public Account build() {
			return account;
		}
	}
}
