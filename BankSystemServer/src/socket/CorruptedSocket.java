package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Random;

import main.Console;
import message.BytePacker;

// Simulate sending/receiving corrupted packets
public class CorruptedSocket extends WrapperSocket{
	
	private final double probability;
	private final Random random;
	public CorruptedSocket(Socket socket, double probability) {
		super(socket);
		this.random = new Random();
		this.probability = probability;
	}
	
	public void corruptData(byte[] message){
		this.random.nextBytes(message);
	}
	
	@Override
	public void send(BytePacker msg, InetAddress address, int port) throws IOException {
		byte[] msgByte = msg.getByteArray();
		if(random.nextDouble()>probability){	
			Console.debug("Sending Corrupted Data");
			corruptData(msgByte);
		}
		NormalSocket socket = (NormalSocket) (this.getSocket());
		DatagramPacket p = new DatagramPacket(msgByte, msgByte.length,address, port);
		socket.send(p);
	}
	@Override
    public void receive(DatagramPacket p) throws IOException {
        super.receive(p);
        if (random.nextDouble()>probability) {
        	Console.debug("Receiving Corrupted Data");
        	corruptData(p.getData());
        }
    }
	
}
