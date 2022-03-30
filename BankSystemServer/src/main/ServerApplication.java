package main;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import bank.Bank;
import services.BalanceTransfer;
import services.BalanceUpdate;
import services.CallbackHandlerClass;
import services.CheckBalanceService;
import services.CloseAccountService;
import services.CreateAccountService;
import services.RegisterCallbackService;
import socket.NormalSocket;
import socket.Socket;

// Server Application that starts server
public class ServerApplication {
	private static Server server;
	private static Bank bank;
	private static CallbackHandlerClass callbackHandler;
	private static InetAddress address;
	private static Socket socket;
	private static int portNumber;
	public static void main(String[] args){
		Console console = new Console(new Scanner(System.in));
		try {
			System.out.println("Starting server");
			bank = new Bank();
			
			// Set server configurations
			String addressInput = console.askForString("Enter IP address on which the server is being hosted:");
			address = InetAddress.getByName(addressInput);
			portNumber = console.askForInteger("Enter Port No. for server to listen at:");
			socket = new NormalSocket(new DatagramSocket(portNumber,address));
			
			// Choose type of server
			int serverChoice = console.askForInteger(1, 2, "Choose Server type: \n1)At-Least-Once\n2)At-Most-Once");
			if(serverChoice==1){
				server = new Server(socket); 			// At-least-once server
			}
			else if(serverChoice==2){
				server = new AtMostOnceServer(socket); 	// At-most-once server
			}
			
			// Specify transmission mode
			int socketType = console.askForInteger(1, 3, "Select Transmission Mode: \n1)Normal Transmission\n2)Sending Transmission Loss");
			 if(socketType==2){
				double probability = 1 - console.askForDouble(0.0, 1.0, "Probability of packetloss:");
				server.useSendingLossSocket(probability);
			 }
			
			// Handle removal of expired subscribers
			callbackHandler = new CallbackHandlerClass(socket);
			Thread validityCheck = new Thread(callbackHandler);
			validityCheck.start();
			
			
			// Add services
			server.addServiceToServer(1, new CreateAccountService(callbackHandler));
			server.addServiceToServer(2, new CloseAccountService(callbackHandler));
			server.addServiceToServer(3, new BalanceUpdate(callbackHandler));
			server.addServiceToServer(4, new RegisterCallbackService(callbackHandler));
			server.addServiceToServer(5, new CheckBalanceService(callbackHandler));
			server.addServiceToServer(6, new BalanceTransfer(callbackHandler));
		
			server.start();							// Start server
		} catch (SocketException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			Console.debug("Server error!");
			e.printStackTrace();
		} 
		
	}
	
}
