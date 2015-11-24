package clientserveur.tp02;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Vector;

public class UDPEchoServerMulti extends UDPEchoServer {
	private Vector<Thread> threads;
	
	@Override
	public void start(int port) throws SocketException
	{
		threads = new Vector<>();
		super.start(port);
	}
	
	@Override
	public void stop()
	{
		for (Thread t : threads)
		{
			try {
				t.join();
			} catch (InterruptedException e) { }
		}
		super.stop();
	}
	
	
	@Override
	protected void handlePacket(DatagramPacket p)
	{
		final DatagramPacket fpacket = p;

		Thread thread = new Thread () {
			private final DatagramPacket packet =
					//Il est important ici de faire une copie
					//De l'object DatagramPacket car sinon les threads
					//vont tous écrire dans le meme buffer ce qui causera
					//des erreurs
					new DatagramPacket(fpacket.getData().clone(),
							   fpacket.getLength(),
							   fpacket.getAddress(),
							   fpacket.getPort());
			
			public void run() {
				
				//UDPEchoServerMulti: représente le nom de l'objet englobant cet objet Thread
				//.super accède à la super-classe de l'objet englobant
				//On appelle donc la méthode handlePacket, de la classe UDPEchoServer (pas UDPEchoServerMulti)
				
				UDPEchoServerMulti.super.handlePacket(packet);
			}
		};
		
		thread.start();
	}
	public static void main(String[] args) {
		try
		{
			UDPEchoServer s = new UDPEchoServerMulti();
			s.start(12345);
			s.mainLoop();
			s.stop();
		} catch (Exception e)
		{
			System.err.println("Erreur du serveur, abandon : ");
			e.printStackTrace();
			return;
		}
	}

}
