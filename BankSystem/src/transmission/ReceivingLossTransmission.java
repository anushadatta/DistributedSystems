package transmission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.Random;

import main.ConsoleLogger;

// Simulate packet loss when receiving
public class ReceivingLossTransmission extends WrapperSocket {
	private Random random;
	private double socketProbability;

	public ReceivingLossTransmission(Socket socket, double socketProbability) {
		super(socket);
		this.random = new Random();
		this.socketProbability = socketProbability;
	}

	// Higher socketProbability - higher chance of receiving i.e. lower packet loss
	public void receive(DatagramPacket p) throws IOException, SocketTimeoutException {
		if (random.nextDouble() < this.socketProbability) {
			super.receive(p);
		} else {
			try {
				Thread.sleep(200);
				ConsoleLogger.debug("Simulate Packet Loss when Receiving");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			throw new SocketTimeoutException();
		}
	}
}
