package transmission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.Random;
import main.ConsoleLogger;

// Simulates packet loss when packets are being received
public class ReceivingLossTransmission extends WrapperSocket {
	private Random random;
	private double socketProbability;

	public ReceivingLossTransmission(Socket socket, double socketProbability) {
		super(socket);
		this.random = new Random();
		this.socketProbability = socketProbability;
	}

	// Higher socketProbability - higher chance of receiving successfully i.e. lower packet loss
	public void receive(DatagramPacket p) throws IOException, SocketTimeoutException {
		if (random.nextDouble() < this.socketProbability) {
			super.receive(p);
		} else {
			try {
				Thread.sleep(1000);
				ConsoleLogger.debug("Simulate Packet Loss while Receiving");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			throw new SocketTimeoutException();
		}
	}
}
