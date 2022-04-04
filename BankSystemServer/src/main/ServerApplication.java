package main;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import bank.Bank;
import services.BalanceTransfer;
import services.BalanceUpdate;
import services.CreateAccountService;
import services.MonitorUpdatesService;
import transmission.NormalTransmission;
import transmission.Socket;
import services.CallbackHandler;
import services.CheckBalanceService;
import services.CloseAccountService;

// Server Application that starts server
public class ServerApplication {
	private static Server server;
	private static Bank bank;
	private static Socket socket;
	private static int portNumber;
	private static InetAddress address;
	private static CallbackHandler callbackHandler;

	public static void main(String[] args) {
		ConsoleLogger console = new ConsoleLogger(new Scanner(System.in));
		try {
			System.out.println("Starting server");
			bank = new Bank();

			// Set server configurations
			String addressInput = console.askForString("Enter IP address on which the server is being hosted:");
			address = InetAddress.getByName(addressInput);
			portNumber = console.askForInteger("Enter Port No. for server to listen at:");
			socket = new NormalTransmission(new DatagramSocket(portNumber, address));

			// Choose type of server
			int serverChoice = console.askForInteger(1, 2, "Choose Server type: \n1)At-Least-Once\n2)At-Most-Once");
			if (serverChoice == 1) {
				server = new Server(socket); // At-least-once server
			} else if (serverChoice == 2) {
				server = new AtMostOnceServer(socket); // At-most-once server
			}

			// Specify transmission mode
			int socketType = console.askForInteger(1, 3,
					"Select Transmission Mode: \n1)Normal Transmission\n2)Sending Transmission Loss\n");
			if (socketType == 2) {
				double socketProbability = 1 - console.askForDouble(0.0, 1.0, "socketProbability of packetloss:");
				server.useSendingLossSocket(socketProbability);
			}


			// Handle removal of expired subscribers
			callbackHandler = new CallbackHandler(socket);
			Thread validityCheck = new Thread(callbackHandler);
			validityCheck.start();

			// Add services
			server.addServiceToServer(1, new CreateAccountService(callbackHandler));
			server.addServiceToServer(2, new CloseAccountService(callbackHandler));
			server.addServiceToServer(3, new BalanceUpdate(callbackHandler));
			server.addServiceToServer(4, new MonitorUpdatesService(callbackHandler));
			server.addServiceToServer(5, new CheckBalanceService(callbackHandler));
			server.addServiceToServer(6, new BalanceTransfer(callbackHandler));

			server.start(); // Start server
		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {
			ConsoleLogger.debug("Server error!");
			e.printStackTrace();
		}

	}

}
