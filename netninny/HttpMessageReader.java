/*
  Hans Kanders
*/

package netninny;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.Arrays;

public class HttpMessageReader
{
      BufferedInputStream dataIn;

      HttpMessageReader(InputStream in)
      {
	    this.dataIn =
		  new BufferedInputStream(in);
      }

      public HttpMessage read()
      {
	    HttpMessage message = null;
	    int arrayLength = 1024;
	    int headerLength;
	    int contentLength;
	    byte[] headerArray = new byte[arrayLength];

	    try
	    {
		  //Read three first bytes of header
		  headerArray[0] = (byte)dataIn.read();
		  headerArray[1] = (byte)dataIn.read();
		  headerArray[2] = (byte)dataIn.read();
		  
		  for(int i = 3; true; i++)
		  {
			//Increase size of array if needed
			if(i == arrayLength)
			{
			      arrayLength = 2*arrayLength;
			      headerArray = Arrays.copyOf(headerArray, arrayLength);
			}

			//Read one byte
			headerArray[i] = (byte)dataIn.read();
			
			//Check if end of header has been reached
			//End of header is \r\n\r\n
			if((headerArray[i-3] == (byte)13) &&
			   (headerArray[i-2] == (byte)10) &&
			   (headerArray[i-1] == (byte)13) &&
			   (headerArray[i] == (byte)10))
			{
			      //Trim of unused space in array and exit loop
			      headerArray = Arrays.copyOf(headerArray, i+1);
			      break;
			}
		  }

		  //Make new HttpMessage with header and new byte array
		  //with size read from Content-Length in header
		  message = new HttpMessage(new String(headerArray));
		  contentLength = message.getContentLength();
		  byte[] bodyArray = new byte[contentLength];

		  //Read body
		  for(int i = 0; i < contentLength; i++)
		  {
			bodyArray[i] = (byte)dataIn.read();
		  }
		  
		  //Set message to use the new body
		  message.body = bodyArray;

		  System.out.println("Read:\n" + message.header);		  
	    }	    

	    catch(IOException e)
	    {
		  System.out.println("Exception cought when trying to read from BufferedInputStream");
		  System.out.println(e.getMessage());
	    }

	    return message;
      }
}
