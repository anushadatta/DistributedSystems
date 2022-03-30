package transmission;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import main.ConsoleLogger;
import message.Marshall;

// Simulate packet loss when sending
public class SendingLossTransmission extends WrapperSocket {

	private final Random random;
	private final double socketProbability;

	public SendingLossTransmission(Socket socket, double socketProbability) {
		super(socket);
		this.random = new Random();
		this.socketProbability = socketProbability;
	}

	// Higher socketProbability - higher chance of sending i.e. lower packet loss
	public void send(Marshall msg, InetAddress address, int port) throws IOException {
		if (random.nextDouble() < socketProbability) {
			super.send(msg, address, port);
		} else {
			try {
				Thread.sleep(1000);
				ConsoleLogger.debug("Simulate Packet Loss on Sending");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
