package main;

import services.CheckBalanceService;
import services.CloseAccountService;
import services.CreateAccountService;
import services.MonitorUpdatesService;
import services.BalanceTransfer;
import services.BalanceUpdate;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

// class that serves as a wrapper for client services

public class Application {
	public static void main(String[] args) {

		ConsoleLogger console = new ConsoleLogger(new Scanner(System.in));
		String serverIpAddress = console.askForString("Enter Server IP Address: ");
		int serverPortNumber = console.askForInteger("Enter Server Port No.: ");
		int timeout = console.askForInteger("Enter desired socket timeout (in seconds): ");

		try {

			Client client = new Client(serverIpAddress, serverPortNumber, timeout * 1000);

			// Add services
			client.addService(1, new CreateAccountService());
			client.addService(2, new CloseAccountService());
			client.addService(3, new BalanceUpdate());
			client.addService(4, new MonitorUpdatesService());
			client.addService(5, new CheckBalanceService());
			client.addService(6, new BalanceTransfer());

			// Choose the desired transmission mode - in order to simulate faults and losses
			int socketType = console.askForInteger(1, 3,
					"Select Transmission Mode: \n1)Normal Transmission\n2)Sending Loss Transmission\n");
			if (socketType != 1) {

				double socketProbability = 1.0
						- console.askForDouble(0.0, 1.0, "Enter socketProbability of Packet Loss:");

				if (socketType == 2) {
					client.useSendingLossSocket(socketProbability);
				} else if (socketType == 3) {
					client.useReceivingLossSocket(socketProbability);
				}
			}

			while (true) {
				client.printMenu();
				int serviceNumber = console.askForInteger("Enter your desired service request: ");
				if (serviceNumber == -1)
					break;
				client.execute(serviceNumber, console);
			}

		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
