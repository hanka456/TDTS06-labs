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
      private HttpMessageReader inFromBrowser;
      private HttpMessageWriter outToBrowser;
      private final static int SO_TIMEOUT = 30000;
      private final static int KEEP_ALIVE = SO_TIMEOUT/1000 - 1;
    
      public ServerThread(Socket socket) throws IOException
      {
	    browserSocket = socket;
	    browserSocket.setSoTimeout(SO_TIMEOUT);
	    inFromBrowser = new HttpMessageReader(browserSocket.getInputStream());
	    outToBrowser = new HttpMessageWriter(browserSocket.getOutputStream());

	    System.out.println("Started new ServerThread");
      }
	
      public void run()
      {
	    HttpMessage messageFromBrowser;
	    HttpMessage messageToBrowser;
	    String adress;

	    try
	    {
		  while(true)
		  {
			messageFromBrowser = inFromBrowser.read();
			adress = messageFromBrowser.getAdress();
			messageFromBrowser.setConnectionClose();
			messageToBrowser = new Client(adress).bounce(messageFromBrowser);
			messageToBrowser.setConnectionKeepAlive(KEEP_ALIVE);
			outToBrowser.write(messageToBrowser);
		  }
	    }

	    catch(IOException e)
	    {
		  System.out.println("Exception caught when trying to read or write from browserSocket");
		  System.out.println(e.getMessage());
	    }

	    finally
	    {
		  close();
	    }
      }

      private void close()
      {
	    try
	    {
		  inFromBrowser.close();
		  outToBrowser.close();
		  browserSocket.close();

		  System.out.println("Streams and socket closed in serverThread " + this.getName());
	    }

	    catch(IOException e)
	    {
		  System.out.println("Exception caught");
		  System.out.println(e.getMessage());
	    }
      }
}
