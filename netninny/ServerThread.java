/*
  Hans Kanders
*/

package netninny;

import java.net.*;
import java.io.*;

/*
  A new ServerThread-object is spawned every time a new connection
  is established. The ServerThread handles communication with the host
  and filters data based on blacklist-matches in the requested URL.
  The connection to the host is reused if the host sends a
  connection: Keep-Alive header-field. 
*/
public class ServerThread extends Thread
{
      private Socket browserSocket;
      private HttpMessageReader inFromBrowser;
      private HttpMessageWriter outToBrowser;

      //Real timeout and timeout declared to host
      private final static int SO_TIMEOUT = 30000;
      private final static int KEEP_ALIVE = SO_TIMEOUT/1000 - 1;
    
      //Constructor
      public ServerThread(Socket socket) throws IOException
      {
	    browserSocket = socket;
	    browserSocket.setSoTimeout(SO_TIMEOUT);
	    inFromBrowser = new HttpMessageReader(browserSocket.getInputStream());
	    outToBrowser = new HttpMessageWriter(browserSocket.getOutputStream());

	    System.out.println("Started new ServerThread");
      }

      //Returns true if blacklist match in URL
      private boolean illegalUrl(HttpMessage message)
      {
	    String url = message.getUrl();	    
	    boolean found = false;

	    if (url != null)
	    {
		  for(int i = 0; i < Global.blackList.length; i++)
		  {
			if (url.toLowerCase().contains(Global.blackList[i].toLowerCase()))
			{
			      found = true;
			      break;
			}
		  }
	    }

	    return found;
      }

      //Message sent to host if URL-blacklist match
      private HttpMessage getIllegalUrlMessage()
      {
	    String body =
		  "<html>\r\n"
		  + "<title>\n"
		  + "Net Ninny Error Page 1 for CPSC 441 Assignment 1\n"
		  + "</title>\n"
		  + "<body>\n"
		  + "<p>\n"
		  + "Sorry, but the Web page that you were trying to access\n"
		  + "is inappropriate for you, based on the URL.\n"
		  + "The page has been blocked to avoid insulting your intelligence.\n"
		  + "</p>\n"
		  + "<p>\n"
		  + "Net Ninny\n"
		  + "</p>\n"
		  + "</body>\n"
		  + "</html>\n";

	    String header =
		  "HTTP/1.1 302 redirection\r\n"
		  + "Connection: Close\r\n"
		  + "Content-length: " + body.getBytes().length + "\r\n"
		  + "Content-Type: text/html\r\n"
		  + "\r\n";

	    return new HttpMessage(header, body.getBytes());
      }

      /* Main ServerThread-loop
	 while true
	 read message
	 block message if URL-blacklist match
	 else forward message to new Client-object
	 exit loop if timeout or Host sends close
      */
      public void run()
      {
	    HttpMessage messageFromBrowser;
	    HttpMessage messageToBrowser;
	    String adress;
	    String connection;

	    try
	    {
		  while(true)
		  {
			messageFromBrowser = inFromBrowser.read();
			if(messageFromBrowser.isMethodGet())
			{
			      connection = messageFromBrowser.getConnection();
			
			      if (illegalUrl(messageFromBrowser))
			      {
				    System.out.println("Illegal URL detected. Blocking.");
				    messageToBrowser = getIllegalUrlMessage();
			      }

			      else
			      {
				    adress = messageFromBrowser.getAdress();
				    messageFromBrowser.setConnectionClose();
				    messageFromBrowser.removeCacheFields();
				    messageToBrowser = new Client(adress).bounce(messageFromBrowser);
				    messageToBrowser.setConnection(connection, KEEP_ALIVE);
			      }

			      outToBrowser.write(messageToBrowser);

			      if (! connection.toLowerCase().equals("keep-alive"))
			      {
				    break;
			      }
			}
		  }
	    }

	    //If the read fails 
	    catch(IOException e)
	    {
		  System.out.println("Exception caught when trying to read or write from browserSocket");
		  System.out.println(e.getMessage());
	    }

	    //Ensures that close is run
	    finally
	    {
		  close();
	    }
      }

      //Closes streams and socket
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
