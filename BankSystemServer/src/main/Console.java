package main;
import java.util.Scanner;

// Print results to console
public class Console {
	private Scanner scanner;
	static boolean debug_info = true;
	
	public Console(Scanner scanner) {
		this.scanner = scanner;
	}

	public static void println(String str) {
		System.out.println(str);
	}

	public static void debug(String str) {
		if (debug_info)
			System.out.println(str);
	}

	// Prompts user to enter integer
	public int askForInteger(String question) {
		System.out.println(question);
		return askForInteger();
	}

	// Scan input as integer
	public int askForInteger() {
		while (true) {
			try {
				return Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public double askForDouble(String question) {
		System.out.println(question);
		return askForDouble();
	}
	
	// Scan input as double
	public double askForDouble() {
		while (true) {
			try {
				return Double.parseDouble(scanner.nextLine());
			} catch (NumberFormatException ignored) {
			}
		}
	}
	
	// Scan input as string
	public String askForString(String question) {
		System.out.println(question);
		return scanner.nextLine();
	}
	
	// Get user input within a range - integer
	public int askForInteger(int min, int max, String question){
		System.out.println(question);
		int choice = min;
		while(true){
			try{
				choice = Integer.parseInt(scanner.nextLine());
				if(choice >= min && choice <=max){
					return choice;
				}
				else{
					System.out.println("Invalid Choice!");
					System.out.println(question);
				}
			}catch(NumberFormatException ignored){}
		}
	}
	
	// Get user input within a range - double
	public double askForDouble(double min, double max, String question){
		System.out.println(question);
		double choice = min;
		while(true){
			try{
				choice = Double.parseDouble(scanner.nextLine());
				if(choice >= min && choice <=max){
					return choice;
				}
				else{
					System.out.println("Invalid Choice!");
					System.out.println(question);
				}
			}catch(NumberFormatException ignored){}
		}
	}
	
	
}
