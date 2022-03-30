package transmission;

import java.net.DatagramPacket;
import java.net.InetAddress;
import main.ConsoleLogger;
import message.Marshall;
import java.util.Random;
import java.io.IOException;

// Simulate sending/receiving corrupted packets
public class CorruptedTransmission extends WrapperSocket {

	private final Random random;
	private final double socketProbability;

	public CorruptedTransmission(Socket socket, double socketProbability) {
		super(socket);
		this.random = new Random();
		this.socketProbability = socketProbability;
	}

	@Override
	public void receive(DatagramPacket p) throws IOException {
		super.receive(p);
		if (random.nextDouble() > socketProbability) {
			ConsoleLogger.debug("Receiving Corrupted Data");
			corruptData(p.getData());
		}
	}

	@Override
	public void send(Marshall msg, InetAddress address, int port) throws IOException {
		byte[] msgByte = msg.getByteArray();
		if (random.nextDouble() > socketProbability) {
			ConsoleLogger.debug("Sending Corrupted Data");
			corruptData(msgByte);
		}
		NormalTransmission socket = (NormalTransmission) (this.getSocket());
		DatagramPacket p = new DatagramPacket(msgByte, msgByte.length, address, port);
		socket.send(p);
	}

	public void corruptData(byte[] message) {
		this.random.nextBytes(message);
	}

}
