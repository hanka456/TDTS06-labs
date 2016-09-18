/*
  Hans Kanders
  hanka892@student.liu.se
  HttpMessageWriter.java
*/

package netninny;

import java.io.*;

//Used as a interface to write HttpMessages to an OutputStream
public class HttpMessageWriter
{
      private BufferedOutputStream dataOut;

      //Constructor
      public HttpMessageWriter(OutputStream out)
      {
	    dataOut = new BufferedOutputStream(out);
      }

      //Writes a HttpMessage to the OutputStream
      public void write(HttpMessage message)
      {
	    try
	    {
		  //Write header and body to outputstream
		  dataOut.write(message.header.getBytes());
		  dataOut.write(message.body);
		  dataOut.flush();

		  System.out.println("Wrote:\n" + message.header);
	    }

	    //Any problems writing to the OutputStream are handled here
	    catch(IOException e)
	    {
		  System.out.println(e.getStackTrace());
	    }
      }

      //Closes the OutputStream
      public void close() throws IOException
      {
	    dataOut.close();
      }
}
