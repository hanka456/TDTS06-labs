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
    
      public ServerThread(Socket socket) throws IOException
      {
	    browserSocket = socket;
	    inFromBrowser = new HttpMessageReader(browserSocket.getInputStream());
	    outToBrowser = new HttpMessageWriter(browserSocket.getOutputStream());
      }
	
      public void run()
      {
	    HttpMessage messageFromBrowser;
	    HttpMessage messageToBrowser;
	    String adress;
	    boolean keepAlive = true;

	    while(keepAlive)
	    {
		  messageFromBrowser = inFromBrowser.read();
		  adress = messageFromBrowser.getAdress();
		  messageFromBrowser.setConnectionClose();
		  messageToBrowser = new Client(adress).bounce(messageFromBrowser);
		  messageToBrowser.setConnectionKeepAlive();
		  outToBrowser.write(messageToBrowser);
	    }
      }
}
