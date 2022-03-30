package main;

import java.util.Scanner;

// Class to print results to console

public class ConsoleLogger {
	private Scanner scanner;
	static boolean debug_info = true;

	public ConsoleLogger(Scanner scanner) {
		this.scanner = scanner;
	}

	public static void println(String str) {
		System.out.println(str);
	}

	public static void debug(String str) {
		if (debug_info)
			System.out.println(str);
	}

	// Request user input
	public int askForInteger(String question) {
		System.out.println(question);
		return askForInteger();
	}

	public int askForInteger() {
		while (true) {
			try {
				return Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public int askForInteger(int min, int max, String question) {
		System.out.println(question);
		int choice = min;
		while (true) {
			try {
				choice = Integer.parseInt(scanner.nextLine());
				if (choice >= min && choice <= max) {
					return choice;
				} else {
					System.out.println("Invalid Choice!");
					System.out.println(question);
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public double askForDouble(String question) {
		System.out.println(question);
		return askForDouble();
	}

	public double askForDouble() {
		while (true) {
			try {
				return Double.parseDouble(scanner.nextLine());
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public double askForDouble(double min, double max, String question) {
		System.out.println(question);
		double choice = min;
		while (true) {
			try {
				choice = Double.parseDouble(scanner.nextLine());
				if (choice >= min && choice <= max) {
					return choice;
				} else {
					System.out.println("Invalid Choice!");
					System.out.println(question);
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public String askForString(String question) {
		System.out.println(question);
		return scanner.nextLine();
	}

}
