package clientserveur.tp02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UDPEchoServer {
	/**
	 * Attribut privé contenant la socket avec laquelle le serveur
	 * communique avec les clients.
	 */
	private DatagramSocket serverSocket;

	/**
	 * Un compteur pour le nombre de message reçus
	 */
	private int receivedMessages;
	
	/**
	 * Attribut contenant l'heure à la quelle le serveur a été démarré (en millisecondes)
	 * Utilisé pour l'affichage des messages.
	 */
	static long origin;
	
	/**
	 * Constante définissant la taille (en octets) des datagrammes UDP
	 */
	static public int PACKET_LENGTH = 128;

	/**
	 * Durée d'attente par défaut pour une connexion.
	 */
	static public int TIMEOUT = 1000;

	/**
	 * Constructeur par défaut. Les attributs sont ré-initialisés dans la méthode
	 * {@link UDPEchoServer#start}
	 */
	public UDPEchoServer () {};
	
	/**
	 * Méthode qui démarre le serveur sur le port numéro <code>port</code>.
	 * Lève l'exception <code>SocketException</code> en cas de problème (numéro de port invalide,
	 * ou port déjà occupé par une autre application).
	 * @param port
	 * @throws SocketException
	 */
	void start(int port) throws SocketException
	{
		if (serverSocket == null)
		{
			serverSocket = new DatagramSocket(null);
		}
		
		receivedMessages = 0;
		origin = java.lang.System.currentTimeMillis();
		
		int size = serverSocket.getReceiveBufferSize();
		serverSocket.setReceiveBufferSize(2 * size);
		
		serverSocket.bind(new InetSocketAddress(port));
		System.out.println("Serveur démarré sur le port UDP " + port + " buffer size: " + size);
	}
	/**
	 * Méthode qui arrête le serveur et affiche le nombre de messages UDP reçus
	 * depuis l'appel à {@link #start(int)}
	 */
	public void stop()
	{
		System.out.printf ("Serveur arrété, %d messages reçus.\n", receivedMessages);
		if (serverSocket != null && !serverSocket.isClosed())
		{
			serverSocket.close();
		}
		serverSocket = null;
	}
	/**
	 * Méthode incrémentant le nombre de messages reçus. Cette méthode est <i>thread-safe</i>.
	 */
	protected synchronized void incrMessageCount()
	{
		receivedMessages++;
	}
	/**
	 * Méthode implémentant le traitement fait pour chaque datagramme reçu. Peut-être
	 * redéfinie par les classes descendantes de la classe {@link UDPEchoServer}.
	 * @param packet
	 */
	protected void handlePacket(DatagramPacket packet)
	{
		incrMessageCount();
		
		System.out.printf("[%f s] Paquet reçu du client: %s, port: %d,"
				+ "message: '%s'\n",
				  ((double) java.lang.System.currentTimeMillis() - origin) / 1000,
				  packet.getAddress().toString(),
				  packet.getPort(),
				  new String(packet.getData(),0, packet.getLength()));
		try {
			//Simule un temps de traitement très long
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Méthode implémentant la boucle d'attente du serveur. Le serveur attends un datagramme
	 * sur {@link #serverSocket} pendant au plus {@link #TIMEOUT} ms.
	 * Si un datagramme est reçu il est passé à la méthode {@link #handlePacket(DatagramPacket)}.
	 * Avant de recevoir à nouveau un paquet, le code vérifie que l'utilisateur n'a pas appuyé sur
	 * une touche avant de recommencer à attendre.
	 * Les datagrammes ont une longueur d'au plus {@link #PACKET_LENGTH} octets.
	 * 
	 * @throws SocketException levée si une erreur se produit lors de l'attente d'un paquet.
	 */
	
	public void mainLoop() throws SocketException
	{
		byte buffer[] = new byte[PACKET_LENGTH];
		DatagramPacket packet = new DatagramPacket(buffer, PACKET_LENGTH);
		serverSocket.setSoTimeout(TIMEOUT);
		System.out.println("Attente de paquet UDP. Pressez [enter] dans la console pour quitter le serveur");
		while (true)
		{
		 try
		 {
			try
			{
				serverSocket.receive(packet);
				this.handlePacket(packet);
				
			} catch (SocketTimeoutException e) {
				//On a juste épuisé le délai. On peut tester s'il y a quelque chose lu au clavier
				//puis recommencer la boucle.
			};
			//On regarde si l'utilisateur à appuyé sur une touche *sans utiliser de méthode bloquante*
			if (System.in.available() > 0)
			{
				return;
			}
			
		} catch (IOException e)
		  {
			System.err.println("Erreur d'entrée sortie, abandon : ");
			e.printStackTrace();
			return;
		  } 
			
			
		}
		
		
	}
	/**
	 * Programme principal.
	 * @param args
	 */
	public static void main(String args[])
	{
		try
		{
			UDPEchoServer s = new UDPEchoServer();
			
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
