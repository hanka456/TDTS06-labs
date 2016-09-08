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
      private BufferedReader inFromWeb;
      private PrintWriter outToWeb;
	
      public Client(String adress)
      {
	    try
	    {
		  webSocket = new Socket(adress, 80);
		  webSocket.setKeepAlive(true);
		  //System.out.println("created new websocket");
		  inFromWeb = 
			new BufferedReader(
			      new InputStreamReader(webSocket.getInputStream()));
		  outToWeb =
			new PrintWriter(webSocket.getOutputStream(), true);
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

      private void writeToWeb(String message)
      {

	    outToWeb.println(message);  
      }
	
      private String readFromWeb()
      {
	    String inLine;
	    String message = "";

	    try
	    {
		  while ((inLine = inFromWeb.readLine()) != null)
		  {
			if (inLine.equals(null))
			      break;
			message += (inLine + "\r\n");
		  }
	    }
	    catch(IOException e)
	    {
		  System.out.println("Exception caught when trying to read from BufferedReader inFromWeb");
		  System.out.println(e.getMessage());
	    }

	    return message;
      }
	
      String bounce(String message)
      {
	    writeToWeb(message);
	    System.out.println("Wrote to web: \n" + message);
	    String messageFromWeb = readFromWeb();
	    System.out.println("Read from web: " + messageFromWeb);
	    //checkHttpContent();
	    //webSocket.close();
	    return messageFromWeb;
      }
}
