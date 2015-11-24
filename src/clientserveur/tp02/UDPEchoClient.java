package clientserveur.tp02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPEchoClient {

	private DatagramSocket clientSocket;
	
	public UDPEchoClient() {}

	public void start(String server, int port) throws SocketException, UnknownHostException
	{
		if (clientSocket == null)
		{
			clientSocket = new DatagramSocket(null);
		}
		
		clientSocket.connect(InetAddress.getByName(server),port);
	}
	
	public void stop()
	{
	  if (clientSocket != null && clientSocket.isConnected())
	  { 
		  clientSocket.disconnect();
	  }
	  clientSocket.close();
	  clientSocket = null;
	}
	
	protected void send(String message) throws IOException
	{
		clientSocket.send(new DatagramPacket(message.getBytes(), message.length()));
	}
	
	protected void sendFull(String message) throws IOException
	{
		int toSend = message.length();
		int offset = 0;
		byte buffer[] = message.getBytes();
		while (toSend > 0)
		{
			int length = Math.min(toSend, UDPEchoServer.PACKET_LENGTH);
			clientSocket.send(new DatagramPacket(buffer,offset, length));
			toSend -= length;
			offset += length;
		}
		
		
	}
	
	public void mainLoop() throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			System.out.println("Entrer un message pour le serveur 'ctrl-] <enter>' pour quitter:");
			String message = in.readLine();
			if (message.length() > 0 && message.charAt(0) == '\u001d')
			{
				System.out.println("Terminaison du client");
				return;
			}
			this.sendFull(message);
			
		}
		
		
	}
	
	public static void main(String [] args)
	{
		try
		{
			UDPEchoClient s = new UDPEchoClient();
			s.start("localhost", 12345);
			s.mainLoop();
			s.stop();
		} catch (Exception e)
		{
			System.err.println("Erreur du client, abandon : ");
			e.printStackTrace();
			return;
		}
	}
	
}
