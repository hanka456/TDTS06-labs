/*
Hans Kanders
*/ 

import java.net.*;
import java.io.*;
import java.util.regex.*;

class Client
{
	private Socket webSocket;
	private String messageFromWeb;
	private BufferedReader inFromWeb;
	private PrintWriter outToWeb;
	
	public Client(String adress) throws IOException, UnknownHostException
	{
		webSocket = new Socket(adress, 80);
		inFromWeb = 
			new BufferedReader(
			new InputStreamReader(webSocket.getInputStream()));
		outToWeb =
			new PrintWriter(webSocket.getOutputStream(), true);		
	}	

	private void writeToSocket(String message) throws IOException
	{
		//System.out.println("Wrote to web: \n" + message);	
		outToWeb.println(message);		
	}
	
	private void readFromSocket() throws IOException
	{
		String inLine;
		messageFromWeb = "";
		while ((inLine = inFromWeb.readLine()) != null)
		{
			if (inLine.equals(null))
				break;
			messageFromWeb += (inLine + "\r\n");
		}
	}
	
	private void checkHttpContent()
	{
	}
	
	String bounce(String message) throws IOException
	{
		writeToSocket(message);
		readFromSocket();
		checkHttpContent();
		return messageFromWeb;
	}
}

class Server
{
	private Socket browserSocket;
	private String messageFromBrowser;
	private String messageFromWeb;
	private BufferedReader inFromBrowser;
	private PrintWriter outToBrowser;
	private Client client;
	
	public Server(Socket socket) throws IOException
	{
		browserSocket = socket;
		inFromBrowser = 
			new BufferedReader(
			new InputStreamReader(browserSocket.getInputStream()));
		outToBrowser =
			new PrintWriter(browserSocket.getOutputStream(), true);
	}
	
	private void readFromBrowser() throws IOException
	{
		String inLine;
		messageFromBrowser = "";
		while ((inLine = inFromBrowser.readLine()) != null)
		{
			if (inLine.equals(""))
				break;
			messageFromBrowser += (inLine + "\r\n");
		}
	}
	
	private void checkHttpHeader()
	{
	}
	
	private void writeToBrowser(String message) throws IOException
	{
		outToBrowser.println(message);
		System.out.println("Wrote to browser: \n" + message);
	}
	
	private String getAdress()
	{
		Pattern pattern = Pattern.compile("Host: (.*?)\r\n");
		Matcher matcher = pattern.matcher(messageFromBrowser);
		if (matcher.find())
		{
			return matcher.group(1);
		}
		else return null;
	}
	
	public void start() throws IOException
	{
		readFromBrowser();
		System.out.println(messageFromBrowser);
		checkHttpHeader();
		client = new Client(getAdress());
		messageFromWeb = client.bounce(messageFromBrowser);
		writeToBrowser(messageFromWeb);
	}
}

public class NetNinny
{
	public static void main(String[] args)
	{
		if (args.length != 1) 
		{
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
		
		int portNumber = Integer.parseInt(args[0]);
		
		try
		(
			//Initial socket used for establishing connection
			ServerSocket serverSocket =
                new ServerSocket(portNumber);
		)
		{
			Server server = new Server(serverSocket.accept());
			server.start();
		}
		
		catch (IOException e) 
		{
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}