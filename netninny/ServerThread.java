/*
  Hans Kanders
*/

package netninny;

import java.net.*;
import java.io.*;
import java.util.regex.*;

public class ServerThread extends Thread
{
      private Socket browserSocket;
      private BufferedReader inFromBrowser;
      private PrintWriter outToBrowser;

      //private String messageFromBrowser;
      //private String messageFromWeb;
      //private Client client;
    
      public ServerThread(Socket socket) throws IOException
      {
	    browserSocket = socket;
	    if (browserSocket.isConnected())
	    {
		  System.out.println("Opened a new connection to browser with browser adress " +
				     browserSocket.getRemoteSocketAddress() +
				     " and local adress " +
				     browserSocket.getLocalSocketAddress());
	    }
	    
	    browserSocket.setKeepAlive(true);
	    inFromBrowser =
		  new BufferedReader(
			new InputStreamReader(browserSocket.getInputStream()));
	    outToBrowser =
		  new PrintWriter(browserSocket.getOutputStream(), true);
      }
    
      private String readFromBrowser()
      {
	    String inLine;
	    String message = "";

	    System.out.println("HEJ");
	    
	    try
	    {
		  while ((inLine = inFromBrowser.readLine()) != null)
		  {			      
			System.out.println("foo");
			if (inLine.isEmpty())
			{
			      System.out.println("Empty");
			      break;
			}
			message += (inLine + "\r\n");
		  }
		  System.out.println("bar");

		  
	    }
	    	   
	    catch (IOException e)
	    {
		  System.out.println("Exception caught when trying to read from BufferedReader inFromBrowser");
		  System.out.println(e.getMessage());
	    }
	    
	    System.out.println("Read from browser:\n" + message);
	    return message;
	    
      }
	
      private void writeToBrowser(String message)
      {
	    outToBrowser.println(message);
	    System.out.println("Wrote to browser:\n" + message);
      }

      private String getHeaderField(String message, String header)
      {
	    Pattern pattern = Pattern.compile(header + ": (.*?)\r\n");
	    Matcher matcher = pattern.matcher(message);
	    if (matcher.find())
	    {
		  return matcher.group(1);
	    }
	    else return null;
      }
      
      private String setConnectionClose(String message)
      {
	    return message.replaceFirst("Connection: keep-alive", "Connection: close");
      }

      private String setConnectionKeepAlive(String message)
      {
	    return message.replaceFirst("Connection: close", "Connection: keep-alive");
      }
	
      public void run()
      {
	    String messageFromBrowser, messageToBrowser,
		  adress, messageToWeb, messageFromWeb;

	    while(true)
	    {
		  System.out.println("TJA");
		  messageFromBrowser = readFromBrowser();
		  adress = getHeaderField(messageFromBrowser, "Host");
		  System.out.println(adress);
		  messageToWeb = setConnectionClose(messageFromBrowser);
		  
		  messageFromWeb = new Client(adress).bounce(messageToWeb);

		  messageToBrowser = setConnectionKeepAlive(messageFromWeb);
		  writeToBrowser(messageToBrowser);
	    }
      }
}
