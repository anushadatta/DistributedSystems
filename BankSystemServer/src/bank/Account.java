package bank;

// Class that handles user's account

public class Account {
	private int accNumber;
	private String accOwner;
	private String accPin;
	private String accCurrency;
	private double accBalance;
	
	public Account(){}
	public Account(int accNumber, String accOwner, String accPin, String accCurrency, double accBalance) {
		this.accNumber = accNumber;
		this.accOwner = accOwner;
		this.accPin = accPin;
		this.accCurrency = accCurrency;
		this.accBalance = accBalance;
	}
	
	public int getAccNumber() {
		return accNumber;
	}
	public void setAccNumber(int accNumber) {
		this.accNumber = accNumber;
	}
	public String getAccOwner() {
		return accOwner;
	}
	public void setAccOwner(String accOwner) {
		this.accOwner = accOwner;
	}
	public String getAccCurrency() {
		return accCurrency;
	}
	public void setAccCurrency(String accCurrency) {
		this.accCurrency = accCurrency;
	}
	public double getAccBalance() {
		return accBalance;
	}
	public void setAccBalance(double accBalance) {
		this.accBalance = accBalance;
	}
	
	public String getAccPin() {
		return accPin;
	}

	public void setAccPin(String accPin) {
		this.accPin = accPin;
	}



	public static class Builder{
		private Account account;
		public Builder(){
			account = new Account();
		}
		public Builder setAccNumber(int accNumber){
			account.setAccNumber(accNumber);
			return this;
		}
		public Builder setAccOwner(String accOwner){
			account.setAccOwner(accOwner);
			return this;
		}
		public Builder setAccBalance(double accBalance){
			account.setAccBalance(accBalance);
			return this;
		}
		public Builder setAccCurrency(String accCurrency){
			account.setAccCurrency(accCurrency);
			return this;
		}
		public Builder setAccPin(String pin){
			account.setAccPin(pin);
			return this;
		}
		
		public Account build(){
			return account;
		}
	}
}
