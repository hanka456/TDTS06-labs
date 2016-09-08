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
      private String messageFromWeb;
      private BufferedReader inFromWeb;
      private PrintWriter outToWeb;
	
      public Client(String adress)
      {
	    try
	    {
		  webSocket = new Socket(adress, 80);
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

      private void printMessageToWeb(String message)
      {
	    System.out.println("Wrote to web: \n" + message);
      }
	
      private void readFromWeb()
      {
	    String inLine;
	    messageFromWeb = "";

	    try
	    {
		  while ((inLine = inFromWeb.readLine()) != null)
		  {
			if (inLine.equals(null))
			      break;
			messageFromWeb += (inLine + "\r\n");
		  }
	    }
	    catch(IOException e)
	    {
		  System.out.println("Exception caught when trying to read from BufferedReader inFromWeb");
		  System.out.println(e.getMessage());
	    }	    
      }

      private void printMessageFromWeb()
      {
	    System.out.println("Recieved from web: \n" + messageFromWeb);
      }
	
      private void checkHttpContent()
      {
      }
	
      String bounce(String message)
      {
	    writeToWeb(message);
	    printMessageToWeb(message);
	    readFromWeb();
	    printMessageFromWeb();
	    checkHttpContent();
	    //webSocket.close();
	    return messageFromWeb;
      }
}
