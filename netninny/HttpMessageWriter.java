/*
Hans Kanders
*/

package netninny;

import java.io.*;
import java.net.*;
import java.lang.String;

public class HttpMessageWriter
{
      private BufferedOutputStream dataOut;

      public HttpMessageWriter(OutputStream out)
      {
	    dataOut = new BufferedOutputStream(out);
      }

      public void write(HttpMessage message)
      {
	    try
	    {
		  //remove utf-8
		  dataOut.write(message.header.getBytes("UTF-8"));
		  dataOut.write(message.body);
		  dataOut.flush();

		  System.out.println("Wrote:\n" + message.header);
	    }

	    catch(IOException e)
	    {
		  System.out.println(e.getStackTrace());
	    }
      }
}
