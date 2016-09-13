/*
  Hans Kanders
*/

package netninny;

import java.net.*;
import java.io.*;
import java.util.regex.*;

public class Client
{
      private Socket webSocket;
      private HttpMessageWriter outToWeb;
      private HttpMessageReader inFromWeb;
	
      public Client(String adress)
      {
	    try
	    {
		  webSocket = new Socket(adress, 80);
		  webSocket.setKeepAlive(true);

		  outToWeb = new HttpMessageWriter(webSocket.getOutputStream());
		  inFromWeb = new HttpMessageReader(webSocket.getInputStream());

		  System.out.println("Started new Client");
		  System.out.println("Opened a new connection to web with web adress " +
				     webSocket.getRemoteSocketAddress() +
				     " and local adress " +
				     webSocket.getLocalSocketAddress());
	    }
	    catch(UnknownHostException e)
	    {
		  System.out.println("Exception caught when trying to bind socket to adress " + adress);
		  System.out.println(e.getMessage());
	    }
	    catch(IOException e)
	    {
		  System.out.println("Exception caught when trying to create new Client");
		  System.out.println(e.getMessage());
	    }	    
      }
	
      HttpMessage bounce(HttpMessage message)
      {
	    outToWeb.write(message);
	    HttpMessage messageFromWeb = inFromWeb.read();
	    return messageFromWeb;
      }
}
