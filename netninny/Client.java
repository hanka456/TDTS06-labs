/*
  Hans Kanders
*/

package netninny;

import java.net.*;
import java.io.*;

/* A new Client-object is spawned when a
   ServerThread-object wants to forward a message to the web.
   The Client is responsible for communication to the web and
   blocking any response if any of the words in the blacklist 
   show up in the response body. Connections are not re-used. */
public class Client
{
      private Socket webSocket;
      private HttpMessageWriter outToWeb;
      private HttpMessageReader inFromWeb;
	
      /* Constructor */
      public Client(String adress)
      {
	    try
	    {
		  webSocket = new Socket(adress, 80);

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

      /* Returns true if response body blacklist match.
	 Only uncompressed text-content are searched,
	 other content returns false.*/
      private boolean illegalBody (HttpMessage message)
      {
	    boolean illegal = false;
	    String contentType = message.getContentType();
	    boolean compressed = message.isCompressed();

	    if(contentType != null && !compressed)
	    {
		  if(contentType.toLowerCase().matches("text(.*?)"))
		  {
			String bodyString = new String(message.body);
			for(int i = 0; i < Global.blackList.length; i++)
			{
			      if (bodyString.toLowerCase().contains(Global.blackList[i].toLowerCase()))
			      {
				    illegal = true;
				    System.out.println("Illegal content detected. Blocking.");
				    break;
			      }
			}
		  }
	    }

	    return illegal;
      }

      /* Message sent to ServerThread if body blacklist match */
      private HttpMessage illegalBodyMessage()
      {
	    String body =
		  "<html>\r\n"
		  + "<title>\n"
		  + "Net Ninny Error Page 1 for CPSC 441 Assignment 1\n"
		  + "</title>\n"
		  + "<body>\n"
		  + "<p>\n"
		  + "Sorry, but the Web page that you were trying to access\n"
		  + "is inappropriate for you, based on some of the words it contains.\n"
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

      /* Takes a request as argument
	 Send message to web
	 read response
	 block message if blacklist-match
	 else return response */
      HttpMessage bounce(HttpMessage message)
      {
	    HttpMessage messageFromWeb = null;
	    
	    try
	    {
		  outToWeb.write(message);
		  messageFromWeb = inFromWeb.read();

		  if(illegalBody(messageFromWeb))
		  {
			messageFromWeb = illegalBodyMessage();
		  }
	    }
	    
	    catch(IOException e)
	    {
		  System.out.println("Exception cought when trying to read or write from webSocket");
		  System.out.println(e.getMessage());
	    }

	    //Ensures that close is run
	    finally
	    {
		  close();
	    }

	    return messageFromWeb;
      }

      /* Closes streams and socket */
      private void close()
      {
	    try
	    {
		  inFromWeb.close();
		  outToWeb.close();
		  webSocket.close();

		  System.out.println("Streams and socket closed in Client");
	    }

	    catch(IOException e)
	    {
		  System.out.println("Exception caught");
		  System.out.println(e.getMessage());
	    }
      }
}
