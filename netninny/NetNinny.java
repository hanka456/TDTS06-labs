/*
  Hans Kanders
*/

package netninny;

import java.net.*;
import java.io.*;
import java.util.regex.*;

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

	    try (
		  //Initial socket used for establishing connection
		  ServerSocket serverSocket =
		  new ServerSocket(portNumber);
		  )
	    {
		  while(true)
		  {
			new ServerThread(serverSocket.accept()).start();
		  }
	    }

	    catch (IOException e)
	    {
		  System.out.println("Exception caught when trying to listen on port "
				     + portNumber + " or listening for a connection");
		  System.out.println(e.getMessage());
	    }
      }
} 
