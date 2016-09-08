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
      private String messageFromBrowser;
      private String messageFromWeb;
      private BufferedReader inFromBrowser;
      private PrintWriter outToBrowser;
      private Client client;
    
      public ServerThread(Socket socket) throws IOException
      {
	    browserSocket = socket;
	    inFromBrowser = 
		  new BufferedReader(
			new InputStreamReader(browserSocket.getInputStream()));
	    outToBrowser =
		  new PrintWriter(browserSocket.getOutputStream(), true);
      }
    
      private void readFromBrowser()
      {
	    String inLine;
	    messageFromBrowser = "";
	    try
	    {
		  while ((inLine = inFromBrowser.readLine()) != null)
		  {
			if (inLine.equals(""))
			      break;
			messageFromBrowser += (inLine + "\r\n");
		  }
	    }
	    catch (IOException e)
	    {
		  System.out.println("Exception caught when trying to read from BufferedReader inFromBrowser");
		  System.out.println(e.getMessage());
	    }
		  
      }
    
      private void printMessageFromBrowser()
      {
	    System.out.println("Recieved from browser: \n" + messageFromBrowser);
      }
	
      private void checkHttpHeader()
      {
      }
	
      private void writeToBrowser(String message)
      {
	    outToBrowser.println(message);
      }

      private void printMessageToBrowser(String message)
      {
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
	
      public void run()
      {
	    readFromBrowser();
	    printMessageFromBrowser();
	    checkHttpHeader();
	    client = new Client(getAdress());
	    messageFromWeb = client.bounce(messageFromBrowser);
	    writeToBrowser(messageFromWeb);
	    printMessageToBrowser(messageFromWeb);
	    // browserSocket.close();	
      }
}
