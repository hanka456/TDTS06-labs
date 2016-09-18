/*
  Hans Kanders
  hanka892@student.liu.se
  NetNinny.java
*/

package netninny;

import java.net.*;
import java.io.*;

public class NetNinny
{
      public static void main(String[] args)
      {
	    //Exit program if number of arguments != 1
	    if (args.length != 1)
	    {
		  System.err.println("Usage: java EchoServer <port number>");
		  System.exit(1);
	    }

	    int portNumber = Integer.parseInt(args[0]);
	    System.out.println("NetNinny started. Waiting for connections on port " + portNumber + "...");

	    try (
		  //Initial socket used for establishing connection
		  ServerSocket serverSocket =
		  new ServerSocket(portNumber);
		  )
	    {
		  //Main program loop. Ctrl+c exits
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
