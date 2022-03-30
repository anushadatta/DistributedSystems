package transmission;

import java.net.InetAddress;
import java.util.Random;
import main.ConsoleLogger;
import message.Marshall;
import java.io.IOException;

// Simulates packet loss when packets are being sent out 
public class SendingLossTransmission extends WrapperSocket {

	private final Random random;
	private final double socketProbability;

	public SendingLossTransmission(Socket socket, double socketProbability) {
		super(socket);
		this.random = new Random();
		this.socketProbability = socketProbability;
	}

	// Higher socketProbability - higher chance of sending successfully i.e. lower
	// packet
	// loss
	public void send(Marshall msg, InetAddress address, int port) throws IOException {
		if (random.nextDouble() < socketProbability) {
			super.send(msg, address, port);
		} else {
			try {
				Thread.sleep(1000);
				ConsoleLogger.debug("Simulate Packet Loss while Sending");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
