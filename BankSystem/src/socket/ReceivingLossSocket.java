package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.Random;

import main.Console;

// Simulate packet loss when receiving
public class ReceivingLossSocket extends WrapperSocket {
	private Random random;
	private double probability;
	
	public ReceivingLossSocket(Socket socket, double probability){
		super(socket);
		this.random = new Random();
		this.probability = probability;
	}
	
	// Higher probability - higher chance of receiving i.e. lower packet loss
	public void receive(DatagramPacket p) throws IOException, SocketTimeoutException{
		if(random.nextDouble()<this.probability){
			super.receive(p);
		}
		else{
			try {
				Thread.sleep(200);
				Console.debug("Simulate Packet Loss when Receiving");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}throw new SocketTimeoutException();
		}
	}
}
